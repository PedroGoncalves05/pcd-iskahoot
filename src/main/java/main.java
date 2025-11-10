import GUI.Frame;
import GUI.ScreenLayout;

import GameState.Question;
import GameState.GameState;
import java.awt.*;
import java.util.List;



public class main {

    public static void main ( String [] args ) {

        List<Question> questions = JSonReader.getQuestions("perguntas.json");

        if (questions == null || questions.isEmpty()) {
            System.err.println("Não foi possível carregar perguntas. A aplicação vai fechar.");
            return;
        }

        GameState jogo = new GameState("JOGO123", questions, 10);

        Frame frame = new Frame();
        ScreenLayout screenLayout = new ScreenLayout(jogo);
        frame.add(screenLayout, BorderLayout.CENTER);
        screenLayout.mostrarPainel(ScreenLayout.PAINEL_INICIO);
        frame.setVisible(true);
    }
}
