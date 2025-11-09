package GUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QuestionPage extends JPanel implements ActionListener {
    private JLabel labelTimer;
    private JTextArea textoPergunta;
    private JButton botaoA, botaoB, botaoC, botaoD;

    public QuestionPage(){
        String pergunta = "A resposta é A? (É a A)";
        String respostaA = "Sou eu sou eu";
        String respostaB = "Não sou eu idiota";
        String respostaC = "Não sou eu idiota";
        String respostaD = "Beto vai ta maze po crl";

        setLayout(new BorderLayout(10, 20));

        // 2. NORTH: O Temporizador
        labelTimer = new JLabel("Tempo: 30", SwingConstants.CENTER);
        add(labelTimer, BorderLayout.NORTH);

        // 3. CENTER: A Pergunta
        textoPergunta = new JTextArea(pergunta);
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
        botaoA = new JButton("A) " + respostaA);
        botaoB = new JButton("B) " + respostaB);
        botaoC = new JButton("C) " + respostaC);
        botaoD = new JButton("D) " + respostaD);

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

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == botaoA) {
            System.out.println("Clicou na Opção A!");
        } else if (source == botaoB) {
            System.out.println("Clicou na Opção B!");
        } else if (source == botaoC) {
            System.out.println("Clicou na Opção C!");
        } else if (source == botaoD) {
            System.out.println("Clicou na Opção D!");
        }
    }
}
