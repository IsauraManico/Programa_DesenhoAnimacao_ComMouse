
import java.awt.Color;


/**
 *
 * @author isaura, google, yutube
 */
public class RadialMenuPaleta extends PaletasPersonalizadas{
    
    
    
   

	public static final int CENTRAL_ITEM = 0;
	private static final int N = 8;

            // Se uma determinada string de rótulo estiver vazia (""),
    // então não há nenhum item de menu exibido para ele.
    // Além disso, o cliente pode desativar temporariamente
    // um item de menu existente definindo seu `` isEnabled ''
    // sinalizar como falso.


	private String [] label = new String[ N + 1 ];
	private boolean [] estaAtiva = new boolean[ N + 1 ];

	// Cada item de menu também possui um número de ID (normalmente distinto).
// Eles são úteis para fazer com que vários itens se agrupem:
// sempre que o usuário arrasta sobre um determinado item,
// ele e todos os outros itens com o mesmo ID hilite juntos.
// Destina-se a casos em que existem itens de menu redundantes
// que mapeiam para a mesma função no código do cliente.


	private int [] itemID = new int[ N + 1 ];

	private int itemSelecionado; //no intervalo [CENTRAL_ITEM, N]



	// coordenadas de pixel do centro do menu
	protected int x0 = 0, y0 = 0;

	// pixel coordinates of current mouse position
	private int mouse_x, mouse_y;


	// These are in pixels.
	public static final int zonaNeutralDosRaios = 10;
	public static final int textHeight = MyFinal.ALTURA_TEXTO;
	public static final int marginAroundText = MyFinal.MARGIN_AROUND_TEXT;
	public static final int margemEntreOsItens = MyFinal.MARGIN_ENTRE_ITEMS;

	public static final Color foregroundColor = new Color( 0, 0, 0 );
	public static final Color foregroundColor2 = new Color( 127, 127, 127 );
	public static final Color backgroundColor = new Color( 255, 255, 255 );

	public RadialMenuPaleta() {
		for (int i = 0; i <= N; ++i) {
			label[i] = new String("");
			estaAtiva[i] = true;

			// Dê a cada item um ID distinto.


			itemID[i] = i;
		}
	}

	public void setItemLabelAndID( int indice, String s, int id ) {
		if ( 0 <= indice && indice <= N ) {
			label[indice] = s;
			itemID[indice] = id;
		}
	}
	public void setItemLabel( int index, String s ) {
		if ( 0 <= index && index <= N ) {
			label[index] = s;
		}
	}
	public int getItemID( int index ) {
		if ( 0 <= index && index <= N ) {
			return itemID[index];
		}
		return -1;
	}

	public void setEnabledByID( boolean flag, int id ) {
		for (int i = 0; i <= N; ++i) {
			if ( itemID[i] == id ) {
				estaAtiva[i] = flag;
			}
		}
	}
// Apenas para uso interno.


	private boolean isItemHilited( int index ) {
		assert 0 <= index && index <= N;
		return itemID[ index ] == itemID[ itemSelecionado ];
	}
// O cliente normalmente chama isso após uma interação com o menu
// está completo, para descobrir o que o usuário selecionou.
// Retorna um índice no intervalo [CENTRAL_ITEM, N]


	public int getSelection() { return itemSelecionado; }

	public int getIDOfSelection() { return getItemID( itemSelecionado ); }

	//retorna o estado do codigo
	public int pressEvent( int x, int y ) {
		x0 = mouse_x = x;
		y0 = mouse_y = y;
		itemSelecionado = CENTRAL_ITEM;
		estaVisivel = true;
		return S_REDESENHAR;
	}
	public int releaseEvent( int x, int y ) {
		if ( estaVisivel ) {
			estaVisivel = false;
			return S_REDESENHAR;
		}
		return EVENTO_NAO_CONSUMIDO;
	}
	public int moveEvent( int x, int y ) {
	// faz o centro do menu seguir o cursor


		x0 = mouse_x = x;
		y0 = mouse_y = y;
		return S_REDESENHAR;
	}
	public int dragEvent( int x, int y ) {
		if ( ! estaVisivel )
			return EVENTO_NAO_CONSUMIDO;

		mouse_x = x;
		mouse_y = y;
		int dx = mouse_x - x0;
		int dy = mouse_y - y0;
		float radius = (float)Math.sqrt( dx*dx + dy*dy );

		int itemNovamenteSelecionado = CENTRAL_ITEM;

		if ( radius > zonaNeutralDosRaios ) {
			float theta = (float)Math.asin( dy / radius );
			if ( dx < 0 ) theta = (float)Math.PI - theta;

			// theta agora é relativo ao eixo + x, que aponta para a direita,
// e aumenta no sentido horário (porque y + aponta para baixo).
// Se adicionarmos pi / 2 radianos, seria relativo ao -y
// eixo (que aponta para cima).
// No entanto, o que realmente queremos é que seja relativo a
// a linha radial que divide o item 1 do item 8.
// Portanto, devemos adicionar pi / 2 + pi / 8 radianos.


			theta += 5 * (float)Math.PI / 8;

			// Ensure it's in [0,2*pi]
			assert theta > 0;
			if ( theta > 2*Math.PI ) theta -= 2*(float)Math.PI;

			itemNovamenteSelecionado = 1 + (int)( theta / ((float)Math.PI / 4) );
			assert 1 <= itemNovamenteSelecionado && itemNovamenteSelecionado <= N;

			if ( label[ itemNovamenteSelecionado ].length() == 0 || ! estaAtiva
                                [ itemNovamenteSelecionado ] ) {
				// loop over all items, looking for the closest one
				float minDifference = 4*(float)Math.PI;
				int larguraDeDiferencaMinDoItem = CENTRAL_ITEM;
				for ( int candidateItem = 1; candidateItem <= N; 
                                        ++candidateItem ) {
					if ( label[ candidateItem ].length() > 0 &&
                                                estaAtiva[ candidateItem ] ) {
						float candidateItemTheta = (candidateItem-1)
                                                        * ((float)Math.PI/4) + (float)Math.PI/8;
						float candidateDifference = Math.abs
        ( candidateItemTheta - theta );
						if ( candidateDifference > Math.PI )
							candidateDifference = 2*(float)Math.PI 
                                                                - candidateDifference;
						if ( candidateDifference < minDifference ) {
							minDifference = candidateDifference;
							larguraDeDiferencaMinDoItem = candidateItem;
						}
					}
				}
				itemNovamenteSelecionado = larguraDeDiferencaMinDoItem;
			}
		}

		if ( itemNovamenteSelecionado != itemSelecionado ) {
			itemSelecionado = itemNovamenteSelecionado;
			return S_REDESENHAR;
		}

		return S_DONT_REDESENHADO;
	}

	protected void drawMenuItems(
		GraphicosDoPrograma gw,
		boolean drawOnlyHilitedItem, // if falso, todos os itens do menu são desenhados
		boolean drawUsingPieStyle,
		int radiusOfPie // usado apenas se `` drawUsingPieStyle '' for verdadeiro


	) {
		final float alpha = MyFinal.MENU_ALPHA;

		if ( drawUsingPieStyle ) {
			gw.setColor( foregroundColor2, alpha );
			gw.fillCircle(
				x0-radiusOfPie, y0-radiusOfPie,
				radiusOfPie
			);
		}

		if ( isItemHilited( CENTRAL_ITEM ) )
			gw.setColor( foregroundColor, alpha );
		else
			gw.setColor( backgroundColor, alpha );
		gw.fillCircle(x0-zonaNeutralDosRaios, y0-zonaNeutralDosRaios,
			zonaNeutralDosRaios
		);
		if ( ! isItemHilited( CENTRAL_ITEM ) )
			gw.setColor( foregroundColor );
		else
			gw.setColor( backgroundColor );
		gw.drawCircle(x0-zonaNeutralDosRaios, y0-zonaNeutralDosRaios,
			zonaNeutralDosRaios
		);


		/*
			Abaixo, temos o quadrante superior direito do menu radial.
                + --------- + \
                | item 1 | ) heightOfItems
                + --------- + /
                . ) margemEntreOsItens
                . + --------- + \
                . | item 2 | ) heightOfItems
                . + --------- + /
                . . ) margemEntreOsItens
                .. + --------- + \
                o ...... | item 3 | ) heightOfItems
                + --------- + /
                Seja r a distância do centro do menu "o" ao centro do item 1,
                e também a distância de "o" ao centro do item 3.
                Da foto, temos
                r == heightOfItems / 2 + margemEntreOsItens + heightOfItems
                + margemEntreOsItens + heightOfItems / 2
                == 2 * (heightOfItems + margemEntreOsItens)
                Seja r 'a distância de "o" ao centro do item 2.
                Essa distância é medida ao longo de uma linha inclinada a 45 graus.
                Conseqüentemente
                r '/ sqrt (2) == heightOfItems / 2 + margemEntreOsItens
                + heightOfItems / 2
                r '== sqrt (2) * (heightOfItems + margemEntreOsItens)
                == r / sqrt (2)
		*/
		int heightOfItem = textHeight + 2*marginAroundText;
		float radius = 2*( heightOfItem + margemEntreOsItens );
		float radiusPrime = radius / (float)Math.sqrt(2.0f);

		for ( int i = 1; i <= N; ++i ) {
			if ( label[i].length() > 0 && estaAtiva[i] ) {
				float theta = (float)( (i-1)*Math.PI/4 - Math.PI/2 );
				// compute center of ith label
				float x = ( (i%2)==1 ? radius : radiusPrime ) * 
                                        (float)Math.cos( theta ) + x0;
				float y = ( (i%2)==1 ? radius : radiusPrime ) * 
                                        (float)Math.sin( theta ) + y0;

				if ( i == 1 && label[2].length() == 0 && 
                                        label[8].length() == 0 ) {
					y = -radius/2 + y0;
				}
				else if ( i == 5 && label[4].length() == 0 && 
                                        label[6].length() == 0 ) {
					y = radius/2 + y0;
				}

				float stringWidth = gw.stringWidth( label[i] );
				float larguraDoItem = stringWidth + 2*marginAroundText;

				// Queremos que os itens que aparecem lado a lado tenham a mesma largura,
                                // para que o menu seja simétrico em relação ao eixo vertical.


				if ( i!=1 && i!=5 && label[N+2-i].length() > 0 ) {
					float otherStringWidth = gw.stringWidth(
                                                label[N+2-i] );
					if ( otherStringWidth > stringWidth )
						larguraDoItem = otherStringWidth 
                                                        + 2*marginAroundText;
				}

				if ( 2 == i || 4 == i ) {
					if ( x - larguraDoItem/2 <= x0 + margemEntreOsItens )
						// o item está muito à esquerda; mude para a direita


						x = x0 + margemEntreOsItens + larguraDoItem/2;
				}
				else if ( 3 == i ) {
					if ( x - larguraDoItem/2 <= x0 + zonaNeutralDosRaios
                                                + margemEntreOsItens )
						// o item está muito à esquerda; mude para a direita


						x = x0 + zonaNeutralDosRaios +
                                                        margemEntreOsItens + larguraDoItem/2;
				}
				else if ( 6 == i || 8 == i ) {
					if ( x + larguraDoItem/2 >= x0 - margemEntreOsItens )
						// o item está muito à direita; desloque para a esquerda


						x = x0 - margemEntreOsItens - larguraDoItem/2;
				}
				else if ( 7 == i ) {
					if ( x + larguraDoItem/2 >= x0 - 
                                                zonaNeutralDosRaios - margemEntreOsItens )
						// o item está muito à direita; desloque para a esquerda


						x = x0 - zonaNeutralDosRaios -
                                                        margemEntreOsItens - larguraDoItem/2;
				}

				if ( isItemHilited( i ) )
					gw.setColor( foregroundColor, alpha );
				else
					gw.setColor( backgroundColor, alpha );
				gw.fillRect(
					x - larguraDoItem/2, y - heightOfItem/2,
					larguraDoItem, heightOfItem
				);
				if ( ! isItemHilited( i ) )
					gw.setColor( foregroundColor );
				else
					gw.setColor( backgroundColor );
				gw.drawRect(
					x - larguraDoItem/2, y - heightOfItem/2,
					larguraDoItem, heightOfItem
				);
				gw.drawString(
					Math.round( x - stringWidth/2 ),
					Math.round( y + textHeight/2 ),
					label[i]
				);
			}
		}
	}

	public void draw(GraphicosDoPrograma gw
	) {
		if ( ! estaVisivel )
			return;

		drawMenuItems( gw, false, false, 0 );
	}

}
