import GUI.Frame;
import GUI.QuestionPage;
import GUI.ScreenLayout;

import java.awt.*;

public class main {
    public static void main ( String [] args ) {
        Frame frame = new Frame();
        ScreenLayout screenLayout = new ScreenLayout();
        frame.add(screenLayout, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
