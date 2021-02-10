/////////////////////////////////////////////////////Isaura///////////////////////////////////
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;



/**
 *
 * @author isaura, mundo da google, colaborador Staroski
 * 
 * O mundo das figuras
 * 
 */
public class GraphicosDoPrograma 
{
    
    AffineTransform transformacaoOriginal = null;

	private int pixelsLarguraJanela = 10; // deve ser inicializado para algo positivo
	private int pixelsAlturaJanela = 10; // deve ser inicializado para algo positivo

	// O cliente pode chamar frame () ou redimensionar () primeiro,
        // e devemos nos inicializar de forma diferente dependendo do caso.
	private boolean temUmaFrameRedimensionadaQueEstaSendoChamadaAntes = false;

	public int getWidth() 
        {
            return pixelsLarguraJanela;
        }
	public int getHeight() 
        { 
            return pixelsAlturaJanela;
        }
        
        
	private Graphics g = null;
	private Graphics2D g2 = null;
	private GeneralPath generalPath = new GeneralPath();
	private Line2D line2D = new Line2D.Float();
	private Path2D path2D = new Path2D.Float();
	private Rectangle2D.Float rectangle2D = new Rectangle2D.Float();
	private Ellipse2D.Float ellipse2D = new Ellipse2D.Float();
	private Arc2D.Float arc2D = new Arc2D.Float();

	public void set( Graphics g )
        { 
            this.g = g;
            this.g2 = (Graphics2D)g;
            this.transformacaoOriginal = g2.getTransform(); 
        
        }
        
        private int fontAltura = 14;
	private Font font = new Font( "Sans-serif", Font.BOLD, fontAltura );
	private FontMetrics fontMetrics = null;
	public void setFontAltura( int h )
        {
		fontAltura = h;
		font = new Font( "Sans-serif", Font.BOLD, fontAltura );
		fontMetrics = null;
	}
	public int getFontAltura()
        {
		return fontAltura;
	}
        
        
        
        
        
        
        private float offsetXInPixels = 0;
	private float offsetYInPixels = 0;
        // maior se o usuário diminuir o zoom

	private float scaleFactorInWorldSpaceUnitsPerPixel = 1.0f; 


	public float convertPixelsToWorldSpaceUnitsX( float XInPixels )
        { 
            return ( XInPixels - offsetXInPixels )*scaleFactorInWorldSpaceUnitsPerPixel;
        }
	public float convertPixelsToWorldSpaceUnitsY( float YInPixels ) 
        {
            return ( YInPixels - offsetYInPixels )*scaleFactorInWorldSpaceUnitsPerPixel;
        }
	public Point2D convertPixelParaMundoDasFormas( Point2D p )
        { 
            return new Point2D(convertPixelsToWorldSpaceUnitsX(p.x())
                    ,convertPixelsToWorldSpaceUnitsY(p.y())); 
        }

        //Esse codigo e universal
	public int convertWorldSpaceUnitsToPixelsX( float x )
        { 
            return Math.round( x / scaleFactorInWorldSpaceUnitsPerPixel + offsetXInPixels ); 
        }
	public int convertWorldSpaceUnitsToPixelsY( float y )
        { 
            return Math.round( y / scaleFactorInWorldSpaceUnitsPerPixel + offsetYInPixels ); 
        }
	public Point2D convertWorldSpaceUnitsToPixels( Point2D p ) 
        { return new Point2D(convertWorldSpaceUnitsToPixelsX(p.x()),
                convertWorldSpaceUnitsToPixelsY(p.y())); 
        }

	public float getScaleFactorInWorldSpaceUnitsPerPixel() 
        { return scaleFactorInWorldSpaceUnitsPerPixel;
        }

	public void pan( float dx, float dy )
        {
		offsetXInPixels += dx;
		offsetYInPixels += dy;
	}
	public void zoomIn(float zoomFactor, //maior que 1 para aumentar o zoom, entre 0 e 1 para diminuir o zoom
		float centerXInPixels,
		float centerYInPixels) 
        {
		scaleFactorInWorldSpaceUnitsPerPixel /= zoomFactor;
		offsetXInPixels = centerXInPixels - (centerXInPixels - offsetXInPixels)
                        * zoomFactor;
		offsetYInPixels = centerYInPixels - (centerYInPixels - offsetYInPixels)
                        * zoomFactor;
	}
	public void zoomIn(float zoomFactor // maior que 1 para aumentar o zoom,
                //entre 0 e 1 para diminuir o zoom
        ) {
		zoomIn( zoomFactor, pixelsLarguraJanela * 0.5f, pixelsAlturaJanela * 0.5f );
	}
// Isso pode ser usado para implementar o controle de câmera bimanual (2 mãos),
// ou controle de câmera com dois dedos, como em um gesto de "pinça"

	public void panAndZoomBasedOnDisplacementOfTwoPoints(
		// these are assumed to be in pixel coordinates
		Point2D A_old, Point2D B_old,
		Point2D A_new, Point2D B_new
	) 
        {
		// Compute midpoints of each pair of points
		Point2D M1 = Point2D.media(A_old, B_old );
		Point2D M2 = Point2D.media(A_new, B_new );

		// This is the translation that the world should appear to undergo.
		Vector2D translation = Point2D.diff(M2, M1 );

		// Compute a vector associated with each pair of points.
		Vector2D v1 = Point2D.diff(A_old, B_old );
		Vector2D v2 = Point2D.diff(A_new, B_new );

		float v1_length = v1.length();
		float v2_length = v2.length();
		float scaleFactor = 1;
		if ( v1_length > 0 && v2_length > 0 )
			scaleFactor = v2_length / v1_length;
		pan( translation.x(), translation.y() );
		zoomIn( scaleFactor, M2.x(), M2.y() );
	}
        
        // vai facilitar nos eventos
        
        public void frame(RectanguloAlinhado2D rect,boolean expande 
// verdadeiro se o chamador quiser uma margem de espaço em branco adicionada ao redor do retângulo


        ) {
		temUmaFrameRedimensionadaQueEstaSendoChamadaAntes = true;
		assert pixelsLarguraJanela > 0 && pixelsAlturaJanela > 0;

		if ( rect.isVazio() || rect.getDiagonal().x() == 0 || 
                        rect.getDiagonal().y() == 0 ) {
			return;
		}
		if ( expande ) 
                {
			float diagonal = rect.getDiagonal().length() / 20;
			Vector2D v = new Vector2D( diagonal, diagonal );
			rect = new RectanguloAlinhado2D( Point2D.diff(rect.getMin(),v),
                                Point2D.sum(rect.getMax(),v) );
		}
		if ( rect.getDiagonal().x() / rect.getDiagonal().y() >= pixelsLarguraJanela
                        / (float)pixelsAlturaJanela ) {
			offsetXInPixels = - rect.getMin().x() * pixelsLarguraJanela /
                                rect.getDiagonal().x();
			scaleFactorInWorldSpaceUnitsPerPixel = rect.getDiagonal().x() /
                                pixelsLarguraJanela;
			offsetYInPixels = pixelsAlturaJanela/2 - rect.getCentro().y() /
                                scaleFactorInWorldSpaceUnitsPerPixel;
		}
		else {
			offsetYInPixels = - rect.getMin().y() * pixelsAlturaJanela / 
                                rect.getDiagonal().y();
			scaleFactorInWorldSpaceUnitsPerPixel = rect.getDiagonal().y() / 
                                pixelsAlturaJanela;
			offsetXInPixels = pixelsLarguraJanela/2 - rect.getCentro().x() / 
                                scaleFactorInWorldSpaceUnitsPerPixel;
		}
	}
        
        
        public void redimensionar( int w, int h ) {
		if ( ! temUmaFrameRedimensionadaQueEstaSendoChamadaAntes ) {
			pixelsLarguraJanela = w;
			pixelsAlturaJanela = h;
			temUmaFrameRedimensionadaQueEstaSendoChamadaAntes = true;
			return;
		}

		Point2D oldCenter = convertPixelParaMundoDasFormas( new Point2D(
			pixelsLarguraJanela * 0.5f, pixelsAlturaJanela * 0.5f
		) );
		float radius = Math.min( pixelsLarguraJanela, pixelsAlturaJanela )
                        * 0.5f * scaleFactorInWorldSpaceUnitsPerPixel;


		pixelsLarguraJanela = w;
		pixelsAlturaJanela = h;

		if ( radius > 0 ) {
			frame(new RectanguloAlinhado2D(new Point2D( oldCenter.x() - radius, 
                                                oldCenter.y() - radius ),
					new Point2D( oldCenter.x() + radius,
                                                oldCenter.y() + radius )
				),false);
		}
	}
        
        
        public void setCordenadasDoSistemaParaPixels() {
		AffineTransform transform = new AffineTransform();
		g2.setTransform(transformacaoOriginal);
		g2.transform(transform);
	}

	public void setCoordinateSystemToWorldSpaceUnits() {
		AffineTransform transform = new AffineTransform();
		transform.translate( offsetXInPixels, offsetYInPixels );
		float s = 1.0f/scaleFactorInWorldSpaceUnitsPerPixel;
		transform.scale( s, s );
		g2.setTransform(transformacaoOriginal);
		g2.transform(transform);
	}

	public void clear( float r, float g, float b ) 
        {
		setColor(r,g,b);
		setCordenadasDoSistemaParaPixels();
		this.g.fillRect( 0, 0, pixelsLarguraJanela, pixelsAlturaJanela );
	}

	public void setupForDrawing() {
	}

	public void enableAlphaBlending() {
	}

	public void disableAlphaBlending() {
	}

	public void setColor( float r, float g, float b ) {
		g2.setColor( new Color( r, g, b ) );
	}

	public void setColor( float r, float g, float b, float alpha ) {
		g2.setColor( new Color( r, g, b, alpha ) );
	}

	public void setColor( Color c ) {
		setColor( c.getRed()/255.0f, c.getGreen()/255.0f, c.getBlue()/
                        255.0f, c.getAlpha()/255.0f );
	}

	public void setColor( Color c, float alpha ) {
		setColor( c.getRed()/255.0f, c.getGreen()/255.0f, c.getBlue()/255.0f, alpha );
	}

	public void setLineWidth( float width ) {
		g2.setStroke( new BasicStroke( width ) );
	}

	public void drawLine( float x1, float y1, float x2, float y2 ) {
		//generalPath.reset();
		//generalPath.moveTo( x1, y1 );//
	//generalPath.lineTo( x2, y2 );//
		//g2.draw( generalPath );//
/////////////////////////////////////////////////////////////////////
		line2D.setLine( x1, y1, x2, y2 );
		g2.draw( line2D );
	}

	public void drawPolyline( ArrayList< Point2D > points, boolean isClosed,
                boolean isFilled ) 
        {
		if ( points.size() <= 1 )
			return;
                
		path2D.reset();
		Point2D p = points.get(0);
		path2D.moveTo( p.x(), p.y() );
		for ( int i = 1; i < points.size(); ++i ) {
			p = points.get(i);
			path2D.lineTo( p.x(), p.y() );
		}
		if ( isClosed )
                {
                    path2D.closePath();
                }
		if ( isFilled ) 
                {
                     g2.fill( path2D );
                }
		else 
                {
                    g2.draw( path2D );
                }
	}

	public void drawPolyline( ArrayList< Point2D > points ) {
		drawPolyline( points, false, false );
	}
	public void drawPolygon( ArrayList< Point2D > points ) {
		drawPolyline( points, true, false );
	}
	public void fillPolygon( ArrayList< Point2D > points ) {
		drawPolyline( points, true, true );
	}

        //onde desenha a paleta de coresssssssssssssssssssssssssssssssssss
	public void drawRect( float x, float y, float w, float h, boolean isFilled )
        {
		if ( isFilled ) fillRect( x, y, w, h );
		else drawRect( x, y, w, h );
	}

	public void drawRect( float x, float y, float w, float h ) {
		rectangle2D.setRect( x, y, w, h );
		g2.draw( rectangle2D );
	}
        

	public void fillRect( float x, float y, float w, float h ) {
		rectangle2D.setRect( x, y, w, h );
		g2.fill( rectangle2D );
	}

	public void drawCircle( float x, float y, float radius, boolean isFilled ) {
		ellipse2D.setFrame( x, y, 2*radius, 2*radius );
		if ( isFilled ) g2.fill( ellipse2D );
		else g2.draw( ellipse2D );
	}

	public void drawCircle( float x, float y, float radius ) {
		drawCircle( x, y, radius, false );
	}

	public void fillCircle( float x, float y, float radius ) {
		drawCircle( x, y, radius, true );
	}

	public void drawCenteredCircle( float x, float y, float radius, boolean isFilled ) {
		x -= radius;
		y -= radius;
		drawCircle( x, y, radius, isFilled );
	}

	public void drawArc(
		float center_x, // increases right
		float center_y, // increases down
		float radius,
		float startAngle, // in radians; zero for right, increasing counterclockwise
		float angleExtent, // in radians; positive for counterclockwise
		boolean isFilled
	) {
		if ( isFilled ) {
			arc2D.setArcByCenter( center_x, center_y, radius, 
                                startAngle/Math.PI*180.0f, angleExtent/
                                        Math.PI*180.0f, Arc2D.PIE );
			g2.fill( arc2D );
		}
		else {
			arc2D.setArcByCenter( center_x, center_y, radius, 
                                startAngle/Math.PI*180.0f, angleExtent/Math.PI*180.0f, 
                                Arc2D.OPEN );
			g2.draw( arc2D );
		}
	}

	public void drawArc(
		float center_x, float center_y, float radius,
		float startAngle, // in radians
		float angleExtent // in radians
	) {
		drawArc( center_x, center_y, radius, startAngle, angleExtent, false );
	}

	public void fillArc(
		float center_x, float center_y, float radius,
		float startAngle, // in radians
		float angleExtent // in radians
	) {
		drawArc( center_x, center_y, radius, startAngle, angleExtent, true );
	}



	// returns the width of a string
	public float stringWidth( String s ) {
		if ( s == null || s.length() == 0 ) return 0;
		if ( fontMetrics == null ) {
			assert g2 != null;
			if ( g2 == null ) return 0;
			fontMetrics = g2.getFontMetrics( font );
		}
		return fontMetrics.stringWidth( s );
	}


	public void drawString(
		float x, float y,      // lower left corner of the string
		String s           // the string
	) {
		if ( s == null || s.length() == 0 ) return;

		g2.setFont( font );
		g2.drawString(
			s, x, y
		);
	}







    
}
