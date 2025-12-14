package GUI;

import GameState.Question;
import javax.swing.*;
import java.awt.*;
import java.io.ObjectOutputStream;

public class ScreenLayout extends JPanel {
    public static final String PAINEL_INICIO = "INICIO";
    public static final String PAINEL_PERGUNTA = "PERGUNTA";
    public static final String PAINEL_PLACAR = "PLACAR";

    private final CardLayout cardLayout;
    private final JPanel painelPrincipal;

    // FrontPage e ScoreBoard podem ser final
    private final QuestionPage question;
    private final ScoreBoard scoreBoard;
    // O 'frontPage' é adicionado mas não acedido depois, pode ser variável local no construtor
    // ou mantido como campo se quiseres expandir depois. Vou manter local para limpar warning.

    private final ObjectOutputStream out;

    public ScreenLayout(ObjectOutputStream out) {
        this.out = out;

        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);

        // Variável local pois só usamos para adicionar ao painel
        FrontPage frontPage = new FrontPage(this);

        // Atualizado: ScoreBoard já não precisa de 'this' pois removemos o botão
        scoreBoard = new ScoreBoard();
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

    public void mostrarPlacarIntermedio(String textoPlacar) {
        scoreBoard.setTitulo("Classificação");
        scoreBoard.setTextoPlacar(textoPlacar);
        mostrarPainel(PAINEL_PLACAR);
    }

    public void mostrarPlacarFinal(String textoPlacar) {
        scoreBoard.setTitulo("Placar Final");
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
            System.err.println("Erro ao enviar resposta: " + e.getMessage());
        }
    }

    public void mostrarPainel(String nomeDoPainel) {
        cardLayout.show(painelPrincipal, nomeDoPainel);
    }
}