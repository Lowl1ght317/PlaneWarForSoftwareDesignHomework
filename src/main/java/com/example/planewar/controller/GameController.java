package com.example.planewar.controller;

import com.example.planewar.model.GameState;
import com.example.planewar.model.GameMode;
import com.example.planewar.model.LevelConfig;
import com.example.planewar.model.Player;
import com.example.planewar.util.HighScoreManager;
import com.example.planewar.util.ImageLoader;
import com.example.planewar.view.GameFrame;
import com.example.planewar.view.GamePanel;
import com.example.planewar.view.GamePanel.MenuAction;

import java.awt.Image;

public class GameController {
    private GameState gameState;
    private GameFrame gameFrame;
    private GamePanel gamePanel;
    private GameLoop gameLoop;
    private KeyController keyController;
    
    private EnemySpawner enemySpawner;
    private CombatSystem combatSystem;
    
    private Image playerImage;
    private Image hpImage;
    
    private long lastShootTime;
    private long gameTime;
    
    private static final int DEFAULT_FIRE_INTERVAL = 200;
    private static final int BULLET_CENTER_OFFSET_X = 3;
    private static final int BULLET_CENTER_OFFSET_Y = 15;
    private static final int BULLET_SIDE_OFFSET_X = 5;
    private static final int BULLET_SIDE_OFFSET_Y = 10;
    private static final int BULLET_RIGHT_OFFSET_X = 11;
    private static final int POWER_BULLET_SPEED_BONUS = 3;

    public GameController() {
        gameState = new GameState();
        gameState.setHighScore(HighScoreManager.loadHighScore());
        
        enemySpawner = new EnemySpawner(gameState);
        combatSystem = new CombatSystem(gameState);

        loadImages();

        gameFrame = new GameFrame();
        gamePanel = gameFrame.getGamePanel();
        gamePanel.setController(this);

        keyController = new KeyController(this);
        gameFrame.addKeyListener(keyController);
        
        gameFrame.addMouseController(this);
    }

    private void loadImages() {
        playerImage = ImageLoader.loadPlayerImage();
        Image normalEnemyImage = ImageLoader.loadNormalEnemyImage();
        Image fastEnemyImage = ImageLoader.loadFastEnemyImage();
        Image eliteEnemyImage = ImageLoader.loadEliteEnemyImage();
        Image bossImage = ImageLoader.loadBossImage();
        Image finalBossImage = ImageLoader.loadFinalBossImage();
        Image healthPowerUpImage = ImageLoader.loadHealthPowerUpImage();
        Image shieldPowerUpImage = ImageLoader.loadShieldPowerUpImage();
        Image threeWayPowerUpImage = ImageLoader.loadThreeWayPowerUpImage();
        Image fastShootPowerUpImage = ImageLoader.loadFastShootPowerUpImage();
        Image speedUpPowerUpImage = ImageLoader.loadSpeedUpPowerUpImage();
        Image superDamagePowerUpImage = ImageLoader.loadSuperDamagePowerUpImage();
        
        hpImage = healthPowerUpImage;
        
        enemySpawner.setImages(normalEnemyImage, fastEnemyImage, eliteEnemyImage,
                               bossImage, finalBossImage,
                               healthPowerUpImage, shieldPowerUpImage,
                               threeWayPowerUpImage, fastShootPowerUpImage,
                               speedUpPowerUpImage, superDamagePowerUpImage);
    }

    public void handleMouseClick(int x, int y) {
        MenuAction action = gamePanel.getMenuAction(x, y);
        switch (action.getType()) {
            case STORY_MODE:
                gameState.setGameMode(GameMode.STORY);
                gamePanel.repaint();
                break;
            case SURVIVAL_MODE:
                startSurvivalMode();
                break;
            case RESUME:
                togglePause();
                break;
            case MAIN_MENU:
                resetToMainMenu();
                break;
            case NEXT_LEVEL:
                startLevel(gameState.getLevel() + 1);
                break;
            case RETRY:
                startLevel(gameState.getLevel());
                break;
            case LEVEL_SELECT:
                startLevel(action.getLevelNumber());
                break;
            case NONE:
            default:
                break;
        }
    }

    public void startGame() {
        startSurvivalMode();
    }

    public void startGame(int mode) {
        if (mode == 1) {
            gameState.setGameMode(GameMode.STORY);
            gamePanel.repaint();
        } else {
            startSurvivalMode();
        }
    }

    public void startSurvivalMode() {
        gameState.reset(GameMode.SURVIVAL);
        gameState.setLevel(6);
        
        Player player = createPlayer();
        gameState.setPlayer(player);

        lastShootTime = System.currentTimeMillis();
        gameTime = 0;
        enemySpawner.init();

        startGameLoop();
    }

    public void startLevel(int levelNumber) {
        gameState.reset(GameMode.STORY);
        gameState.setLevel(levelNumber);
        
        Player player = createPlayer();
        gameState.setPlayer(player);

        lastShootTime = System.currentTimeMillis();
        gameTime = 0;
        enemySpawner.init();

        startGameLoop();
    }

    private Player createPlayer() {
        Player player = new Player(
                GameFrame.WIDTH / 2 - 25,
                GameFrame.HEIGHT - 100,
                50,
                50
        );
        player.setImage(playerImage);
        return player;
    }

    private void startGameLoop() {
        if (gameLoop != null) {
            gameLoop.stopGame();
        }
        gameLoop = new GameLoop(this);
        gameLoop.start();
    }

    public void update() {
        if (gameState.isPaused() || gameState.isGameOver()) {
            return;
        }

        Player player = gameState.getPlayer();
        if (player == null) {
            return;
        }

        gameTime += 16;
        enemySpawner.setGameTime(gameTime);

        updatePlayer();
        combatSystem.updateEnemies(enemySpawner);
        combatSystem.updateBullets();
        combatSystem.updatePowerUps();
        enemySpawner.spawnEnemies();
        combatSystem.checkCollisions();
        checkWinCondition();

        if (!player.isAlive()) {
            gameOver();
        }

        gamePanel.repaint();
    }

    private void updatePlayer() {
        Player player = gameState.getPlayer();
        if (player == null) return;

        player.update();

        if (keyController.isKeyPressed(KeyController.KEY_LEFT)) {
            player.moveLeft();
        }
        if (keyController.isKeyPressed(KeyController.KEY_RIGHT)) {
            player.moveRight();
        }
        if (keyController.isKeyPressed(KeyController.KEY_UP)) {
            player.moveUp();
        }
        if (keyController.isKeyPressed(KeyController.KEY_DOWN)) {
            player.moveDown();
        }

        shoot();

        if (player.getX() < 0) player.setX(0);
        if (player.getX() > GameFrame.WIDTH - player.getWidth()) {
            player.setX(GameFrame.WIDTH - player.getWidth());
        }
        if (player.getY() < 0) player.setY(0);
        if (player.getY() > GameFrame.HEIGHT - player.getHeight()) {
            player.setY(GameFrame.HEIGHT - player.getHeight());
        }
    }

    private void shoot() {
        long currentTime = System.currentTimeMillis();
        Player player = gameState.getPlayer();
        double fireRateMultiplier = player.getFireRateMultiplier();
        int fireInterval = (int) (DEFAULT_FIRE_INTERVAL / fireRateMultiplier);
        
        if (currentTime - lastShootTime > fireInterval) {
            int bulletLevel = player.getBulletLevel();
            
            if (bulletLevel >= 1) {
                gameState.addBullet(new com.example.planewar.model.Bullet(
                        player.getX() + player.getWidth() / 2 - BULLET_CENTER_OFFSET_X,
                        player.getY() - BULLET_CENTER_OFFSET_Y,
                        true
                ));
            }
            
            if (bulletLevel >= 2) {
                gameState.addBullet(new com.example.planewar.model.Bullet(
                        player.getX() + BULLET_SIDE_OFFSET_X,
                        player.getY() - BULLET_SIDE_OFFSET_Y,
                        true
                ));
                gameState.addBullet(new com.example.planewar.model.Bullet(
                        player.getX() + player.getWidth() - BULLET_RIGHT_OFFSET_X,
                        player.getY() - BULLET_SIDE_OFFSET_Y,
                        true
                ));
            }
            
            if (bulletLevel >= 3) {
                com.example.planewar.model.Bullet bullet = new com.example.planewar.model.Bullet(
                        player.getX() + player.getWidth() / 2 - BULLET_CENTER_OFFSET_X,
                        player.getY() - BULLET_CENTER_OFFSET_Y,
                        true
                );
                bullet.setSpeed(bullet.getSpeed() + POWER_BULLET_SPEED_BONUS);
                gameState.addBullet(bullet);
            }
            
            lastShootTime = currentTime;
        }
    }

    private void checkWinCondition() {
        if (gameState.getGameMode() == GameMode.STORY) {
            LevelConfig levelConfig = LevelConfig.getLevels()[Math.min(gameState.getLevel() - 1, 4)];
            
            if (levelConfig.hasBoss() && !enemySpawner.isBossSpawned() && 
                gameState.getEnemiesKilled() >= enemySpawner.getBossSpawnKillThreshold()) {
                enemySpawner.spawnBoss(gameState.getLevel() == 5);
            }
            
            switch (levelConfig.getWinCondition()) {
                case SCORE:
                    if (gameState.getScore() >= levelConfig.getTargetScore()) {
                        levelComplete();
                    }
                    break;
                case KILL_COUNT:
                    if (gameState.getEnemiesKilled() >= levelConfig.getTargetKillCount()) {
                        levelComplete();
                    }
                    break;
            }
        } else if (gameState.getGameMode() == GameMode.SURVIVAL) {
            int bossThreshold = gameState.getBossWave() * enemySpawner.getSurvivalBossThresholdBase();
            if (!enemySpawner.isBossSpawned() && gameState.getScore() >= bossThreshold) {
                enemySpawner.spawnBoss(gameState.getBossWave() >= 3);
                gameState.incrementBossWave();
            }
        }
    }

    private void levelComplete() {
        gameState.setWin(true);
        gameState.setGameOver(true);
        
        if (gameState.getLevel() < 5) {
            gameState.setScore(gameState.getScore() + 500);
        }
        
        if (gameState.getScore() > gameState.getHighScore()) {
            gameState.setHighScore(gameState.getScore());
            HighScoreManager.saveHighScore(gameState.getScore());
        }
        
        gamePanel.repaint();
    }

    private void gameOver() {
        gameState.setGameOver(true);
        
        if (gameState.getScore() > gameState.getHighScore()) {
            gameState.setHighScore(gameState.getScore());
            HighScoreManager.saveHighScore(gameState.getScore());
        }
        
        gamePanel.repaint();
    }

    public void resetToMainMenu() {
        if (gameLoop != null) {
            gameLoop.stopGame();
        }
        gameState = new GameState();
        gameState.setHighScore(HighScoreManager.loadHighScore());
        
        enemySpawner = new EnemySpawner(gameState);
        combatSystem = new CombatSystem(gameState);
        loadImages();
        
        gamePanel.setController(this);
        gamePanel.repaint();
    }

    public void togglePause() {
        gameState.setPaused(!gameState.isPaused());
        gamePanel.repaint();
    }

    public boolean isPaused() {
        return gameState.isPaused();
    }

    public GameState getGameState() {
        return gameState;
    }

    public Image getHpImage() {
        return hpImage;
    }
}