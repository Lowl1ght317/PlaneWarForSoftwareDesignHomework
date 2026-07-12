package com.example.planewar.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class KeyController implements KeyListener {
    public static final int KEY_LEFT = KeyEvent.VK_LEFT;
    public static final int KEY_RIGHT = KeyEvent.VK_RIGHT;
    public static final int KEY_UP = KeyEvent.VK_UP;
    public static final int KEY_DOWN = KeyEvent.VK_DOWN;
    public static final int KEY_SHOOT = KeyEvent.VK_SPACE;
    public static final int KEY_PAUSE = KeyEvent.VK_P;
    public static final int KEY_W = KeyEvent.VK_W;
    public static final int KEY_A = KeyEvent.VK_A;
    public static final int KEY_S = KeyEvent.VK_S;
    public static final int KEY_D = KeyEvent.VK_D;
    public static final int KEY_1 = KeyEvent.VK_1;
    public static final int KEY_2 = KeyEvent.VK_2;
    public static final int KEY_ESC = KeyEvent.VK_ESCAPE;

    private GameController controller;
    private Map<Integer, Boolean> keyPressed;

    public KeyController(GameController controller) {
        this.controller = controller;
        this.keyPressed = new HashMap<>();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        keyPressed.put(key, true);

        if (key == KEY_SHOOT) {
            e.consume();
            if (!controller.getGameState().isGameStarted() || controller.getGameState().isGameOver()) {
                controller.startGame();
            }
        } else if (key == KEY_1) {
            if (!controller.getGameState().isGameStarted()) {
                controller.startGame(1);
            }
        } else if (key == KEY_2) {
            if (!controller.getGameState().isGameStarted()) {
                controller.startGame(2);
            }
        } else if (key == KEY_ESC) {
            if (!controller.getGameState().isGameStarted() && controller.getGameState().getGameMode() != null) {
                controller.resetToMainMenu();
            } else if (controller.getGameState().isGameStarted() && !controller.getGameState().isGameOver()) {
                controller.togglePause();
            }
        } else if (key == KEY_PAUSE) {
            if (controller.getGameState().isGameStarted() && !controller.getGameState().isGameOver()) {
                controller.togglePause();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        keyPressed.put(key, false);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public boolean isKeyPressed(int keyCode) {
        if (keyCode == KEY_LEFT) {
            return keyPressed.getOrDefault(KEY_LEFT, false) || keyPressed.getOrDefault(KEY_A, false);
        } else if (keyCode == KEY_RIGHT) {
            return keyPressed.getOrDefault(KEY_RIGHT, false) || keyPressed.getOrDefault(KEY_D, false);
        } else if (keyCode == KEY_UP) {
            return keyPressed.getOrDefault(KEY_UP, false) || keyPressed.getOrDefault(KEY_W, false);
        } else if (keyCode == KEY_DOWN) {
            return keyPressed.getOrDefault(KEY_DOWN, false) || keyPressed.getOrDefault(KEY_S, false);
        }
        return keyPressed.getOrDefault(keyCode, false);
    }
}