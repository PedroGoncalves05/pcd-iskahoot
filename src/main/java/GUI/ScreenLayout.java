package GUI;

import GameState.GameState;
import GameState.Question;
import GameState.Player;
import java.util.List;
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
    private GameState game;

    public ScreenLayout(GameState game) {
        this.game = game;

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

    public void showNextQuestion() {
       Question q = game.getNextQuestion();
        if (q != null) {
            question.setQuestion(q);
            mostrarPainel(PAINEL_PERGUNTA);
        } else {
            int finalScore = game.getScore();
            Player player = game.getPlayer();
            String username = player.getUsername();
            scoreBoard.updateScore(username, finalScore);
            mostrarPainel(PAINEL_PLACAR);
        }




    }

    public void registerAndStart(String username) {
        // 1. Criar o objeto Player
        Player newPlayer = new Player(username);

        // 2. Adicionar o jogador ao "cérebro"
        game.addPlayer(newPlayer);

        // 3. Começar o jogo
        showNextQuestion();
    }

    public void addPointsToScore(int points) {
        game.addScore(points); // Diz ao "cérebro" para guardar os pontos
    }

    public void mostrarPainel(String nomeDoPainel) {
        cardLayout.show(painelPrincipal, nomeDoPainel);
    }
}
