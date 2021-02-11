
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;



/**
 *
 * @author isaura
 */
// Isso é usado para classificar os pontos por alguma "pontuação",
// que pode ser um ângulo ou outra métrica associada a cada ponto.


class Point2DAndScore {
	public Point2D point;
	public float score;
	public boolean isPontoPositivoEnfinidade; // se e verdade, o ponto e ignorado
	public Point2DAndScore(Point2D p,float s,boolean isPosInf) {
		point = p; score = s; isPontoPositivoEnfinidade = isPosInf;
	}
}

class Point2DAndScoreComparator implements Comparator<Point2DAndScore>
{
	public int compare( Point2DAndScore a, Point2DAndScore b ) {
		if ( a.isPontoPositivoEnfinidade ) {
			if ( b.isPontoPositivoEnfinidade ) return 0; // equal
			else return 1; // a is greater
		}
		else if ( b.isPontoPositivoEnfinidade ) {
			return -1; // b is greater
		}
		else return (a.score<b.score) ? -1 : ( (a.score>b.score) ? 1 : 0 );
	}
}

public class Point2DUtil {

	static public Point2D calcularCentroidDosPontos( ArrayList<Point2D> points ) {
		float x = 0, y = 0;
		for ( Point2D p : points ) {
			x += p.x();
			y += p.y();
		}
		if ( points.size() > 1 ) {
			x /= points.size();
			y /= points.size();
		}
		return new Point2D( x, y );
	}

	static public boolean isPontoDentroDoPoligono( ArrayList< Point2D > polygonPoints, 
                Point2D q ) {

		boolean returnValue = false;
		int i, j;

		for (i = 0, j = polygonPoints.size()-1; i < polygonPoints.size(); j = i++) {

			Point2D pi = polygonPoints.get(i);
			float xi = pi.x();
			float yi = pi.y();
			Point2D pj = polygonPoints.get(j);
			float xj = pj.x();
			float yj = pj.y();

			if (
				(((yi <= q.y()) && (q.y() < yj)) || ((yj <= q.y())
                                && (q.y() < yi)))
				&& (q.x() < (xj - xi) * (q.y() - yi) / (yj - yi) + xi)
			) {
				returnValue = ! returnValue;
			}
		}
		return returnValue;
	}

	// Retorna os pontos no casco convexo no sentido anti-horário
// (assumindo um sistema de coordenadas com x + direita ey + para cima).
// Usa o conhecido algoritmo "varredura de Graham" para calcular o casco convexo em 2D,
// um algoritmo bem explicado em
// http://www.personal.kent.edu/~rmuhamma/Compgeometry/MyCG/ConvexHull/GrahamScan/grahamScan.htm


	static public ArrayList< Point2D > computeConvexHull(
		// input
		ArrayList< Point2D > points
	) {
		if ( points == null ) return null;
		if ( points.size() < 3 ) {
			ArrayList< Point2D > returnValue = new ArrayList< Point2D >();
			for ( Point2D p : points ) {
				returnValue.add( p );
			}
			return returnValue;
		}
// Pode haver um ou mais pontos com coordenada y mínima.
// Vamos chamá-los de pontos "inferiores".
// Destes, encontramos aquele com coordenada x mínima (o ponto "inferior esquerdo")
// e coordenada x máxima (o ponto "inferior direito").


		int indexOfBottomLeftPoint = 0;
		Point2D pontoInferiorEsquerdoDoBotao = points.get( 0 );
		int indexOfBottomRightPoint = 0;
		Point2D pontoInferiorDireitoDoBotao = points.get( 0 );
		for ( int i = 1; i < points.size(); ++i ) {
			Point2D candidatePoint = points.get( i );
			if ( candidatePoint.y() < pontoInferiorEsquerdoDoBotao.y() ) {
				indexOfBottomLeftPoint = indexOfBottomRightPoint = i;
				pontoInferiorEsquerdoDoBotao = pontoInferiorDireitoDoBotao = candidatePoint;
			}
			else if ( candidatePoint.y() == pontoInferiorEsquerdoDoBotao.y() ) {
				if ( candidatePoint.x() < pontoInferiorEsquerdoDoBotao.x() ) {
					indexOfBottomLeftPoint = i;
					pontoInferiorEsquerdoDoBotao = candidatePoint;
				}
				else if ( candidatePoint.x() > pontoInferiorDireitoDoBotao.x() ) {
					indexOfBottomRightPoint = i;
					pontoInferiorDireitoDoBotao = candidatePoint;
				}
			}
		}

		/// Imagine que, para cada ponto, calculamos o ângulo do ponto em relação a bottomLeftPoint,
// e então classifique os pontos por este ângulo.
// Isso é equivalente a classificar os pontos por sua cotangente, que é mais rápido de calcular.
// Pontos com coordenada y mínima (ou seja, pontos "inferiores")
// receberá uma cotangente de + infinito e será tratada mais tarde.


		Point2DAndScore [] pontosComContagens = new Point2DAndScore[ points.size() ];
		for ( int i = 0; i < points.size(); ++i ) {
			Point2D p = points.get( i );
			float delta_y = p.y() - pontoInferiorEsquerdoDoBotao.y();
			assert delta_y >= 0;
			if ( delta_y == 0 ) {
				pontosComContagens[i] = new Point2DAndScore( p, 0, true );
			}
			else {
				float delta_x = p.x() - pontoInferiorEsquerdoDoBotao.x();
				pontosComContagens[i] = new Point2DAndScore( p, delta_x/delta_y /* the cotangent */, false );
			}
		}
		// classificar os pontos por seu cotangente

                         //ordenar
		Arrays.sort(pontosComContagens, new Point2DAndScoreComparator());

		// Precisamos ser capazes de remover pontos de consideração de maneira eficiente,
            // então, nós os copiamos para uma lista vinculada.
            // Ao fazer isso, também invertemos a ordem dos pontos
            // (então eles estão em ordem decrescente de cotangente, ou seja, em ordem anti-horária).
            // Os pontos com + cotangente infinito (ou seja, os pontos "inferiores")
            // também pode ser removido da consideração aqui,
            // contanto que mantenhamos os pontos "inferior esquerdo" e "inferior direito".
                            LinkedList< Point2D > pontosOrdenados = new LinkedList< Point2D >();
		pontosOrdenados.add( pontoInferiorEsquerdoDoBotao );
		// check if the "bottom left" and "bottom right" points are distinct
		if ( indexOfBottomLeftPoint != indexOfBottomRightPoint )
			pontosOrdenados.add( pontoInferiorDireitoDoBotao );
		for ( int i = pontosComContagens.length - 1; i >= 0; --i ) {
			if ( ! pontosComContagens[i].isPontoPositivoEnfinidade ) {
				pontosOrdenados.add( pontosComContagens[i].point );
			}
		}

		if ( pontosOrdenados.size() > 2 ) {
			// Iremos percorrer os pontos ordenados, processando 3 pontos consecutivos por vez.
// Dois iteradores são usados ​​para fazer backup e avançar.


			Point2D p0 = pontosOrdenados.get(0);
			Point2D p1 = pontosOrdenados.get(1);
			Point2D p2 = pontosOrdenados.get(2);
			ListIterator< Point2D > it3 = pontosOrdenados.listIterator(3);
			assert it3.nextIndex() == 3;
			while ( true ) {
				assert pontosOrdenados.size() > 2;
				Vector2D v01 = new Vector2D( p1.x()-p0.x(), p1.y()-p0.y() );
				Vector2D v12 = new Vector2D( p2.x()-p1.x(), p2.y()-p1.y() );
                        // Calcula o componente z do produto vetorial de v1 e v2
                        // (Observe que os componentes xey do produto vetorial são zero,
                        // porque os componentes z de aeb são ambos zero)


				float crossProduct_z = v01.x()*v12.y() - v01.y()*v12.x();

				if ( crossProduct_z > 0 ) {
					// temos uma curva à esquerda; tente dar um passo a frente


					if ( it3.hasNext() ) {
						p0 = p1;
						p1 = p2;
						p2 = it3.next();
					}
					else {
						// não podemos avançar


						break;
					}
				}
				else {
					// Ou temos uma curva para a direita,
                            // ou os pontos são colineares (com o 3º ponto na frente ou atrás do 2º)
                            // Em qualquer caso, removemos o segundo ponto da consideração e (tentamos) fazer backup.


					assert it3.hasPrevious();
					it3.previous();
					assert it3.hasPrevious();
					it3.previous();
					it3.remove(); // apaga o segundo ponto
					assert it3.hasNext();
					it3.next(); // now the iterator is back to where it used to be

					// agora tentamos fazer backup


					assert it3.hasPrevious();
					it3.previous();
					assert it3.hasPrevious();
					it3.previous();
					if ( it3.hasPrevious() ) {
						p1 = p0;
						p0 = it3.previous();
						it3.next();
						it3.next();
						it3.next();
					}
					else {
						it3.next();
						it3.next();
						// damos um passo à frente em vez disso


						if ( it3.hasNext() ) {
							p1 = p2;
							p2 = it3.next();
						}
						else {
							// não podemos nos mover em nenhuma direção


							break;
						}
					}
				}
			} // while
		}

		// copiar os resultados para o formato de saída apropriado


		ArrayList< Point2D > retornaValor = new ArrayList< Point2D >();

		for ( Point2D p : pontosOrdenados ) {
			retornaValor.add( p );
		}
		return retornaValor;
	}

	static public ArrayList< Point2D > calculaPoligynExpandido(
		ArrayList< Point2D > pointspontos, // input
		float margemDeEsessura
	) {
		ArrayList< Point2D > novoPontos = new ArrayList< Point2D >();
		if ( pointspontos.size() == 0 ) {
			// faz nada
		}
		else if ( pointspontos.size() == 1 ) {
			Point2D p = pointspontos.get(0);
			novoPontos.add( new Point2D( p.x()-margemDeEsessura, p.y() ) );
			novoPontos.add( new Point2D( p.x(), p.y()-margemDeEsessura ) );
			novoPontos.add( new Point2D( p.x()+margemDeEsessura, p.y() ) );
			novoPontos.add( new Point2D( p.x(), p.y()+margemDeEsessura ) );
		}
		else if ( pointspontos.size() == 2 ) {
			Point2D p0 = pointspontos.get(0);
			Point2D p1 = pointspontos.get(1);
			Vector2D v0 = Vector2D.mult( Point2D.diff(p1,p0).normalizado(), 
                                margemDeEsessura );
			Vector2D v1 = new Vector2D( -v0.y(), v0.x() );
			novoPontos.add( Point2D.sum( p0, v1 ) );
			novoPontos.add( Point2D.sum( p0, v0.negado() ) );
			novoPontos.add( Point2D.sum( p0, v1.negado() ) );
			novoPontos.add( Point2D.sum( p1, v1.negado() ) );
			novoPontos.add( Point2D.sum( p1, v0 ) );
			novoPontos.add( Point2D.sum( p1, v1 ) );
		}
		else
                {
			for ( int i = 0; i < pointspontos.size(); ++i ) {
				Point2D p = pointspontos.get(i);
				Point2D p_anterior = pointspontos.get( i==0 ? pointspontos.size()-1 : i-1 );
				Point2D p_proximo = pointspontos.get( (i+1) % pointspontos.size() );
				Vector2D v_anterior = Point2D.diff(p, p_anterior ).normalizado();
				Vector2D v_proximo = Point2D.diff(p, p_proximo ).normalizado();

				novoPontos.add( Point2D.sum( p, Vector2D.mult(new Vector2D(v_anterior.y(),-v_anterior.x()),
                                        margemDeEsessura) ) );
				novoPontos.add( Point2D.sum( p, Vector2D.mult( Vector2D.sum(v_proximo,v_anterior).
                                        normalizado(), margemDeEsessura ) ) );
				novoPontos.add( Point2D.sum( p, Vector2D.mult(new Vector2D(-v_proximo.y(),v_proximo.x()),
                                        margemDeEsessura) ) );
			}
		}
		return novoPontos;
	}

	// Retorna falso se o PCA falhar.


	static public boolean analiseDoComponentePrincipal(
		// Input
		Point2D [] pontos,

		// Ouput.
// O primeiro autovetor é o principal


		Vector2D eigenvector1,
		Vector2D eigenvector2,
		double [] autoValores // resultado; o chamador deve passar em uma matriz de 2 elementos (para autovalor1 e autovalor2)

	) {
		if ( pontos.length < 2 )
			return false;

		// Calcule a média dos pontos


		double meanX = 0, meanY = 0;
		int i;
		for ( i = 0; i < pontos.length; ++i ) {
			meanX += pontos[i].x();
			meanY += pontos[i].y();
		}
		meanX /= pontos.length;
		meanY /= pontos.length;

		// Calcula a matriz de covariância
// (que é uma matriz 2x2 simétrica):
//
// [covXX covXY]
// [covXY covYY]
//
// onde "covAB" é a covariância de A e B,
// e "covAA" é o mesmo que a variância de A.
//


		double covXX = 0, covXY = 0, covYY = 0;
		for ( i = 0; i < pontos.length; ++i ) {
			covXX += (pontos[i].x()-meanX)*(pontos[i].x()-meanX);
			covXY += (pontos[i].x()-meanX)*(pontos[i].y()-meanY);
			covYY += (pontos[i].y()-meanY)*(pontos[i].y()-meanY);
		}
		covXX /= ( pontos.length - 1 );
		covXY /= ( pontos.length - 1 );
		covYY /= ( pontos.length - 1 );

		// BEGIN: Execute a decomposição automática da matriz de covariância



		double discriminant = Math.sqrt( (covXX-covYY)*(covXX-covYY) + 4*covXY*covXY );
		// Observe que, se o discriminante for quase zero,
// isso significa que a variância em X e em Y é quase a mesma,
// e que X e Y são quase não correlacionados.
// Em outras palavras, não há uma direção dominante única para os dados.


		if ( Math.abs(discriminant) <= Float.MIN_VALUE )
			return false;

		double autoValores1 = ((covXX+covYY) + discriminant)/2;
		double autoValores2 = ((covXX+covYY) - discriminant)/2;

		// Observe que, se autovalor1 for quase zero,
// então quase não há variação nos dados em qualquer direção,
// e autovalor2 também devem ser quase zero.
// No entanto, se apenas autovalor2 for quase zero,
// então covXX * covYY é quase igual a covXY ^ 2, mas isso não é um problema.


		if ( Math.abs(autoValores1) <= Float.MIN_VALUE )
			return false;

		eigenvector1.copiar( new Vector2D((float)covXY,(float)(autoValores1-covXX)).normalizado() );
		eigenvector2.copiar( new Vector2D((float)covXY,(float)(autoValores2-covXX)).normalizado() );

		// END: Execute a decomposição automática da matriz de covariância



		autoValores[0] = autoValores1;
		autoValores[1] = autoValores2;

		return true;
	}

	// Imagine uma folha de papel em uma superfície horizontal,
// e imagine que o usuário coloque a ponta do dedo na folha de papel
// e arrasta o dedo.
// A folha de papel será traduzida e girada com o dedo.
// Este método implementa tal transformação.
// Os pontos fornecidos podem ser os cantos da folha de papel,
// ou vértices de um polígono ou outra forma.
// A localização antiga e a nova do dedo também são fornecidas.
// Este método pode ser útil para permitir que o usuário arraste
// uma forma em 2D usando um dispositivo de entrada de ponto único.
// Implementação baseada em Michel Beaudouin-Lafon http://doi.acm.org/10.1145/502348.502371
//


	static public void transformPointsBasedOnDisplacementOfOnePoint(
		ArrayList<Point2D> points,
		// estes devem, é claro, estar no mesmo sistema de coordenadas que os pontos a serem transformados


		Point2D P_old,
		Point2D P_new
	) {
		Point2D centroid = calcularCentroidDosPontos( points );
		Vector2D v1 = Point2D.diff(P_old, centroid );
		Vector2D v2 = Point2D.diff(P_new, centroid );
		float rotationAngle = Vector2D.anguloDeCalculoAssinado( v1, v2 );
		float lengthToPreserve = v1.length();
		Point2D newCentroid = Point2D.sum(
			P_new,
			Vector2D.mult( v2.normalizado(), - lengthToPreserve )
		);
		Vector2D translation = Point2D.diff(newCentroid, centroid );
		float cosine = (float)Math.cos( rotationAngle );
		float sine = (float)Math.sin( rotationAngle );

		for ( Point2D p : points ) {
			float relativeX = p.x() - centroid.x();
			float relativeY = p.y() - centroid.y();
			p.get()[0] = (cosine*relativeX - sine*relativeY) + translation.x() + centroid.x();
			p.get()[1] = (sine*relativeX + cosine*relativeY) + translation.y() + centroid.y();
		}
	}

	// Isso pode ser usado para implementar a manipulação bimanual (com as duas mãos),
// ou manipulação de 2 dedos, como em um gesto de "pinça"


	static public void compute2DTransformBasedOnDisplacementOfTwoPoints(
		// input
		Point2D A_old, Point2D B_old,
		Point2D A_new, Point2D B_new,

		// output
		Vector2D translacao,
		Point2D centroDeRotacao,
		// Resultado. O chamador deve passar uma matriz de 1 elemento usada para passar o valor de volta ao chamador.
// O ângulo está em radianos.
// Um ​​valor positivo significa uma rotação anti-horária em um sistema (x + direita, y + para cima).
		float [] anguloDeRotacao,
		// Resultado. O chamador deve passar uma matriz de 1 elemento usada para passar o valor de volta ao chamador.


		float [] fatorEscala
	) {
		// Calcula os pontos médios de cada par de pontos


		Point2D M1 = Point2D.media(A_old, B_old );
		Point2D M2 = Point2D.media(A_new, B_new );

	// Esta é a tradução que os pontos devem sofrer.
		translacao.copiar( Point2D.diff(M2, M1 ) );
// Calcula um vetor associado a cada par de pontos.


		Vector2D v1 = Point2D.diff(A_old, B_old );
		Vector2D v2 = Point2D.diff(A_new, B_new );

		float v1_length = v1.length();
		float v2_length = v2.length();
		fatorEscala[0] = 1;
		if ( v1_length > 0 && v2_length > 0 )
			fatorEscala[0] = v2_length / v1_length;
		anguloDeRotacao[0] = Vector2D.anguloDeCalculoAssinado( v1, v2 );

		centroDeRotacao.copiar( M2 );
	}

	// Isso pode ser usado para implementar a manipulação bimanual (com as duas mãos),
// ou manipulação de 2 dedos, como em um gesto de "pinça"


	static public void transformPointsBasedOnDisplacementOfTwoPoints(
		ArrayList<Point2D> pontos,
		// these should, of course, be in the same coordinate system as the points to transform
		Point2D A_old, Point2D B_old,
		Point2D A_new, Point2D B_new
	) {
		Vector2D translacao = new Vector2D();
		Point2D centroDeRotacao = new Point2D();
		float [] anguloDeRotacao = new float[1];
		float [] fatorEscala = new float[1];
		compute2DTransformBasedOnDisplacementOfTwoPoints(
			A_old, B_old, A_new, B_new,
			translacao, centroDeRotacao, anguloDeRotacao, fatorEscala
		);

		float cosine = (float)Math.cos(anguloDeRotacao[0]);
		float seno = (float)Math.sin(anguloDeRotacao[0]);

		for ( Point2D p : pontos ) {
			float relativeX = ( p.x() + translacao.x() ) - centroDeRotacao.x();
			float relativeY = ( p.y() + translacao.y() ) - centroDeRotacao.y();
			p.get()[0] = fatorEscala[0]*(cosine*relativeX - seno*relativeY) + centroDeRotacao.x();
			p.get()[1] = fatorEscala[0]*(seno*relativeX + cosine*relativeY) + centroDeRotacao.y();
		}

	}


// Dado um conjunto de orientações 2D expressas em ângulos,
// para encontrar a orientação média,
// podemos tentar fazer algo como
//
// float averageAngle = 0;
// int N = 0;
//    ciclo {
// ...
// ângulo de flutuação = ...
//mediaDoAngulo + = angle;
// ++ N;
//}
// averageAngle / = N;
//
// mas isso acabou não funcionando bem.
// Por exemplo, se houver dois ângulos iguais a
// 10 graus e 350 graus, a média numérica acima
// calculará sua média em 180 graus,
// Considerando que a média que devemos calcular é
// 0 graus (ou, equivalentemente, 360 graus).
// Para calcular a média corretamente, usamos um "vetor de rotação",
// conforme implementado abaixo.
//
	static public float calculoMedioDoAngulo(
		ArrayList< Float > angulos, // ângulos, em radianos


		float mediaParaDoAngulo // ângulo médio a ser retornado se todos os ângulos dados cancelarem, ou se angles.size () == 0


	) {
		Vector2D mediaDaRotacaoDoVetor = new Vector2D();
		for ( float angulo : angulos ) {
			mediaDaRotacaoDoVetor = Vector2D.sum(
				mediaDaRotacaoDoVetor,
				new Vector2D( (float)Math.cos(angulo), (float)Math.sin(angulo) )
			);
		}
		if ( mediaDaRotacaoDoVetor.length() == 0 )
			return mediaParaDoAngulo;
		return mediaDaRotacaoDoVetor.angulo();
	}
	static public float calculoDaMediaDoAngulo(
		ArrayList< Float > angulos
	) {
		return calculoMedioDoAngulo( angulos, 0 );
	}

}
