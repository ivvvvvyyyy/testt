package tile;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManger {
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];

    public TileManger(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[150];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        getTileImages();
        loadMap("/maps/map.txt");
    }

    public void getTileImages() {
        try {
            tile[40] = new Tile();
            tile[40].image = ImageIO.read(getClass().getResourceAsStream("/tileset/40.png"));
            tile[41] = new Tile();
            tile[41].image = ImageIO.read(getClass().getResourceAsStream("/tileset/41.png"));
            tile[42] = new Tile();
            tile[42].image = ImageIO.read(getClass().getResourceAsStream("/tileset/42.png"));
            tile[43] = new Tile();
            tile[43].image = ImageIO.read(getClass().getResourceAsStream("/tileset/43.png"));
            tile[46] = new Tile();
            tile[46].image = ImageIO.read(getClass().getResourceAsStream("/tileset/46.png"));
            tile[48] = new Tile();
            tile[48].image = ImageIO.read(getClass().getResourceAsStream("/tileset/48.png"));
            tile[49] = new Tile();
            tile[49].image = ImageIO.read(getClass().getResourceAsStream("/tileset/49.png"));
            tile[71] = new Tile();
            tile[71].image = ImageIO.read(getClass().getResourceAsStream("/tileset/71.png"));
            tile[74] = new Tile();
            tile[74].image = ImageIO.read(getClass().getResourceAsStream("/tileset/74.png"));
            tile[77] = new Tile();
            tile[77].image = ImageIO.read(getClass().getResourceAsStream("/tileset/77.png"));
            tile[88] = new Tile();
            tile[88].image = ImageIO.read(getClass().getResourceAsStream("/tileset/88.png"));
            tile[118] = new Tile();
            tile[118].image = ImageIO.read(getClass().getResourceAsStream("/tileset/118.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col = 0;
            int row = 0;

            while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
                String line = br.readLine();
                while (col < gp.maxWorldCol) {
                    String numbers[] = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    col++;
                }
                if (col == gp.maxWorldCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {

            int tileNum = mapTileNum[worldCol][worldRow];

            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;


            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            if (tileNum >= 0 && tileNum < tile.length && tile[tileNum] != null && tile[tileNum].image != null) {
                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }

            worldCol++;

            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}

