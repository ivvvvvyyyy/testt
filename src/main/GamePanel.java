package main;

import Entity.Player;
import tile.TileManger;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 32;
    public final int maxScreenRow = 18;
    public final int screenWidth;
    public final int screenHeight;
    int FPS = 60;

    KeyButtons keyB = new KeyButtons();
    Thread gameThread;
    Player player = new Player(this,keyB);
    TileManger tileM = new TileManger(this);

    public GamePanel(){

        screenWidth = tileSize * maxScreenCol;
        screenHeight = tileSize * maxScreenRow;

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyB);
        this.setFocusable(true);



    }
    public void startGameThread(){
        this.requestFocus();
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void run() {

        double drawInterval = 1000000000 /FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {


            currentTime= System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();

                repaint();
                delta--;
            }
        }
    }
    public void update(){
        player.update();

    }
    public void paintComponent(Graphics g){

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        tileM.draw(g2);
        player.draw(g2);
        g2.dispose();
    }

}
