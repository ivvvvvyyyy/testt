package Entity;

import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NPC extends Entity {
    GamePanel gp;
    public String name;
    public String[] dialoguePages;
    public boolean hostile;

    public NPC(GamePanel gp, String name, int worldX, int worldY, String[] dialoguePages, boolean hostile) {
        this.gp = gp;
        this.name = name;
        this.worldX = worldX;
        this.worldY = worldY;
        this.dialoguePages = dialoguePages;
        this.hostile = hostile;
        this.direction = "down";
        solidArea = new Rectangle(4, 8, 24, 22);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        // Disabled image loading so it won't crash
        getImage();
    }

    public void getImage() {
        // I have commented this out so Java stops looking for the missing images.
        // You can uncomment this later once you have your sprites ready!

        /*
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/npc/npc_down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/npc/npc_down2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Check if the NPC is on the screen before drawing
        if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
                screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {

            // Draw the placeholder shape
            // I set it to White, but made hostile NPCs Red so you can tell them apart!
            if (hostile) {
                g2.setColor(Color.RED);
            } else {
                g2.setColor(Color.WHITE);
            }

            g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
        }
    }
}