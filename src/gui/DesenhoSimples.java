

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import static java.awt.Component.TOP_ALIGNMENT;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


/**
 *
 * free code
 * O codigo é nossoooo, estás a vontade!!
 * 
 * Criei classes Internas para facilitar o processo
 * @author isaura, isaura, mundo da google, 
 */


class MinhaForma 
{
	public static boolean habilitarComposicao = false;
        
        public static int posX = 0;

	public static final int POLIGONO = 0;
	public static final int CIRCULO = 1;

	public int tipo; // POLIGONO or CIRCULO
	public Color cor; // pode ter um alfa diferente de zero para composição

	public Point2D centro = new Point2D();

	// Se a forma for um polígono, armazena os pontos no sentido anti-horário.
        // Se a forma for um círculo, este armazena dois pontos que estão localizados no
        // circunferência e são diametralmente opostas.


	public ArrayList< Point2D > pontos = new ArrayList< Point2D>();

	public void translate( Vector2D v /* deslocamento no espaço mundial */ )
        {
		centro.copiar(Point2D.sum(centro, v ) );
		for ( Point2D p : pontos ) 
                {
			p.copiar( Point2D.sum( p, v ) );
		}
	}
	public void rotateAndScaleAroundCenter( float anguloRadianos, float scaleFactorX, 
                float scaleFactorY ) 
        {
		float coseno = (float)Math.cos( anguloRadianos );
		float seno = (float)Math.sin( anguloRadianos );
		for ( Point2D p : pontos ) {
			float dx = p.x() - centro.x();
			float dy = p.y() - centro.y();
			p.copy(scaleFactorX*( coseno * dx - seno * dy ) + centro.x(),
				scaleFactorY*( seno * dx + coseno * dy ) + centro.y()
			);
		}
	}
        
        public void translate(int x)
        {
         
        }
	public void rotacaoEmVoltaDoCentro( float anguloEmRadiano )
        {
		rotateAndScaleAroundCenter( anguloEmRadiano, 1, 1 );
	}
	public void scaleAroundCenter( float scaleFactorX, float scaleFactorY ) {
		rotateAndScaleAroundCenter( 0, scaleFactorX, scaleFactorY );
	}

	public void draw( GraphicosDoPrograma gw, boolean isFilled, boolean isHilited ) {
		Color c = new Color(
			isHilited ? 255-(255-cor.getRed())/2 : cor.getRed(),
			isHilited ? 255-(255-cor.getGreen())/2 : cor.getGreen(),
			isHilited ? 255-(255-cor.getBlue())/2 : cor.getBlue(),
			habilitarComposicao ? cor.getAlpha() : 255
		);

		switch (tipo) {
			case POLIGONO:
                        {
				if ( isFilled ) {
					gw.setColor( c );
					gw.fillPolygono(pontos );
				}
				gw.setColor( isHilited ? Color.black : Color.gray );
				gw.drawPolygono(pontos );
			} 
                        break;
			case CIRCULO:
                        {
				float radius = Point2D.diff(pontos.get(0), centro ).length();
				if ( isFilled ) {
					gw.setColor( c );
					gw.drawCenteredCircle(centro.x(), centro.y(), 
                                                radius, true );
				}
				gw.setColor( isHilited ? Color.black : Color.gray );
				gw.drawCenteredCircle(centro.x(), centro.y(), radius, false );
			} break;
		}
	}

	boolean isPontoAoLadoDaForma( Point2D p /* no espaco da tela */ ) {
		if ( tipo == POLIGONO ) {
			return Point2DUtil.isPontoDentroDoPoligono(pontos, p );
		}
		else if ( tipo == CIRCULO ) {
			float distanciaDoQuadro = Point2D.diff(p, centro ).
                                ComprimentoAoQuadrado();
			float radiusOfCircleSquared = Point2D.diff(pontos.get(0), centro )
                                .ComprimentoAoQuadrado();
			return distanciaDoQuadro <= radiusOfCircleSquared;
		}
		return false;
	}
}
//Meu painellllllllllllllllllllllllllllllllllll############################################# 
//precisoooooo dessa classssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss
class MinhaCanvas extends JPanel implements MouseListener, MouseMotionListener,Runnable 
{

         private int x;
 
         private int y;
         
         
 
        Thread thread;
	DesenhoSimples simplesDesenho;
	GraphicosDoPrograma gw = new GraphicosDoPrograma();

	PaletaFerramentaCor corDaPaleta = new PaletaFerramentaCor();
        
	public void setCorPaletaVisivel( boolean flag ) 
        {
		corDaPaleta.setVisible( flag );
	}

	RadialMenuPaleta radialMenu = new RadialMenuPaleta();
	ControleMenuFerramenta controleMenu = new ControleMenuFerramenta();
	// Armazena todas as formas na tela,
        // exceto para qualquer forma que está sendo criada.


	public static ArrayList< MinhaForma > formas = new ArrayList< MinhaForma >();
        
       
	int mouse_x, mouse_y, old_mouse_x, old_mouse_y;

	// Eles são usados ​​durante a criação de uma nova forma.
//
	boolean is2ndPointBeingDraggedOut = false;
	boolean is3rdPointBeingDraggedOut = false;
	int x1, y1, x2, y2, x3, y3;
        
	MinhaForma formaEstandoForaDragged = null;

	// Usado para mover uma forma que já foi criada.


	int currentlyHilitedShape = -1; // -1 means none
	boolean isFormaQueEstaSendoMovida = false;

	MinhaForma formaSobreCursorNoComecoDoDrag = null;

	boolean isRolando = false;

	public MinhaCanvas( DesenhoSimples sp ) 
        {
                thread = new Thread(this);
		simplesDesenho = sp;
		setBorder( BorderFactory.createLineBorder( Color.black ) );
                
		setBackground( Color.white );
                //////////////////////////////////////////eventos add aquiiiiiii
		addMouseListener( this );
		addMouseMotionListener( this );
                //addMouseListener( simplesDesenho. );
                 DesenhoSimples.btn.addActionListener(new  DesenhoSimples());
                  DesenhoSimples.btn0.addActionListener(new  DesenhoSimples());
                 
                

		radialMenu.setItemLabelAndID(RadialMenuPaleta.CENTRAL_ITEM,           "",     
                        sp.FERRAMENTA_SELECIONADA_E_MOVIDA );
		radialMenu.setItemLabelAndID(1, sp.nomesFerramentas[ sp.
                        FERRAMENTA_SELECIONADA_E_MOVIDA  ],        
                        sp.FERRAMENTA_SELECIONADA_E_MOVIDA );
		radialMenu.setItemLabelAndID(3, sp.nomesFerramentas[
                        sp.FERRAMENTA_CRIADA_RECTANGULO ],   
                        sp.FERRAMENTA_CRIADA_RECTANGULO );
		radialMenu.setItemLabelAndID(4, sp.nomesFerramentas[
                        sp.FERRAMENTA_CRIADA_QUADRADO    ],        
                        sp.FERRAMENTA_CRIADA_QUADRADO );
		radialMenu.setItemLabelAndID(5, sp.nomesFerramentas[
                        sp.FERRAMENTA_CRIADA_CIRCULO    ],          
                        sp.FERRAMENTA_CRIADA_CIRCULO );
		radialMenu.setItemLabelAndID(6, sp.nomesFerramentas[
                        sp.FERRAMENTA_CRIADA_TRIANGULO  ],          
                        sp.FERRAMENTA_CRIADA_TRIANGULO );
		radialMenu.setItemLabelAndID(7, sp.nomesFerramentas[
                        sp.FERRAMENTA_CRIADA_TIANGULO_EQUILATERO ], 
                        sp.FERRAMENTA_CRIADA_TIANGULO_EQUILATERO );

                
               //vou precisar desses ids para colocar movimento na cena
		controleMenu.setItemLabelAndID( ControleMenuFerramenta.CENTRAL_ITEM, "", -1 );
		controleMenu.setItemLabelAndID(1, "Move", sp.MOVIMENTO_OPERACAO );
		controleMenu.setItemLabelAndID(8, "Move+Rotate", sp.OPERACAO_MOVIMENTO_E_ROTATE );
		controleMenu.setItemLabelAndID(7, "Rotate", sp.OPERACAO_ROTATE );
		controleMenu.setItemLabelAndID( 6, "Rotate+Scale",
                        sp.OPERATION_ROTATE_AND_UNIFORMLY_SCALE );
		controleMenu.setItemLabelAndID( 5, "Scale", sp.OPERATION_UNIFORMLY_SCALE );
		controleMenu.setItemLabelAndID( 4, "Scale x,y", sp.OPERATION_NON_UNIFORMLY_SCALE );
		controleMenu.setItemLabelAndID( 3, "Zoom", sp.OPERATION_ZOOM );
		controleMenu.setItemLabelAndID( 2, "Pan", sp.OPERATION_PAN );

               
                //System.out.println(""+formas.clone());
	}
        
        
        
        @Override
	public Dimension getPreferredSize() {
		return new Dimension( MyFinal.TAMANHO_LARGURA_INICIAL_JANELA, 
                        MyFinal.TAMANHO_ALTURA_INICIAL_JANELA );
	}
	public void limpar() 
        {
		formas.clear();
		repaint();
	}
        
        /* pintandooooo*/
        @Override
	public void paintComponent( Graphics g )
        {
		super.paintComponent( g );
                
                Graphics2D g2 = (Graphics2D)g;
                
                RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHints(rh);
                    
                  
                    //onde foi chamdo o set Do graphicoPrograma......................
                    
                    
		gw.set( g );
                
                
		if ( getWidth() != gw.getWidth() || getHeight() != gw.getHeight() )
			gw.redimensionar( getWidth(), getHeight() );
		gw.clear(1,1,1);
		gw.setupForDrawing();
		gw.setCoordinateSystemToWorldSpaceUnits();
		gw.enableAlphaBlending();
                    
		
		if ( is2ndPointBeingDraggedOut || is3rdPointBeingDraggedOut ) {
			assert formaEstandoForaDragged != null;
			formaEstandoForaDragged.draw( gw, false, true );
		}

		gw.setCordenadasDoSistemaParaPixels();

		if ( corDaPaleta.estaVisivel() )
			corDaPaleta.draw( gw );
		if ( radialMenu.estaVisivel() )
			radialMenu.draw( gw );
		if ( controleMenu.estaVisivel() )
			controleMenu.draw( gw );
                for ( int i = 0; i < formas.size(); ++i ) {
                    
//#######      ##############              //coloquei o rotate e translate  aquiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii!!!!
                     //g2.translate(x, 0);
                     //g2.rotate(x);
			MinhaForma shape = formas.get(i);
			shape.draw( gw, true, i == currentlyHilitedShape );
		}
                
                //g2.rotate(Math.toRadians(DesenhoSimples.xa),100,100);
               g2.translate(DesenhoSimples.xa,0);
               
                
                 //MinhaCanvas.formas.get(0).scaleAroundCenter(10, 10);
	}
        
        //Escolhe qual figura pretendes desenhar??
        
        
        ///////////////////////////////////////////////////////////////////////////////////////

	private static void usoDePostoEspecificadoParaCalcularAfORMA(
		int ferramentaAtual,
		float x1, float y1, float x2, float y2, float x3, float y3,
		Color color,
		MinhaForma s
	) {
		Point2D p1 = new Point2D( x1, y1 );
		Point2D p2 = new Point2D( x2, y2 );
		float rotacaoDeAngulo;
		s.cor = color;
		switch ( ferramentaAtual ) {
			case DesenhoSimples.FERRAMENTA_CRIADA_CIRCULO:
				s.tipo = MinhaForma.CIRCULO;
				s.centro.copy( x1, y1 );
				s.pontos.clear();
				float delta_x = x2 - x1;
				float delta_y = y2 - y1;
				s.pontos.add( new Point2D( x1-delta_x, y1-delta_y ) );
				s.pontos.add( new Point2D( x1+delta_x, y1+delta_y ) );
				break;
			case DesenhoSimples.FERRAMENTA_CRIADA_QUADRADO:
				s.tipo = MinhaForma.POLIGONO;
				s.centro.copiar( Point2D.media(p1, p2 ) );
				s.pontos.clear();
				float lengthOfDiagonal = Point2D.diff(p2, p1 ).length();
				float lengthOfSide = (float)( lengthOfDiagonal / Math.sqrt(2.0f) );
				s.pontos.add(new Point2D( s.centro.x()-lengthOfSide/2,
                                        s.centro.y()-lengthOfSide/2 ) );
				s.pontos.add(new Point2D( s.centro.x()-lengthOfSide/2, 
                                        s.centro.y()+lengthOfSide/2 ) );
				s.pontos.add(new Point2D( s.centro.x()+lengthOfSide/2,
                                        s.centro.y()+lengthOfSide/2 ) );
				s.pontos.add(new Point2D( s.centro.x()+lengthOfSide/2,
                                        s.centro.y()-lengthOfSide/2 ) );
				rotacaoDeAngulo = new Vector2D( x2-x1, y2-y1 ).angulo()+ 
                                        (float)Math.PI / 4;
				s.rotacaoEmVoltaDoCentro( rotacaoDeAngulo );
				break;
			case DesenhoSimples.FERRAMENTA_CRIADA_RECTANGULO:
				s.tipo = MinhaForma.POLIGONO;
				s.pontos.clear();
				s.pontos.add( new Point2D( x1, y1 ) );
				s.pontos.add( new Point2D( x1, y2 ) );
				s.pontos.add( new Point2D( x2, y2 ) );
				s.pontos.add( new Point2D( x2, y1 ) );
				s.centro.copiar(Point2DUtil.calcularCentroidDosPontos(s.pontos ) );
				rotacaoDeAngulo = Vector2D.anguloDeCalculoAssinado(new Vector2D( x2-s.centro.x(), y2-s.centro.y() ),
					new Vector2D( x3-s.centro.x(), y3-s.centro.y() )
				);
				s.rotacaoEmVoltaDoCentro( rotacaoDeAngulo );
				break;
			case DesenhoSimples.FERRAMENTA_CRIADA_TIANGULO_EQUILATERO:
				s.tipo = MinhaForma.POLIGONO;

				// mude o terceiro ponto para impor um triângulo equilátero


				float delta_x12 = x2 - x1;
				float delta_y12 = y2 - y1;
				float midpoint_x12 = x1 + delta_x12/2;
				float midpoint_y12 = y1 + delta_y12/2;
				float tangent = (float)Math.tan( Math.PI/3 );
				x3 = midpoint_x12 + tangent * delta_y12/2;
				y3 = midpoint_y12 - tangent * delta_x12/2;

				s.pontos.clear();
				s.pontos.add( new Point2D( x1, y1 ) );
				s.pontos.add( new Point2D( x2, y2 ) );
				s.pontos.add( new Point2D( x3, y3 ) );
				s.centro.copiar(Point2DUtil
                                        .calcularCentroidDosPontos(s.pontos ) );
				break;
			case DesenhoSimples.FERRAMENTA_CRIADA_TRIANGULO:
				s.tipo = MinhaForma.POLIGONO;
				s.pontos.clear();
				s.pontos.add( new Point2D( x1, y1 ) );
				s.pontos.add( new Point2D( x2, y2 ) );
				s.pontos.add( new Point2D( x3, y3 ) );
				s.centro.copiar(Point2DUtil.
                                        calcularCentroidDosPontos(s.pontos ) );
				break;
		}
	}
        
        /////////////////////////////////////////////////////////////////////////////////////
        
        /* numero de pontos usados para calcular forma*/

	private static int numPontoUsadoParaCalcularForma( int ferramentaAtual )
        {
		switch ( ferramentaAtual )
                {
			case DesenhoSimples.FERRAMENTA_CRIADA_QUADRADO:
			case DesenhoSimples.FERRAMENTA_CRIADA_CIRCULO:
				return 2;
			case DesenhoSimples.FERRAMENTA_CRIADA_RECTANGULO:
				return 3;
			case DesenhoSimples.FERRAMENTA_CRIADA_TIANGULO_EQUILATERO:
				return 2;
			case DesenhoSimples.FERRAMENTA_CRIADA_TRIANGULO:
				return 3;
		}
		return -1; // erro forma desconhecida
	}

	public void mouseClicked( MouseEvent e ) { }
	public void mouseEntered( MouseEvent e ) { }
	public void mouseExited( MouseEvent e ) { }

	private void completaACriacaoDaForma() {
		formas.add(formaEstandoForaDragged );
		formaEstandoForaDragged = null;
		is2ndPointBeingDraggedOut = false;
		is3rdPointBeingDraggedOut = false;
	}

	// retorna -1 se nenhuma forma estiver sob o cursor do mouse


	private int indiceDaFormaSobPixel( int x, int y )
        {
		for ( int i = formas.size()-1; i >= 0; --i ) {
			MinhaForma shape = formas.get(i);
			if ( shape.isPontoAoLadoDaForma(
				gw.convertPixelParaMundoDasFormas( new Point2D(x,y) )
			) ) {
				return i;
			}
		}
		// nenhuma forma foi encontrada sob o mouse


		return -1;
	}

	private void updateHilitedShape() {
		int i = indiceDaFormaSobPixel(mouse_x,mouse_y);
		if ( i != currentlyHilitedShape ) {
			currentlyHilitedShape = i;
			repaint();
		}
	}

        @Override
	public void mousePressed( MouseEvent e ) 
        {
		old_mouse_x = mouse_x;
		old_mouse_y = mouse_y;
		mouse_x = e.getX();
		mouse_y = e.getY();

                x = e.getX();
                y = e.getY();
		// Esta informação é salva para mais tarde,
                // caso seja necessário para implementar operações no controleMenu

                 System.out.println("Formas:"+formas.size());
                //formas.add(0, formaEstandoForaDragged);
                
                //sempre que estiver na telaaaaaa desaparece@@@@@@@@@@@@@@@@@@@@##############
                //formas.clear(); //desenha primeiro e quando pressionar desapareceeeeee
		int i = indiceDaFormaSobPixel(mouse_x,mouse_y);
		formaSobreCursorNoComecoDoDrag = i>=0 ? formas.get(i) : null;


		if ( radialMenu.estaVisivel() || (SwingUtilities.isLeftMouseButton(e)
                        && e.isControlDown()) ) {
			int returnValue = radialMenu.pressEvent( mouse_x, mouse_y );
			if ( returnValue == PaletasPersonalizadas.S_REDESENHAR )
				repaint();
			if ( returnValue != PaletasPersonalizadas.EVENTO_NAO_CONSUMIDO )
				return;
		}
		if ( controleMenu.estaVisivel() || (SwingUtilities.isLeftMouseButton(e)
                        && e.isShiftDown()) ) {
			if ( ! controleMenu.estaVisivel() ) {
				// a paleta será exibida;
                                // habilitar ou desabilitar itens apropriadamente


				controleMenu.setEnabledByID(formaSobreCursorNoComecoDoDrag != 
                                        null,
                                        DesenhoSimples.MOVIMENTO_OPERACAO );
				controleMenu.setEnabledByID(formaSobreCursorNoComecoDoDrag !=
                                        null,
                                        DesenhoSimples.OPERACAO_MOVIMENTO_E_ROTATE );
				controleMenu.setEnabledByID(formaSobreCursorNoComecoDoDrag != 
                                        null,
                                        DesenhoSimples.OPERACAO_ROTATE );
				controleMenu.setEnabledByID(formaSobreCursorNoComecoDoDrag !=
                                        null,
                                        DesenhoSimples.OPERATION_ROTATE_AND_UNIFORMLY_SCALE );
				controleMenu.setEnabledByID(formaSobreCursorNoComecoDoDrag !=
                                        null,
                                        DesenhoSimples.OPERATION_UNIFORMLY_SCALE );
				controleMenu.setEnabledByID(formaSobreCursorNoComecoDoDrag !=
                                        null,
                                        DesenhoSimples.OPERATION_NON_UNIFORMLY_SCALE );
			}
			int retornaValor = controleMenu.pressEvent( mouse_x, mouse_y );
			if ( retornaValor == PaletasPersonalizadas.S_REDESENHAR )
				repaint();
			if ( retornaValor != PaletasPersonalizadas.EVENTO_NAO_CONSUMIDO )
				return;
		}
		if ( corDaPaleta.estaVisivel() ) {
			int retornaValorS = corDaPaleta.pressEvent( mouse_x, mouse_y );
			if ( retornaValorS == PaletasPersonalizadas.S_REDESENHAR )
				repaint();
			if ( retornaValorS != PaletasPersonalizadas.EVENTO_NAO_CONSUMIDO )
				return;
		}
		if (
			is2ndPointBeingDraggedOut
			|| is3rdPointBeingDraggedOut
			|| isFormaQueEstaSendoMovida
			|| isRolando
		) {
			return;
		}
		if ( SwingUtilities.isLeftMouseButton(e) ) {
			if ( simplesDesenho.atualFerramenta == DesenhoSimples.
                                FERRAMENTA_SELECIONADA_E_MOVIDA ) {
				if ( currentlyHilitedShape > -1 ) {
					isFormaQueEstaSendoMovida = true;
				}
			}
			else {
				//comeca criando a nova forma
				is2ndPointBeingDraggedOut = true;
				x1 = x2 = x3 = mouse_x;
				y1 = y2 = y3 = mouse_y;
				formaEstandoForaDragged = new MinhaForma();
				usoDePostoEspecificadoParaCalcularAfORMA(simplesDesenho.
                                        atualFerramenta,
					gw.convertPixelsToWorldSpaceUnitsX(x1),
                                        gw.convertPixelsToWorldSpaceUnitsY(y1),
					gw.convertPixelsToWorldSpaceUnitsX(x2),
                                        gw.convertPixelsToWorldSpaceUnitsY(y2),
					gw.convertPixelsToWorldSpaceUnitsX(x3),
                                        gw.convertPixelsToWorldSpaceUnitsY(y3),
					corDaPaleta.getCorAtualSelecionada(),
					formaEstandoForaDragged
				);
				repaint();
			}
		}
		else if ( SwingUtilities.isRightMouseButton(e) ) {
			isRolando = true;
		}
	}

        @Override
	public void mouseReleased( MouseEvent e ) 
        {
		old_mouse_x = mouse_x;
		old_mouse_y = mouse_y;
		mouse_x = e.getX();
		mouse_y = e.getY();

		if ( radialMenu.estaVisivel() ) {
			int returnValue = radialMenu.releaseEvent( mouse_x, mouse_y );

			int itemID = radialMenu.getIDOfSelection();
			if ( 0 <= itemID && itemID < DesenhoSimples.NUM_FERRAMENTAS ) {
				simplesDesenho.setAtualFerramentas(itemID);
			}

			if ( returnValue == PaletasPersonalizadas.S_REDESENHAR )
				repaint();
			if ( returnValue != PaletasPersonalizadas.EVENTO_NAO_CONSUMIDO )
				return;
		}
		if ( controleMenu.estaVisivel() ) {
			int returnValue = controleMenu.releaseEvent( mouse_x, mouse_y );

			if ( returnValue == PaletasPersonalizadas.S_REDESENHAR )
				repaint();
			if ( returnValue != PaletasPersonalizadas.EVENTO_NAO_CONSUMIDO )
				return;
		}
		if ( corDaPaleta.estaVisivel() ) {
			int returnValue = corDaPaleta.releaseEvent( mouse_x, mouse_y );
			if ( returnValue == PaletasPersonalizadas.S_REDESENHAR )
				repaint();
			if ( returnValue != PaletasPersonalizadas.EVENTO_NAO_CONSUMIDO )
				return;
		}
		if ( simplesDesenho.atualFerramenta == DesenhoSimples.
                        FERRAMENTA_SELECIONADA_E_MOVIDA ) {
			if ( SwingUtilities.isLeftMouseButton(e) ) {
				isFormaQueEstaSendoMovida = false;
			}
		}
		else {
			if ( is2ndPointBeingDraggedOut ) {
				if ( SwingUtilities.isLeftMouseButton(e) ) {
					if ( numPontoUsadoParaCalcularForma(simplesDesenho.
                                                atualFerramenta) == 2 ) {
						completaACriacaoDaForma();
						repaint();
					}
					else {
						is2ndPointBeingDraggedOut = false;
						is3rdPointBeingDraggedOut = true;
					}
				}
			}
			else if ( is3rdPointBeingDraggedOut ) {
				if ( SwingUtilities.isLeftMouseButton(e) ) {
					completaACriacaoDaForma();
					repaint();
				}
			}
		}
		if ( isRolando ) {
			if ( SwingUtilities.isRightMouseButton(e) ) {
				isRolando = false;
			}
		}
                
                System.out.println("x:"+old_mouse_x+"y:"+old_mouse_y);
                
	}

        @Override
	public void mouseMoved( MouseEvent e ) {
		if ( is2ndPointBeingDraggedOut || is3rdPointBeingDraggedOut ) {
			mouseDragged(e);
			return;
		}

		old_mouse_x = mouse_x;
		old_mouse_y = mouse_y;
		mouse_x = e.getX();
		mouse_y = e.getY();

		if ( radialMenu.estaVisivel() ) {
			int returnValue = radialMenu.moveEvent( mouse_x, mouse_y );
			if ( returnValue == PaletasPersonalizadas.S_REDESENHAR )
				repaint();
			if ( returnValue != PaletasPersonalizadas.EVENTO_NAO_CONSUMIDO )
				return;
		}
		if ( controleMenu.estaVisivel() ) {
			int returnValue = controleMenu.moveEvent( mouse_x, mouse_y );
			if ( returnValue == PaletasPersonalizadas.S_REDESENHAR )
				repaint();
			if ( returnValue != PaletasPersonalizadas.EVENTO_NAO_CONSUMIDO )
				return;
		}
		if ( corDaPaleta.estaVisivel() ) {
			int retornaValor = corDaPaleta.moveEvent( mouse_x, mouse_y );
			if ( retornaValor == PaletasPersonalizadas.S_REDESENHAR )
				repaint();
			if ( retornaValor != PaletasPersonalizadas.EVENTO_NAO_CONSUMIDO )
				return;
		}
		if ( isFormaQueEstaSendoMovida || isRolando ) {
			return;
		}

		if ( simplesDesenho.atualFerramenta == DesenhoSimples.
                        FERRAMENTA_SELECIONADA_E_MOVIDA ) {
			updateHilitedShape();
		}
	}

        @Override
	public void mouseDragged( MouseEvent e ) 
        {
            
                int dx = e.getX() - x;
 
                int dy = e.getY() - y;
 
		old_mouse_x = mouse_x;
		old_mouse_y = mouse_y;
		mouse_x = e.getX();
		mouse_y = e.getY();
		int delta_x = mouse_x - old_mouse_x;
		int delta_y = mouse_y - old_mouse_y;

		if ( radialMenu.estaVisivel() ) 
                {
			int returnValue = radialMenu.dragEvent( mouse_x, mouse_y );
			if ( returnValue == PaletasPersonalizadas.S_REDESENHAR )
				repaint();
			if ( returnValue != PaletasPersonalizadas.EVENTO_NAO_CONSUMIDO )
				return;
		}
		if ( controleMenu.estaVisivel() ) {
			if ( controleMenu.isInMenuingMode() ) {
				int returnValue = controleMenu.dragEvent( mouse_x, mouse_y );
				if ( returnValue == PaletasPersonalizadas.S_REDESENHAR )
					repaint();
				if ( returnValue != PaletasPersonalizadas.EVENTO_NAO_CONSUMIDO )
					return;
			}
			else {
				// use the drag event to change the appropriate parameter
				switch ( controleMenu.getIDOfSelection() ) {
				case DesenhoSimples.MOVIMENTO_OPERACAO:
					if ( formaSobreCursorNoComecoDoDrag != null)
						formaSobreCursorNoComecoDoDrag.translate( Point2D.diff(
							gw.convertPixelParaMundoDasFormas( new Point2D( mouse_x, mouse_y ) ),
							gw.convertPixelParaMundoDasFormas( new Point2D( old_mouse_x, old_mouse_y ) )
						) );
					break;
				case DesenhoSimples.OPERACAO_MOVIMENTO_E_ROTATE:
					if ( formaSobreCursorNoComecoDoDrag != null) {
						Point2DUtil.transformPointsBasedOnDisplacementOfOnePoint(formaSobreCursorNoComecoDoDrag.pontos,
							gw.convertPixelParaMundoDasFormas( new Point2D( old_mouse_x, old_mouse_y ) ),
							gw.convertPixelParaMundoDasFormas( new Point2D( mouse_x, mouse_y ) )
						);
						formaSobreCursorNoComecoDoDrag.centro.copiar(Point2DUtil.calcularCentroidDosPontos
                                    (formaSobreCursorNoComecoDoDrag.pontos ) );
					}
					break;
				case DesenhoSimples.OPERACAO_ROTATE:
					if ( formaSobreCursorNoComecoDoDrag != null) {
						Point2D foemaCentro = gw.convertWorldSpaceUnitsToPixels(formaSobreCursorNoComecoDoDrag.centro );
						Vector2D v1 = new Vector2D( old_mouse_x-foemaCentro.x(), old_mouse_y-foemaCentro.y() );
						Vector2D v2 = new Vector2D( mouse_x-foemaCentro.x(), mouse_y-foemaCentro.y() );
						formaSobreCursorNoComecoDoDrag.rotacaoEmVoltaDoCentro(
							Vector2D.anguloDeCalculoAssinado( v1, v2 )
						);
					}
					break;
				case DesenhoSimples.OPERATION_ROTATE_AND_UNIFORMLY_SCALE:
					if ( formaSobreCursorNoComecoDoDrag != null) {
						Point2D formaCentro = gw.convertWorldSpaceUnitsToPixels(formaSobreCursorNoComecoDoDrag.centro );
						Vector2D v1 = new Vector2D( old_mouse_x-formaCentro.x(), old_mouse_y-formaCentro.y() );
						Vector2D v2 = new Vector2D( mouse_x-formaCentro.x(), mouse_y-formaCentro.y() );
						float uniformScaleFactor = (float)Math.pow(MyFinal.zoomFactorPerPixelDragged, v2.length()-v1.length());
						formaSobreCursorNoComecoDoDrag.rotateAndScaleAroundCenter(
							Vector2D.anguloDeCalculoAssinado( v1, v2 ),
							uniformScaleFactor,
							uniformScaleFactor
						);
					}
					break;
				case DesenhoSimples.OPERATION_UNIFORMLY_SCALE:
					if ( formaSobreCursorNoComecoDoDrag != null) {
						float uniformScaleFactor = (float)Math.pow(MyFinal.zoomFactorPerPixelDragged, delta_x-delta_y);
						formaSobreCursorNoComecoDoDrag.scaleAroundCenter(
							uniformScaleFactor,
							uniformScaleFactor
						);
					}
					break;
				case DesenhoSimples.OPERATION_NON_UNIFORMLY_SCALE:
					if ( formaSobreCursorNoComecoDoDrag != null)
						formaSobreCursorNoComecoDoDrag.scaleAroundCenter(
							(float)Math.pow(MyFinal.zoomFactorPerPixelDragged, delta_x),
							(float)Math.pow(MyFinal.zoomFactorPerPixelDragged,-delta_y)
						);
					break;
				case DesenhoSimples.OPERATION_PAN:
					gw.pan( delta_x, delta_y );
					break;
				case DesenhoSimples.OPERATION_ZOOM:
					gw.zoomIn( (float)Math.pow( MyFinal.zoomFactorPerPixelDragged, delta_x-delta_y ) );
					break;
				}
				repaint();
			}
		}
		if ( corDaPaleta.estaVisivel() ) {
			int returnValue = corDaPaleta.dragEvent( mouse_x, mouse_y );
			if ( returnValue == PaletasPersonalizadas.S_REDESENHAR )
				repaint();
			if ( returnValue != PaletasPersonalizadas.EVENTO_NAO_CONSUMIDO )
				return;
		}
		if ( isFormaQueEstaSendoMovida && currentlyHilitedShape > -1 ) {
			MinhaForma shape = formas.get(currentlyHilitedShape);
			shape.translate( Point2D.diff(
				gw.convertPixelParaMundoDasFormas( new Point2D( mouse_x, mouse_y ) ),
				gw.convertPixelParaMundoDasFormas( new Point2D( old_mouse_x, 
                                        old_mouse_y ) )
			) );
			repaint();
		}
		else if ( is2ndPointBeingDraggedOut || is3rdPointBeingDraggedOut ) 
                {
			if ( is2ndPointBeingDraggedOut ) {
				x2 = x3 = mouse_x;
				y2 = y3 = mouse_y;
			}
			else {
				x3 = mouse_x;
				y3 = mouse_y;
			}
			usoDePostoEspecificadoParaCalcularAfORMA(simplesDesenho.
                                atualFerramenta,
				gw.convertPixelsToWorldSpaceUnitsX(x1), 
                                gw.convertPixelsToWorldSpaceUnitsY(y1),
				gw.convertPixelsToWorldSpaceUnitsX(x2),
                                gw.convertPixelsToWorldSpaceUnitsY(y2),
				gw.convertPixelsToWorldSpaceUnitsX(x3),
                                gw.convertPixelsToWorldSpaceUnitsY(y3),
				corDaPaleta.getCorAtualSelecionada(),
				formaEstandoForaDragged
			);
			repaint();
		}
		else if ( isRolando ) 
                {
			gw.pan( delta_x, delta_y );
			repaint();
		}
                
                if(GraphicosDoPrograma.ellipse2D.contains(x,y))
                {
                    GraphicosDoPrograma.ellipse2D.x += dx;
 
                    GraphicosDoPrograma.ellipse2D.y += dy;
 
                    repaint();
                }
                
                x += dx;
 
                y += dy;
	}

    @Override
    public void run() {
     
        while(true)
        {
            if(DesenhoSimples.btnClique)
            {
                MinhaForma.posX++;
            }
            this.repaint();
            
            try 
            {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                System.out.println("Nao foi possivel ");
            }
        }
    }

}

public class DesenhoSimples implements ActionListener
{

	static final String nomeDaApplicacao = "Meu Programa de Desenho e Animacao";
        static int xa = 0;
        Timer tempo = new Timer(5,this);
        
         public static JButton btn,btn0,btn1;
         static Boolean btnClique;
	JFrame janela;
	Container panelDeFerramentas;
	MinhaCanvas canvas;

	JMenuItem limparMenuItem, sairMenuItem, aboutMenuItem;
	JCheckBoxMenuItem toolsMenuItem, colorsMenuItem, enableCompositingMenuItem;
        
        public DesenhoSimples()
        {
            tempo.start();
        }

	public static final int FERRAMENTA_SELECIONADA_E_MOVIDA = 0;
	public static final int FERRAMENTA_CRIADA_QUADRADO = 1;
	public static final int FERRAMENTA_CRIADA_RECTANGULO = 2;
	public static final int FERRAMENTA_CRIADA_CIRCULO = 3;
	public static final int FERRAMENTA_CRIADA_TIANGULO_EQUILATERO = 4;
	public static final int FERRAMENTA_CRIADA_TRIANGULO = 5;
	public static final int NUM_FERRAMENTAS = 6;

	public static final int MOVIMENTO_OPERACAO = 0;
	public static final int OPERACAO_MOVIMENTO_E_ROTATE = 1;
	public static final int OPERACAO_ROTATE = 2;
	public static final int OPERATION_ROTATE_AND_UNIFORMLY_SCALE = 3;
	public static final int OPERATION_UNIFORMLY_SCALE = 4;
	public static final int OPERATION_NON_UNIFORMLY_SCALE = 5;
	public static final int OPERATION_PAN = 6;
	public static final int OPERATION_ZOOM = 7;

	JRadioButton [] botoesFerramenta = new JRadioButton[ NUM_FERRAMENTAS ];
	public String [] nomesFerramentas = new String[ NUM_FERRAMENTAS ];
	public int atualFerramenta = FERRAMENTA_CRIADA_QUADRADO;

	public void setAtualFerramentas( int ferramenta ) 
        {
		atualFerramenta = ferramenta;
		botoesFerramenta[ferramenta].setSelected(true);
	}
        
       

        @Override
	public void actionPerformed(ActionEvent e)
        {
            
                if(btn == e.getSource())
                {
                    System.out.println("Clicaste me!!");
                    this.btnClique = true;
                   
                   
                  for( int i = 0;i< MinhaCanvas.formas.size();i++)
                  {
                      MinhaCanvas.formas.get(i).rotacaoEmVoltaDoCentro(xa);
                      
                      // MinhaCanvas.formas.get(i).scaleAroundCenter(100, TOP_ALIGNMENT);
                  }
                    // MinhaCanvas.formas.get(1).rotacaoEmVoltaDoCentro(10);
                   
                    ///System.out.println(""+MinhaCanvas.formas.clone().toString());
                   // MinhaCanvas.formas.clone();
                }
                xa+=100;
                
                if(btn0 == e.getSource())
                {
                    
                    for(int i = 0;i<MinhaCanvas.formas.size();i++)
                   {
                       MinhaCanvas.formas.get(i).scaleAroundCenter(10, 10);
                        //MinhaCanvas.formas.get(i).rotateAndScaleAroundCenter(100, 100, 100);
                   }
   
                }
		Object fonte = e.getSource();
		if ( fonte == limparMenuItem ) {
			canvas.limpar();
		}
		else if ( fonte == sairMenuItem ) {
			int response = JOptionPane.showConfirmDialog(janela,
				"Reaalmente sair?",
				"Confirma a saidat",
				JOptionPane.YES_NO_OPTION
			);

			if (response == JOptionPane.YES_OPTION) 
                        {
				System.exit(0);
			}
		}
		else if ( fonte == toolsMenuItem ) {
			Container pane = janela.getContentPane();
			if ( toolsMenuItem.isSelected() ) {
				pane.removeAll();
				pane.add(panelDeFerramentas );
				pane.add( canvas );
			}
			else {
				pane.removeAll();
				pane.add( canvas );
			}
			janela.invalidate();
			janela.validate();
		}
		else if ( fonte == colorsMenuItem ) {
			canvas.setCorPaletaVisivel( colorsMenuItem.isSelected() );
			canvas.repaint();
		}
		else if ( fonte == enableCompositingMenuItem ) {
			MinhaForma.habilitarComposicao = enableCompositingMenuItem.isSelected();
			canvas.repaint();
		}
		else if ( fonte == aboutMenuItem ) {
			JOptionPane.showMessageDialog(janela,
				"'" + nomeDaApplicacao + "' Simples Programa\n"
					+ " para criacao de \n"
					+ "Animacaoes com java 2D",
				"Sobre",
				JOptionPane.INFORMATION_MESSAGE
			);
		}
		else {
			for ( int i = 0; i < NUM_FERRAMENTAS; ++i ) {
				if ( fonte == botoesFerramenta[i] ) {
					atualFerramenta = i;
					return;
				}
			}
		}
	}


	// Para segurança do thread, isso deve ser invocado
// do thread de despacho de eventos.
//
	private void criarUI() 
        {
		if ( ! SwingUtilities.isEventDispatchThread() ) {
			System.out.println(
				"Aviso: a IU não está sendo criada no Event Dispatch Thread\n" +
                        "   !");
			assert false;
		}

		nomesFerramentas[ FERRAMENTA_SELECIONADA_E_MOVIDA ] = "Seleciona e Mova";
		nomesFerramentas[ FERRAMENTA_CRIADA_QUADRADO ] = "Quadrado";
		nomesFerramentas[ FERRAMENTA_CRIADA_RECTANGULO ] = "Rectangulo";
		nomesFerramentas[ FERRAMENTA_CRIADA_CIRCULO ] = "Circulo";
		nomesFerramentas[ FERRAMENTA_CRIADA_TIANGULO_EQUILATERO ] = "Triangulo Equilatero";
		nomesFerramentas[ FERRAMENTA_CRIADA_TRIANGULO ] = "Triangulo";

		janela = new JFrame( nomeDaApplicacao );
		janela.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                
               
                
                btn= new JButton("Rotacionar");
                btn0 = new JButton("Play");
                btn1 = new JButton("Translate");
                //btn.setBounds(100, 100,100,100);
                

		JMenuBar menuBar = new JMenuBar();
			JMenu menu = new JMenu("Arquivo");
				limparMenuItem = new JMenuItem("Limpar");
				limparMenuItem.addActionListener(this);
				menu.add(limparMenuItem);

				menu.addSeparator();

				sairMenuItem = new JMenuItem("Sair");
				sairMenuItem.addActionListener(this);
				menu.add(sairMenuItem);
			menuBar.add(menu);
			menu = new JMenu("Visão");
				toolsMenuItem = new JCheckBoxMenuItem("Mostrar Ferramentas");
				toolsMenuItem.setSelected( true );
				toolsMenuItem.addActionListener(this);
				menu.add(toolsMenuItem);

				colorsMenuItem = new JCheckBoxMenuItem("Mostrar cores");
				colorsMenuItem.setSelected( true );
				colorsMenuItem.addActionListener(this);
				menu.add(colorsMenuItem);

				enableCompositingMenuItem = new JCheckBoxMenuItem
                                                        ("Habilitar composição");
				enableCompositingMenuItem.setSelected(MinhaForma.
                                        habilitarComposicao );
				enableCompositingMenuItem.addActionListener(this);
				menu.add(enableCompositingMenuItem);
			menuBar.add(menu);
			menu = new JMenu("Ajuda");
				aboutMenuItem = new JMenuItem("Sobre");
				aboutMenuItem.addActionListener(this);
				menu.add(aboutMenuItem);
			menuBar.add(menu);
		janela.setJMenuBar(menuBar);

		panelDeFerramentas = new JPanel();
		panelDeFerramentas.setLayout(new BoxLayout( panelDeFerramentas, 
                        BoxLayout.Y_AXIS ) );

		canvas = new MinhaCanvas(this);

		Container pane = janela.getContentPane();
		pane.setLayout( new BoxLayout( pane, BoxLayout.X_AXIS ) );
		pane.add(panelDeFerramentas );
		pane.add( canvas );
                
                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn0.setOpaque(false);
                btn0.setContentAreaFilled(false);
                //btn.setBorderPainted(false);
                
                pane.add(btn,BorderLayout.SOUTH);
                pane.add(btn0);

		ButtonGroup group = new ButtonGroup();
		for ( int i = 0; i < NUM_FERRAMENTAS; ++i )
                {
			botoesFerramenta[i] = new JRadioButton( nomesFerramentas[i] );
			botoesFerramenta[i].setAlignmentX( Component.LEFT_ALIGNMENT );
			botoesFerramenta[i].addActionListener(this);
			if ( i == atualFerramenta )
				botoesFerramenta[i].setSelected(true);
			panelDeFerramentas.add(botoesFerramenta[i] );
			group.add(botoesFerramenta[i] );
		}

		janela.pack();
		janela.setVisible( true );
	}

	public static void main( String[] args )
        {
		// Programe a criação da IU para o thread de despacho de evento.


		javax.swing.SwingUtilities.invokeLater(new Runnable() {
                                @Override
				public void run() {
					DesenhoSimples sp = new DesenhoSimples();
					sp.criarUI();
				}
			}
		);
	}
}


