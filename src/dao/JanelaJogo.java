
package dao;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

/**
 *
 * @author isaura
 */
public class JanelaJogo extends JFrame
{
   
    
    JButton btn;
    
    public JanelaJogo()
    {
         
        this.setTitle("Meu Programa Desenho");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 700);
        this.setLocationRelativeTo(this);
        
        
        //this.setLayout(null);
        
        
        this.btn = new JButton(new ImageIcon(getClass().getResource("../icon/rect.png")));
        btn.setBounds(30, 30, 30, 30);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(true);

        this.add(pane());
        
    }
    
    
    
    
    public JLayeredPane pane()
    {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0,0,900,1000);
        //layeredPane.add(label1, JLayeredPane.DEFAULT_LAYER);
        // layeredPane.add(painel, Integer.valueOf(0));
   // layeredPane.add(slider, Integer.valueOf(1));
   
   //layeredPane.add(label2, Integer.valueOf(2));
   //layeredPane.add(label3, Integer.valueOf(1));
   
   layeredPane.add(new DrawPanel(), Integer.valueOf(0));
   layeredPane.add(btn, Integer.valueOf(1));
   
   
   return  layeredPane;
    }
    
}
