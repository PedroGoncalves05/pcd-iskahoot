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
    private QuestionPage question;
    private FrontPage frontPage;
    private ScoreBoard scoreBoard;

    // Stream para enviar dados ao servidor
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

    // --- MÉTODOS CHAMADOS PELO CLIENTE (REDE) ---

    // 1. Receber uma nova pergunta e mostrá-la
    public void receberPergunta(Question q) {
        if (q != null) {
            question.setQuestion(q);
            mostrarPainel(PAINEL_PERGUNTA);
        }
    }

    // 2. Terminar o jogo e mostrar o placar (O MÉTODO QUE FALTAVA)
    public void terminarJogo(String username, int score) {
        scoreBoard.updateScore(username, score);
        mostrarPainel(PAINEL_PLACAR);
    }

    // --- MÉTODOS CHAMADOS PELA GUI ---

    // Enviar a resposta escolhida para o servidor
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

    // Métodos antigos (podem ficar vazios pois a lógica agora é do servidor)
    public void registerAndStart(String username) {}
    public void addPointsToScore(int points) {}
    public void showNextQuestion() {}
}