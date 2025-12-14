package GameState;

public class ModifiedCountDownLatch {
    private int count;             // Quantos jogadores faltam responder
    private int bonusCount;        // Quantos bónus ainda restam (ex: 2)
    private int bonusFactor;       // O valor do bónus (ex: 2x)
    private long deadline;         // O momento exato (timestamp) em que o tempo acaba
    private boolean isOpen = false;// Flag para indicar se a porta abriu (fim da ronda)

    /**
     * @param bonusFactor Fator de multiplicação (ex: 2 para duplicar pontos).
     * @param bonusCount  Quantos jogadores recebem o bónus (ex: os primeiros 2).
     * @param waitPeriod  Tempo máximo de espera em segundos.
     * @param count       Número total de jogadores que esperamos.
     */
    public ModifiedCountDownLatch(int bonusFactor, int bonusCount, int waitPeriod, int count) {
        this.bonusFactor = bonusFactor;
        this.bonusCount = bonusCount;
        this.count = count;
        // Define o tempo limite absoluto (Agora + Segundos * 1000)
        this.deadline = System.currentTimeMillis() + (waitPeriod * 1000L);
    }

    /**
     * Decrementa o contador de respostas.
     * @return O fator de bónus a aplicar (bonusFactor ou 1).
     */
    public synchronized int countdown() {
        // Se a ronda já fechou (timeout), retorna 1 para não dar erros,
        // mas idealmente o servidor já não aceitaria respostas.
        if (isOpen) return 1;

        count--;

        // Determinar o bónus para este jogador
        int currentMultiplier = 1;
        if (bonusCount > 0) {
            currentMultiplier = bonusFactor;
            bonusCount--;
        }

        // Se todos já responderam, abrimos a porta imediatamente
        if (count <= 0) {
            isOpen = true;
            notifyAll(); // Acorda todos os que estão no await()
        }

        return currentMultiplier;
    }

    /**
     * Bloqueia a thread até que todos respondam ou o tempo expire.
     * Implementa a lógica de Timer sugerida.
     */
    public synchronized void await() throws InterruptedException {
        while (!isOpen) {
            long now = System.currentTimeMillis();
            long timeLeft = deadline - now;

            if (timeLeft <= 0) {
                // O tempo acabou! Forçamos a abertura da barreira/latch.
                isOpen = true;
                notifyAll(); // Acorda todos
                break;
            }

            // Espera apenas pelo tempo que resta
            wait(timeLeft);
        }
    }
}