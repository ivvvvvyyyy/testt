package Entity;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

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

        getImage();
    }

    public void getImage() {
        try {
            if (hostile) {
                down1 = ImageIO.read(getClass().getResourceAsStream("/player/npc/evil.png"));

            } else {

                down1 = ImageIO.read(getClass().getResourceAsStream("/player/npc/crow.png"));
            }
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Error loading image for: " + name + ". Check if the file exists in res/player/npc/");
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;


        if (screenX + gp.tileSize * 2 > 0 && screenX < gp.screenWidth &&
                screenY + gp.tileSize * 2 > 0 && screenY < gp.screenHeight) {

            if (down1 != null) {
                if (hostile) {

                    double scale = 1.5;
                    int bigSize = (int)(gp.tileSize * scale);


                    int yOffset = bigSize - gp.tileSize;
                    int xOffset = (bigSize - gp.tileSize) / 2;

                    g2.drawImage(down1, screenX - xOffset, screenY - yOffset, bigSize, bigSize, null);
                } else {

                    g2.drawImage(down1, screenX, screenY, gp.tileSize, gp.tileSize, null);
                }
            } else {

                g2.setColor(hostile ? Color.RED : Color.WHITE);
                g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            }
        }
    }
}