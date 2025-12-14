package GameState;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Barrier {
    private final int participantesNecessarios;
    private int contador = 0;
    private int generation = 0; // Para distinguir rondas diferentes
    private final Lock lock = new ReentrantLock();
    private final Condition condicao = lock.newCondition();

    public Barrier(int participantesNecessarios) {
        this.participantesNecessarios = participantesNecessarios;
    }

    // Agora o await recebe o tempo limite
    public void await(long tempo, TimeUnit unidade) throws InterruptedException {
        lock.lock();
        try {
            int minhaGeracao = generation; // Memoriza em que ronda estamos
            contador++;

            if (contador < participantesNecessarios) {
                // Se ainda não chegaram todos, espera até ao tempo limite
                boolean tempoSobrou = condicao.await(tempo, unidade);

                // Se acordou e a geração ainda é a mesma, significa que NINGUÉM sinalizou
                // e o tempo acabou (ou foi um spurious wakeup, mas assumimos timeout).
                // O enunciado diz: se expirar, todos devem ser desbloqueados.
                if (!tempoSobrou && generation == minhaGeracao) {
                    // Timeout ocorreu! Forçar avanço.
                    quebrarBarreira();
                }
            } else {
                // Fui o último a chegar!
                proximaGeracao();
            }
        } finally {
            lock.unlock();
        }
    }

    private void quebrarBarreira() {
        // Reiniciar contadores e acordar todos
        contador = 0;
        generation++; // Avança a geração para invalidar esperas antigas
        condicao.signalAll();
    }

    private void proximaGeracao() {
        contador = 0;
        generation++;
        condicao.signalAll();
    }
}