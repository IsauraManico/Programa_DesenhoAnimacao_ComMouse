/////////////////////////////////////////////////////Isaura///////////////////////////////////
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author isaura, mundo da google, 
 * 
 * O mundo das figuras
 * 
 */
public class GraphicosDoPrograma  implements Runnable
{
    
    AffineTransform transformacaoOriginal = null;
    
    Thread thread;
    static int posx= 0;

	private int pixelsLarguraJanela = 10; // deve ser inicializado para algo positivo
	private int pixelsAlturaJanela = 10; // deve ser inicializado para algo positivo

	// O cliente pode chamar quadroFiguras () ou redimensionar () primeiro,
        // e devemos nos inicializar de forma diferente dependendo do caso.
	private boolean temUmaFrameRedimensionadaQueEstaSendoChamadaAntes = false;
        
        
        public GraphicosDoPrograma ()
        {
            thread = new Thread(this);
            
        }

	public int getWidth() 
        {
            return pixelsLarguraJanela;
        }
	public int getHeight() 
        { 
            return pixelsAlturaJanela;
        }
        
        
	static public Graphics g = null;
	static public Graphics2D g2 = null;
	static public GeneralPath generalPath = new GeneralPath();
	static public Line2D line2D = new Line2D.Float();
	static public Path2D path2D = new Path2D.Float();
	static public Rectangle2D.Float rectangle2D = new Rectangle2D.Float();
	static public Ellipse2D.Float ellipse2D = new Ellipse2D.Float();
	private Arc2D.Float arc2D = new Arc2D.Float();

        
        public ArrayList<Shape> shapes = new ArrayList<>();
        
        
	public void set( Graphics g )
        { 
            this.g = g;
            this.g2 = (Graphics2D)g;
             RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
             
                    g2.setRenderingHints(rh);
                    
                    g2.translate(posx, 0);
                    
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
	public void zoomIn(float zoomFactor, //maior que 1 para aumentar o zoom,
            // entre 0 e 1 para diminuir o zoom
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
		Vector2D translacao = Point2D.diff(M2, M1 );

		// Compute a vector associated with each pair of points.
		Vector2D v1 = Point2D.diff(A_old, B_old );
		Vector2D v2 = Point2D.diff(A_new, B_new );

		float v1_length = v1.length();
		float v2_length = v2.length();
		float scaleFactor = 1;
		if ( v1_length > 0 && v2_length > 0 )
			scaleFactor = v2_length / v1_length;
		pan( translacao.x(), translacao.y() );
		zoomIn( scaleFactor, M2.x(), M2.y() );
	}
        
        // vai facilitar nos eventos
        
        public void quadroFiguras(RectanguloAlinhado2D rect,boolean expande 
// verdadeiro se o chamador quiser uma margem de espaço em branco adicionada ao redor do retângulo


        ) {
		temUmaFrameRedimensionadaQueEstaSendoChamadaAntes = true;
		assert pixelsLarguraJanela > 0 && pixelsAlturaJanela > 0;

		if ( rect.isVazio() || rect.getDiagonal().x() == 0 || 
                        rect.getDiagonal().y() == 0 )
                {
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
        
        
        public void redimensionar( int w, int h )
        {
		if ( ! temUmaFrameRedimensionadaQueEstaSendoChamadaAntes )
                {
			pixelsLarguraJanela = w;
			pixelsAlturaJanela = h;
			temUmaFrameRedimensionadaQueEstaSendoChamadaAntes = true;
			return;
		}

		Point2D centroAntigo = convertPixelParaMundoDasFormas( new Point2D(
			pixelsLarguraJanela * 0.5f, pixelsAlturaJanela * 0.5f
		) );
		float radios = Math.min( pixelsLarguraJanela, pixelsAlturaJanela )
                        * 0.5f * scaleFactorInWorldSpaceUnitsPerPixel;


		pixelsLarguraJanela = w;
		pixelsAlturaJanela = h;

		if ( radios > 0 ) {
			quadroFiguras(new RectanguloAlinhado2D(new Point2D( centroAntigo.x() 
                                        - radios, 
                                                centroAntigo.y() - radios ),
					new Point2D( centroAntigo.x() + radios,
                                                centroAntigo.y() + radios )
				),false);
		}
	}
        
        
        public void setCordenadasDoSistemaParaPixels() 
        {
		AffineTransform transform = new AffineTransform();
		g2.setTransform(transformacaoOriginal);
		g2.transform(transform);
	}

	public void setCoordinateSystemToWorldSpaceUnits() 
        {
		AffineTransform transform = new AffineTransform();
		transform.translate( offsetXInPixels, offsetYInPixels );
		float s = 1.0f/scaleFactorInWorldSpaceUnitsPerPixel;
		transform.scale( s, s );
		g2.setTransform(transformacaoOriginal);
		g2.transform(transform);
	}

        //limpandoo a telaaaaaaaaaaaaaaaaaaaaaaaaaa
        
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

	public void setLineWidth( float width ) 
        {
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
                System.out.println("Desenhei linhaaaa");
	}

	public void drawPolylinha( ArrayList< Point2D > pontos, boolean isFechado,
                boolean isPintado ) 
        {
		if ( pontos.size() <= 1 )
                {
                    return;
                }
                
		path2D.reset();
		Point2D p = pontos.get(0);
		path2D.moveTo( p.x(), p.y() );
		for ( int i = 1; i < pontos.size(); ++i ) 
                {
			p = pontos.get(i);
			path2D.lineTo( p.x()+posx, p.y() );
                         System.out.println("Desenhei polinhaaaa");
		}
		if ( isFechado )
                {
                    path2D.closePath();
                }
		if ( isPintado ) 
                {
                     g2.fill( path2D );
                       System.out.println("Desenhei polinhaaaa");
                }
		else 
                {
                    g2.draw( path2D );
                }
	}

	public void drawPolylinha( ArrayList< Point2D > pontos )
        {
		GraphicosDoPrograma.this.drawPolylinha( pontos, false, false );
	}
	public void drawPolygono( ArrayList< Point2D > pontos ) {
		GraphicosDoPrograma.this.drawPolylinha( pontos, true, false );
	}
	public void fillPolygono( ArrayList< Point2D > pontos )
        {
		GraphicosDoPrograma.this.drawPolylinha( pontos, true, true );
	}

        //onde desenha a paleta de coresssssssssssssssssssssssssssssssssss
	public void drawRect( float x, float y, float w, float h, boolean isFilled )
        {
		if ( isFilled ) 
                {
                    fillRect( x, y, w, h );
                }
		else 
                {
                    drawRect( x, y, w, h );
                }
	}

        
        //Desenho do rectangulooooooo na tela para as cores//////////////////////////////////////
	public void drawRect( float x, float y, float w, float h ) 
        {
		rectangle2D.setRect( x+posx, y, w, h );
		g2.draw( rectangle2D );
	}
        

	public void fillRect( float x, float y, float w, float h ) 
        {
		rectangle2D.setRect( x+posx, y, w, h );
		g2.fill( rectangle2D );
	}
///////////////////////////////////////////////////////////////////////////////////////////////////
	
        //Criacao da Ellipse ou o ovallllllllllllllllllllllllllllllllllllllll
        
        public void drawCirculo( float x, float y, float radios, boolean isFilled )
        {
		ellipse2D.setFrame( x+posx, y, 2*radios, 2*radios );
		if ( isFilled )
                {
                     g2.fill( ellipse2D );
                }
		else
                {
                     g2.draw( ellipse2D );
                }
	}
        
       

	public void drawCirculo( float x, float y, float radius ) 
        {
		GraphicosDoPrograma.this.drawCirculo( x, y, radius, false );
	}

	public void fillCirculo( float x, float y, float radius ) {
		GraphicosDoPrograma.this.drawCirculo( x, y, radius, true );
	}

	public void drawCenteredCircle( float x, float y, float radius, boolean isFilled ) 
        {
		x -= radius;
		y -= radius;
		GraphicosDoPrograma.this.drawCirculo( x, y, radius, isFilled );
	}

	public void drawArc(
		float center_x, // increases right
		float center_y, // increases down
		float radios,
		float comecaAngulo, // in radians; zero for right, increasing counterclockwise
		float anguloExtendido, // in radians; positive for counterclockwise
		boolean isFilled
	) {
		if ( isFilled ) {
			arc2D.setArcByCenter( center_x, center_y, radios, 
                                comecaAngulo/Math.PI*180.0f, anguloExtendido/
                                        Math.PI*180.0f, Arc2D.PIE );
			g2.fill( arc2D );
		}
		else {
			arc2D.setArcByCenter( center_x, center_y, radios, 
                                comecaAngulo/Math.PI*180.0f, anguloExtendido/Math.PI*180.0f, 
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



	
	public float stringWidth( String s )
        {
		if ( s == null || s.length() == 0 ) return 0;
		if ( fontMetrics == null ) {
			assert g2 != null;
			if ( g2 == null ) return 0;
			fontMetrics = g2.getFontMetrics( font );
		}
		return fontMetrics.stringWidth( s );
	}


	public void drawString(float x, float y,String s) 
        {
		if ( s == null || s.length() == 0 ) 
                {
                    return;
                }

		g2.setFont( font );
		g2.drawString(s, x, y);
	}

    @Override
    public void run() {
      
        
        while(true)
        {
            if(DesenhoSimples.btnClique)
            {
                posx+=10;
                System.out.println("entrou o cliqueeeeeeeeeeeeeee");
            }
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
               ex.printStackTrace();
            }
        }
    }







    
}
