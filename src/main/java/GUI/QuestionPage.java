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

    // Já não precisamos guardar a 'currentQuestion' para verificar respostas,
    // apenas para mostrar o texto.

    public QuestionPage(ScreenLayout layout){
        this.screenLayout = layout;

        setLayout(new BorderLayout(10, 20));

        labelTimer = new JLabel("Tempo: 30", SwingConstants.CENTER);
        add(labelTimer, BorderLayout.NORTH);

        textoPergunta = new JTextArea("A aguardar pergunta...");
        textoPergunta.setEditable(false);
        textoPergunta.setFont(new Font("Arial", Font.PLAIN, 28));
        textoPergunta.setWrapStyleWord(true);
        textoPergunta.setLineWrap(true);

        add(textoPergunta, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new GridLayout(2, 2, 10, 10));

        botaoA = new JButton("A) ");
        botaoB = new JButton("B) ");
        botaoC = new JButton("C) ");
        botaoD = new JButton("D) ");

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
        // Reset aos textos dos botões
        if (options.size() >= 4) {
            botaoA.setText("A) " + options.get(0));
            botaoB.setText("B) " + options.get(1));
            botaoC.setText("C) " + options.get(2));
            botaoD.setText("D) " + options.get(3));
        }
        // Reset ao timer visual (funcionalidade futura)
        labelTimer.setText("Tempo: 30");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        int respostaIndex = -1;

        if (source == botaoA) {
            respostaIndex = 0;
        } else if (source == botaoB) {
            respostaIndex = 1;
        } else if (source == botaoC) {
            respostaIndex = 2;
        } else if (source == botaoD) {
            respostaIndex = 3;
        }

        if (respostaIndex != -1) {
            // Envia a resposta para o servidor
            screenLayout.enviarResposta(respostaIndex);

            // Feedback visual simples
            textoPergunta.setText("Resposta enviada! À espera da próxima...");
        }
    }
}