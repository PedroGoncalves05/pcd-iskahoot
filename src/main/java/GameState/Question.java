package GameState;

import java.util.List;

public class Question {
    private String question;
    private int correct;
    private List<String> options;
    private int points;


    public String getQuestion() {
        return question;
    }

    public int getCorrect() {
        return correct;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getPoints() {
        return points;
    }


}