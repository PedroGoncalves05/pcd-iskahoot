package GUI;

import GameState.Question;
import javax.swing.*;
import java.awt.*;
import java.io.ObjectOutputStream;

public class ScreenLayout extends JPanel {
    public static final String PAINEL_INICIO = "INICIO";
    public static final String PAINEL_PERGUNTA = "PERGUNTA";
    public static final String PAINEL_PLACAR = "PLACAR";

    private CardLayout cardLayout;
    private JPanel painelPrincipal;

    private FrontPage frontPage;
    private QuestionPage question;
    private ScoreBoard scoreBoard;

    private ObjectOutputStream out;

    public ScreenLayout(ObjectOutputStream out) {
        this.out = out;

        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);

        frontPage = new FrontPage(this);
        scoreBoard = new ScoreBoard(this);
        question = new QuestionPage(this);

        painelPrincipal.add(frontPage, PAINEL_INICIO);
        painelPrincipal.add(question, PAINEL_PERGUNTA);
        painelPrincipal.add(scoreBoard, PAINEL_PLACAR);

        this.setLayout(new BorderLayout());
        this.add(painelPrincipal, BorderLayout.CENTER);
    }

    public void receberPergunta(Question q) {
        if (q != null) {
            question.setQuestion(q);
            mostrarPainel(PAINEL_PERGUNTA);
        }
    }

    // Placar Intermédio (Título: Classificação)
    public void mostrarPlacarIntermedio(String textoPlacar) {
        scoreBoard.setTitulo("Classificação");
        scoreBoard.setTextoPlacar(textoPlacar);
        mostrarPainel(PAINEL_PLACAR);
    }

    // NOVO: Placar Final (Título: Placar Final)
    public void mostrarPlacarFinal(String textoPlacar) {
        scoreBoard.setTitulo("Placar Final"); // Muda o título da janela
        scoreBoard.setTextoPlacar(textoPlacar);
        mostrarPainel(PAINEL_PLACAR);
    }

    public void enviarResposta(int indexOpcao) {
        try {
            if (out != null) {
                out.writeObject(indexOpcao);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mostrarPainel(String nomeDoPainel) {
        cardLayout.show(painelPrincipal, nomeDoPainel);
    }
}