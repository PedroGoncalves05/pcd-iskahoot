import GUI.Frame;
import GUI.QuestionPage;

import java.awt.*;

public class main {
    public static void main ( String [] args ) {
        Frame frame = new Frame();
        QuestionPage questionPage = new QuestionPage();
        frame.add(questionPage, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
