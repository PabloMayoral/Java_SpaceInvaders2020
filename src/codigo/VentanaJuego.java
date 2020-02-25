/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import java.awt.Color;
import static java.awt.Color.black;
import static java.awt.Color.white;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Timer;

/**
 *
 * @author Pablo Martin
 */
public class VentanaJuego extends javax.swing.JFrame {

    static int ANCHOPANTALLA = 800;
    static int ALTOPANTALLA = 600;

    int filasMarcianos = 5;
    int columnasMarcianos = 10;

    BufferedImage buffer = null;
    //buffer para guardar las imagenes
    BufferedImage plantilla = null;
    BufferedImage fondo = null;

    Image[] imagenes = new Image[30];

    int contador = 0;
    //declaro variables para poder marcar la puntuacion de la partida
    public static Label label1 = new Label();
    int puntuacion = 0;
    //declaramos un boolean para marcar el fin del juego
    boolean gameOver = false;

    Clip sonidoFondo;
    //blucle de animacion del juego
    //en este caso es un hilo de ejecucion nuevo que se encarga de refrescar 
    //el contenido de la pantalla
    Timer temporizador = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO: codigo de la animacion
            bucleDelJuego();

        }
    });

    Marcianos miMarciano = new Marcianos(ANCHOPANTALLA);
    Nave miNave = new Nave();
    Disparo miDisparo = new Disparo();

    ArrayList<Disparo> listaDisparos = new ArrayList();
    ArrayList<Explosion> listaExplosiones = new ArrayList();
    //el array de dos dimensiones guarda la lista de los marcianos
    Marcianos[][] listaMarcianos = new Marcianos[filasMarcianos][columnasMarcianos];
    Image fondito;
    //direccion en el que se mueve el disparo
    boolean direccionMarciano = true;

    /**
     * Creates new form VentanaJuego
     */
    public VentanaJuego() {

        initComponents();
        try {
            fondito = ImageIO.read(getClass().getResource("/imagenes/fondoSpace.jpg"));
           
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("hay un error wacho");
        }
        Font font1;
        font1 = new Font("Courier New", Font.BOLD, 30);
        //puntuacion
        label1.setFont(font1);
        label1.setForeground(white);
        label1.setBackground(black);
        label1.setBounds(600, 0, 100, 30);
        label1.setText("Score:    ");
         jPanel1.add(label1);
         
        try {
            plantilla = ImageIO.read(getClass().getResource("/imagenes/invaders2.png"));
        } catch (IOException ex) {
        }
        //cargo las 30 imágenes del spritesheet en el array de bufferedimages
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                imagenes[i * 4 + j] = plantilla
                        .getSubimage(j * 64, i * 64, 64, 64)
                        .getScaledInstance(32, 32, Image.SCALE_SMOOTH);

            }
        }
        imagenes[20] = plantilla.getSubimage(0, 320, 66, 32); //sprite de la nave
        imagenes[21] = plantilla.getSubimage(66, 320, 64, 32);
        imagenes[23] = plantilla.getSubimage(255, 320, 32, 32);//explosion parteB
        imagenes[22] = plantilla.getSubimage(255, 289, 32, 32);//explosion parteA

        setSize(ANCHOPANTALLA, ALTOPANTALLA);
        jPanel1.setSize(ANCHOPANTALLA, ALTOPANTALLA);
        buffer = (BufferedImage) jPanel1.createImage(ANCHOPANTALLA, ALTOPANTALLA);//inicializo el buffer
        buffer.createGraphics();
        temporizador.start();//arranco el temporizador
        miNave.imagen = imagenes[21];
        miNave.posX = ANCHOPANTALLA / 2 - miNave.imagen.getWidth(this) / 2;
        miNave.posY = ALTOPANTALLA - 100;
        //creamos el array de marcianos
        for (int i = 0; i < filasMarcianos; i++) {
            for (int j = 0; j < columnasMarcianos; j++) {
                listaMarcianos[i][j] = new Marcianos(ANCHOPANTALLA);
                listaMarcianos[i][j].imagen1 = imagenes[2 * i];
                listaMarcianos[i][j].imagen2 = imagenes[2 * i + 1];
                listaMarcianos[i][j].posX = j * (15 + listaMarcianos[i][j].imagen1.getWidth(null));
                listaMarcianos[i][j].posY = i * (10 + listaMarcianos[i][j].imagen1.getHeight(null));
            }
        }
        miDisparo.posY = -2000;
        
    }

    public void sonidoFondo() {
        try {//siempre que hace la lectura con algo que hay en el disco, se ejecuta un try
            //catch,esto hace que proteja lo que se encuentra en el disco.

            sonidoFondo = AudioSystem.getClip();
            sonidoFondo.open(AudioSystem.getAudioInputStream(getClass().getResource("/sonidos/467497__enviromaniac2__pokemon-route-201-cheesy-mix (1).wav")));
//En caso de no poner IO se transforma en una exception generico con errores gerenicos
        } catch (Exception e) {
            System.out.println("Hoal");
        }
    }

    private void pintaMarcianos(Graphics2D _g2) {
        for (int i = 0; i < filasMarcianos; i++) {
            for (int j = 0; j < columnasMarcianos; j++) {
                listaMarcianos[i][j].mueve(direccionMarciano);
                if (listaMarcianos[i][j].posX >= ANCHOPANTALLA - listaMarcianos[i][j].imagen1.getWidth(null) || listaMarcianos[i][j].posX <= 0) {
                    direccionMarciano = !direccionMarciano;

                    //hago q todos los marcianos bajen a la fila de abajo
                    for (int k = 0; k < filasMarcianos; k++) {
                        for (int m = 0; m < columnasMarcianos; m++) {
                            listaMarcianos[k][m].posY += listaMarcianos[k][m].imagen1.getHeight(null);
                        }
                    }
                }
                if (contador < 50) {
                    _g2.drawImage(listaMarcianos[i][j].imagen1, listaMarcianos[i][j].posX, listaMarcianos[i][j].posY, null);
                } else if (contador < 100) {
                    _g2.drawImage(listaMarcianos[i][j].imagen2, listaMarcianos[i][j].posX, listaMarcianos[i][j].posY, null);
                } else {
                    contador = 0;
                }

            }

        }
    }

    private void pintaDisparos(Graphics2D g2) {
        //pinta todos los disparos
        Disparo disparoAux;
        for (int i = 0; i < listaDisparos.size(); i++) {
            disparoAux = listaDisparos.get(i);
            disparoAux.mueve();
            if (disparoAux.posY < 0) {
                listaDisparos.remove(i);
            } else {
                g2.drawImage(disparoAux.imagen, disparoAux.posX, disparoAux.posY, null);
            }
        }

    }

    private void pintaExplosiones(Graphics2D g2) {
        //pinta todas las explosiones
        Explosion explosionAux;
        for (int i = 0; i < listaExplosiones.size(); i++) {
            explosionAux = listaExplosiones.get(i);
            explosionAux.tiempoDeVida--;
            if (explosionAux.tiempoDeVida > 25) {
                g2.drawImage(explosionAux.imagen1, explosionAux.posX, explosionAux.posY, null);
            } else {
                g2.drawImage(explosionAux.imagen2, explosionAux.posX, explosionAux.posY, null);

            }
            //si el tiempo de vida de la explosion es menor o igual a cero la elimino
            if (explosionAux.tiempoDeVida <= 0) {
                listaExplosiones.remove(i);
            }
        }

    }

    private void bucleDelJuego() {
        //este metodo gobierna el redibujado de los objetos en el jpanel1

        //primero borro todo lo que hay en el buffer
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();
        if(!gameOver){
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, ANCHOPANTALLA, ALTOPANTALLA);
        g2.drawImage(fondo, 0, 0, null);
        ////////////////////////////////////////////////
        contador++;
        pintaMarcianos(g2);

        //dibujo la nave
        g2.drawImage(miNave.imagen, miNave.posX, miNave.posY, null);
        pintaDisparos(g2);
        pintaExplosiones(g2);
        miNave.mueve();
        chequeaColision();
        //////////////////////////////////////////////////
        //dibujo de golpe todo el buffer sobre el jpanel1
        g2 = (Graphics2D) jPanel1.getGraphics();
        g2.drawImage(buffer, 0, 0, null);
        }
    }
    //cheque si un disparo y un marciano colisiona

    private void chequeaColision() {
        Rectangle2D.Double rectanguloMarciano = new Rectangle2D.Double();
        Rectangle2D.Double rectanguloDisparo = new Rectangle2D.Double();

        for (int k = 0; k < listaDisparos.size(); k++) {
            //calculo el rectangulo que contiene al disparo correspondiente
            rectanguloDisparo.setFrame(listaDisparos.get(k).posX,
                    listaDisparos.get(k).posY,
                    listaDisparos.get(k).imagen.getWidth(null),
                    listaDisparos.get(k).imagen.getHeight(null));

            for (int i = 0; i < filasMarcianos; i++) {
                for (int j = 0; j < columnasMarcianos; j++) {
                    //calculo el rectángulo corresponmdiente al marciano que estoy comprobando
                    rectanguloMarciano.setFrame(listaMarcianos[i][j].posX,
                            listaMarcianos[i][j].posY,
                            listaMarcianos[i][j].imagen1.getWidth(null),
                            listaMarcianos[i][j].imagen1.getHeight(null)
                    );
                    if (rectanguloDisparo.intersects(rectanguloMarciano)) {
                        //si entra aquí es porque han chocado un marciano y el disparo
                        Explosion e = new Explosion();
                        e.posX = listaMarcianos[i][j].posX;
                        e.posY = listaMarcianos[i][j].posY;
                        e.imagen1 = imagenes[23];
                        e.imagen2 = imagenes[22];
                        listaExplosiones.add(e);

                        e.sonidoExplosion.start();//suena el sonido

                        listaMarcianos[i][j].posY = 2000;
                        listaDisparos.remove(k);
                          puntuacion = puntuacion + 50;
                        label1.setText("" + puntuacion);
                    }
                }
            }
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 848, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 366, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                miNave.setPulsadoIzq(true);
                break;
            case KeyEvent.VK_RIGHT:
                miNave.setPulsadoDcha(true);
                break;
            case KeyEvent.VK_SPACE:
                Disparo d = new Disparo();
                d.posicionDisparo(miNave);
                //agregamos el disparo a la lista de disparos
                listaDisparos.add(d);
                d.sonidoDisparo.start();
                break;
        }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                miNave.setPulsadoIzq(false);
                break;
            case KeyEvent.VK_RIGHT:
                miNave.setPulsadoDcha(false);
                break;
        }
    }//GEN-LAST:event_formKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaJuego().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
