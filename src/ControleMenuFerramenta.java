

/**
 *
 * @author isaura
 */
public class ControleMenuFerramenta extends RadialMenuPaleta
{
    
    private boolean estaEmModoMenu = false;
	private int menuRaio = zonaNeutralDosRaios * 6;

                // Se retornar falso,
                // então os eventos de arrastar devem ser interpretados pelo cliente
                // para controlar o parâmetro selecionado pelo usuário.

	public boolean isInMenuingMode()
        {
            return this.estaEmModoMenu;
        }

	// retorna o estado do cod
    @Override
	public int pressEvent( int x, int y )
        {
		estaEmModoMenu = true;
		return super.pressEvent(x,y);
	}

	public int dragEvent( int x, int y )
        {
		if ( ! estaVisivel )
			return EVENTO_NAO_CONSUMIDO;

		if ( estaEmModoMenu ) 
                {
			int retornaValor = super.dragEvent(x,y);
			float quadroDistancia = new Vector2D(x-x0,y-y0).ComprimentoAoQuadrado();
			if ( quadroDistancia > menuRaio * menuRaio )
                        {
				estaEmModoMenu = false;
				return S_REDESENHAR;
			}
			return retornaValor;
		}

		// O widget não está no modo de menu, está no modo de arrastar,
// e o cliente deve processar o evento.


		return EVENTO_NAO_CONSUMIDO;
	}

    @Override
	public void draw(GraphicosDoPrograma gw)
        {
		if ( ! estaVisivel )
			return;

		drawMenuItems( gw, estaEmModoMenu, true, menuRaio );
	}

    
}
