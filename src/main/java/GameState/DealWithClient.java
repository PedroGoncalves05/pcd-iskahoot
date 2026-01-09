package GameState;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DealWithClient implements Runnable {
    private final Socket socket;
    private final ConcurrentHashMap<String, GameState> jogos;

    public DealWithClient(Socket socket, ConcurrentHashMap<String, GameState> jogos) {
        this.socket = socket;
        this.jogos = jogos;
    }

    @Override
    public void run() {
        String gameCode = null;
        String teamName = null;
        GameState jogo = null;


        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            gameCode = (String) in.readObject();
            teamName = (String) in.readObject();
            String username = (String) in.readObject();

            jogo = jogos.get(gameCode);

            if (jogo == null) {
                out.writeObject("Jogo não encontrado");
            }

            jogo.registarJogadorNaEquipa(teamName);
            System.out.println("Jogador " + username + " ligado à equipa " + teamName);
            out.writeObject("OK");
            out.flush();

            jogo.getStartLatch().countDown();
            jogo.getStartLatch().await();

            GameManager gm = jogo.getGameManager();
            List<Question> perguntas = jogo.getQuestions();

            for (int i = 0; i < perguntas.size(); i++) {
                gm.inicioRonda();
                Question question = perguntas.get(i);
                out.writeObject(question);
                out.flush();

                int respostaId = (int) in.readObject();

                int pontosImediatos = gm.processarResposta(teamName, i, respostaId);
                if (pontosImediatos == -1) {
                    jogo.getBarreira(teamName).await(35, TimeUnit.SECONDS);
                    gm.finalizarPontuacaoEquipa(teamName, i, question.getPoints());
                }

                jogo.getBarreiraInicioRonda().await(35, TimeUnit.SECONDS);

                String placarRonda = jogo.getPlacarTexto(i);
                out.writeObject("PLACAR:" + placarRonda);
                out.flush();
                Thread.sleep(5000);
            }
            out.writeObject("Fim");
            out.writeObject(jogo.getPlacarFinal());
            out.flush();
        } catch (Exception e) {
            System.err.println("Erro na ligação com " + teamName + ": " + e.getMessage());
        } finally {
            fecharConexao(gameCode, jogo);
        }
    }

    private void fecharConexao(String code, GameState jogo) {
        try {
            if (code != null && jogo != null) {
                if (jogo.registarConclusaoJogador()) {
                    jogos.remove(code);
                }
            }
            if (!socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}