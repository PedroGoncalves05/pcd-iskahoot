package GUI;

import javax.swing.JFrame;
import java.awt.BorderLayout;

public class Frame extends JFrame {
    public Frame (){
        /*Criação de uma nova janel com nome, tamanho, localização no ecrã, de tamanho final
        e a terminar quando se clica no x*/
        setTitle(" IsKahoot " );
        setSize(600,600 );
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //
        setLayout(new BorderLayout());
    }
}
