package main;

import Entity.Player;
import tile.TileManger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class GamePanel extends JPanel implements Runnable {

    final int originalTileSize = 16;
    final int scale = 2;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 30;
    public final int maxScreenRow = 24;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;


    public final int maxWorldCol = 70;
    public final int maxWorldRow = 45;


    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public int subState = 0;
    public int commandNum = 0;


    public Font dtmSans;

    int FPS = 60;


    public KeyButtons keyB = new KeyButtons(this);
    Thread gameThread;
    public TileManger tileM = new TileManger(this);
    public Player player = new Player(this, keyB);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyB);

        loadFont();
        gameState = titleState;
    }

    public void loadFont() {
        try {

            InputStream is = getClass().getResourceAsStream("/font/DTM-Sans.otf");
            dtmSans = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            dtmSans = new Font("Arial", Font.BOLD, 24); // Fallback
        }
    }

    public void startGameThread() {
        this.requestFocus();
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        if (gameState == playState) {
            player.update();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (gameState == titleState) {
            drawTitleScreen(g2);
        } else if (gameState == playState) {
            tileM.draw(g2);
            player.draw(g2);
        }
        g2.dispose();
    }

    public void drawTitleScreen(Graphics2D g2) {
        // BACKGROUND
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, screenHeight);


        g2.setFont(dtmSans.deriveFont(Font.BOLD, 80F));
        String text = "SINS HUNTER";
        g2.setColor(Color.BLACK);
        g2.drawString(text, getXforCenteredText(text, g2), tileSize * 5);


        g2.setFont(dtmSans.deriveFont(Font.PLAIN, 32F));
        int btnWidth = tileSize * 16;
        int btnHeight = tileSize * 2;
        int btnX = screenWidth / 2 - btnWidth / 2;

        if (subState == 0) {

            drawButton(g2, "NEW GAME", btnX, tileSize * 10, btnWidth, btnHeight, 0);
            drawButton(g2, "LANGUAGE", btnX, tileSize * 13, btnWidth, btnHeight, 1);
        } else if (subState == 1) {

            drawButton(g2, "ENGLISH", btnX, tileSize * 10, btnWidth, btnHeight, 0);
            drawButton(g2, "OTHER (COMING SOON)", btnX, tileSize * 13, btnWidth, btnHeight, 1);

            g2.setFont(dtmSans.deriveFont(20F));
            g2.drawString("Press ESC to return", getXforCenteredText("Press ESC to return", g2), tileSize * 17);
        }
    }

    public void drawButton(Graphics2D g2, String text, int x, int y, int width, int height, int index) {

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(x, y, width, height);

        if (commandNum == index) {
            g2.setColor(new Color(255, 255, 255, 100));
            g2.fillRect(x, y, width, height);
            g2.setColor(Color.WHITE);

            g2.drawString(">", x - tileSize, y + (tileSize * 1.4f));
        }


        g2.drawString(text, getXforCenteredText(text, g2), y + (tileSize * 1.4f));
    }

    public int getXforCenteredText(String text, Graphics2D g2) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return screenWidth / 2 - length / 2;
    }
}




