package GameState;

public class GameManager {
    private final GameState state;
    private int rondaAtual = 0;


        public GameManager(GameState state) {
            this.state = state;
        }

        public void iniciarRonda(int index) throws InterruptedException {
            state.getBarreiraInicioRonda().await(30, java.util.concurrent.TimeUnit.SECONDS);
            this.rondaAtual = index;
        }


        public int processarResposta(String teamName, int indexPergunta, int respostaIndex) {
            Question q = state.getQuestions().get(indexPergunta);
            boolean acertou = (respostaIndex == q.getCorrect());
            int pontosBase = q.getPoints();
            boolean isIndividual = (indexPergunta % 2 == 0);

            if (isIndividual) {
                ModifiedCountDownLatch latch = state.getLatch(indexPergunta, 2, 30);
                int bonus = latch.countdown();
                return acertou ? pontosBase * bonus : 0;
            } else {
                state.registarAcertoRonda(teamName, indexPergunta, acertou);
                return -1;
            }
        }

        public int finalizarPontuaçãoEquipa(String teamName, int indexPergunta, int pontosBase) {
            int pontos = state.calcularPontosEquipa(teamName, indexPergunta, pontosBase);
            return pontos;
        }
}

