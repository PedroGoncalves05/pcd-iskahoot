package GUI;

import javax.swing.*;
import java.awt.*;

public class ScoreBoard extends JPanel {

    private final JTextArea areaPlacar;
    private final JLabel labelTitulo;
    // O 'botaoJogarNovamente' foi removido porque não era usado
    // O 'screenLayout' também não estava a ser usado aqui, removi a dependência local.

    public ScoreBoard() {
        setLayout(new BorderLayout());

        labelTitulo = new JLabel("Classificação", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 32));
        add(labelTitulo, BorderLayout.NORTH);

        areaPlacar = new JTextArea("A aguardar resultados...");
        areaPlacar.setFont(new Font("Monospaced", Font.BOLD, 20));
        areaPlacar.setEditable(false);
        areaPlacar.setMargin(new Insets(20, 20, 20, 20));

        add(new JScrollPane(areaPlacar), BorderLayout.CENTER);
    }

    public void setTextoPlacar(String texto) {
        areaPlacar.setText(texto);
    }

    public void setTitulo(String titulo) {
        labelTitulo.setText(titulo);
        if (titulo.toLowerCase().contains("final")) {
            labelTitulo.setForeground(new Color(0, 150, 0));
        } else {
            labelTitulo.setForeground(Color.BLACK);
        }
    }
}