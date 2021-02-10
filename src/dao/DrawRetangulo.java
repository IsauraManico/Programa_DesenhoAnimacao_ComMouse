
package dao;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author isaura
 */
public class DrawRetangulo implements Cloneable
{
    
    Objeto retangulo = new Objeto();
    
    public DrawRetangulo (int posX, int posY, int largura, int altura)
    {
        retangulo.setPosX(300);
        retangulo.setPosY(300);
        retangulo.setLargura(300);
        retangulo.setAltura(300);
    }
    
    public void fillRect(Graphics g)
    {
        g.setColor(Color.red);
        g.fillRect(retangulo.getPosX(),retangulo.getPosY(),retangulo.getLargura(),
                    retangulo.getAltura());
    }
    
    public DrawRetangulo clone()
    {
        return new DrawRetangulo(retangulo.getPosX(),retangulo.getPosY(),retangulo.getLargura(),
        retangulo.getAltura());
    }
    
    
}
