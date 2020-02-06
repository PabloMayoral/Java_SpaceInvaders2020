/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import java.awt.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author Pablo Martin
 */
public class Disparo {

    Image imagen = null;
    public int posX = 0;
    public int posY = 0;

    public Disparo() {
        try {//siempre que hace la lectura con algo que hay en el disco, se ejecuta un try
            //catch,esto hace que proteja lo que se encuentra en el disco.
            imagen = ImageIO.read(getClass().getResource("/imagenes/disparo.png"));
//En caso de no poner IO se transforma en una exception generico con errores gerenicos
        } catch (Exception e) {

        }
    }

    public void mueve() {
        posY--;
    }

    public void posicionDisparo(Nave _nave) {
        posX = _nave.posX + _nave.imagen.getWidth(null) / 2 - imagen.getWidth(null) / 2;
        posY = _nave.posY - _nave.imagen.getHeight(null) / 2;
    }
}
