package com.example.planewar.view;

import com.example.planewar.controller.MouseController;
import com.example.planewar.controller.GameController;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    public static final int WIDTH = 600;
    public static final int HEIGHT = 800;

    private GamePanel gamePanel;

    public GameFrame() {
        setTitle("飞机大战");
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        gamePanel = new GamePanel();
        add(gamePanel);

        setVisible(true);
    }

    public void addMouseController(GameController controller) {
        MouseController mouseController = new MouseController(controller);
        addMouseListener(mouseController);
        gamePanel.addMouseListener(mouseController);
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }
}