
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;



/**
 *
 * Cria a paleta de cores
 * 
 * prontos tentei caprichar hhhhhhhhhhhhhhh
 * @author isaura
 */
public class PaletaFerramentaCor extends PaletasPersonalizadas
{
    
    
	static final int larguraDeCadaAmostra = 26; 
	static final int alturaDeCadaAmostra = 42; 
	static final int alturaDeLidar = 14; 
	int x0 = 20, y0 = 20; 
	int antigo_mouse_x, antigo_mouse_y, mouse_x, mouse_y;

	ArrayList< Color > cores = new ArrayList< Color >();
	int indiceAtualDaCorSelecionada = 0;

	int indiceAtualDoEventoCorSelec = -1; // nenhum
	boolean isHandleHilited = false;
// Se for verdade, o mouse está sendo arrastado
// em um arrasto que se originou sobre a paleta de cores.


	boolean esteMouseTemDragged = false;
        
        public PaletaFerramentaCor()
        {
            estaVisivel = true;

		// arcoires hhhhhhh soa 17 cores
		cores.add( new Color( 255,   0,   0 ) );
		cores.add( new Color( 255, 127,   0 ) );
		cores.add( new Color( 255, 255,   0 ) );
		cores.add( new Color( 127, 255,   0 ) );
		cores.add( new Color(   0, 255,   0 ) );
		cores.add( new Color(   0, 255, 127 ) );
		cores.add( new Color(   0, 255, 255 ) );
		cores.add( new Color(   0, 127, 255 ) );
		cores.add( new Color(   0,   0, 255 ) );
		cores.add( new Color( 127,   0, 255 ) );
		cores.add( new Color( 255,   0, 255 ) );
		cores.add( new Color( 255,   0, 127 ) );

		// tons de cinza
		cores.add( new Color(   0,   0,   0 ) );
		cores.add( new Color(  63,  63,  63 ) );
		cores.add( new Color( 127, 127, 127 ) );
		cores.add( new Color( 191, 191, 191 ) );
		cores.add( new Color( 255, 255, 255 ) );
        }
        
        Color getCorAtualSelecionada() 
        {
		final float DEFAULT_ALPHA = 0.3f; // transparência: 1 para opaco, 0 para invisível, 0,5f para 50% de transparência


		Color c = cores.get(indiceAtualDaCorSelecionada );
		return new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)(DEFAULT_ALPHA*255));
	}

	private int larguraDaPaleta()
        {
		return cores.size() * larguraDeCadaAmostra;
	}
	private int aalturaDaPaleta() {
		return alturaDeLidar + alturaDeCadaAmostra;
	}

	public boolean isMouseOverWidget() //mouse esta sobre a paleta
        {
            
            System.out.println("Entrou hhhhhhhhhhhhhhhhhhhhh");
		return
			x0 <= mouse_x
			&& mouse_x < x0 + larguraDaPaleta()
			&& y0 <= mouse_y
			&& mouse_y < y0 + aalturaDaPaleta();
	}

	// Atualiza hiliting e retorna true se um redesenho for necessário


	private boolean hasHilitingChanged() {
		int new_indexOfCurrentlyHilitedColor = -1;
		boolean new_isHandleHilited = false;
		if ( isMouseOverWidget() ) {
			if ( mouse_y-y0 < alturaDeLidar ) {
				// mouse is over handle
				new_isHandleHilited = true;
			}
			else {
				// mouse is over a swatch
				new_indexOfCurrentlyHilitedColor
					= (mouse_x-x0)/larguraDeCadaAmostra;
			}
		}

		boolean hasHilitingChanged = false;
		if (
			new_indexOfCurrentlyHilitedColor != indiceAtualDoEventoCorSelec
			|| new_isHandleHilited != isHandleHilited
		) {
			hasHilitingChanged = true;
		}
		indiceAtualDoEventoCorSelec = new_indexOfCurrentlyHilitedColor;
		isHandleHilited = new_isHandleHilited;
		return hasHilitingChanged;
	}

	public int pressEvent( int x, int y ) {
		mouse_x = x;
		mouse_y = y;
		if ( isMouseOverWidget() ) {
			esteMouseTemDragged = true;
			if ( indiceAtualDoEventoCorSelec >= 0 ) {
				indiceAtualDaCorSelecionada = indiceAtualDoEventoCorSelec;
				return S_REDESENHAR;
			}
			return S_DONT_REDESENHADO;
		}
		return EVENTO_NAO_CONSUMIDO;
	}

	public int releaseEvent( int x, int y ) {
		mouse_x = x;
		mouse_y = y;
		if ( esteMouseTemDragged ) {
			esteMouseTemDragged = false;
			return S_DONT_REDESENHADO;
		}
		return EVENTO_NAO_CONSUMIDO;
	}
        
        
        @Override
        public int moveEvent( int x, int y ) {
		mouse_x = x;
		mouse_y = y;
		if ( hasHilitingChanged() )
			return S_REDESENHAR;
		if ( isMouseOverWidget() )
			return S_DONT_REDESENHADO;
		return EVENTO_NAO_CONSUMIDO;
	}

	public int dragEvent( int x, int y ) { //evento dragggg
		antigo_mouse_x = mouse_x;
		antigo_mouse_y = mouse_y;
		mouse_x = x;
		mouse_y = y;
		if ( esteMouseTemDragged ) {
			if ( isHandleHilited ) {
				x0 += mouse_x - antigo_mouse_x;
				y0 += mouse_y - antigo_mouse_y;
				if ( x0 < 0 ) x0 = 0;
				if ( y0 < 0 ) y0 = 0;
				return S_REDESENHAR;
			}
		}
		return EVENTO_NAO_CONSUMIDO;
	}
        
        private void drawTriangulo( GraphicosDoPrograma gw, int indice )
        {
		// desenha o cursor triangular abaixo da cor atualmente selecionada

		final int alturaDoTriangulo = 10;
		ArrayList< Point2D > points = new ArrayList< Point2D >();
		points.add(new Point2D(
			x0 + indice*larguraDeCadaAmostra + larguraDeCadaAmostra/2,
			y0 + alturaDeLidar + alturaDeCadaAmostra + alturaDoTriangulo/2
		) );
		points.add( new Point2D(
			points.get(0).x() - alturaDoTriangulo,
			points.get(0).y() + alturaDoTriangulo
		) );
		points.add( new Point2D(
			points.get(0).x() + alturaDoTriangulo,
			points.get(1).y()
		) );
		gw.fillPolygono( points );
	}

	public void draw( GraphicosDoPrograma gw )
        {
		if ( ! estaVisivel )
                {
                    return;
                }

		gw.setColor( Color.black );
		gw.drawRect(x0,y0,larguraDaPaleta() - 1,alturaDeLidar - 1);
		if ( isHandleHilited )
                {
			gw.setColor( Color.lightGray );
			gw.fillRect(x0 + 1,y0 + 1,larguraDaPaleta() - 2,alturaDeLidar - 2);
		}

		// desenha um triângulo para indicar qual amostra está atualmente selecionada
		gw.setColor( Color.black );
		drawTriangulo(gw, indiceAtualDaCorSelecionada );

		if (indiceAtualDoEventoCorSelec >= 0
			&& indiceAtualDoEventoCorSelec != indiceAtualDaCorSelecionada
		) 
                {
			// desenhe um triângulo para a amostra hilited


			gw.setColor( Color.ORANGE );
			drawTriangulo(gw, indiceAtualDoEventoCorSelec );
		}

                //desenhando os retangulos da paleta de ferramenta de cor
		for ( int i = 0; i < cores.size(); ++i ) 
                {
			gw.setColor( Color.black );
			gw.drawRect(x0 + i*larguraDeCadaAmostra,
				y0 + alturaDeLidar,
				larguraDeCadaAmostra - 1,
				alturaDeCadaAmostra - 1);
			gw.setColor(cores.get( i ) );
			gw.fillRect(x0 + i*larguraDeCadaAmostra + 1,
				y0 + alturaDeLidar + 1,
				larguraDeCadaAmostra - 2,
				alturaDeCadaAmostra - 2);
                          
		}
                
                
                //paleta para a timeline
                for ( int i = 0; i < cores.size(); ++i ) 
                {
			gw.setColor( cores.get( i ) );
			gw.fillRect(x0 + i*larguraDeCadaAmostra+200,
				y0+650 + alturaDeLidar,
				larguraDeCadaAmostra - 1,
				alturaDeCadaAmostra - 1
			);
			gw.setColor(cores.get( i ) );
			gw.fillRect(x0+300 + i*larguraDeCadaAmostra + 1,
				y0 +650+ alturaDeLidar + 1,
				larguraDeCadaAmostra+100 - 2,
				alturaDeCadaAmostra - 2
			);
                        
                        
                        
                        
                        gw.setColor(Color.red);
                        //gw.drawRect(300, 300, 300, 300);
                        
                       // gw.drawString(250, 650, "Em desenvolvimento para a timeLine se der certo hhhhhh");
                        
                        System.out.println("Desenhou txeee");
		}
	}

    
}
