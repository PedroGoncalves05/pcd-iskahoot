package GUI;
import javax.swing.*;
import java.awt.*;

// Um painel para mostrar as pontuações
public class ScoreBoard extends JPanel {

    private JTextArea areaPlacar;

    public ScoreBoard() {
        setLayout(new BorderLayout()); // Para o placar preencher o espaço

        JLabel labelTitulo = new JLabel("ScoreBoard", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 32));
        add(labelTitulo, BorderLayout.NORTH);

        areaPlacar = new JTextArea("1º Equipa A - 5000 pts\n2º Equipa B - 3000 pts");
        areaPlacar.setFont(new Font("Monospaced", Font.PLAIN, 24));
        areaPlacar.setEditable(false);

        add(new JScrollPane(areaPlacar), BorderLayout.CENTER);
    }


}