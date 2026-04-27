package main;

import Entity.Entity;
import Entity.NPC;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        int entityLeftWorldX   = entity.worldX + entity.solidArea.x;
        int entityRightWorldX  = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY    = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol   = entityLeftWorldX  / gp.tileSize;
        int entityRightCol  = entityRightWorldX / gp.tileSize;
        int entityTopRow    = entityTopWorldY   / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int tileNum1, tileNum2;

        switch (entity.direction) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                if (isSolid(tileNum1) || isSolid(tileNum2)) entity.collisionOn = true;
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (isSolid(tileNum1) || isSolid(tileNum2)) entity.collisionOn = true;
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                if (isSolid(tileNum1) || isSolid(tileNum2)) entity.collisionOn = true;
                break;
            case "right":
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (isSolid(tileNum1) || isSolid(tileNum2)) entity.collisionOn = true;
                break;
        }
    }

    public void checkNPC(Entity entity) {
        for (NPC npc : gp.npcs) {
            if (npc == null) continue;

            int entityLeft   = entity.worldX + entity.solidArea.x;
            int entityRight  = entityLeft + entity.solidArea.width;
            int entityTop    = entity.worldY + entity.solidArea.y;
            int entityBottom = entityTop + entity.solidArea.height;

            int npcLeft   = npc.worldX + npc.solidArea.x;
            int npcRight  = npcLeft + npc.solidArea.width;
            int npcTop    = npc.worldY + npc.solidArea.y;
            int npcBottom = npcTop + npc.solidArea.height;

            switch (entity.direction) {
                case "up":    entityTop    -= entity.speed; break;
                case "down":  entityBottom += entity.speed; break;
                case "left":  entityLeft   -= entity.speed; break;
                case "right": entityRight  += entity.speed; break;
            }

            if (entityRight > npcLeft && entityLeft < npcRight &&
                    entityBottom > npcTop && entityTop < npcBottom) {
                entity.collisionOn = true;
            }
        }
    }

    private boolean isSolid(int tileNum) {
        if (tileNum < 0 || tileNum >= gp.tileM.tile.length) return true;
        if (gp.tileM.tile[tileNum] == null) return true;
        return gp.tileM.tile[tileNum].collision;
    }
}
