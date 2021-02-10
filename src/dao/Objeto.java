
package dao;

/**
 *
 * @author isaura
 */
public class Objeto 
{
    private int posX, posY, largura, altura;
    private int velX = 0,  velY = 0;
    
    
    public Objeto()
    {
        this.posX = 0;
        this.posY = 0;
        this.largura = 0;
        this.altura = 0;
        
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
