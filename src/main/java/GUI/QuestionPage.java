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
    private Question currentQuestion;

    public QuestionPage(ScreenLayout layout){

        this.screenLayout = layout;

        setLayout(new BorderLayout(10, 20));

        // 2. NORTH: O Temporizador
        labelTimer = new JLabel("Tempo: 30", SwingConstants.CENTER);
        add(labelTimer, BorderLayout.NORTH);

        // 3. CENTER: A Pergunta
        textoPergunta = new JTextArea("A Carregar");
        textoPergunta.setEditable(false);
        textoPergunta.setFont(new Font("Arial", Font.PLAIN, 28));
        textoPergunta.setWrapStyleWord(true);
        textoPergunta.setLineWrap(true);

        add(textoPergunta, BorderLayout.CENTER);

        // 4. SOUTH: Os Botões de Resposta
        // Criamos um painel *novo* só para os botões
        JPanel painelBotoes = new JPanel();
        // E esse painel usará GridLayout [cite: 2109]
        painelBotoes.setLayout(new GridLayout(2, 2, 10, 10)); // 2 linhas, 2 colunas

        // Criamos os botões [cite: 1990]
        botaoA = new JButton("A) ");
        botaoB = new JButton("B) ");
        botaoC = new JButton("C) ");
        botaoD = new JButton("D) ");

        // Adicionamos os botões ao painel de botões
        painelBotoes.add(botaoA);
        painelBotoes.add(botaoB);
        painelBotoes.add(botaoC);
        painelBotoes.add(botaoD);

        // Adicionamos o painel de botões ao sul do painel principal
        add(painelBotoes, BorderLayout.SOUTH);


        botaoA.addActionListener(this);
        botaoB.addActionListener(this);
        botaoC.addActionListener(this);
        botaoD.addActionListener(this);
    }



    public void setQuestion(Question q) {
        this.currentQuestion = q;
        textoPergunta.setText(q.getQuestion());

        List<String> options = q.getOptions();

        // Atualiza o texto dos botões
        if (options.size() >= 4) { // Proteção para o caso de a pergunta ter < 4 opções
            botaoA.setText("A) " + options.get(0));
            botaoB.setText("B) " + options.get(1));
            botaoC.setText("C) " + options.get(2));
            botaoD.setText("D) " + options.get(3));
        }

        // TODO: Reiniciar o temporizador
        labelTimer.setText("Tempo: 30");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        int respostaIndex = -1;

        if (source == botaoA) {
            System.out.println("Clicou na Opção A!");
            respostaIndex = 0;
        } else if (source == botaoB) {
            System.out.println("Clicou na Opção B!");
            respostaIndex = 1;
        } else if (source == botaoC) {
            System.out.println("Clicou na Opção C!");
            respostaIndex = 2;
        } else if (source == botaoD) {
            System.out.println("Clicou na Opção D!");
            respostaIndex = 3;
        }
        if (respostaIndex != -1) {
            if (respostaIndex == currentQuestion.getCorrect()){
                System.out.println("Resposta certa");
                screenLayout.addPointsToScore(currentQuestion.getPoints());
            } else{
                System.out.println("Resposta errada");
            }
        }


        screenLayout.showNextQuestion();
    }
}
