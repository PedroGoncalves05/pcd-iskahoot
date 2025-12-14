import GameState.GameState;
import GameState.Question;
import GameState.Barrier; // Importar a nova classe
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DealWithClient implements Runnable {
    private Socket socket;
    private ConcurrentHashMap<String, GameState> jogos;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public DealWithClient(Socket socket, ConcurrentHashMap<String, GameState> jogos) {
        this.socket = socket;
        this.jogos = jogos;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // 1. Ler Login
            String gameCode = (String) in.readObject();
            String teamName = (String) in.readObject();
            String username = (String) in.readObject();

            GameState jogo = jogos.get(gameCode);

            if (jogo == null) {
                out.writeObject("ERRO: Jogo não existe");
            } else {
                // Registar na equipa e obter a barreira
                jogo.registarJogadorNaEquipa(teamName);
                Barrier barreiraDaEquipa = jogo.getBarreira(teamName);

                out.writeObject("OK");
                out.flush();
                System.out.println(username + " (Equipa " + teamName + ") entrou.");

                // CICLO DE JOGO
                List<Question> listaPerguntas = jogo.getQuestions();
                int pontuacaoLocal = 0;

                for (Question q : listaPerguntas) {
                    // a) Enviar Pergunta
                    out.writeObject(q);
                    out.flush();

                    // b) Receber Resposta
                    Object respostaRecebida = in.readObject();

                    if (respostaRecebida instanceof Integer) {
                        int respostaIndex = (Integer) respostaRecebida;
                        if (respostaIndex == q.getCorrect()) {
                            pontuacaoLocal += q.getPoints();
                            System.out.println(username + " acertou.");
                        }
                    }

                    // c) SINCRONIZAÇÃO: Esperar pelo colega de equipa!
                    System.out.println(username + " à espera do parceiro na barreira...");
                    barreiraDaEquipa.await(30, java.util.concurrent.TimeUnit.SECONDS);

                    System.out.println(username + " passou a barreira! Avançando...");

                    // Nota: Aqui podíamos calcular a pontuação da equipa,
                    // mas por agora avançamos só para a próxima pergunta.
                }

                // Fim do Jogo
                out.writeObject("FIM");
                out.writeObject(pontuacaoLocal);
                out.flush();
            }

        } catch (Exception e) {
            System.out.println("Erro ou Desconexão: " + e.getMessage());
            e.printStackTrace();
        }
    }
}