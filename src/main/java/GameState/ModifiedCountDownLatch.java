package GameState;

public class ModifiedCountDownLatch {
    private int count;
    private int bonusCount;
    private final int bonusFactor;
    private final long deadline;
    private boolean isOpen = false;

    public ModifiedCountDownLatch(int bonusFactor, int bonusCount, int waitPeriod, int count) {
        this.bonusFactor = bonusFactor;
        this.bonusCount = bonusCount;
        this.count = count;
        this.deadline = System.currentTimeMillis() + (waitPeriod * 1000L);
    }

    public synchronized int countdown() {
        if (isOpen) return 1;

        count--;

        int currentMultiplier = 1;
        if (bonusCount > 0) {
            currentMultiplier = bonusFactor;
            bonusCount--;
        }

        if (count <= 0) {
            isOpen = true;
            notifyAll();
        }

        return currentMultiplier;
    }

    public synchronized void await() throws InterruptedException {
        while (!isOpen) {
            long now = System.currentTimeMillis();
            long timeLeft = deadline - now;

            if (timeLeft <= 0) {
                isOpen = true;
                notifyAll();
                break;
            }
            wait(timeLeft);
        }
    }
}