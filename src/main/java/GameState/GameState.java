package GameState;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GameState {

    // === ATRIBUTOS IMUTÁVEIS (FINAL) ===
    private final String gameCode;
    private final List<Question> allQuestions;
    private final int maxPlayersPerTeam;

    // === ESTRUTURAS DE DADOS CONCORRENTES (FINAL) ===
    // Mapa: NomeDaEquipa -> Barreira (para perguntas de equipa)
    private final Map<String, Barrier> barreirasDasEquipas;

    // Mapa: ÍndicePergunta -> Latch (para perguntas individuais)
    private final Map<Integer, ModifiedCountDownLatch> latchesPerguntas;

    // Mapa: NomeDaEquipa -> Quantidade de Jogadores (para controlo)
    private final Map<String, Integer> jogadoresPorEquipa;

    // Pontuação Total Acumulada: Equipa -> Total
    private final ConcurrentHashMap<String, AtomicInteger> pontuacoesEquipa;

    // Histórico de Pontos: Equipa -> (Ronda -> PontosGanhos)
    private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, AtomicInteger>> pontosPorRonda;

    // Registo de Acertos para Lógica de Equipa: Equipa -> (Ronda -> NumAcertos)
    private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, AtomicInteger>> acertosPorRonda;

    // Contador atómico para saber quando o jogo acabou (Memory Management)
    private final AtomicInteger jogadoresFinalizados;

    // === ATRIBUTOS MUTÁVEIS ===
    private int numJogadoresTotal = 0;

    // === CONSTRUTOR ===
    public GameState(String gameCode, List<Question> questions, int maxPlayersPerTeam) {
        this.gameCode = gameCode;
        this.allQuestions = questions;
        this.maxPlayersPerTeam = maxPlayersPerTeam;

        // Inicialização de todas as estruturas
        this.barreirasDasEquipas = new ConcurrentHashMap<>();
        this.jogadoresPorEquipa = new ConcurrentHashMap<>();
        this.latchesPerguntas = new ConcurrentHashMap<>();
        this.pontuacoesEquipa = new ConcurrentHashMap<>();
        this.pontosPorRonda = new ConcurrentHashMap<>();
        this.acertosPorRonda = new ConcurrentHashMap<>();

        this.jogadoresFinalizados = new AtomicInteger(0);
    }

    // === MÉTODOS DE GET E SETUP ===

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
        // Garante que todos na mesma pergunta usam o mesmo Latch
        return latchesPerguntas.computeIfAbsent(questionIndex, k ->
                new ModifiedCountDownLatch(2, bonusCount, waitTime, numJogadoresTotal)
        );
    }

    public synchronized void registarJogadorNaEquipa(String teamName) {
        jogadoresPorEquipa.merge(teamName, 1, Integer::sum);
        numJogadoresTotal++;

        // Inicializa as entradas nos mapas para evitar NullPointerExceptions depois
        pontuacoesEquipa.putIfAbsent(teamName, new AtomicInteger(0));
        pontosPorRonda.putIfAbsent(teamName, new ConcurrentHashMap<>());
        acertosPorRonda.putIfAbsent(teamName, new ConcurrentHashMap<>());
    }

    // === MÉTODOS DE LÓGICA DE JOGO ===

    /**
     * Chamado quando um jogador termina o jogo (ou sai).
     * @return true se foi o ÚLTIMO jogador a sair (sinal para limpar memória).
     */
    public boolean registarConclusaoJogador() {
        int finalizados = jogadoresFinalizados.incrementAndGet();
        return finalizados >= numJogadoresTotal;
    }

    /**
     * Regista se um jogador acertou numa pergunta de equipa.
     * Deve ser chamado ANTES da barreira.
     */
    public void registarAcertoRonda(String teamName, int rondaIndex, boolean acertou) {
        if (acertou) {
            acertosPorRonda.computeIfAbsent(teamName, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(rondaIndex, k -> new AtomicInteger(0))
                    .incrementAndGet();
        }
    }

    /**
     * Calcula os pontos totais da equipa baseado no número de acertos.
     * Chamado DEPOIS da barreira.
     */
    public int calcularPontosEquipa(String teamName, int rondaIndex, int pointsBase) {
        int acertos = 0;
        if (acertosPorRonda.containsKey(teamName) && acertosPorRonda.get(teamName).containsKey(rondaIndex)) {
            acertos = acertosPorRonda.get(teamName).get(rondaIndex).get();
        }

        // Regra: Todos acertam = Dobro dos pontos.
        if (acertos == maxPlayersPerTeam) {
            return pointsBase * 2;
        }
        // Regra: Pelo menos um acerta = Pontos normais.
        else if (acertos > 0) {
            return pointsBase;
        }
        // Regra: Ninguém acerta = 0.
        else {
            return 0;
        }
    }

    public void adicionarPontosEquipa(String teamName, int pontos, int rondaIndex) {
        // Atualiza o total global
        pontuacoesEquipa.computeIfAbsent(teamName, k -> new AtomicInteger(0))
                .addAndGet(pontos);

        // Regista no histórico da ronda (para o placar intermédio)
        pontosPorRonda.computeIfAbsent(teamName, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(rondaIndex, k -> new AtomicInteger(0))
                .addAndGet(pontos);
    }

    // === MÉTODOS DE PLACAR ===

    public String getPlacarTexto(int rondaIndex) {
        StringBuilder sb = new StringBuilder("=== PLACAR (Ronda " + (rondaIndex + 1) + ") ===\n");

        pontuacoesEquipa.entrySet().stream()
                // Ordena por pontuação decrescente
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