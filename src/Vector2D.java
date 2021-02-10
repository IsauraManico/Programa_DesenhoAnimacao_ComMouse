

/**
 *
 * @author isaura
 */
public class Vector2D {
    
    public float [] vet = new float[2];

	public Vector2D() {
		vet[0] = vet[1] = 0;
	}

	public Vector2D( float x, float y ) {
		vet[0] = x;
		vet[1] = y;
	}

	public Vector2D( Point2D P ) {
		vet[0] = P.ponto[0];
		vet[1] = P.ponto[1];
	}

	public void copiar( float x, float y ) {
		vet[0] = x;
		vet[1] = y;
	}

	public void copiar( Vector2D V ) {
		vet[0] = V.vet[0];
		vet[1] = V.vet[1];
	}

	public float x() { return vet[0]; }
	public float y() { return vet[1]; }

	public float ComprimentoAoQuadrado() {
		return x()*x() + y()*y();
	}
	public float length()
        {
		return (float)Math.sqrt(ComprimentoAoQuadrado() );
	}

	// O angulo retornado está em [0, 2 * pi]
	public float angulo() {
		return angle( 0 );
	}

	// O angulo retornado está em [limiteInferior, limiteInferior + 2 * pi]

	public float angle( //asin retorna o arco seno
		float limiteInferior // em radianos
	) {
		float l = length();
		if ( l <= 0 )
			return limiteInferior;
		float angulo = (float)Math.asin(y()/l);
		// agora o angulo esta em [-pi/2,pi/2]
		if ( x() < 0 )
			angulo = (float)Math.PI - angulo;
		// agora o angulo esta em [-pi/2,3*pi/2]

		float doisPi = (float)( 2*Math.PI );
		float limiteSuperior = limiteInferior + doisPi;
		while ( angulo > limiteSuperior )
                {
                    angulo -= doisPi;
                }
		while ( angulo < limiteInferior )
                {
                    angulo += doisPi;
                }
		// agora o angulo entre [limiteInferior, limiteSuperior]
		return angulo;
	}

	public Vector2D negado() 
        {
		return new Vector2D(-x(),-y());
	}

	public Vector2D normalizado()
        {
		float l = length();
		if ( l > 0 )
                {
			float k = 1/l; // scale fator
			return new Vector2D(k*x(),k*y());
		}
		else 
                {
                    return new Vector2D(x(),y());
                }
	}

	// retorna o produto escalar dos vetores fornecidos
	static public float dot( Vector2D a, Vector2D b ) {
		return a.x()*b.x() + a.y()*b.y();
	}
    // retorna a soma escalar dos vetores fornecidos
	static public Vector2D sum( Vector2D a, Vector2D b ) {
		return new Vector2D( a.x()+b.x(), a.y()+b.y() );
	}

	// retorna a diferenca escalar dos vetores fornecidos
	static public Vector2D diff( Vector2D a, Vector2D b ) {
		return new Vector2D( a.x()-b.x(), a.y()-b.y() );
	}

	// retorna o produto do vetor e escalar fornecidos
	static public Vector2D mult( Vector2D a, float b )
        {
		return new Vector2D( a.x()*b, a.y()*b );
	}

	// Calcula o ângulo de rotação de v1 a v2 em torno da origem.
// O angulo retornado está no intervalo [-pi, pi],
// onde um angulo positivo corresponde a uma rotação no sentido anti-horário
// (assumindo x + direita, y + para cima).

	static public float anguloDeCalculoAssinado( Vector2D v1, Vector2D v2 ) {
		//return Vector3D.anguloDeCalculoAssinado(
		//	new Vector3D( v1.x(), v1.y(), 0 ),
		//	new Vector3D( v2.x(), v2.y(), 0 ),
		//	new Vector3D( 0, 0, 1 )
		//);

		float productOfLengths = v1.length() * v2.length();
		if ( productOfLengths <= 0 )
			return 0;

		// Calcula o componente z do produto vetorial de v1 e v2
// (Observe que os componentes xey do produto vetorial são zero,
// porque os componentes z de aeb são ambos zero)


		double crossProduct_z = v1.x()*v2.y() - v1.y()*v2.x();

		double senoDoAngulo = Math.abs(crossProduct_z) / productOfLengths;

		// Devido a imprecisões numéricas, o seno que calculamos
// pode ser um pouco mais de 1.
// Chamar arcsin em tal valor pode ser ruim, então não o fazemos.


		double angle = ( senoDoAngulo >= 1 ) ? Math.PI/2 : Math.asin( senoDoAngulo );

		// Calcule o produto escalar de v1 e v2


		float dotProduct = Vector2D.dot( v1, v2 );

		if ( dotProduct < 0 )
			angle = Math.PI - angle;

		if ( crossProduct_z < 0 )
			angle = - angle;

		return (float)angle;
	}

    
}
