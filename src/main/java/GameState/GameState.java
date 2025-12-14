package GameState;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameState {
    private String gameCode;
    private List<Question> allQuestions;
    private int maxPlayersPerTeam = 2; // Definido no enunciado como equipas de 2

    // Mapa: NomeDaEquipa -> BarreiraDessaEquipa
    private Map<String, Barrier> barreirasDasEquipas;

    // Mapa para controlar quantos jogadores já entraram em cada equipa (opcional mas útil)
    private Map<String, Integer> jogadoresPorEquipa;

    public GameState(String gameCode, List<Question> questions, int maxPlayersPerTeam) {
        this.gameCode = gameCode;
        this.allQuestions = questions;
        this.maxPlayersPerTeam = maxPlayersPerTeam;

        this.barreirasDasEquipas = new ConcurrentHashMap<>();
        this.jogadoresPorEquipa = new ConcurrentHashMap<>();
    }

    // Método para obter (ou criar) a barreira de uma equipa
    public Barrier getBarreira(String teamName) {
        // computeIfAbsent: Se a equipa não existe, cria uma nova Barreira para ela
        return barreirasDasEquipas.computeIfAbsent(teamName, k -> new Barrier(maxPlayersPerTeam));
    }

    public void registarJogadorNaEquipa(String teamName) {
        jogadoresPorEquipa.merge(teamName, 1, Integer::sum);
    }

    public List<Question> getQuestions() {
        return allQuestions;
    }
}