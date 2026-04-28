package main;

import Entity.NPC;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class CombatManager {
    GamePanel gp;
    NPC currentEnemy;
    public int playerHealth = 100;
    public int enemyHealth = 100;

    // CIRCLE MECHANICS
    private class TargetLetter {
        char character;
        int x, y;
        int maxLife;
        int currentLife;

        public TargetLetter(char c, int x, int y, int lifeTime) {
            this.character = c;
            this.x = x;
            this.y = y;
            this.maxLife = lifeTime;
            this.currentLife = lifeTime;
        }
    }

    private ArrayList<TargetLetter> targets = new ArrayList<>();
    private Random random = new Random();
    private int spawnCounter = 0;
    private int spawnRate = 60;
    private int letterLifeTime = 120;

    public CombatManager(GamePanel gp) {
        this.gp = gp;
    }

    public void startCombat(NPC npc) {
        gp.stopMusic();
        gp.playMusic(0);
        this.currentEnemy = npc;
        this.playerHealth = 100;
        this.enemyHealth = 100;

        this.targets.clear();
        this.spawnCounter = 0;
        gp.gameState = gp.combatState;
    }

    public void update() {
        //  Check Win / Loss
        if (enemyHealth <= 0) {
            gp.stopMusic();
            gp.playMusic(1);
            gp.gameState = gp.winDialogueState;
            gp.talkingNPC = currentEnemy;

            //dialogue pages
            currentEnemy.dialoguePages = new String[] {
                    "RAAAAAAGGGHHHHH!!!! ... I... I am finally cold.",
                    "Even the hottest fire eventually turns to ash. Rest now, Sinner."
            };

            gp.winSpeakers = new String[] { currentEnemy.name, "Hunter" };

            gp.currentSpeaker = gp.winSpeakers[0];
            gp.fullText = currentEnemy.dialoguePages[0];
            gp.displayedText = "";
            gp.charIndex = 0;
            gp.dialoguePageIndex = 0;
            enemyHealth = 1;
            return;
        } else if (playerHealth <= 0) {
            gp.stopMusic();
            gp.gameState = gp.gameOverState;
            return;
        }

        //  Spawn New Letters
        spawnCounter++;
        if (spawnCounter >= spawnRate) {
            char randomChar = (char) ('A' + random.nextInt(26));

            int radius = 30;
            int minX = radius * 2;
            int maxX = gp.screenWidth - (radius * 2);
            int minY = radius * 2;
            int maxY = gp.screenHeight - (gp.tileSize * 7) - radius;

            int randomX = minX + random.nextInt(maxX - minX);
            int randomY = minY + random.nextInt(maxY - minY);

            targets.add(new TargetLetter(randomChar, randomX, randomY, letterLifeTime));
            spawnCounter = 0;
        }

        // Update letter lifetimes
        Iterator<TargetLetter> iterator = targets.iterator();
        while (iterator.hasNext()) {
            TargetLetter t = iterator.next();
            t.currentLife--;

            if (t.currentLife <= 0) {
                playerHealth -= 10;
                iterator.remove();
            }
        }
    }

    public void handleKeyPress(char c) {
        char pressedChar = Character.toUpperCase(c);

        Iterator<TargetLetter> iterator = targets.iterator();
        while (iterator.hasNext()) {
            TargetLetter t = iterator.next();
            if (t.character == pressedChar) {
                enemyHealth -= 10;
                iterator.remove();
                break;
            }
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        if (currentEnemy != null && currentEnemy.up1 != null) {
            int width = gp.tileSize * 5;
            int height = gp.tileSize * 5;
            int x = gp.screenWidth / 2 - width / 2;
            int y = gp.tileSize;
            g2.drawImage(currentEnemy.up1, x, y, width, height, null);
        }

        g2.setFont(gp.dtmSans.deriveFont(Font.BOLD, 32F));
        for (TargetLetter t : targets) {
            int radius = 25;

            g2.setColor(Color.RED);
            int timerRadius = (int) (radius * 1.5 * ((double) t.currentLife / t.maxLife));
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(t.x - timerRadius, t.y - timerRadius, timerRadius * 2, timerRadius * 2);

            g2.setColor(new Color(20, 20, 20, 200));
            g2.fillOval(t.x - radius, t.y - radius, radius * 2, radius * 2);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(t.x - radius, t.y - radius, radius * 2, radius * 2);

            FontMetrics fm = g2.getFontMetrics();
            String letter = String.valueOf(t.character);
            int textX = t.x - (fm.stringWidth(letter) / 2);
            int textY = t.y + (fm.getAscent() - fm.getDescent()) / 2 - 2;

            g2.setColor(Color.WHITE);
            g2.drawString(letter, textX, textY);
        }

        int boxX = gp.tileSize * 2;
        int boxY = gp.screenHeight - (gp.tileSize * 6);
        int boxW = gp.screenWidth - (gp.tileSize * 4);
        int boxH = gp.tileSize * 5;

        g2.setColor(Color.BLACK);
        g2.fillRect(boxX, boxY, boxW, boxH);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(boxX, boxY, boxW, boxH);

        g2.setFont(gp.dtmSans.deriveFont(Font.BOLD, 22F));

        g2.drawString("HUNTER HP", boxX + 30, boxY + 45);
        g2.setColor(Color.RED);
        g2.fillRect(boxX + 30, boxY + 60, 200, 25);
        g2.setColor(Color.GREEN);
        g2.fillRect(boxX + 30, boxY + 60, (int)(200 * (Math.max(0, playerHealth)/100.0)), 25);

        g2.setColor(Color.WHITE);
        String enemyName = currentEnemy.name.toUpperCase() + " HP";
        g2.drawString(enemyName, boxX + boxW - 230, boxY + 45);
        g2.setColor(Color.RED);
        g2.fillRect(boxX + boxW - 230, boxY + 60, 200, 25);
        g2.setColor(Color.YELLOW);
        g2.fillRect(boxX + boxW - 230, boxY + 60, (int)(200 * (Math.max(0, enemyHealth)/100.0)), 25);

        g2.setColor(Color.WHITE);
        g2.setFont(gp.dtmSans.deriveFont(Font.ITALIC, 20F));
        String prompt = "PRESS THE LETTERS BEFORE THE RED RING CLOSES!";
        g2.drawString(prompt, gp.getXforCenteredText(prompt, g2), boxY + boxH - 20);
    }
}