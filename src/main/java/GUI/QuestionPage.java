package GUI;
import GameState.Question;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class QuestionPage extends JPanel implements ActionListener {
    private ScreenLayout screenLayout;
    private JLabel labelTimer;
    private JTextArea textoPergunta;
    private JButton botaoA, botaoB, botaoC, botaoD;

    // Variáveis do Temporizador
    private Timer timerVisual;
    private int segundosRestantes;

    public QuestionPage(ScreenLayout layout){
        this.screenLayout = layout;

        setLayout(new BorderLayout(10, 20));

        // Label do Tempo (vermelho para destaque)
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

        botaoA.addActionListener(this);
        botaoB.addActionListener(this);
        botaoC.addActionListener(this);
        botaoD.addActionListener(this);
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

        // Reativar botões para a nova ronda e iniciar timer
        ativarBotoes(true);
        iniciarTimer(30);
    }

    private void iniciarTimer(int segundos) {
        // Parar timer anterior se existir
        if (timerVisual != null && timerVisual.isRunning()) {
            timerVisual.stop();
        }

        this.segundosRestantes = segundos;
        labelTimer.setText("Tempo: " + segundosRestantes);

        timerVisual = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                segundosRestantes--;

                if (segundosRestantes >= 0) {
                    labelTimer.setText("Tempo: " + segundosRestantes);
                } else {
                    // === AQUI ESTÁ A CORREÇÃO ===
                    // O tempo acabou!
                    timerVisual.stop();
                    labelTimer.setText("Tempo Esgotado!");
                    ativarBotoes(false);

                    // Envia -1 ao servidor para desbloquear o jogo
                    screenLayout.enviarResposta(-1);
                }
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

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        int respostaIndex = -1;

        if (source == botaoA) respostaIndex = 0;
        else if (source == botaoB) respostaIndex = 1;
        else if (source == botaoC) respostaIndex = 2;
        else if (source == botaoD) respostaIndex = 3;

        if (respostaIndex != -1) {
            // Parar o timer visual porque o jogador respondeu a tempo
            if (timerVisual != null) timerVisual.stop();
            labelTimer.setText("Resposta Enviada!");

            screenLayout.enviarResposta(respostaIndex);
            ativarBotoes(false);
        }
    }
}