import GameState.GameState;
import GameState.Question;
import GameState.Barrier;
import GameState.ModifiedCountDownLatch;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DealWithClient implements Runnable {
    private Socket socket;
    // Referência ao mapa GLOBAL de jogos do servidor
    private ConcurrentHashMap<String, GameState> jogos;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public DealWithClient(Socket socket, ConcurrentHashMap<String, GameState> jogos) {
        this.socket = socket;
        this.jogos = jogos;
    }

    @Override
    public void run() {
        String gameCode = null; // Guardar o código para remover no fim
        GameState jogo = null;  // Guardar referência ao jogo

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            gameCode = (String) in.readObject();
            String teamName = (String) in.readObject();
            String username = (String) in.readObject();

            jogo = jogos.get(gameCode);

            if (jogo == null) {
                out.writeObject("ERRO: Jogo não existe");
                return;
            }

            jogo.registarJogadorNaEquipa(teamName);
            out.writeObject("OK");
            out.flush();
            System.out.println(username + " entrou.");

            List<Question> listaPerguntas = jogo.getQuestions();

            for (int i = 0; i < listaPerguntas.size(); i++) {
                Question q = listaPerguntas.get(i);
                out.writeObject(q);
                out.flush();

                Object respostaRecebida = in.readObject();
                int respostaIndex = -1;
                if (respostaRecebida instanceof Integer) respostaIndex = (Integer) respostaRecebida;

                boolean acertou = (respostaIndex == q.getCorrect());
                int pontosBase = q.getPoints();
                int pontosAAdicionar = 0;

                boolean isPerguntaIndividual = (i % 2 == 0);

                if (isPerguntaIndividual) {
                    ModifiedCountDownLatch latch = jogo.getLatch(i, 2, 30);
                    int bonusFactor = latch.countdown();
                    latch.await();

                    if (acertou) pontosAAdicionar = pontosBase * bonusFactor;
                } else {
                    jogo.registarAcertoRonda(teamName, i, acertou);
                    Barrier barreira = jogo.getBarreira(teamName);
                    barreira.await(30, TimeUnit.SECONDS);

                    int pontosTotaisEquipa = jogo.calcularPontosEquipa(teamName, i, pontosBase);
                    pontosAAdicionar = pontosTotaisEquipa / 2;
                }

                if (pontosAAdicionar > 0) {
                    jogo.adicionarPontosEquipa(teamName, pontosAAdicionar, i);
                }

                Thread.sleep(100);
                out.writeObject("PLACAR:" + jogo.getPlacarTexto(i));
                out.flush();
                Thread.sleep(5000);
            }

            out.writeObject("FIM");
            out.writeObject(jogo.getPlacarFinal());
            out.flush();

        } catch (Exception e) {
            System.out.println("Cliente saiu ou erro: " + e.getMessage());
        } finally {
            // === GESTÃO DE MEMÓRIA (LIMPEZA) ===
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) { /* Ignorar */ }

            if (gameCode != null && jogo != null) {
                // Notifica o jogo que este jogador acabou
                boolean ultimoASair = jogo.registarConclusaoJogador();

                // Se for o último, apaga a luz (remove o jogo da memória)
                if (ultimoASair) {
                    jogos.remove(gameCode);
                    System.out.println(">>> JOGO " + gameCode + " ENCERRADO E REMOVIDO DA MEMÓRIA. <<<");
                }
            }
        }
    }
}