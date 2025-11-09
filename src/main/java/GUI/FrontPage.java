package GUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrontPage extends JPanel implements ActionListener {

    private ScreenLayout screenLayout;
    private JTextArea textoPergunta;
    private JButton botaoA;

    public FrontPage(ScreenLayout layout) {
        this.screenLayout = layout;

        setLayout(new BorderLayout(10, 20));


        // 3. CENTER: A Pergunta
        textoPergunta = new JTextArea("Clique para começar");
        textoPergunta.setEditable(false);
        textoPergunta.setFont(new Font("Arial", Font.PLAIN, 28));
        textoPergunta.setWrapStyleWord(true);
        textoPergunta.setLineWrap(true);

        add(textoPergunta, BorderLayout.CENTER);

        JPanel painelButtons = new JPanel();

        // Criamos os botões [cite: 1990]
        botaoA = new JButton("Começar");
        // Adicionamos os botões ao painel de botões
        painelButtons.add(botaoA);


        // Adicionamos o painel de botões ao sul do painel principal
        add(painelButtons, BorderLayout.SOUTH);


        botaoA.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == botaoA)
            screenLayout.mostrarPainel(ScreenLayout.PAINEL_PERGUNTA);
    }
}
