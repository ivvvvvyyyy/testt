package main;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class KeyButtons implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed;


    public void keyTyped(KeyEvent e){
    }
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();


        if (code == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }
    public void keyReleased(KeyEvent e){
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed= false;

        }
        if (code == KeyEvent.VK_D) {
            rightPressed=false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed=false;
        }

    }

}
