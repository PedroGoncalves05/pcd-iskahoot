package GameState;

import java.util.List;
import java.util.ArrayList;


public class GameState {
    //variavéis necessárias para o jogo,
    private String gameCode;
    private List<Question> allQuestions;
    private int currentQuestionIndex;
    private List<Player> players;
    private int maxPlayers;
    private int totalScore;

    public GameState(String gameCode, List<Question> questions, int maxPlayers) {
        this.gameCode = gameCode;
        this.allQuestions = questions;
        this.maxPlayers = maxPlayers;
        this.currentQuestionIndex = -1;
        this.players = new ArrayList<>();
        this.totalScore = 0;
    }


    public boolean addPlayer(Player player) {
        if (players.size() < maxPlayers) {
            players.add(player);
            return true;
        }
        return false; // Jogo está cheio
    }


    public Question getNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < allQuestions.size())
            return allQuestions.get(currentQuestionIndex);
        return null;
    }

    public Player getPlayer() {
        if (players.isEmpty()) {
            return null;
        }
        return players.get(0);
    }
    public void addScore(int score) {
        totalScore += score;
    }
    public int getScore() {
        return totalScore;
    }
}
