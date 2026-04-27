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
                    // Resetting the story variables just to be perfectly safe!
                    gp.storyPageIndex = 0;
                    gp.fullText = gp.storyPages[0];
                    gp.displayedText = "";
                    gp.charIndex = 0;
                }
                if (gp.commandNum == 1) System.exit(0);
            }
        }
        // FIXED: Changed to 'else if' to prevent the Enter key from cascading!
        else if (gp.gameState == gp.storyState) {
            if (code == KeyEvent.VK_ENTER) {
                boolean textDone = gp.charIndex >= gp.fullText.length();
                if (!textDone) {
                    // Skip typing animation
                    gp.displayedText = gp.fullText;
                    gp.charIndex = gp.fullText.length();
                } else {
                    // Next page
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
        // FIXED: Changed to 'else if'
        else if (gp.gameState == gp.playState) {
            if (code == KeyEvent.VK_W) upPressed = true;
            if (code == KeyEvent.VK_S) downPressed = true;
            if (code == KeyEvent.VK_D) rightPressed = true;
            if (code == KeyEvent.VK_A) leftPressed = true;
            if (code == KeyEvent.VK_E) gp.interactPressed = true;
        }
        // FIXED: Changed to 'else if'
        else if (gp.gameState == gp.dialogueState) {
            if (code == KeyEvent.VK_ENTER) {
                boolean textDone = gp.charIndex >= gp.fullText.length();
                if (!textDone) {
                    gp.displayedText = gp.fullText;
                    gp.charIndex = gp.fullText.length();
                } else {
                    gp.dialoguePageIndex++;
                    if (gp.dialoguePageIndex >= gp.talkingNPC.dialoguePages.length) {
                        gp.gameState = gp.playState;
                        gp.talkingNPC = null;
                        gp.dialoguePageIndex = 0;
                    } else {
                        gp.fullText = gp.talkingNPC.dialoguePages[gp.dialoguePageIndex];
                        gp.displayedText = "";
                        gp.charIndex = 0;
                    }
                }
            }
        }
        // FIXED: Changed to 'else if'
        else if (gp.gameState == gp.combatState) {
            char c = e.getKeyChar();
            if (Character.isLetter(c)) {
                gp.combatManager.handleKeyPress(c);
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