
package dao;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author isaura
 */
public class DrawPanel extends JPanel
{
    public DrawPanel()
    {
        
        this.addMouseMotionListener(mouseMotionHandler);
        this.addMouseListener(mouserListenerHandler);
         this.setOpaque(true);
         this.setBackground(Color.white);
         this.setBounds(300,30,800,500);

    }
    
    
    
     public static MouseListener mouserListenerHandler = new MouseListener()
    {
        @Override
        public void mouseClicked(MouseEvent e) {
           
        }

        @Override
        public void mousePressed(MouseEvent e) {
            
        }

        @Override
        public void mouseReleased(MouseEvent e) {
           
        }

        @Override
        public void mouseEntered(MouseEvent e) {
          
        }

        @Override
        public void mouseExited(MouseEvent e) {
          
        }
        
    };
    
    public static  MouseMotionListener mouseMotionHandler = new MouseMotionAdapter()
    {
        @Override
        public void mouseDragged(MouseEvent e) 
        {
           
        }

        @Override
        public void mouseMoved(MouseEvent e)
        {
           
        }
        
    };
}
