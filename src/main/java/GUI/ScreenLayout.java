package GUI;

import javax.swing.*;
import java.awt.*;

public class ScreenLayout extends JPanel {
    public static final String PAINEL_INICIO = "INICIO";
    public static final String PAINEL_PERGUNTA = "PERGUNTA";
    public static final String PAINEL_PLACAR = "PLACAR";

    private CardLayout cardLayout;
    private JPanel painelPrincipal;
    private QuestionPage question;
    private FrontPage frontPage;
    private ScoreBoard scoreBoard;

    public ScreenLayout(){
        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);

        frontPage = new FrontPage(this);
        scoreBoard = new ScoreBoard();

        question = new QuestionPage(this);

        painelPrincipal.add(frontPage, PAINEL_INICIO);
        painelPrincipal.add(question, PAINEL_PERGUNTA);
        painelPrincipal.add(scoreBoard, PAINEL_PLACAR);

        this.add(painelPrincipal);
    }

    public void mostrarPainel(String nomeDoPainel) {
        cardLayout.show(painelPrincipal, nomeDoPainel);
    }
}
