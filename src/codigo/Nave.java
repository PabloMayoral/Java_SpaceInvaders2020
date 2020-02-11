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
public class Nave {

    Image imagen = null;
    public int posX = 0;
    public int posY = 0;

    private boolean pulsadoIzq = false;
    private boolean pulsadoDcha = false;

    public Nave() {
        try {//siempre que hace la lectura con algo que hay en el disco, se ejecuta un try
            //catch,esto hace que proteja lo que se encuentra en el disco.
            imagen = ImageIO.read(getClass().getResource("/imagenes/nave.png"));
//En caso de no poner IO se transforma en una exception generico con errores gerenicos
        } catch (Exception e) {

        }
    }

    public void mueve() {
        if (pulsadoIzq && posX > 0) {
            posX -= 5;
        }
        if (pulsadoDcha && posX < VentanaJuego.ANCHOPANTALLA - imagen.getWidth(null) - 15) {
            posX += 5;
        }
    }

    public boolean isPulsadoIzq() {
        return pulsadoIzq;
    }

    public void setPulsadoIzq(boolean pulsadoIzq) {
        this.pulsadoIzq = pulsadoIzq;
        this.pulsadoDcha = false;
    }

    public boolean isPulsadoDcha() {
        return pulsadoDcha;
    }

    public void setPulsadoDcha(boolean pulsadoDcha) {
        this.pulsadoDcha = pulsadoDcha;
        this.pulsadoIzq = false;
    }
}
