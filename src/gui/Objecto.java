
/**
 *
 * @author isaura
 */
public class Objecto {
    
      private int posX,posY,largura,altura;
    private int velX = 0, velY = 0;
    
    
    public Objecto()
    {
        
    }

    public Objecto(int posX, int posY, int largura, int altura) {
        this.posX = posX;
        this.posY = posY;
        this.largura = largura;
        this.altura = altura;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getLargura() {
        return largura;
    }

    public void setLargura(int largura) {
        this.largura = largura;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public int getVelX() {
        return velX;
    }

    public void setVelX(int velX) {
        this.velX = velX;
    }

    public int getVelY() {
        return velY;
    }

    public void setVelY(int velY) {
        this.velY = velY;
    }
    
    
    
}
