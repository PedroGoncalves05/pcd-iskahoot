package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScoreBoard extends JPanel implements ActionListener {

    private JTextArea areaPlacar;
    private JLabel labelTitulo; // Agora é um atributo da classe
    private JButton botaoJogarNovamente;
    private ScreenLayout screenLayout;

    public ScoreBoard(ScreenLayout layout) {
        this.screenLayout = layout;
        setLayout(new BorderLayout());

        // Guardamos a referência na variável labelTitulo
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

    // NOVO: Permite alterar o título (ex: para "Placar Final")
    public void setTitulo(String titulo) {
        labelTitulo.setText(titulo);
        // Opcional: Mudar a cor para destacar que acabou
        if (titulo.toLowerCase().contains("final")) {
            labelTitulo.setForeground(new Color(0, 150, 0)); // Verde escuro
        } else {
            labelTitulo.setForeground(Color.BLACK);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Lógica de botões se necessário
    }
}