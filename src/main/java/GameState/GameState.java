package GameState;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GameState {

    private final String gameCode;
    private final List<Question> allQuestions;
    private final int maxPlayersPerTeam;
    private final Map<String, Barrier> barreirasDasEquipas;
    private final Map<Integer, ModifiedCountDownLatch> latchesPerguntas;
    private final Map<String, Integer> jogadoresPorEquipa;
    private final ConcurrentHashMap<String, AtomicInteger> pontuacoesEquipa;
    private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, AtomicInteger>> pontosPorRonda;
    private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, AtomicInteger>> acertosPorRonda;
    private final AtomicInteger jogadoresFinalizados;
    private int numJogadoresTotal = 0;

    public GameState(String gameCode, List<Question> questions, int maxPlayersPerTeam) {
        this.gameCode = gameCode;
        this.allQuestions = questions;
        this.maxPlayersPerTeam = maxPlayersPerTeam;

        this.barreirasDasEquipas = new ConcurrentHashMap<>();
        this.jogadoresPorEquipa = new ConcurrentHashMap<>();
        this.latchesPerguntas = new ConcurrentHashMap<>();
        this.pontuacoesEquipa = new ConcurrentHashMap<>();
        this.pontosPorRonda = new ConcurrentHashMap<>();
        this.acertosPorRonda = new ConcurrentHashMap<>();

        this.jogadoresFinalizados = new AtomicInteger(0);
    }

    public String getGameCode() {
        return gameCode;
    }

    public List<Question> getQuestions() {
        return allQuestions;
    }

    public int getNumJogadoresTotal() {
        return numJogadoresTotal;
    }

    public Barrier getBarreira(String teamName) {
        return barreirasDasEquipas.computeIfAbsent(teamName, k -> new Barrier(maxPlayersPerTeam));
    }

    public ModifiedCountDownLatch getLatch(int questionIndex, int bonusCount, int waitTime) {
        return latchesPerguntas.computeIfAbsent(questionIndex, k ->
                new ModifiedCountDownLatch(2, bonusCount, waitTime, numJogadoresTotal)
        );
    }

    public synchronized void registarJogadorNaEquipa(String teamName) {
        jogadoresPorEquipa.merge(teamName, 1, Integer::sum);
        numJogadoresTotal++;

        pontuacoesEquipa.putIfAbsent(teamName, new AtomicInteger(0));
        pontosPorRonda.putIfAbsent(teamName, new ConcurrentHashMap<>());
        acertosPorRonda.putIfAbsent(teamName, new ConcurrentHashMap<>());
    }

    public boolean registarConclusaoJogador() {
        int finalizados = jogadoresFinalizados.incrementAndGet();
        return finalizados >= numJogadoresTotal;
    }

    public void registarAcertoRonda(String teamName, int rondaIndex, boolean acertou) {
        if (acertou) {
            acertosPorRonda.computeIfAbsent(teamName, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(rondaIndex, k -> new AtomicInteger(0))
                    .incrementAndGet();
        }
    }

    public int calcularPontosEquipa(String teamName, int rondaIndex, int pointsBase) {
        int acertos = 0;
        if (acertosPorRonda.containsKey(teamName) && acertosPorRonda.get(teamName).containsKey(rondaIndex)) {
            acertos = acertosPorRonda.get(teamName).get(rondaIndex).get();
        }

        if (acertos == maxPlayersPerTeam) {
            return pointsBase * 2;
        } else if (acertos > 0) {
            return pointsBase;
        } else {
            return 0;
        }
    }

    public void adicionarPontosEquipa(String teamName, int pontos, int rondaIndex) {
        pontuacoesEquipa.computeIfAbsent(teamName, k -> new AtomicInteger(0))
                .addAndGet(pontos);

        pontosPorRonda.computeIfAbsent(teamName, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(rondaIndex, k -> new AtomicInteger(0))
                .addAndGet(pontos);
    }

    public String getPlacarTexto(int rondaIndex) {
        StringBuilder sb = new StringBuilder("=== PLACAR (Ronda " + (rondaIndex + 1) + ") ===\n");

        pontuacoesEquipa.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().get(), e1.getValue().get()))
                .forEach(entry -> {
                    String equipa = entry.getKey();
                    int total = entry.getValue().get();

                    int ganhosRonda = 0;
                    if (pontosPorRonda.containsKey(equipa) && pontosPorRonda.get(equipa).containsKey(rondaIndex)) {
                        ganhosRonda = pontosPorRonda.get(equipa).get(rondaIndex).get();
                    }

                    sb.append(equipa).append(": ").append(total).append(" pts");
                    sb.append(" (+").append(ganhosRonda).append(")\n");
                });

        return sb.toString();
    }

    public String getPlacarFinal() {
        StringBuilder sb = new StringBuilder("=== PLACAR FINAL ===\n");
        pontuacoesEquipa.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().get(), e1.getValue().get()))
                .forEach(entry -> {
                    sb.append(entry.getKey()).append(": ").append(entry.getValue().get()).append(" pts\n");
                });
        return sb.toString();
    }
}