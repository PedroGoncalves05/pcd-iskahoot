package GUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScoreBoard extends JPanel implements ActionListener {

    private JTextArea areaPlacar;
    private JButton botaoJogarNovamente;
    private ScreenLayout screenLayout;

    public ScoreBoard(ScreenLayout layout) {
        this.screenLayout = layout;
        setLayout(new BorderLayout());

        JLabel labelTitulo = new JLabel("Placar Final", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 32));
        add(labelTitulo, BorderLayout.NORTH);

        areaPlacar = new JTextArea("A calcular pontuação...");
        areaPlacar.setFont(new Font("Monospaced", Font.PLAIN, 24));
        areaPlacar.setEditable(false);
        add(new JScrollPane(areaPlacar), BorderLayout.CENTER);


    }


    public void updateScore(String username, int score) {
        areaPlacar.setText(username + " - Pontuação Final: " + score + " pts");
    }



    // 5. Adicionar o método de ação
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botaoJogarNovamente) {
            screenLayout.mostrarPainel(ScreenLayout.PAINEL_INICIO);
        }
    }
}