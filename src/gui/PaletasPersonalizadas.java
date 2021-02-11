

/**
 *
 * @author isaura
 */
public class PaletasPersonalizadas
{
    
    // Estes são códigos de status retornados 


	public static final int EVENTO_NAO_CONSUMIDO = 0; // evento nao processado
	public static final int S_DONT_REDESENHADO = 1; // evento foi processado e não há necessidade de redesenhar
	public static final int S_REDESENHAR = 2; //o evento foi processado e, por favor, redesenhe


	protected boolean estaVisivel = false;

	public boolean estaVisivel() { return estaVisivel; }
	public void setVisible( boolean flag ) { estaVisivel = flag; }

	public boolean isMouseOverWidget() { return false; }

	// Cada um deles retorna um código de status.
	public int pressEvent( int x, int y ) { return S_DONT_REDESENHADO; }
	public int releaseEvent( int x, int y ) { return S_DONT_REDESENHADO; }
	public int moveEvent( int x, int y ) { return S_DONT_REDESENHADO; }
	public int dragEvent( int x, int y ) { return S_DONT_REDESENHADO; }
	public void draw( GraphicosDoPrograma gw ) { }
    
}
