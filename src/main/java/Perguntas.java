import GameState.Question;

import java.util.List;

public class Perguntas {

    // Os nomes TÊM de ser iguais aos do JSON
    private String name;
    private List<GameState.Question> questions;

    // Getters para poderes aceder aos dados depois de lidos
    public String getName() {
        return name;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}