package diversosCodes;


import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 *
 * @author isaura
 */
public class Novo extends JPanel
{
    
    public Novo()
    {
        
        
        
    }
    
    
    public void retangulo( Graphics g)
    {
        int x = 100;
        int y = 100;
        
        g.setColor(Color.red);
        for( int i=0 ;i<10;i++)
            
        {
            
            g.drawRect(x, y, 100, 100);
             x+=10;
             y+=10;
        }
       
    }
    @Override
    public void paintComponent( Graphics g)
    {
        this.retangulo(g);
        //g.fillRect(100, 100, 100, 100);
        
    }
    
    
    public static void main(String[] args) 
    {
        
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);
        frame.add( new Novo());
        frame.setVisible(true);
    }
    
}
