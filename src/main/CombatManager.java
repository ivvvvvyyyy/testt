package main;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class CombatManager {
    GamePanel gp;
    Random rand = new Random();

    public ArrayList<FallingLetter> letters = new ArrayList<>();

    private int spawnTimer = 0;
    private int spawnInterval = 90;

    public int dangerLineY;
    public Entity.NPC targetNPC = null;

    private int fallSpeed = 3;

    public boolean playerHit = false;
    public boolean enemyHit = false;
    private int hitFlashTimer = 0;

    private static final char[] VALID_KEYS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public CombatManager(GamePanel gp) {
        this.gp = gp;
        dangerLineY = gp.screenHeight - 120;
    }

    public void startCombat(Entity.NPC npc) {
        targetNPC = npc;
        letters.clear();
        spawnTimer = 0;
        playerHit = false;
        enemyHit = false;
        npc.currentHearts = npc.maxHearts;
        gp.player.currentHearts = gp.player.maxHearts;
        gp.gameState = gp.combatState;
    }

    public void update() {
        if (targetNPC == null) return;

        if (hitFlashTimer > 0) {
            hitFlashTimer--;
            if (hitFlashTimer == 0) {
                playerHit = false;
                enemyHit = false;
            }
        }

        spawnTimer++;
        if (spawnTimer >= spawnInterval) {
            spawnLetter();
            spawnTimer = 0;
        }

        ArrayList<FallingLetter> toRemove = new ArrayList<>();
        for (FallingLetter fl : letters) {
            fl.y += fallSpeed;
            if (fl.y >= dangerLineY) {
                toRemove.add(fl);
                hitPlayer();
            }
        }
        letters.removeAll(toRemove);

        if (gp.player.currentHearts <= 0) endCombat(false);
        if (targetNPC.currentHearts <= 0)  endCombat(true);
    }

    private void spawnLetter() {
        char c = VALID_KEYS[rand.nextInt(VALID_KEYS.length)];
        int x = 80 + rand.nextInt(gp.screenWidth - 160);
        letters.add(new FallingLetter(c, x, 60));
    }

    public void handleKeyPress(char key) {
        char upper = Character.toUpperCase(key);
        FallingLetter match = null;
        int lowestY = -1;

        for (FallingLetter fl : letters) {
            if (fl.letter == upper && fl.y > lowestY) {
                lowestY = fl.y;
                match = fl;
            }
        }

        if (match != null) {
            letters.remove(match);
            hitEnemy();
        }
    }

    private void hitPlayer() {
        gp.player.currentHearts--;
        playerHit = true;
        hitFlashTimer = 30;
    }

    private void hitEnemy() {
        if (targetNPC != null) {
            targetNPC.currentHearts--;
            enemyHit = true;
            hitFlashTimer = 30;
        }
    }

    private void endCombat(boolean playerWon) {
        letters.clear();
        if (playerWon) {
            for (int i = 0; i < gp.npcs.length; i++) {
                // Fixed: Changed gp.NPC[i] to gp.npcs[i]
                if (gp.npcs[i] == targetNPC) {
                    gp.npcs[i] = null;
                    break;
                }
            }
        }
        targetNPC = null;
        gp.gameState = gp.playState;
    }

    public void draw(Graphics2D g2) {
        if (targetNPC == null) return;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, dangerLineY, gp.screenWidth, dangerLineY);

        g2.setFont(gp.dtmSans.deriveFont(Font.BOLD, 16F));
        g2.setColor(Color.RED);
        g2.drawString("DANGER", 10, dangerLineY - 6);

        g2.setFont(gp.dtmSans.deriveFont(Font.BOLD, 40F));
        for (FallingLetter fl : letters) {
            float danger = Math.min(1f, (float) fl.y / dangerLineY);
            Color letterColor = new Color(
                    (int)(255 * danger),
                    (int)(255 * (1 - danger)),
                    50
            );
            g2.setColor(letterColor);
            g2.drawString(String.valueOf(fl.letter), fl.x, fl.y);
        }

        drawHearts(g2);

        if (playerHit) {
            g2.setColor(new Color(255, 0, 0, 60));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }
        if (enemyHit) {
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }

        g2.setFont(gp.dtmSans.deriveFont(Font.PLAIN, 18F));
        g2.setColor(Color.WHITE);
        g2.drawString("Press the falling letters before they cross the red line!",
                20, gp.screenHeight - 20);
    }

    private void drawHearts(Graphics2D g2) {
        int heartSize = 32;
        int padding = 8;

        g2.setFont(gp.dtmSans.deriveFont(Font.BOLD, 18F));
        g2.setColor(Color.WHITE);
        g2.drawString("YOU", 20, dangerLineY + 30);
        for (int i = 0; i < gp.player.maxHearts; i++) {
            drawHeart(g2, 20 + i * (heartSize + padding), dangerLineY + 40, heartSize, i < gp.player.currentHearts);
        }

        String npcName = targetNPC.name.toUpperCase();
        g2.setFont(gp.dtmSans.deriveFont(Font.BOLD, 18F));
        g2.setColor(Color.WHITE);
        int nameX = gp.screenWidth - 20 - (targetNPC.maxHearts * (heartSize + padding));
        g2.drawString(npcName, nameX, dangerLineY + 30);
        for (int i = 0; i < targetNPC.maxHearts; i++) {
            drawHeart(g2, nameX + i * (heartSize + padding), dangerLineY + 40, heartSize, i < targetNPC.currentHearts);
        }
    }

    private void drawHeart(Graphics2D g2, int x, int y, int size, boolean filled) {
        g2.setColor(filled ? Color.RED : new Color(80, 80, 80));
        int half = size / 2;
        int quarter = size / 4;
        g2.fillOval(x, y, half, half);
        g2.fillOval(x + quarter, y, half, half);
        int[] xPoints = {x, x + size, x + half};
        int[] yPoints = {y + quarter, y + quarter, y + size};
        g2.fillPolygon(xPoints, yPoints, 3);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.drawOval(x, y, half, half);
        g2.drawOval(x + quarter, y, half, half);
        g2.drawPolygon(xPoints, yPoints, 3);
    }

    public static class FallingLetter {
        public char letter;
        public int x, y;
        public FallingLetter(char letter, int x, int y) {
            this.letter = letter;
            this.x = x;
            this.y = y;
        }
    }
}