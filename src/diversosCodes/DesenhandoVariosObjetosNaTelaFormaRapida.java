package diversosCodes;




import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class DesenhandoVariosObjetosNaTelaFormaRapida extends JFrame {

  public static void main(String[] args) {
    new DesenhandoVariosObjetosNaTelaFormaRapida();
  }

  public DesenhandoVariosObjetosNaTelaFormaRapida() {
    this.setSize(300, 300);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.add(new PaintSurface(), BorderLayout.CENTER);
    this.setVisible(true);
  }

  private class PaintSurface extends JComponent {
    ArrayList<Shape> shapes = new ArrayList<Shape>();

    Point comecandoDrag, terminnandoDrag;

    public PaintSurface() {
      this.addMouseListener(new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
          comecandoDrag = new Point(e.getX(), e.getY());
          terminnandoDrag = comecandoDrag;
          repaint();
          
         // shapes.remove(0);
        }

        public void mouseReleased(MouseEvent e) {
          Shape r = fazElipse(comecandoDrag.x, comecandoDrag.y, e.getX(), e.getY());
         // Shape r2 = fazRectangulo(comecandoDrag.x+100, comecandoDrag.y+100, e.getX(), e.getY());
          shapes.add(r);
         // shapes.add(r2);
          comecandoDrag = null;
          terminnandoDrag = null;
          repaint();
        }
      });

      this.addMouseMotionListener(new MouseMotionAdapter() {
        public void mouseDragged(MouseEvent e) {
          terminnandoDrag = new Point(e.getX(), e.getY());
          repaint();
        }
      });
    }
    private void paintBackground(Graphics2D g2){
      g2.setPaint(Color.LIGHT_GRAY);
      for (int i = 0; i < getSize().width; i += 10) {
        Shape line = new Line2D.Float(i, 0, i, getSize().height);
        g2.draw(line);
      }

      for (int i = 0; i < getSize().height; i += 10) {
        Shape line = new Line2D.Float(0, i, getSize().width, i);
        g2.draw(line);
      }

      
    }
    public void paint(Graphics g) {
        
        int x= 200;
        int y = 500;
        
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      paintBackground(g2);
      Color[] colors = { Color.YELLOW, Color.MAGENTA, Color.CYAN , Color.RED, Color.BLUE, Color.PINK};
      int colorIndex = 0;

      
      g2.setStroke(new BasicStroke(2));
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));

      for (Shape s : shapes) {
        g2.setPaint(Color.BLACK);
        g2.draw(s);
        g2.setPaint(colors[(colorIndex++) % 6]);
        g2.fill(s);
      }
      
      for ( int i = 0;i<100;i++)
      {
          g2.drawRect(x, y, 200, 100);
          x+=10;
      }

      if (comecandoDrag != null && terminnandoDrag != null) {
        g2.setPaint(Color.LIGHT_GRAY);
        //Shape r = fazRectangulo(comecandoDrag.x, comecandoDrag.y, terminnandoDrag.x, terminnandoDrag.y);
        Shape r1 = fazElipse(comecandoDrag.x, comecandoDrag.y, terminnandoDrag.x, terminnandoDrag.y);
        g2.draw(r1);
      }
    }
    
    public void timeLine()
    {
        ArrayList<Shape> formas = new ArrayList<>();
        
       
        for( int i = 0;i<formas.size();i++)
        {
             formas.add(fazRectangulo(100,100,100,100));
        
        }
    }
    
    //faz qualquer figura que quiseressssss
    private Ellipse2D.Double fazElipse(int x1, int y1, int x2, int y2)
    {
        return new Ellipse2D.Double(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    private Rectangle2D.Float fazRectangulo(int x1, int y1, int x2, int y2) {
      return new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
    }
  }
}