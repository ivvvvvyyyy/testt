package main;

import Entity.NPC;
import Entity.Player;
import tile.TileManger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {

    final int originalTileSize = 16;
    final int scale = 2;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 30;
    public final int maxScreenRow = 24;
    public final int screenWidth  = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    public boolean screenShake = false;
    public final int maxWorldCol = 70;
    public final int maxWorldRow = 45;
    public int gameState;
    public final int titleState     = 0;
    public final int playState      = 1;
    public final int storyState     = 2;
    public final int dialogueState  = 3;
    public final int combatState    = 4;
    public final int winDialogueState = 5;
    public final int gameOverState  = 6;

    public int commandNum = 0;

    // Story
    public String[] storyPages = {
            "My great-great-great-great grandma, Pandora",
            "Had one job, keep the lid closed.",
            "She failed...",
            "She let the darkness breathe, and for centuries, humanity has been suffocating on the fumes.",
            "The Sins didn't just vanish into the wind",
            "They grew. They evolved. They became the architects of our misery, turning the world into a playground for their hunger.",
            "Grief, Rage, and Vanity aren't just feelings anymore ... they're the monsters under your bed ",
            "And the shadows in the streets.",
            "The Gods are gone, but their garbage remained",
            "For generations, my bloodline has been haunted by the echo of that empty box.",
            "We don't get to be heroes. We don't get to be legends. We are just the cosmic janitors of a broken world",
            "The world is drowning in a mess I didn't make, but I'm the only one left with the bucket",
            "It's time to hunt."


    };
    public int storyPageIndex = 0;
    public String fullText      = "";
    public String displayedText = "";
    public int charIndex   = 0;
    public int textCounter = 0;
    public final int TEXT_SPEED = 2;

    // Dialogue System
    public int dialoguePageIndex = 0;
    public NPC talkingNPC = null;
    public String currentSpeaker = "";
    public String[] winSpeakers;

    // Interaction
    public boolean interactPressed = false;
    private Random random = new Random();

    public Font dtmSans;
    int FPS = 60;

    public KeyButtons keyB = new KeyButtons(this);
    Thread gameThread;
    public TileManger tileM = new TileManger(this);
    public Player player = new Player(this, keyB);
    public SoundTracks music = new SoundTracks();
    public SoundTracks se = new SoundTracks(); // Sound Effects
    public CollisionChecker collisionChecker = new CollisionChecker(this);
    public CombatManager combatManager;

    public NPC[] npcs = new NPC[10];

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyB);

        loadFont();
        setupGame();
        combatManager = new CombatManager(this);
        gameState = titleState;

        playMusic(1);
    }

    public void loadFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/font/DTM-Sans.otf");
            if (is != null) {
                dtmSans = Font.createFont(Font.TRUETYPE_FONT, is);
            } else {
                dtmSans = new Font("Arial", Font.BOLD, 24);
            }
        } catch (FontFormatException | IOException e) {
            dtmSans = new Font("Arial", Font.BOLD, 24);
        }
    }

    public void setupGame() {
        npcs[0] = new NPC(this, "Crow", tileSize * 30, tileSize * 18,
                new String[]{
                        "Ah, a traveler. You look lost.",
                        "You will find sins lying behind closed doors.",
                        "May luck be with you"
                }, false);
    }

    public void switchMap(String mapFilePath, int spawnCol, int spawnRow) {
        tileM.loadMap(mapFilePath);

        player.worldX = tileSize * spawnCol;
        player.worldY = tileSize * spawnRow;

        for (int i = 0; i < npcs.length; i++) {
            npcs[i] = null;
        }

        npcs[0] = new NPC(this, "Anger", tileSize * 35, tileSize * 20,
                new String[]{
                        "I CANNOT ESCAPE MADNESS.",
                        "YOU CANNOT ESCAPE MADNESS",
                        "I SHALL BURN THIS PLACE TO THE GROUND"
                }, true);
    }

    public void startGameThread() {
        this.requestFocus();
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
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
            checkNPCInteraction();
        } else if (gameState == storyState || gameState == dialogueState || gameState == winDialogueState) {
            updateTypewriter();
        } else if (gameState == combatState) {
            combatManager.update();
        }
    }

    public void updateTypewriter() {
        if (charIndex < fullText.length()) {
            textCounter++;
            if (textCounter >= TEXT_SPEED) {
                displayedText += fullText.charAt(charIndex);
                charIndex++;
                textCounter = 0;
            }
        }
    }

    public void checkNPCInteraction() {
        for (NPC npc : npcs) {
            if (npc == null) continue;
            int dx = Math.abs(player.worldX - npc.worldX);
            int dy = Math.abs(player.worldY - npc.worldY);
            if (dx <= tileSize && dy <= tileSize && interactPressed) {
                interactPressed = false;
                talkingNPC = npc;
                dialoguePageIndex = 0;
                fullText = npc.dialoguePages[0];
                currentSpeaker = npc.name;
                displayedText = "";
                charIndex = 0;
                gameState = dialogueState;
                return;
            }
        }
        interactPressed = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        boolean isAngerSpeaking = (gameState == dialogueState || gameState == winDialogueState)
                && "Anger".equals(currentSpeaker)
                && charIndex < fullText.length();

        int offsetX = 0;
        int offsetY = 0;

        if (screenShake || isAngerSpeaking) {
            offsetX = random.nextInt(9) - 4;
            offsetY = random.nextInt(9) - 4;
            g2.translate(offsetX, offsetY);
        }

        if (gameState == titleState) {
            drawTitleScreen(g2);
        } else {
            tileM.draw(g2);
            for (NPC npc : npcs) { if (npc != null) npc.draw(g2); }
            player.draw(g2);

            if (gameState == storyState) drawStoryScreen(g2);
            if (gameState == dialogueState || gameState == winDialogueState) drawDialogueBox(g2);
            if (gameState == combatState) combatManager.draw(g2);

            if (gameState == gameOverState) drawGameOverScreen(g2);
        }

        if (screenShake || isAngerSpeaking) {
            g2.translate(-offsetX, -offsetY);
        }

        g2.dispose();
    }

    public void drawTitleScreen(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, screenHeight);
        g2.setFont(dtmSans.deriveFont(Font.BOLD, 80F));
        g2.setColor(Color.WHITE);
        String text = "SINS HUNTER";
        g2.drawString(text, getXforCenteredText(text, g2), tileSize * 5);

        g2.setFont(dtmSans.deriveFont(Font.PLAIN, 32F));
        int btnWidth  = tileSize * 16;
        int btnHeight = tileSize * 2;
        int btnX = screenWidth / 2 - btnWidth / 2;

        drawButton(g2, "NEW GAME", btnX, tileSize * 10, btnWidth, btnHeight, 0);
        drawButton(g2, "EXIT",     btnX, tileSize * 13, btnWidth, btnHeight, 1);
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
        g2.setColor(Color.WHITE);
        g2.drawString(text, getXforCenteredText(text, g2), y + (tileSize * 1.4f));
    }

    public void drawStoryScreen(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, screenHeight);

        int boxW = screenWidth - (tileSize * 4);
        int boxH = tileSize * 8;
        int boxX = tileSize * 2;
        int boxY = (screenHeight / 2) - (boxH / 2);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4));
        g2.drawRect(boxX, boxY, boxW, boxH);

        g2.setFont(dtmSans.deriveFont(Font.PLAIN, 34F));
        drawCenteredWrappedText(g2, displayedText, screenWidth / 2, screenHeight / 2, boxW - (tileSize * 2), 48);
    }

    public void drawDialogueBox(Graphics2D g2) {
        int boxX = tileSize;
        int boxY = screenHeight - tileSize * 6;
        int boxW = screenWidth  - tileSize * 2;
        int boxH = tileSize * 5;

        g2.setColor(new Color(20, 20, 20, 220));
        g2.fillRect(boxX, boxY, boxW, boxH);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(boxX, boxY, boxW, boxH);

        String tagName = currentSpeaker;
        if (tagName == null || tagName.isEmpty()) tagName = "Unknown";

        int tagW = tileSize * 4;
        int tagH = tileSize;
        int tagY = boxY - tagH;
        int tagX = tagName.equalsIgnoreCase("Hunter") ? boxX : boxX + boxW - tagW;

        g2.setColor(new Color(20, 20, 20, 220));
        g2.fillRect(tagX, tagY, tagW, tagH);
        g2.setColor(Color.WHITE);
        g2.drawRect(tagX, tagY, tagW, tagH);

        g2.setFont(dtmSans.deriveFont(Font.BOLD, 18F));
        int nameX = tagX + tagW / 2 - (int) g2.getFontMetrics().getStringBounds(tagName, g2).getWidth() / 2;
        g2.drawString(tagName, nameX, tagY + tagH - 8);

        g2.setFont(dtmSans.deriveFont(Font.PLAIN, 24F));
        drawWrappedText(g2, displayedText, boxX + tileSize / 2, boxY + tileSize, boxW - tileSize, 34);
    }

    public void drawGameOverScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setFont(dtmSans.deriveFont(Font.BOLD, 90F));
        String text = "GAME OVER";
        g2.setColor(Color.BLACK);
        g2.drawString(text, getXforCenteredText(text, g2), tileSize * 8);
        g2.setColor(new Color(200, 0, 0));
        g2.drawString(text, getXforCenteredText(text, g2) - 4, tileSize * 8 - 4);

        g2.setFont(dtmSans.deriveFont(Font.PLAIN, 32F));
        int btnWidth  = tileSize * 12;
        int btnHeight = tileSize * 2;
        int btnX = screenWidth / 2 - btnWidth / 2;

        commandNum = 0;
        drawButton(g2, "TRY AGAIN", btnX, tileSize * 14, btnWidth, btnHeight, 0);
    }

    public void drawCenteredWrappedText(Graphics2D g2, String text, int centerX, int startY, int maxWidth, int lineHeight) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        List<String> lines = new ArrayList<>();

        for (String word : words) {
            String test = line + (line.length() > 0 ? " " : "") + word;
            if ((int) g2.getFontMetrics().getStringBounds(test, g2).getWidth() > maxWidth) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                if (line.length() > 0) line.append(" ");
                line.append(word);
            }
        }
        if (line.length() > 0) lines.add(line.toString());

        int currentY = startY - ((lines.size() * lineHeight) / 2);

        for (String l : lines) {
            int x = centerX - (int) g2.getFontMetrics().getStringBounds(l, g2).getWidth() / 2;
            g2.drawString(l, x, currentY);
            currentY += lineHeight;
        }
    }

    public void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int currentY = y;
        for (String word : words) {
            String test = line + (line.length() > 0 ? " " : "") + word;
            if ((int) g2.getFontMetrics().getStringBounds(test, g2).getWidth() > maxWidth) {
                g2.drawString(line.toString(), x, currentY);
                currentY += lineHeight;
                line = new StringBuilder(word);
            } else {
                if (line.length() > 0) line.append(" ");
                line.append(word);
            }
        }
        if (line.length() > 0) g2.drawString(line.toString(), x, currentY);
    }

    public int getXforCenteredText(String text, Graphics2D g2) {
        return screenWidth / 2 - (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth() / 2;
    }
    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }
}