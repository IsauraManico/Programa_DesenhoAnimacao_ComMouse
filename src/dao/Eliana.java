package dao;

//package Projecto;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Eliana implements ActionListener {
 
  public static void main(String[] args) {
   
   // JLayeredPane = Swing container that provides a 
   //    third dimension for positioning components
   //    ex. depth, Z-index     
   JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 70);
   JPanel painel= new JPanel();
   painel.setOpaque(true);
   painel.setBackground(Color.white);
   painel.setBounds(300,50,800,500);
   
   
   
   slider.setMinorTickSpacing(5);
slider.setMajorTickSpacing(20);
slider.setPaintTicks(true);
slider.setPaintLabels(true);
slider.setBounds(40, 50, 290, 600);
   
   /*JLabel label2= new JLabel();
   label2.setOpaque(true);
   label2.setBackground(Color.GREEN);
   label2.setBounds(100,100,200,200);
   
   JLabel label3= new JLabel();
   label3.setOpaque(true);
   label3.setBackground(Color.BLUE);
   label3.setBounds(150,150,200,200);*/
   
   JLayeredPane layeredPane = new JLayeredPane();
   layeredPane.setBounds(0,0,1000,1000);
   
   //layeredPane.add(label1, JLayeredPane.DEFAULT_LAYER);
   layeredPane.add(painel, Integer.valueOf(0));
    layeredPane.add(slider, Integer.valueOf(1));
   
   //layeredPane.add(label2, Integer.valueOf(2));
   //layeredPane.add(label3, Integer.valueOf(1));
    
      JFrame frame = new JFrame("Para minha amiga Eliana.....JLayeredPane");
      frame.add(layeredPane);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(new Dimension(500, 500));
      //frame.setLayout(null);
      frame.setVisible(true);
  }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
