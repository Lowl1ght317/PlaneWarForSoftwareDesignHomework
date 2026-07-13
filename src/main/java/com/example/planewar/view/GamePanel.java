package com.example.planewar.view;

import com.example.planewar.controller.GameController;
import com.example.planewar.model.Enemy;
import com.example.planewar.model.Enemy.EnemyType;
import com.example.planewar.model.Bullet;
import com.example.planewar.model.GameState;
import com.example.planewar.model.GameMode;
import com.example.planewar.model.LevelConfig;
import com.example.planewar.model.Player;
import com.example.planewar.model.PowerUp;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private GameController controller;
    private GameState gameState;

    private static final LevelConfig[] LEVELS = LevelConfig.getLevels();

    public static final int MENU_STORY_X = 150;
    public static final int MENU_STORY_Y = 250;
    public static final int MENU_STORY_WIDTH = 150;
    public static final int MENU_STORY_HEIGHT = 50;

    public static final int MENU_SURVIVAL_X = 300;
    public static final int MENU_SURVIVAL_Y = 250;
    public static final int MENU_SURVIVAL_WIDTH = 150;
    public static final int MENU_SURVIVAL_HEIGHT = 50;

    public static final int PAUSE_RESUME_X = 150;
    public static final int PAUSE_RESUME_Y = 300;
    public static final int PAUSE_RESUME_WIDTH = 150;
    public static final int PAUSE_RESUME_HEIGHT = 50;

    public static final int PAUSE_MENU_X = 300;
    public static final int PAUSE_MENU_Y = 300;
    public static final int PAUSE_MENU_WIDTH = 150;
    public static final int PAUSE_MENU_HEIGHT = 50;

    public static final int WIN_NEXT_X = 150;
    public static final int WIN_NEXT_Y = 350;
    public static final int WIN_NEXT_WIDTH = 150;
    public static final int WIN_NEXT_HEIGHT = 50;

    public static final int WIN_MENU_X = 300;
    public static final int WIN_MENU_Y = 350;
    public static final int WIN_MENU_WIDTH = 150;
    public static final int WIN_MENU_HEIGHT = 50;
    
    private static final int LEVEL_BUTTON_WIDTH = 400;
    private static final int LEVEL_BUTTON_HEIGHT = 70;
    private static final int LEVEL_BUTTON_SPACING = 20;
    private static final int LEVEL_BUTTON_START_Y = 150;
    
    private Font getFont(int style, int size) {
        return new Font(Font.SANS_SERIF, style, size);
    }

    public GamePanel() {
        setDoubleBuffered(true);
    }

    public void setController(GameController controller) {
        this.controller = controller;
        this.gameState = controller.getGameState();
    }

    public MenuAction getMenuAction(int x, int y) {
        if (!gameState.isGameStarted()) {
            if (gameState.getGameMode() == null) {
                return getMainMenuAction(x, y);
            } else {
                return getLevelSelectAction(x, y);
            }
        } else if (gameState.isPaused()) {
            return getPauseMenuAction(x, y);
        } else if (gameState.isGameOver()) {
            return getGameOverAction(x, y);
        }
        return MenuAction.NONE;
    }

    private MenuAction getMainMenuAction(int x, int y) {
        if (isInBounds(x, y, MENU_STORY_X, MENU_STORY_Y, MENU_STORY_WIDTH, MENU_STORY_HEIGHT)) {
            return MenuAction.STORY_MODE;
        } else if (isInBounds(x, y, MENU_SURVIVAL_X, MENU_SURVIVAL_Y, MENU_SURVIVAL_WIDTH, MENU_SURVIVAL_HEIGHT)) {
            return MenuAction.SURVIVAL_MODE;
        }
        return MenuAction.NONE;
    }

    private MenuAction getLevelSelectAction(int x, int y) {
        LevelConfig[] levels = LevelConfig.getLevels();
        for (int i = 0; i < levels.length; i++) {
            int levelX = (GameFrame.WIDTH - LEVEL_BUTTON_WIDTH) / 2;
            int levelY = LEVEL_BUTTON_START_Y + i * (LEVEL_BUTTON_HEIGHT + LEVEL_BUTTON_SPACING);
            if (isInBounds(x, y, levelX, levelY, LEVEL_BUTTON_WIDTH, LEVEL_BUTTON_HEIGHT)) {
                if (i + 1 == 6) {
                    return MenuAction.SURVIVAL_MODE;
                } else {
                    return new MenuAction(MenuAction.Type.LEVEL_SELECT, i + 1);
                }
            }
        }
        return MenuAction.NONE;
    }

    private MenuAction getPauseMenuAction(int x, int y) {
        if (isInBounds(x, y, PAUSE_RESUME_X, PAUSE_RESUME_Y, PAUSE_RESUME_WIDTH, PAUSE_RESUME_HEIGHT)) {
            return MenuAction.RESUME;
        } else if (isInBounds(x, y, PAUSE_MENU_X, PAUSE_MENU_Y, PAUSE_MENU_WIDTH, PAUSE_MENU_HEIGHT)) {
            return MenuAction.MAIN_MENU;
        }
        return MenuAction.NONE;
    }

    private MenuAction getGameOverAction(int x, int y) {
        if (isInBounds(x, y, WIN_NEXT_X, WIN_NEXT_Y, WIN_NEXT_WIDTH, WIN_NEXT_HEIGHT)) {
            if (gameState.isWin() && gameState.getLevel() < 5) {
                return MenuAction.NEXT_LEVEL;
            } else if (!gameState.isWin()) {
                return MenuAction.RETRY;
            }
        }
        if (isInBounds(x, y, WIN_MENU_X, WIN_MENU_Y, WIN_MENU_WIDTH, WIN_MENU_HEIGHT)) {
            return MenuAction.MAIN_MENU;
        }
        return MenuAction.NONE;
    }

    private boolean isInBounds(int x, int y, int boundX, int boundY, int width, int height) {
        return x >= boundX && x <= boundX + width && y >= boundY && y <= boundY + height;
    }

    public static class MenuAction {
        public enum Type {
            NONE, STORY_MODE, SURVIVAL_MODE, RESUME, MAIN_MENU, NEXT_LEVEL, RETRY, LEVEL_SELECT
        }

        private final Type type;
        private final int levelNumber;

        public MenuAction(Type type) {
            this(type, 0);
        }

        public MenuAction(Type type, int levelNumber) {
            this.type = type;
            this.levelNumber = levelNumber;
        }

        public Type getType() {
            return type;
        }

        public int getLevelNumber() {
            return levelNumber;
        }

        public static final MenuAction NONE = new MenuAction(Type.NONE);
        public static final MenuAction STORY_MODE = new MenuAction(Type.STORY_MODE);
        public static final MenuAction SURVIVAL_MODE = new MenuAction(Type.SURVIVAL_MODE);
        public static final MenuAction RESUME = new MenuAction(Type.RESUME);
        public static final MenuAction MAIN_MENU = new MenuAction(Type.MAIN_MENU);
        public static final MenuAction NEXT_LEVEL = new MenuAction(Type.NEXT_LEVEL);
        public static final MenuAction RETRY = new MenuAction(Type.RETRY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        drawStars(g2d);

        if (!gameState.isGameStarted()) {
            if (gameState.getGameMode() == null) {
                drawMainMenu(g2d);
            } else {
                drawLevelSelect(g2d);
            }
            return;
        }

        if (gameState.isGameOver()) {
            drawGameOverScreen(g2d);
            return;
        }

        if (gameState.isPaused()) {
            drawPausedScreen(g2d);
        }

        drawPlayer(g2d);
        drawEnemies(g2d);
        drawBullets(g2d);
        drawPowerUps(g2d);
        drawHUD(g2d);
    }

    private void drawStars(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 50));
        for (int i = 0; i < 50; i++) {
            int x = (i * 37) % getWidth();
            int y = (i * 23) % getHeight();
            int size = (i % 3) + 1;
            g2d.fillOval(x, y, size, size);
        }
    }

    private void drawMainMenu(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(new Color(0, 200, 255));
        g2d.setFont(getFont(Font.BOLD, 56));
        FontMetrics fm = g2d.getFontMetrics();
        String title = "飞机大战";
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        int titleY = 150;
        g2d.drawString(title, titleX, titleY);

        g2d.setColor(Color.WHITE);
        g2d.setFont(getFont(Font.PLAIN, 18));
        String subtitle = "PLANE WAR";
        int subX = (getWidth() - g2d.getFontMetrics().stringWidth(subtitle)) / 2;
        g2d.drawString(subtitle, subX, 190);

        GradientPaint gradient1 = new GradientPaint(
                MENU_STORY_X, MENU_STORY_Y, new Color(50, 200, 50),
                MENU_STORY_X + MENU_STORY_WIDTH, MENU_STORY_Y + MENU_STORY_HEIGHT, new Color(30, 150, 30)
        );
        g2d.setPaint(gradient1);
        g2d.fillRoundRect(MENU_STORY_X, MENU_STORY_Y, MENU_STORY_WIDTH, MENU_STORY_HEIGHT, 15, 15);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(getFont(Font.BOLD, 24));
        fm = g2d.getFontMetrics();
        String storyText = "闯关模式";
        int storyTextX = MENU_STORY_X + (MENU_STORY_WIDTH - fm.stringWidth(storyText)) / 2;
        int storyTextY = MENU_STORY_Y + (MENU_STORY_HEIGHT + fm.getHeight()) / 2 - 2;
        g2d.drawString(storyText, storyTextX, storyTextY);

        GradientPaint gradient2 = new GradientPaint(
                MENU_SURVIVAL_X, MENU_SURVIVAL_Y, new Color(255, 150, 50),
                MENU_SURVIVAL_X + MENU_SURVIVAL_WIDTH, MENU_SURVIVAL_Y + MENU_SURVIVAL_HEIGHT, new Color(200, 100, 30)
        );
        g2d.setPaint(gradient2);
        g2d.fillRoundRect(MENU_SURVIVAL_X, MENU_SURVIVAL_Y, MENU_SURVIVAL_WIDTH, MENU_SURVIVAL_HEIGHT, 15, 15);
        
        g2d.setColor(Color.WHITE);
        String survivalText = "无限模式";
        int survivalTextX = MENU_SURVIVAL_X + (MENU_SURVIVAL_WIDTH - fm.stringWidth(survivalText)) / 2;
        int survivalTextY = MENU_SURVIVAL_Y + (MENU_SURVIVAL_HEIGHT + fm.getHeight()) / 2 - 2;
        g2d.drawString(survivalText, survivalTextX, survivalTextY);

        g2d.setColor(Color.WHITE);
        g2d.setFont(getFont(Font.PLAIN, 14));
        String hint = "点击按钮选择模式";
        int hintX = (getWidth() - g2d.getFontMetrics().stringWidth(hint)) / 2;
        g2d.drawString(hint, hintX, 330);

        if (gameState.getHighScore() > 0) {
            g2d.setFont(getFont(Font.PLAIN, 16));
            String highScore = "最高分: " + gameState.getHighScore();
            int hsX = (getWidth() - g2d.getFontMetrics().stringWidth(highScore)) / 2;
            g2d.drawString(highScore, hsX, 400);
        }
    }

    private void drawLevelSelect(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(new Color(0, 200, 255));
        g2d.setFont(getFont(Font.BOLD, 36));
        FontMetrics fm = g2d.getFontMetrics();
        String title = "选择关卡";
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 80);

        LevelConfig[] levels = LevelConfig.getLevels();
        int startY = 150;
        int levelWidth = 400;
        int levelHeight = 70;
        int spacing = 20;

        for (int i = 0; i < levels.length; i++) {
            LevelConfig level = levels[i];
            int x = (getWidth() - levelWidth) / 2;
            int y = startY + i * (levelHeight + spacing);

            GradientPaint gradient = new GradientPaint(
                    x, y, new Color(60, 60, 80),
                    x, y + levelHeight, new Color(40, 40, 60)
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(x, y, levelWidth, levelHeight, 15, 15);

            g2d.setColor(new Color(0, 200, 255));
            g2d.setFont(getFont(Font.BOLD, 24));
            String levelNum = "第 " + level.getLevelNumber() + " 关";
            g2d.drawString(levelNum, x + 20, y + 30);

            g2d.setColor(Color.WHITE);
            g2d.setFont(getFont(Font.BOLD, 18));
            g2d.drawString(level.getName(), x + 20, y + 55);

            g2d.setColor(new Color(150, 150, 200));
            g2d.setFont(getFont(Font.PLAIN, 14));
            g2d.drawString(level.getDescription(), x + 200, y + 45);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(getFont(Font.PLAIN, 14));
        String backHint = "按 ESC 返回主菜单";
        int backX = (getWidth() - g2d.getFontMetrics().stringWidth(backHint)) / 2;
        g2d.drawString(backHint, backX, 600);
    }

    private void drawGameOverScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        boolean isWin = gameState.isWin();
        
        if (isWin) {
            g2d.setColor(new Color(0, 200, 100));
            g2d.setFont(getFont(Font.BOLD, 48));
        } else {
            g2d.setColor(new Color(255, 50, 50));
            g2d.setFont(getFont(Font.BOLD, 48));
        }
        
        FontMetrics fm = g2d.getFontMetrics();
        String title = isWin ? "关卡完成!" : "游戏结束";
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 200);

        g2d.setColor(Color.WHITE);
        g2d.setFont(getFont(Font.PLAIN, 24));
        fm = g2d.getFontMetrics();
        
        String score = "得分: " + gameState.getScore();
        int scoreX = (getWidth() - fm.stringWidth(score)) / 2;
        g2d.drawString(score, scoreX, 260);

        String level = "关卡: " + gameState.getLevel();
        int levelX = (getWidth() - fm.stringWidth(level)) / 2;
        g2d.drawString(level, levelX, 300);

        String highScore = "最高分: " + gameState.getHighScore();
        int hsX = (getWidth() - fm.stringWidth(highScore)) / 2;
        g2d.drawString(highScore, hsX, 340);

        g2d.setFont(getFont(Font.BOLD, 22));

        if (isWin && gameState.getLevel() < 5) {
            GradientPaint gradient1 = new GradientPaint(
                    WIN_NEXT_X, WIN_NEXT_Y, new Color(50, 200, 50),
                    WIN_NEXT_X + WIN_NEXT_WIDTH, WIN_NEXT_Y + WIN_NEXT_HEIGHT, new Color(30, 150, 30)
            );
            g2d.setPaint(gradient1);
            g2d.fillRoundRect(WIN_NEXT_X, WIN_NEXT_Y, WIN_NEXT_WIDTH, WIN_NEXT_HEIGHT, 15, 15);
            
            g2d.setColor(Color.WHITE);
            String nextText = "下一关";
            int nextTextX = WIN_NEXT_X + (WIN_NEXT_WIDTH - fm.stringWidth(nextText)) / 2;
            int nextTextY = WIN_NEXT_Y + (WIN_NEXT_HEIGHT + fm.getAscent()) / 2;
            g2d.drawString(nextText, nextTextX, nextTextY);
        } else if (isWin) {
            GradientPaint gradient1 = new GradientPaint(
                    WIN_NEXT_X, WIN_NEXT_Y, new Color(255, 150, 50),
                    WIN_NEXT_X + WIN_NEXT_WIDTH, WIN_NEXT_Y + WIN_NEXT_HEIGHT, new Color(200, 100, 30)
            );
            g2d.setPaint(gradient1);
            g2d.fillRoundRect(WIN_NEXT_X, WIN_NEXT_Y, WIN_NEXT_WIDTH, WIN_NEXT_HEIGHT, 15, 15);
            
            g2d.setColor(Color.WHITE);
            String completeText = "恭喜通关!";
            int completeTextX = WIN_NEXT_X + (WIN_NEXT_WIDTH - fm.stringWidth(completeText)) / 2;
            int completeTextY = WIN_NEXT_Y + (WIN_NEXT_HEIGHT + fm.getAscent()) / 2;
            g2d.drawString(completeText, completeTextX, completeTextY);
        } else {
            GradientPaint gradient1 = new GradientPaint(
                    WIN_NEXT_X, WIN_NEXT_Y, new Color(50, 100, 200),
                    WIN_NEXT_X + WIN_NEXT_WIDTH, WIN_NEXT_Y + WIN_NEXT_HEIGHT, new Color(30, 70, 150)
            );
            g2d.setPaint(gradient1);
            g2d.fillRoundRect(WIN_NEXT_X, WIN_NEXT_Y, WIN_NEXT_WIDTH, WIN_NEXT_HEIGHT, 15, 15);
            
            g2d.setColor(Color.WHITE);
            String retryText = "重新挑战";
            int retryTextX = WIN_NEXT_X + (WIN_NEXT_WIDTH - fm.stringWidth(retryText)) / 2;
            int retryTextY = WIN_NEXT_Y + (WIN_NEXT_HEIGHT + fm.getAscent()) / 2;
            g2d.drawString(retryText, retryTextX, retryTextY);
        }

        GradientPaint gradient2 = new GradientPaint(
                WIN_MENU_X, WIN_MENU_Y, new Color(200, 50, 50),
                WIN_MENU_X + WIN_MENU_WIDTH, WIN_MENU_Y + WIN_MENU_HEIGHT, new Color(150, 30, 30)
        );
        g2d.setPaint(gradient2);
        g2d.fillRoundRect(WIN_MENU_X, WIN_MENU_Y, WIN_MENU_WIDTH, WIN_MENU_HEIGHT, 15, 15);
        
        g2d.setColor(Color.WHITE);
        String menuText = "返回主菜单";
        int menuTextX = WIN_MENU_X + (WIN_MENU_WIDTH - fm.stringWidth(menuText)) / 2;
        int menuTextY = WIN_MENU_Y + (WIN_MENU_HEIGHT + fm.getAscent()) / 2;
        g2d.drawString(menuText, menuTextX, menuTextY);
    }

    private void drawPausedScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.WHITE);
        g2d.setFont(getFont(Font.BOLD, 36));
        FontMetrics fm = g2d.getFontMetrics();
        String paused = "暂停";
        int pausedX = (getWidth() - fm.stringWidth(paused)) / 2;
        g2d.drawString(paused, pausedX, 200);

        GradientPaint gradient1 = new GradientPaint(
                PAUSE_RESUME_X, PAUSE_RESUME_Y, new Color(50, 200, 50),
                PAUSE_RESUME_X + PAUSE_RESUME_WIDTH, PAUSE_RESUME_Y + PAUSE_RESUME_HEIGHT, new Color(30, 150, 30)
        );
        g2d.setPaint(gradient1);
        g2d.fillRoundRect(PAUSE_RESUME_X, PAUSE_RESUME_Y, PAUSE_RESUME_WIDTH, PAUSE_RESUME_HEIGHT, 15, 15);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(getFont(Font.BOLD, 22));
        fm = g2d.getFontMetrics();
        String resumeText = "继续游戏";
        int resumeTextX = PAUSE_RESUME_X + (PAUSE_RESUME_WIDTH - fm.stringWidth(resumeText)) / 2;
        int resumeTextY = PAUSE_RESUME_Y + (PAUSE_RESUME_HEIGHT + fm.getAscent()) / 2;
        g2d.drawString(resumeText, resumeTextX, resumeTextY);

        GradientPaint gradient2 = new GradientPaint(
                PAUSE_MENU_X, PAUSE_MENU_Y, new Color(200, 50, 50),
                PAUSE_MENU_X + PAUSE_MENU_WIDTH, PAUSE_MENU_Y + PAUSE_MENU_HEIGHT, new Color(150, 30, 30)
        );
        g2d.setPaint(gradient2);
        g2d.fillRoundRect(PAUSE_MENU_X, PAUSE_MENU_Y, PAUSE_MENU_WIDTH, PAUSE_MENU_HEIGHT, 15, 15);
        
        g2d.setColor(Color.WHITE);
        String menuText = "返回主菜单";
        int menuTextX = PAUSE_MENU_X + (PAUSE_MENU_WIDTH - fm.stringWidth(menuText)) / 2;
        int menuTextY = PAUSE_MENU_Y + (PAUSE_MENU_HEIGHT + fm.getAscent()) / 2;
        g2d.drawString(menuText, menuTextX, menuTextY);
    }

    private void drawPlayer(Graphics2D g2d) {
        Player player = gameState.getPlayer();
        if (player == null) return;

        if (player.isInvincible() && (System.currentTimeMillis() / 100) % 2 == 0) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }

        Image image = player.getImage();
        if (image != null) {
            g2d.drawImage(image, player.getX(), player.getY(), player.getWidth(), player.getHeight(), null);
        } else {
            g2d.setColor(Color.BLUE);
            g2d.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    private void drawEnemies(Graphics2D g2d) {
        for (Enemy enemy : gameState.getEnemies()) {
            Image image = enemy.getImage();
            if (image != null) {
                g2d.drawImage(image, enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight(), null);
            } else {
                switch (enemy.getType()) {
                    case NORMAL:
                        g2d.setColor(Color.RED);
                        break;
                    case FAST:
                        g2d.setColor(Color.ORANGE);
                        break;
                    case ELITE:
                        g2d.setColor(new Color(128, 0, 128));
                        break;
                    case BOSS:
                        g2d.setColor(new Color(255, 0, 100));
                        break;
                    case FINAL_BOSS:
                        g2d.setColor(new Color(100, 0, 255));
                        break;
                }
                g2d.fillRect(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
            }

            if (enemy.getHealth() < enemy.getMaxHealth()) {
                int healthBarWidth = enemy.getWidth();
                int healthBarHeight = enemy.getType() == EnemyType.BOSS || enemy.getType() == EnemyType.FINAL_BOSS ? 8 : 4;
                int healthPercent = (enemy.getHealth() * 100) / enemy.getMaxHealth();

                g2d.setColor(Color.RED);
                g2d.fillRect(enemy.getX(), enemy.getY() - 15, healthBarWidth, healthBarHeight);
                g2d.setColor(Color.GREEN);
                g2d.fillRect(enemy.getX(), enemy.getY() - 15, healthBarWidth * healthPercent / 100, healthBarHeight);
            }
        }
    }

    private void drawBullets(Graphics2D g2d) {
        for (Bullet bullet : gameState.getBullets()) {
            if (bullet.isPlayerBullet()) {
                g2d.setColor(Color.YELLOW);
            } else {
                g2d.setColor(Color.CYAN);
            }
            g2d.fillRect(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
        }
    }

    private void drawPowerUps(Graphics2D g2d) {
        for (PowerUp powerUp : gameState.getPowerUps()) {
            Image image = powerUp.getImage();
            if (image != null) {
                g2d.drawImage(image, powerUp.getX(), powerUp.getY(), powerUp.getWidth(), powerUp.getHeight(), null);
            } else {
                g2d.setColor(powerUp.getType().getColor());
                g2d.fillRect(powerUp.getX(), powerUp.getY(), powerUp.getWidth(), powerUp.getHeight());
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont(Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                String name = powerUp.getType().getName();
                int nameX = powerUp.getX() + (powerUp.getWidth() - fm.stringWidth(name)) / 2;
                int nameY = powerUp.getY() + (powerUp.getHeight() + fm.getHeight()) / 2 - 2;
                g2d.drawString(name, nameX, nameY);
            }
        }
    }

    private void drawHUD(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillRect(0, 0, getWidth(), 50);

        g2d.setColor(Color.WHITE);
        g2d.setFont(getFont(Font.PLAIN, 16));

        String score = "得分: " + gameState.getScore();
        g2d.drawString(score, 15, 25);

        String level = "关卡 " + gameState.getLevel();
        if (gameState.getGameMode() == GameMode.SURVIVAL) {
            level = "无限模式 — 第 " + gameState.getBossWave() + " 波";
        }
        int levelX = getWidth() / 2 - g2d.getFontMetrics().stringWidth(level) / 2;
        g2d.drawString(level, levelX, 25);

        LevelConfig currentLevel = LEVELS[Math.min(gameState.getLevel() - 1, LEVELS.length - 1)];
        String condition = "";
        if (gameState.getGameMode() == GameMode.SURVIVAL) {
            condition = "最高分: " + Math.max(gameState.getScore(), gameState.getHighScore());
        } else if (currentLevel.getWinCondition() == LevelConfig.WinCondition.SCORE) {
            condition = "目标: " + currentLevel.getTargetScore() + "分";
        } else if (currentLevel.getWinCondition() == LevelConfig.WinCondition.KILL_COUNT) {
            condition = "击杀: " + gameState.getEnemiesKilled() + "/" + currentLevel.getTargetKillCount();
        } else if (currentLevel.getWinCondition() == LevelConfig.WinCondition.DEFEAT_BOSS) {
            condition = "击败BOSS";
        }
        g2d.drawString(condition, 15, 45);

        Player player = gameState.getPlayer();
        if (player == null) return;

        Image hpImg = controller.getHpImage();
        for (int i = 0; i < player.getLives(); i++) {
            if (hpImg != null) {
                g2d.drawImage(hpImg, getWidth() - 35 - i * 30, 15, 25, 20, null);
            } else {
                g2d.setColor(new Color(0, 200, 255));
                g2d.fillRect(getWidth() - 35 - i * 30, 15, 25, 20);
            }
        }
    }
}