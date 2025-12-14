import GUI.Frame;
import GUI.ScreenLayout;
import GameState.Question;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        // Validação dos argumentos
        // Formato esperado: java Client <IP> <PORT> <Jogo> <Equipa> <Username>
        if (args.length < 5) {
            System.out.println("Erro: Argumentos insuficientes.");
            System.out.println("Uso: java Client <IP> <PORT> <Jogo> <Equipa> <Username>");
            return;
        }

        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        String gameCode = args[2];
        String teamName = args[3];
        String username = args[4];

        try {
            System.out.println("A tentar conectar a " + ip + ":" + port + "...");
            Socket socket = new Socket(ip, port);

            // IMPORTANTE: Criar o output stream antes do input para evitar bloqueio
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // --- FASE 4: Protocolo de Ligação Inicial ---
            // Enviar dados de identificação para o servidor
            out.writeObject(gameCode);
            out.writeObject(teamName);
            out.writeObject(username);
            out.flush();

            // Ler resposta do servidor (se aceita ou recusa)
            Object resposta = in.readObject();

            if ("OK".equals(resposta)) {
                System.out.println("Login aceite pelo servidor! A abrir jogo...");

                // --- INICIAR GUI ---
                Frame frame = new Frame();

                // Passamos o 'out' para o ScreenLayout poder enviar respostas
                ScreenLayout screenLayout = new ScreenLayout(out);

                frame.add(screenLayout, BorderLayout.CENTER);
                screenLayout.mostrarPainel(ScreenLayout.PAINEL_INICIO);
                frame.setVisible(true);

                // --- FASE 5: Thread de Escuta (Ouvir o Servidor) ---
                // Esta thread fica sempre ativa à espera de mensagens do servidor
                new Thread(() -> {
                    try {
                        while (true) {
                            Object mensagem = in.readObject();

                            // CASO 1: Receber uma Pergunta
                            if (mensagem instanceof Question) {
                                System.out.println("Pergunta recebida!");
                                Question q = (Question) mensagem;
                                screenLayout.receberPergunta(q);
                            }
                            // CASO 2: Receber Mensagens de Texto (ex: "FIM" ou Erros)
                            else if (mensagem instanceof String) {
                                String texto = (String) mensagem;

                                if (texto.equals("FIM")) {
                                    System.out.println("O jogo terminou. A receber pontuação...");

                                    // O servidor manda a pontuação (int) logo a seguir ao "FIM"
                                    Object pontuacaoObj = in.readObject();

                                    if (pontuacaoObj instanceof Integer) {
                                        int pontuacaoFinal = (Integer) pontuacaoObj;

                                        // Atualizar GUI para mostrar o Placar Final
                                        screenLayout.terminarJogo(username, pontuacaoFinal);
                                        System.out.println("Pontuação final: " + pontuacaoFinal);
                                    }

                                    // Sair do loop (terminar a thread de escuta)
                                    break;
                                } else {
                                    System.out.println("Mensagem do Servidor: " + texto);
                                }
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Conexão perdida com o servidor.");
                    }
                }).start();

            } else {
                System.out.println("O servidor recusou a entrada: " + resposta);
                socket.close();
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro na conexão: " + e.getMessage());
            e.printStackTrace();
        }
    }
}