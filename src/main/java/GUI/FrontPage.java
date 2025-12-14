package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrontPage extends JPanel implements ActionListener {

    private ScreenLayout screenLayout;
    private JTextField fieldUsername;
    private JTextField fieldGameCode;
    private JButton botaoA;

    public FrontPage(ScreenLayout layout) {
        this.screenLayout = layout;

        setLayout(new BorderLayout(10, 20));

        JPanel painelLogin = new JPanel();
        painelLogin.setLayout(new GridLayout(2, 2, 10, 10));

        JLabel labelUsername = new JLabel("Username:");
        fieldUsername = new JTextField();
        // Opcional: Desativar edição se os dados vierem dos argumentos
        // fieldUsername.setEditable(false);

        JLabel labelGameCode = new JLabel("Game Code:");
        fieldGameCode = new JTextField();
        // fieldGameCode.setEditable(false);

        painelLogin.add(labelUsername);
        painelLogin.add(fieldUsername);
        painelLogin.add(labelGameCode);
        painelLogin.add(fieldGameCode);
        add(painelLogin, BorderLayout.CENTER);


        JPanel painelButtons = new JPanel();
        botaoA = new JButton("Começar");
        painelButtons.add(botaoA);
        add(painelButtons, BorderLayout.SOUTH);
        botaoA.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == botaoA) {
            // CORREÇÃO: Removemos a chamada ao screenLayout.registerAndStart
            // Agora apenas damos feedback visual de que estamos à espera
            System.out.println("Botão clicado. A aguardar início do jogo...");
            botaoA.setEnabled(false); // Desativa o botão para não clicar várias vezes
            botaoA.setText("A aguardar...");
        }
    }
}