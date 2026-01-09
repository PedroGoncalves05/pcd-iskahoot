package GameState;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private final GameState state;
    private final ConcurrentHashMap<String, Set<Integer>> rondasEquipaFinalizadas = new ConcurrentHashMap<>();


        public GameManager(GameState state) {
            this.state = state;
        }

        public void inicioRonda() throws InterruptedException {
            state.getBarreiraInicioRonda().await(30, java.util.concurrent.TimeUnit.SECONDS);
        }


        public int processarResposta(String teamName, int indexPergunta, int respostaIndex) {
            Question q = state.getQuestions().get(indexPergunta);
            boolean acertou = (respostaIndex == q.getCorrect());
            int pontosBase = q.getPoints();

            if (indexPergunta % 2 == 0) {
                ModifiedCountDownLatch latch = state.getLatch(indexPergunta, 2, 30);
                int bonus = latch.countdown();
                int totalPontos = acertou ? (pontosBase * bonus) : 0;
                state.adicionarPontosEquipa(teamName, totalPontos, indexPergunta);

                return totalPontos;
            } else {
                state.registarAcertoRonda(teamName, indexPergunta, acertou);
                return -1;
            }
        }

        public int finalizarPontuacaoEquipa(String teamName, int indexPergunta, int pontosBase) {
            Set<Integer> finalizadas = rondasEquipaFinalizadas.computeIfAbsent(teamName, k -> ConcurrentHashMap.newKeySet());
            if (finalizadas.add(indexPergunta)) {
                int pontos = state.calcularPontosEquipa(teamName, indexPergunta, pontosBase);
                if (pontos > 0) {
                    state.adicionarPontosEquipa(teamName, pontos, indexPergunta);
                }
                return pontos;
            }
            return 0;
        }
}

