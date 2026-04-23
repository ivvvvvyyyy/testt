package Entity;
import main.GamePanel;
import main.KeyButtons;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player extends Entity {
    GamePanel gp;
    KeyButtons keyB;
    public final int screenX;
    public final int screenY;


    public Player(GamePanel gp, KeyButtons keyB) {
        this.gp = gp;
        this.keyB = keyB;
        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        setDefaultValues();
        getPlayerImage();
    }
    public void setDefaultValues() {
        worldX = gp.tileSize * 35;
        worldY = gp.tileSize * 20;
        speed = 4;
        direction= "down";
    }
    public void getPlayerImage(){
        try {

            up1 = ImageIO.read(getClass().getResourceAsStream("/player/back1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/player/back2.png"));
            up3 = ImageIO.read(getClass().getResourceAsStream("/player/back3.png"));
            up4 = ImageIO.read(getClass().getResourceAsStream("/player/back4.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/player/front1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/player/front2.png"));
            down3 = ImageIO.read(getClass().getResourceAsStream("/player/front3.png"));
            down4 = ImageIO.read(getClass().getResourceAsStream("/player/front4.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/left2.png"));
            left3 = ImageIO.read(getClass().getResourceAsStream("/player/left3.png"));
            left4 = ImageIO.read(getClass().getResourceAsStream("/player/left4.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/right2.png"));
            right3 = ImageIO.read(getClass().getResourceAsStream("/player/right3.png"));
            right4 = ImageIO.read(getClass().getResourceAsStream("/player/right4.png"));



        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void update(){
        if (keyB.upPressed || keyB.downPressed || keyB.leftPressed || keyB.rightPressed) {

            if (keyB.upPressed == true) {
                worldY -= speed;
                direction = "up";
            }
            if (keyB.downPressed == true) {
                worldY += speed;
                direction = "down";
            }
            if (keyB.leftPressed == true) {
                worldX -= speed;
                direction = "left";
            }
            if (keyB.rightPressed == true) {
                worldX += speed;
                direction = "right";
            }
            spriteCounter++;
            if (spriteCounter>14){
                spriteNum++;
                if (spriteNum>4){
                    spriteNum=1;
                }
                spriteCounter = 0;
            }
        }
    }

    public void draw(Graphics2D g2){
        BufferedImage image = null;
        switch (direction) {
            case "up":
                if (spriteNum == 1) {
                    image = up1;
                }
                if (spriteNum == 2) {
                    image = up2;
                }
                if (spriteNum == 3) {
                    image = up3;
                }
                if (spriteNum == 4) {
                    image = up4;
                }
                break;
            case "down":
                if (spriteNum == 1) {
                    image = down1;
                }
                if (spriteNum == 2) {
                    image = down2;
                }
                if (spriteNum == 3) {
                    image = down3;
                }
                if (spriteNum == 4) {
                    image = down4;
                }
                break;
            case "left":
                if (spriteNum == 1) {
                    image = left1;
                }
                if (spriteNum == 2) {
                    image = left2;
                }
                if (spriteNum == 3) {
                    image = left3;
                }
                if (spriteNum == 4) {
                    image = left4;
                }
                break;
            case "right":
                if (spriteNum == 1) {
                    image = right1;
                }
                if (spriteNum == 2) {
                    image = right2;
                }
                if (spriteNum == 3) {
                    image = right3;
                }
                if (spriteNum == 4) {
                    image = right4;
                }
                break;
        }
        g2.drawImage(image ,screenX,screenY, gp.tileSize , gp.tileSize, null);


    }
}