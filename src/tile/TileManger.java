package tile;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
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
            setup(40, "40"); setup(41, "41"); setup(42, "42");
            setup(43, "43"); setup(46, "46"); setup(48, "48");
            setup(49, "49"); setup(71, "71"); setup(74, "74");
            setup(77, "77"); setup(88, "88"); setup(118, "118");
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void setup(int index, String name) {
        try {
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(getClass().getResourceAsStream("/tileset/" + name + ".png"));
        } catch (Exception e) {}
    }

    public void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col = 0; int row = 0;
            while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
                String line = br.readLine();
                String numbers[] = line.split(" ");
                while (col < gp.maxWorldCol) {
                    mapTileNum[col][row] = Integer.parseInt(numbers[col]);
                    col++;
                }
                if (col == gp.maxWorldCol) { col = 0; row++; }
            }
            br.close();
        } catch (Exception e) {}
    }

    public void draw(Graphics2D g2) {
        int worldCol = 0; int worldRow = 0;
        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            int tileNum = mapTileNum[worldCol][worldRow];
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            if (tileNum >= 0 && tile[tileNum] != null) {
                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }
            worldCol++;
            if (worldCol == gp.maxWorldCol) { worldCol = 0; worldRow++; }
        }
    }
}