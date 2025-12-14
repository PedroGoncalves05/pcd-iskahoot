package GUI;
import GameState.Question;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class QuestionPage extends JPanel {
    private final ScreenLayout screenLayout;
    private final JLabel labelTimer;
    private final JTextArea textoPergunta;
    private final JButton botaoA;
    private final JButton botaoB;
    private final JButton botaoC;
    private final JButton botaoD;

    private Timer timerVisual;
    private int segundosRestantes;

    public QuestionPage(ScreenLayout layout){
        this.screenLayout = layout;

        setLayout(new BorderLayout(10, 20));

        labelTimer = new JLabel("Tempo: 30", SwingConstants.CENTER);
        labelTimer.setFont(new Font("Arial", Font.BOLD, 20));
        labelTimer.setForeground(Color.RED);
        add(labelTimer, BorderLayout.NORTH);

        textoPergunta = new JTextArea("A aguardar pergunta...");
        textoPergunta.setEditable(false);
        textoPergunta.setFont(new Font("Arial", Font.PLAIN, 28));
        textoPergunta.setWrapStyleWord(true);
        textoPergunta.setLineWrap(true);
        textoPergunta.setMargin(new Insets(10,10,10,10));

        add(textoPergunta, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new GridLayout(2, 2, 10, 10));

        // Inicialização dos botões
        botaoA = new JButton("A) ");
        botaoB = new JButton("B) ");
        botaoC = new JButton("C) ");
        botaoD = new JButton("D) ");

        Font fontBotoes = new Font("Arial", Font.BOLD, 16);
        botaoA.setFont(fontBotoes);
        botaoB.setFont(fontBotoes);
        botaoC.setFont(fontBotoes);
        botaoD.setFont(fontBotoes);

        painelBotoes.add(botaoA);
        painelBotoes.add(botaoB);
        painelBotoes.add(botaoC);
        painelBotoes.add(botaoD);

        add(painelBotoes, BorderLayout.SOUTH);

        // Uso de Method Reference ou Lambdas para limpar o código
        botaoA.addActionListener(e -> processarResposta(e.getSource()));
        botaoB.addActionListener(e -> processarResposta(e.getSource()));
        botaoC.addActionListener(e -> processarResposta(e.getSource()));
        botaoD.addActionListener(e -> processarResposta(e.getSource()));
    }

    public void setQuestion(Question q) {
        textoPergunta.setText(q.getQuestion());

        List<String> options = q.getOptions();
        if (options.size() >= 4) {
            botaoA.setText("A) " + options.get(0));
            botaoB.setText("B) " + options.get(1));
            botaoC.setText("C) " + options.get(2));
            botaoD.setText("D) " + options.get(3));
        }

        ativarBotoes(true);
        iniciarTimer(30);
    }

    private void iniciarTimer(int segundos) {
        if (timerVisual != null && timerVisual.isRunning()) {
            timerVisual.stop();
        }

        this.segundosRestantes = segundos;
        labelTimer.setText("Tempo: " + segundosRestantes);

        // Lambda aqui em vez de 'new ActionListener()'
        timerVisual = new Timer(1000, e -> {
            segundosRestantes--;

            if (segundosRestantes >= 0) {
                labelTimer.setText("Tempo: " + segundosRestantes);
            } else {
                timerVisual.stop();
                labelTimer.setText("Tempo Esgotado!");
                ativarBotoes(false);
                screenLayout.enviarResposta(-1);
            }
        });

        timerVisual.start();
    }

    private void ativarBotoes(boolean ativo) {
        botaoA.setEnabled(ativo);
        botaoB.setEnabled(ativo);
        botaoC.setEnabled(ativo);
        botaoD.setEnabled(ativo);
    }

    private void processarResposta(Object source) {
        int respostaIndex = -1;

        if (source == botaoA) respostaIndex = 0;
        else if (source == botaoB) respostaIndex = 1;
        else if (source == botaoC) respostaIndex = 2;
        else if (source == botaoD) respostaIndex = 3;

        if (respostaIndex != -1) {
            if (timerVisual != null) timerVisual.stop();
            labelTimer.setText("Resposta Enviada!");
            screenLayout.enviarResposta(respostaIndex);
            ativarBotoes(false);
        }
    }
}