import GUI.Frame;
import GUI.ScreenLayout;
import GameState.Question;

import javax.swing.*;
import java.awt.BorderLayout;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        String ip;
        int port;
        String gameCode;
        String teamName;
        String username;

        if (args.length >= 5) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
            gameCode = args[2];
            teamName = args[3];
            username = args[4];
        } else {
            ip = "localhost";
            port = 12345;
            gameCode = JOptionPane.showInputDialog(null, "Código do Jogo:", "Login", JOptionPane.QUESTION_MESSAGE);
            if (gameCode == null || gameCode.trim().isEmpty()) return;
            teamName = JOptionPane.showInputDialog(null, "Equipa:", "Login", JOptionPane.QUESTION_MESSAGE);
            if (teamName == null || teamName.trim().isEmpty()) return;
            username = JOptionPane.showInputDialog(null, "Username:", "Login", JOptionPane.QUESTION_MESSAGE);
            if (username == null || username.trim().isEmpty()) return;
        }

        try {
            System.out.println("A conectar...");
            Socket socket = new Socket(ip, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(gameCode);
            out.writeObject(teamName);
            out.writeObject(username);
            out.flush();

            Object resposta = in.readObject();

            if ("OK".equals(resposta)) {
                Frame frame = new Frame();
                frame.setTitle("IsKahoot - " + username + " (" + teamName + ")");

                ScreenLayout screenLayout = new ScreenLayout(out);
                frame.add(screenLayout, BorderLayout.CENTER);
                screenLayout.mostrarPainel(ScreenLayout.PAINEL_INICIO);
                frame.setVisible(true);

                new Thread(() -> {
                    try {
                        while (true) {
                            Object mensagem = in.readObject();

                            if (mensagem instanceof Question) {
                                screenLayout.receberPergunta((Question) mensagem);
                            }
                            else if (mensagem instanceof String) {
                                String texto = (String) mensagem;

                                if (texto.startsWith("PLACAR:")) {
                                    // Placar Intermédio
                                    screenLayout.mostrarPlacarIntermedio(texto.substring(7));
                                }
                                else if (texto.equals("FIM")) {
                                    // Fim do Jogo
                                    Object placarFinal = in.readObject();
                                    if (placarFinal instanceof String) {
                                        screenLayout.mostrarPlacarFinal((String) placarFinal);
                                    }
                                    socket.close();
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Fim da conexão.");
                        System.exit(0);
                    }
                }).start();

            } else {
                JOptionPane.showMessageDialog(null, "Erro: " + resposta);
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}