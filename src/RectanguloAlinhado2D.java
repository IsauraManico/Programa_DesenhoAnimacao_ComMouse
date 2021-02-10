




/**
 *
 * @author isaura
 * com os pontos tudo fica mais simples
 */
public class RectanguloAlinhado2D 
{
    
        public boolean isVazio = true;
	private Point2D minimo = new Point2D(0,0);
	private Point2D maximo = new Point2D(0,0);
        
        public RectanguloAlinhado2D ()
        {
            
        }
        
        public RectanguloAlinhado2D (Point2D p0, Point2D p1)
        {
            
        }
        
        public void limpar()
        {
            isVazio = true;
        }
        
        // Aumente o retângulo conforme necessário para conter o ponto dado
	public void bound( Point2D p ) {
		if ( isVazio) {
			this.minimo.copiar(p);
			this.maximo.copiar(p);
			this.isVazio = false;
		}
		else {
			if ( p.x() < minimo.x() ) minimo.ponto[0] = p.x();
			else if ( p.x() > maximo.x() ) maximo.ponto[0] = p.x();

			if ( p.y() < minimo.y() ) minimo.ponto[1] = p.y();
			else if ( p.y() > maximo.y() ) maximo.ponto[1] = p.y();
		}
	}
        
       // Aumente o retângulo conforme necessário para conter o retângulo dado
	public void bound( RectanguloAlinhado2D rect ) {
		bound( rect.minimo );
		bound( rect.maximo );
	}

	public boolean isVazio() 
        { 
            return isVazio;
        }

	public boolean contem( Point2D p ) 
        {
		return !isVazio
			&& minimo.x() <= p.x() && p.x() <= maximo.x()
			&& minimo.y() <= p.y() && p.y() <= maximo.y();
	}

	public Point2D getMin()
        { 
            return minimo; 
        }
	public Point2D getMax()
        { return maximo;
        }
	public Vector2D getDiagonal()
        { 
            return Point2D.diff(maximo,minimo);
        }
	public Point2D getCentro()
        {
		return Point2D.media( minimo, maximo );
	}
        
        
    
    
        
}
