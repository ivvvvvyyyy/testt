package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyButtons implements KeyListener {

    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    public KeyButtons(GamePanel gp) {
        this.gp = gp;
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (gp.gameState == gp.titleState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.commandNum--;
                if (gp.commandNum < 0) gp.commandNum = 1;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.commandNum++;
                if (gp.commandNum > 1) gp.commandNum = 0;
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.commandNum == 0) {
                    gp.gameState = gp.storyState;
                    gp.storyPageIndex = 0;
                    gp.fullText = gp.storyPages[0];
                    gp.displayedText = "";
                    gp.charIndex = 0;
                }
                if (gp.commandNum == 1) System.exit(0);
            }
        }
        else if (gp.gameState == gp.storyState) {
            if (code == KeyEvent.VK_ENTER) {
                if (gp.charIndex < gp.fullText.length()) {
                    gp.displayedText = gp.fullText;
                    gp.charIndex = gp.fullText.length();
                } else {
                    gp.storyPageIndex++;
                    if (gp.storyPageIndex >= gp.storyPages.length) {
                        gp.gameState = gp.playState;
                    } else {
                        gp.fullText = gp.storyPages[gp.storyPageIndex];
                        gp.displayedText = "";
                        gp.charIndex = 0;
                    }
                }
            }
        }
        else if (gp.gameState == gp.playState) {
            if (code == KeyEvent.VK_W) upPressed = true;
            if (code == KeyEvent.VK_S) downPressed = true;
            if (code == KeyEvent.VK_D) rightPressed = true;
            if (code == KeyEvent.VK_A) leftPressed = true;
            if (code == KeyEvent.VK_E) gp.interactPressed = true;
        }
        else if (gp.gameState == gp.dialogueState) {
            if (code == KeyEvent.VK_ENTER) {
                if (gp.charIndex < gp.fullText.length()) {
                    gp.displayedText = gp.fullText;
                    gp.charIndex = gp.fullText.length();
                } else {
                    gp.dialoguePageIndex++;
                    if (gp.dialoguePageIndex >= gp.talkingNPC.dialoguePages.length) {
                        if (gp.talkingNPC.hostile) {
                            gp.combatManager.startCombat(gp.talkingNPC);
                        } else {
                            gp.gameState = gp.playState;
                        }
                    } else {
                        gp.fullText = gp.talkingNPC.dialoguePages[gp.dialoguePageIndex];
                        gp.currentSpeaker = gp.talkingNPC.name;
                        gp.displayedText = "";
                        gp.charIndex = 0;
                    }
                }
            }
        }
        else if (gp.gameState == gp.winDialogueState) {
            if (code == KeyEvent.VK_ENTER) {
                if (gp.charIndex < gp.fullText.length()) {
                    gp.displayedText = gp.fullText;
                    gp.charIndex = gp.fullText.length();
                } else {
                    gp.dialoguePageIndex++;
                    if (gp.dialoguePageIndex >= gp.talkingNPC.dialoguePages.length) {


                        for (int i = 0; i < gp.npcs.length; i++) {
                            if (gp.npcs[i] == gp.talkingNPC) {
                                gp.npcs[i] = null;
                                break;
                            }
                        }

                        gp.gameState = gp.playState;
                    } else {
                        gp.fullText = gp.talkingNPC.dialoguePages[gp.dialoguePageIndex];
                        gp.currentSpeaker = gp.winSpeakers[gp.dialoguePageIndex];
                        gp.displayedText = "";
                        gp.charIndex = 0;
                    }
                }
            }
        }
        else if (gp.gameState == gp.combatState) {
            char c = e.getKeyChar();
            if (Character.isLetter(c)) gp.combatManager.handleKeyPress(c);
        }
        else if (gp.gameState == gp.gameOverState) {
            if (code == KeyEvent.VK_ENTER) {
                gp.gameState = gp.playState;


                gp.switchMap("/maps/room1.txt", 35, 31);
            }
        }

        if (code == KeyEvent.VK_ESCAPE) System.exit(0);
    }

    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
    }
}