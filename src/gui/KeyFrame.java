
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 *
 * @author isaura
 */
public class KeyFrame  extends JPanel implements Runnable, ActionListener
{
    
    
    JButton btn = new JButton("Translate");
    JButton btn2 = new JButton("Rotate");
    
   
    
    Thread thread;
    Objecto obj = new Objecto(100,100,100,100);
    
    int x = 0;
  
    public KeyFrame()
    {
       
        
        thread = new Thread(this);
        thread.start();
        
        
        JFrame frame = new JFrame("Meu teste");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.add(this);
        
        this.add(btn,BorderLayout.EAST);
        this.add(btn2,BorderLayout.CENTER);
        
        
        btn.addActionListener(this);
        btn2.addActionListener(this);
        
        System.out.println("Minha:"+ MinhaCanvas.formas.size());
        
       
    
        
    }
    
    
    @Override
    public void paintComponent(Graphics g)
    {
        Ellipse2D.Double elipse = new Ellipse2D.Double(100,100,100,100); 
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setColor(Color.red);
        g2d.fillOval( obj .getPosX(), obj .getPosY(), obj .getLargura(), obj.getAltura());
        g2d.translate(obj.getVelX(), 0);
        
        
        for(int i = 0;i<MinhaCanvas.formas.size();i++)
       {
           MinhaCanvas.formas.get(i).scaleAroundCenter(10, 10);
           MinhaCanvas.formas.get(i).draw(fazElipse(100,100,100,100), true, true);
       }
   
    }

    
      Ellipse2D fazElipse(int x1, int y1, int x2, int y2)
    {
        return new Ellipse2D.Double(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
    }
    @Override
    public void run() 
    {
        
    
        while(true)
        {
              obj.setPosX(obj.getPosX()+obj.getVelX());
            this.repaint();
           obj.setVelX(obj.getVelX()+1);
            try 
            {
                Thread.sleep(1000/3);
            } 
            catch (InterruptedException ex) 
            {
                ex.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args)
    {
        
        new KeyFrame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       
        
        if(btn == e.getSource())
        {
            System.out.println("Clicaste");
            obj.setVelX(obj.getVelX()+1);
        }
    }
    
    
}
