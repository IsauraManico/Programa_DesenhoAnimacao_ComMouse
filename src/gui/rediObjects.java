

/**
 *
 * @author Sara Tuma
 */
 
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
 
import javax.swing.JFrame;
import javax.swing.JPanel;
 
public class rediObjects extends JPanel {
 
    private Rectangle2D.Float myRect = new Rectangle2D.Float(90, 90, 90, 90);
        private Rectangle2D.Float myRect1 = new Rectangle2D.Float(5, 5, 20, 20);
    BindMouseMove movingAdapt = new BindMouseMove();
 
    public rediObjects() {
 
  addMouseMotionListener(movingAdapt);
 
  addMouseListener(movingAdapt);
 
  addMouseWheelListener(new ResizeHandler());
    }
 
    @Override
    public void paint(Graphics graphics) {
 
  super.paint(graphics);
 
  Graphics2D graphics2d = (Graphics2D) graphics;
 
  graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
  graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 
  graphics2d.setColor(new Color(0, 0, 200));
 
  graphics2d.fill(myRect);
  
  
    graphics2d.setColor(Color.red);
 
  graphics2d.fill(myRect1);
    }
 
class BindMouseMove extends MouseAdapter {
 
  private int x;
 
  private int y;
 
  @Override
 
  public void mousePressed(MouseEvent event) {
 
x = event.getX();
 
y = event.getY();
   // System.out.println("X: "+myRect1.x);
    //myRect1.x += 100;
   // myRect1.setRect(100, 10, 10, 10);
    //myRect1.y += 100;
     // System.out.println("X: "+myRect1.x);
 
  }
 
   @Override
 
  public void mouseReleased(MouseEvent event){
      System.out.println("X: "+myRect1.x);
    myRect1.x += 100;
 
    myRect1.y += 100;
      System.out.println("X: "+myRect1.x);
      repaint();
  }
  
  @Override
 
  public void mouseDragged(MouseEvent event) {
 
int dx = event.getX() - x;
 
int dy = event.getY() - y;
 
if (myRect.getBounds2D().contains(x, y)) {
 
    myRect.x += dx;
 
    myRect.y += dy;
 
    repaint();
 
}

x += dx;
 
y += dy;
 
}
    }
 
    class ResizeHandler implements MouseWheelListener {
 
  @Override
 
  public void mouseWheelMoved(MouseWheelEvent e) {
 
        int x = e.getX();

        int y = e.getY();

        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {

            if (myRect.getBounds2D().contains(x, y)) {

                float amount = e.getWheelRotation() * 5f;

                myRect.width += amount;

                myRect.height += amount;

                repaint();

            }

        }
 
  }
    }
 
    public static void main(String[] args) {
 
        JFrame jFrame = new JFrame("Moving and Scaling");

        rediObjects resi = new rediObjects();

        resi.setDoubleBuffered(true);

        jFrame.add(resi);

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jFrame.setSize(300, 300);

        jFrame.setLocationRelativeTo(null);

        jFrame.setVisible(true);
    }
}
