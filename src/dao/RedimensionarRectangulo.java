import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class RedimensionarRectangulo extends JPanel
{
  private int SIZE = 8;
  private Rectangle2D[] pontos = { new Rectangle2D.Double(50, 50,SIZE, SIZE), new Rectangle2D.Double(150, 100,SIZE, SIZE) };
  Rectangle2D s = new Rectangle2D.Double();

  //para redimensionar o retangulo
  ShapeResizeHandler redimensionadorEventos = new ShapeResizeHandler();

  public RedimensionarRectangulo() {
    addMouseListener(redimensionadorEventos);
    addMouseMotionListener(redimensionadorEventos);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;
   // g2.fillOval(200, 200, 200, 200);

    for (int i = 0; i < pontos.length; i++) {
      g2.fill(pontos[i]);
    }
    s.setRect(pontos[0].getCenterX(), pontos[0].getCenterY(),
        Math.abs(pontos[1].getCenterX()-pontos[0].getCenterX()),
        Math.abs(pontos[1].getCenterY()- pontos[0].getCenterY()));

    g2.draw(s);
  }

  class ShapeResizeHandler extends MouseAdapter {
    Rectangle2D r = new Rectangle2D.Double(0,0,SIZE,SIZE);
    private int pos = -1;
    public void mousePressed(MouseEvent event) {
      Point p = event.getPoint();

      for (int i = 0; i < pontos.length; i++) {
        if (pontos[i].contains(p)) {
          pos = i;
          return;
        }
      }
    }

    public void mouseReleased(MouseEvent event) {
      pos = -1;
    }

    public void mouseDragged(MouseEvent event) {
      if (pos == -1)
        return;

      pontos[pos].setRect(event.getPoint().x,event.getPoint().y,pontos[pos].getWidth(),
          pontos[pos].getHeight());
      repaint();
    }
  }

  public static void main(String[] args) {

    JFrame frame = new JFrame("Resize Rectangle");

    frame.add(new RedimensionarRectangulo());
    frame.setSize(300, 300);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}

   
    
    
    
    
    
  