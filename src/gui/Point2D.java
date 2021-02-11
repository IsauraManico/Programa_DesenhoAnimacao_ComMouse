

/**
 *
 * Coloquei os meus objetos por meio de pontos, simplemente uma classe geradora de outros pontos
 * @author isaura
 */
public class Point2D {
    
    public float [] ponto = new float[2];

	public Point2D()
        {
		ponto[0] = ponto[1] = 0;
	}

	public Point2D( float x, float y )
        {
		ponto[0] = x;
		ponto[1] = y;
	}

	public Point2D( Point2D P ) 
        {
		ponto[0] = P.ponto[0];
		ponto[1] = P.ponto[1];
	}

	public Point2D( Vector2D V ) {
		ponto[0] = V.vet[0];
		ponto[1] = V.vet[1];
	}

	public void copy( float x, float y ) {
		ponto[0] = x;
		ponto[1] = y;
	}

	public void copiar( Point2D P ) {
		ponto[0] = P.ponto[0];
		ponto[1] = P.ponto[1];
	}

	public boolean igual( Point2D outro ) 
        {
		return x() == outro.x() && y() == outro.y();
	}

	public float x() { return ponto[0]; }
	public float y() { return ponto[1]; }

	// used to pass coordinates directly to OpenGL routines
	public float [] get() { return ponto; }

	// return the difference between two given points
	static public Vector2D diff( Point2D a, Point2D b ) {
		return new Vector2D( a.x()-b.x(), a.y()-b.y() );
	}

	// return the sum of the given point and vector
	static public Point2D sum( Point2D a, Vector2D b ) {
		return new Point2D( a.x()+b.x(), a.y()+b.y() );
	}

	// return the difference between the given point and vector
	static public Point2D diff( Point2D a, Vector2D b ) {
		return new Point2D( a.x()-b.x(), a.y()-b.y() );
	}

	public float distancia( Point2D outroPonto ) {
		return diff( this, outroPonto ).length();
	}

	static Point2D media( Point2D a, Point2D b ) {
		// return new Point2D( Vector2D.mult( Vector2D.sum( new Vector2D(a), new Vector2D(b) ), 0.5f ) );
		return new Point2D( (a.x()+b.x())*0.5f, (a.y()+b.y())*0.5f );
	}
    
}
