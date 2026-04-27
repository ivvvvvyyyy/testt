package main;

import Entity.NPC;
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
    public final int screenWidth  = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    public final int maxWorldCol = 70;
    public final int maxWorldRow = 45;

    // Game states
    public int gameState;
    public final int titleState    = 0;
    public final int playState     = 1;
    public final int storyState    = 2;
    public final int dialogueState = 3;
    public final int combatState   = 4;

    public int subState  = 0;
    public int commandNum = 0;

    // Story / typewriter
    public String[] storyPages = {
            "In a world consumed by sin, one hunter stands against the darkness...",
            "Seven evils roam the land. You must face them all.",
            "Your journey begins now."
    };
    public int storyPageIndex = 0;
    public String fullText      = "";
    public String displayedText = "";
    public int charIndex   = 0;
    public int textCounter = 0;
    public final int TEXT_SPEED = 2;

    // Dialogue
    public int dialoguePageIndex = 0;
    public NPC talkingNPC = null;

    // Interaction
    public boolean interactPressed = false;

    public Font dtmSans;
    int FPS = 60;

    public KeyButtons keyB = new KeyButtons(this);
    Thread gameThread;
    public TileManger tileM = new TileManger(this);
    public Player player = new Player(this, keyB);
    public CollisionChecker collisionChecker = new CollisionChecker(this);
    public CombatManager combatManager;

    // Fixed: Now utilizing the imported NPC class directly
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

        // FIXED: Start on title screen instead of story screen
        gameState   = titleState;

        fullText    = storyPages[0];
        displayedText = "";
        charIndex   = 0;
    }

    public void loadFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/font/DTM-Sans.otf");
            dtmSans = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            dtmSans = new Font("Arial", Font.BOLD, 24);
        }
    }

    public void setupGame() {
        npcs[0] = new NPC(this, "Elder",
                tileSize * 30, tileSize * 18,
                new String[]{
                        "Ah, a traveler. You look lost.",
                        "The first sin lies to the east. Be careful."
                }, false);

        npcs[1] = new NPC(this, "Guard",
                tileSize * 38, tileSize * 22,
                new String[]{
                        "Halt! Only hunters may pass."
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
            checkNPCInteraction();
        }
        if (gameState == storyState)    updateTypewriter();
        if (gameState == dialogueState) updateTypewriter();
        if (gameState == combatState)   combatManager.update();
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
                if (npc.hostile) {
                    combatManager.startCombat(npc);
                } else {
                    talkingNPC = npc;
                    dialoguePageIndex = 0;
                    fullText = npc.dialoguePages[0];
                    displayedText = "";
                    charIndex = 0;
                    gameState = dialogueState;
                }
            }
        }
        interactPressed = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == titleState) {
            drawTitleScreen(g2);
        } else if (gameState == storyState) {
            drawStoryScreen(g2);
        } else if (gameState == playState) {
            tileM.draw(g2);
            for (NPC npc : npcs) { if (npc != null) npc.draw(g2); }
            player.draw(g2);
        } else if (gameState == dialogueState) {
            tileM.draw(g2);
            for (NPC npc : npcs) { if (npc != null) npc.draw(g2); }
            player.draw(g2);
            drawDialogueBox(g2);
        } else if (gameState == combatState) {
            tileM.draw(g2);
            player.draw(g2);
            combatManager.draw(g2);
        }

        g2.dispose();
    }

    public void drawTitleScreen(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setFont(dtmSans.deriveFont(Font.BOLD, 80F));
        String text = "SINS HUNTER";
        g2.setColor(Color.WHITE);
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

        int boxX = tileSize * 2;
        int boxY = tileSize * 3;
        int boxW = screenWidth  - tileSize * 4;
        int boxH = screenHeight - tileSize * 6;

        g2.setColor(new Color(20, 20, 20, 230));
        g2.fillRect(boxX, boxY, boxW, boxH);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4));
        g2.drawRect(boxX, boxY, boxW, boxH);

        g2.setFont(dtmSans.deriveFont(Font.PLAIN, 26F));
        g2.setColor(Color.WHITE);
        drawWrappedText(g2, displayedText, boxX + tileSize, boxY + tileSize + 10, boxW - tileSize * 2, 36);

        if (charIndex >= fullText.length()) {
            String prompt = storyPageIndex < storyPages.length - 1 ? "[ ENTER: Next ]" : "[ ENTER: Begin ]";
            g2.setFont(dtmSans.deriveFont(Font.PLAIN, 20F));
            g2.drawString(prompt, getXforCenteredText(prompt, g2), boxY + boxH - 20);
        }
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

        String tagName = (talkingNPC != null) ? talkingNPC.name : "You";
        int tagW = tileSize * 4;
        int tagH = tileSize;
        int tagY = boxY - tagH;
        int tagX = (talkingNPC != null) ? boxX + boxW - tagW : boxX;

        g2.setColor(new Color(20, 20, 20, 220));
        g2.fillRect(tagX, tagY, tagW, tagH);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(tagX, tagY, tagW, tagH);

        g2.setFont(dtmSans.deriveFont(Font.BOLD, 18F));
        int nameX = tagX + tagW / 2 - (int) g2.getFontMetrics().getStringBounds(tagName, g2).getWidth() / 2;
        g2.drawString(tagName, nameX, tagY + tagH - 8);

        g2.setFont(dtmSans.deriveFont(Font.PLAIN, 24F));
        drawWrappedText(g2, displayedText, boxX + tileSize / 2, boxY + tileSize, boxW - tileSize, 34);

        if (charIndex >= fullText.length() && talkingNPC != null) {
            String prompt = dialoguePageIndex < talkingNPC.dialoguePages.length - 1
                    ? "[ ENTER: Next ]" : "[ ENTER: Close ]";
            g2.setFont(dtmSans.deriveFont(Font.PLAIN, 18F));
            g2.drawString(prompt, boxX + boxW - tileSize * 4, boxY + boxH - 10);
        }
    }

    public void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int currentY = y;

        for (String word : words) {
            String test = line + (line.length() > 0 ? " " : "") + word;
            int testWidth = (int) g2.getFontMetrics().getStringBounds(test, g2).getWidth();
            if (testWidth > maxWidth) {
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
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return screenWidth / 2 - length / 2;
    }
}