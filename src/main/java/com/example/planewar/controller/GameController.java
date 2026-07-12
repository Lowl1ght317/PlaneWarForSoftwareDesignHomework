package com.example.planewar.controller;

import com.example.planewar.model.Enemy;
import com.example.planewar.model.Enemy.EnemyType;
import com.example.planewar.model.Bullet;
import com.example.planewar.model.GameState;
import com.example.planewar.model.GameMode;
import com.example.planewar.model.LevelConfig;
import com.example.planewar.model.Player;
import com.example.planewar.model.PowerUp;
import com.example.planewar.util.CollisionDetector;
import com.example.planewar.util.HighScoreManager;
import com.example.planewar.util.ImageLoader;
import com.example.planewar.view.GameFrame;
import com.example.planewar.view.GamePanel;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController {
    private GameState gameState;
    private GameFrame gameFrame;
    private GamePanel gamePanel;
    private GameLoop gameLoop;
    private KeyController keyController;

    private Image playerImage;
    private Image normalEnemyImage;
    private Image fastEnemyImage;
    private Image eliteEnemyImage;
    private Image bossImage;
    private Image finalBossImage;
    private Image healthPowerUpImage;
    private Image shieldPowerUpImage;
    private Image threeWayPowerUpImage;
    private Image fastShootPowerUpImage;
    private Image speedUpPowerUpImage;
    private Image superDamagePowerUpImage;
    private Image hpImage;

    private long lastEnemySpawnTime;
    private long lastShootTime;
    private boolean bossSpawned;

    public GameController() {
        gameState = new GameState();
        gameState.setHighScore(HighScoreManager.loadHighScore());

        playerImage = ImageLoader.loadPlayerImage();
        normalEnemyImage = ImageLoader.loadNormalEnemyImage();
        fastEnemyImage = ImageLoader.loadFastEnemyImage();
        eliteEnemyImage = ImageLoader.loadEliteEnemyImage();
        bossImage = ImageLoader.loadBossImage();
        finalBossImage = ImageLoader.loadFinalBossImage();
        healthPowerUpImage = ImageLoader.loadHealthPowerUpImage();
        shieldPowerUpImage = ImageLoader.loadShieldPowerUpImage();
        threeWayPowerUpImage = ImageLoader.loadThreeWayPowerUpImage();
        fastShootPowerUpImage = ImageLoader.loadFastShootPowerUpImage();
        speedUpPowerUpImage = ImageLoader.loadSpeedUpPowerUpImage();
        superDamagePowerUpImage = ImageLoader.loadSuperDamagePowerUpImage();
        
        hpImage = healthPowerUpImage;

        gameFrame = new GameFrame();
        gamePanel = gameFrame.getGamePanel();
        gamePanel.setController(this);

        keyController = new KeyController(this);
        gameFrame.addKeyListener(keyController);
        
        gameFrame.addMouseController(this);

        bossSpawned = false;
    }

    public void handleMouseClick(int x, int y) {
        if (!gameState.isGameStarted()) {
            if (gameState.getGameMode() == null) {
                handleMainMenuClick(x, y);
            } else {
                handleLevelSelectClick(x, y);
            }
        } else if (gameState.isPaused()) {
            handlePauseMenuClick(x, y);
        } else if (gameState.isGameOver()) {
            handleGameOverClick(x, y);
        }
    }

    private void handlePauseMenuClick(int x, int y) {
        if (x >= GamePanel.PAUSE_RESUME_X && x <= GamePanel.PAUSE_RESUME_X + GamePanel.PAUSE_RESUME_WIDTH &&
            y >= GamePanel.PAUSE_RESUME_Y && y <= GamePanel.PAUSE_RESUME_Y + GamePanel.PAUSE_RESUME_HEIGHT) {
            togglePause();
        } else if (x >= GamePanel.PAUSE_MENU_X && x <= GamePanel.PAUSE_MENU_X + GamePanel.PAUSE_MENU_WIDTH &&
                   y >= GamePanel.PAUSE_MENU_Y && y <= GamePanel.PAUSE_MENU_Y + GamePanel.PAUSE_MENU_HEIGHT) {
            resetToMainMenu();
        }
    }

    private void handleGameOverClick(int x, int y) {
        if (x >= GamePanel.WIN_NEXT_X && x <= GamePanel.WIN_NEXT_X + GamePanel.WIN_NEXT_WIDTH &&
            y >= GamePanel.WIN_NEXT_Y && y <= GamePanel.WIN_NEXT_Y + GamePanel.WIN_NEXT_HEIGHT) {
            if (gameState.isWin() && gameState.getLevel() < 5) {
                startLevel(gameState.getLevel() + 1);
            } else if (!gameState.isWin()) {
                startLevel(gameState.getLevel());
            }
            return;
        }
        if (x >= GamePanel.WIN_MENU_X && x <= GamePanel.WIN_MENU_X + GamePanel.WIN_MENU_WIDTH &&
            y >= GamePanel.WIN_MENU_Y && y <= GamePanel.WIN_MENU_Y + GamePanel.WIN_MENU_HEIGHT) {
            resetToMainMenu();
        }
    }

    private void handleMainMenuClick(int x, int y) {
        if (x >= GamePanel.MENU_STORY_X && x <= GamePanel.MENU_STORY_X + GamePanel.MENU_STORY_WIDTH &&
            y >= GamePanel.MENU_STORY_Y && y <= GamePanel.MENU_STORY_Y + GamePanel.MENU_STORY_HEIGHT) {
            gameState.setGameMode(GameMode.STORY);
            gamePanel.repaintGame();
        } else if (x >= GamePanel.MENU_SURVIVAL_X && x <= GamePanel.MENU_SURVIVAL_X + GamePanel.MENU_SURVIVAL_WIDTH &&
                   y >= GamePanel.MENU_SURVIVAL_Y && y <= GamePanel.MENU_SURVIVAL_Y + GamePanel.MENU_SURVIVAL_HEIGHT) {
            startSurvivalMode();
        }
    }

    private void handleLevelSelectClick(int x, int y) {
        LevelConfig[] levels = LevelConfig.getLevels();
        int startY = 150;
        int levelWidth = 400;
        int levelHeight = 70;
        int spacing = 20;

        for (int i = 0; i < levels.length; i++) {
            int levelX = (GameFrame.WIDTH - levelWidth) / 2;
            int levelY = startY + i * (levelHeight + spacing);
            
            if (x >= levelX && x <= levelX + levelWidth &&
                y >= levelY && y <= levelY + levelHeight) {
                if (i + 1 == 6) {
                    startSurvivalMode();
                } else {
                    startLevel(i + 1);
                }
                return;
            }
        }
    }

    public void startGame() {
        startSurvivalMode();
    }

    public void startGame(int mode) {
        if (mode == 1) {
            gameState.setGameMode(GameMode.STORY);
            gamePanel.repaintGame();
        } else {
            startSurvivalMode();
        }
    }

    public void startSurvivalMode() {
        gameState.reset(GameMode.SURVIVAL);
        gameState.setLevel(6);
        
        Player player = new Player(
                GameFrame.WIDTH / 2 - 25,
                GameFrame.HEIGHT - 100,
                50,
                50
        );
        player.setImage(playerImage);
        gameState.setPlayer(player);

        lastEnemySpawnTime = System.currentTimeMillis();
        lastShootTime = System.currentTimeMillis();
        bossSpawned = false;

        if (gameLoop != null) {
            gameLoop.stopGame();
        }
        gameLoop = new GameLoop(this);
        gameLoop.start();
    }

    public void startLevel(int levelNumber) {
        gameState.reset(GameMode.STORY);
        gameState.setLevel(levelNumber);
        
        Player player = new Player(
                GameFrame.WIDTH / 2 - 25,
                GameFrame.HEIGHT - 100,
                50,
                50
        );
        player.setImage(playerImage);
        gameState.setPlayer(player);

        lastEnemySpawnTime = System.currentTimeMillis();
        lastShootTime = System.currentTimeMillis();
        bossSpawned = false;

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

        updatePlayer();
        updateEnemies();
        updateBullets();
        updatePowerUps();
        spawnEnemies();
        checkCollisions();
        checkWinCondition();

        if (!gameState.getPlayer().isAlive()) {
            gameOver();
        }

        gamePanel.repaintGame();
    }

    private void updatePlayer() {
        Player player = gameState.getPlayer();
        if (player == null) return;

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
        int fireInterval = (int) (200 / fireRateMultiplier);
        
        if (currentTime - lastShootTime > fireInterval) {
            int bulletLevel = player.getBulletLevel();
            
            if (bulletLevel >= 1) {
                Bullet bullet = new Bullet(
                        player.getX() + player.getWidth() / 2 - 3,
                        player.getY() - 15,
                        true
                );
                gameState.addBullet(bullet);
            }
            
            if (bulletLevel >= 2) {
                Bullet bulletLeft = new Bullet(
                        player.getX() + 5,
                        player.getY() - 10,
                        true
                );
                Bullet bulletRight = new Bullet(
                        player.getX() + player.getWidth() - 11,
                        player.getY() - 10,
                        true
                );
                gameState.addBullet(bulletLeft);
                gameState.addBullet(bulletRight);
            }
            
            if (bulletLevel >= 3) {
                Bullet bulletLeft = new Bullet(
                        player.getX() + player.getWidth() / 2 - 3,
                        player.getY() - 15,
                        true
                );
                bulletLeft.setSpeed(bulletLeft.getSpeed() + 3);
                gameState.addBullet(bulletLeft);
            }
            
            lastShootTime = currentTime;
        }
    }

    private void updateEnemies() {
        List<Enemy> enemiesToRemove = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        
        for (Enemy enemy : gameState.getEnemies()) {
            if (enemy.getType() == EnemyType.BOSS || enemy.getType() == EnemyType.FINAL_BOSS) {
                updateBossMovement(enemy);
            } else {
                enemy.update();
            }
            
            if (enemy.getY() > GameFrame.HEIGHT + 50) {
                enemiesToRemove.add(enemy);
            }
            
            if (enemy.isDestroyed()) {
                spawnPowerUp(enemy);
                enemiesToRemove.add(enemy);
                gameState.addScore(enemy.getScore());
                
                if (enemy.getType() == EnemyType.BOSS || enemy.getType() == EnemyType.FINAL_BOSS) {
                    bossSpawned = false;
                } else {
                    gameState.incrementEnemiesKilled();
                }
            }
            
            if (enemy.getY() > 0 && currentTime - enemy.getLastShootTime() > enemy.getType().getShootInterval()) {
                shootEnemyBullet(enemy);
                enemy.setLastShootTime(currentTime);
            }
        }
        
        for (Enemy enemy : enemiesToRemove) {
            gameState.removeEnemy(enemy);
        }
    }

    private void updateBossMovement(Enemy boss) {
        if (boss.getY() < 50) {
            boss.setY(boss.getY() + 1);
        } else {
            long time = System.currentTimeMillis() / 500;
            boss.setX((int) (GameFrame.WIDTH / 2 + Math.sin(time) * 150 - boss.getWidth() / 2));
        }
    }

    private void shootEnemyBullet(Enemy enemy) {
        if (enemy.getType() == EnemyType.BOSS || enemy.getType() == EnemyType.FINAL_BOSS) {
            Bullet bullet1 = new Bullet(
                    enemy.getX() + 20,
                    enemy.getY() + enemy.getHeight(),
                    false
            );
            Bullet bullet2 = new Bullet(
                    enemy.getX() + enemy.getWidth() / 2 - 3,
                    enemy.getY() + enemy.getHeight(),
                    false
            );
            Bullet bullet3 = new Bullet(
                    enemy.getX() + enemy.getWidth() - 26,
                    enemy.getY() + enemy.getHeight(),
                    false
            );
            gameState.addBullet(bullet1);
            gameState.addBullet(bullet2);
            gameState.addBullet(bullet3);
        } else {
            Bullet bullet = new Bullet(
                    enemy.getX() + enemy.getWidth() / 2 - 3,
                    enemy.getY() + enemy.getHeight(),
                    false
            );
            gameState.addBullet(bullet);
        }
    }

    private void checkWinCondition() {
        if (gameState.getGameMode() == GameMode.STORY) {
            LevelConfig levelConfig = LevelConfig.getLevels()[Math.min(gameState.getLevel() - 1, 4)];
            
            if (levelConfig.hasBoss() && !bossSpawned && gameState.getEnemiesKilled() >= 10) {
                spawnBoss();
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
            int bossWave = 1;
            int bossThreshold = bossWave * 12000;
            if (!bossSpawned && gameState.getScore() >= bossThreshold) {
                spawnSurvivalBoss();
                bossWave++;
            }
        }
    }

    private void spawnBoss() {
        bossSpawned = true;
        EnemyType bossType = gameState.getLevel() == 5 ? EnemyType.FINAL_BOSS : EnemyType.BOSS;
        Enemy boss = new Enemy(
                GameFrame.WIDTH / 2 - bossType.getWidth() / 2,
                -bossType.getHeight(),
                bossType
        );
        
        if (bossType == EnemyType.FINAL_BOSS) {
            boss.setImage(finalBossImage);
        } else {
            boss.setImage(bossImage);
        }
        
        gameState.addEnemy(boss);
    }

    private void spawnSurvivalBoss() {
        bossSpawned = true;
        EnemyType bossType = gameState.getLevel() >= 3 ? EnemyType.FINAL_BOSS : EnemyType.BOSS;
        Enemy boss = new Enemy(
                GameFrame.WIDTH / 2 - bossType.getWidth() / 2,
                -bossType.getHeight(),
                bossType
        );
        
        if (bossType == EnemyType.FINAL_BOSS) {
            boss.setImage(finalBossImage);
        } else {
            boss.setImage(bossImage);
        }
        
        gameState.addEnemy(boss);
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
        
        gamePanel.repaintGame();
    }

    private void spawnPowerUp(Enemy enemy) {
        Random random = new Random();
        int dropRate = 20;
        switch (enemy.getType()) {
            case NORMAL:
                dropRate = 20;
                break;
            case FAST:
                dropRate = 30;
                break;
            case ELITE:
                dropRate = 45;
                break;
            case BOSS:
                dropRate = 90;
                break;
            case FINAL_BOSS:
                dropRate = 100;
                break;
        }
        if (random.nextInt(100) < dropRate) {
            PowerUp.PowerUpType type;
            int randType = random.nextInt(6);
            switch (randType) {
                case 0:
                    type = PowerUp.PowerUpType.HEALTH;
                    break;
                case 1:
                    type = PowerUp.PowerUpType.SHIELD;
                    break;
                case 2:
                    type = PowerUp.PowerUpType.WEAPON_3WAY;
                    break;
                case 3:
                    type = PowerUp.PowerUpType.WEAPON_FAST;
                    break;
                case 4:
                    type = PowerUp.PowerUpType.SPEED_UP;
                    break;
                default:
                    type = PowerUp.PowerUpType.SUPER_DAMAGE;
                    break;
            }
            PowerUp powerUp = new PowerUp(
                    enemy.getX() + enemy.getWidth() / 2 - 15,
                    enemy.getY(),
                    type
            );
            
            switch (type) {
                case HEALTH:
                    powerUp.setImage(healthPowerUpImage);
                    break;
                case SHIELD:
                    powerUp.setImage(shieldPowerUpImage);
                    break;
                case WEAPON_3WAY:
                    powerUp.setImage(threeWayPowerUpImage);
                    break;
                case WEAPON_FAST:
                    powerUp.setImage(fastShootPowerUpImage);
                    break;
                case SPEED_UP:
                    powerUp.setImage(speedUpPowerUpImage);
                    break;
                case SUPER_DAMAGE:
                    powerUp.setImage(superDamagePowerUpImage);
                    break;
            }
            
            gameState.addPowerUp(powerUp);
        }
    }

    private void updateBullets() {
        List<Bullet> bulletsToRemove = new ArrayList<>();
        
        for (Bullet bullet : gameState.getBullets()) {
            bullet.update();
            
            if (bullet.getY() < -20 || bullet.getY() > GameFrame.HEIGHT) {
                bulletsToRemove.add(bullet);
            }
            
            if (bullet.isDestroyed()) {
                bulletsToRemove.add(bullet);
            }
        }
        
        for (Bullet bullet : bulletsToRemove) {
            gameState.removeBullet(bullet);
        }
    }

    private void updatePowerUps() {
        List<PowerUp> powerUpsToRemove = new ArrayList<>();
        
        for (PowerUp powerUp : gameState.getPowerUps()) {
            powerUp.update();
            
            if (powerUp.getY() > GameFrame.HEIGHT) {
                powerUpsToRemove.add(powerUp);
            }
            
            if (powerUp.isCollected()) {
                powerUpsToRemove.add(powerUp);
            }
        }
        
        for (PowerUp powerUp : powerUpsToRemove) {
            gameState.removePowerUp(powerUp);
        }
    }

    private void spawnEnemies() {
        LevelConfig levelConfig = LevelConfig.getLevels()[Math.min(gameState.getLevel() - 1, 4)];

        long currentTime = System.currentTimeMillis();
        int spawnInterval = levelConfig.getEnemySpawnInterval();
        
        if (gameState.getGameMode() == GameMode.SURVIVAL) {
            spawnInterval = Math.max(300, 1000 - gameState.getLevel() * 50);
        }
        
        if (currentTime - lastEnemySpawnTime > spawnInterval) {
            int enemyCount = 0;
            for (Enemy enemy : gameState.getEnemies()) {
                if (enemy.getType() != EnemyType.BOSS && enemy.getType() != EnemyType.FINAL_BOSS) {
                    enemyCount++;
                }
            }
            
            if (enemyCount < levelConfig.getMaxEnemiesOnScreen()) {
                Enemy enemy = Enemy.createRandomEnemy(GameFrame.WIDTH);
                switch (enemy.getType()) {
                    case NORMAL:
                        enemy.setImage(normalEnemyImage);
                        break;
                    case FAST:
                        enemy.setImage(fastEnemyImage);
                        break;
                    case ELITE:
                        enemy.setImage(eliteEnemyImage);
                        break;
                }
                gameState.addEnemy(enemy);
            }
            lastEnemySpawnTime = currentTime;
        }
    }

    private void checkCollisions() {
        Player player = gameState.getPlayer();
        
        for (Enemy enemy : gameState.getEnemies()) {
            if (CollisionDetector.checkPlayerEnemyCollision(player, enemy)) {
                player.loseLife();
                if (enemy.getType() != EnemyType.BOSS && enemy.getType() != EnemyType.FINAL_BOSS) {
                    enemy.setDestroyed(true);
                }
            }
            
            for (Bullet bullet : gameState.getBullets()) {
                if (bullet.isPlayerBullet() && CollisionDetector.checkBulletEnemyCollision(bullet, enemy)) {
                    enemy.takeDamage(bullet.getDamage() * player.getDamageMultiplier());
                    bullet.setDestroyed(true);
                }
            }
        }
        
        for (Bullet bullet : gameState.getBullets()) {
            if (!bullet.isPlayerBullet() && CollisionDetector.checkBulletPlayerCollision(bullet, player)) {
                player.loseLife();
                bullet.setDestroyed(true);
            }
        }
        
        for (PowerUp powerUp : gameState.getPowerUps()) {
            if (CollisionDetector.checkPlayerPowerUpCollision(player, powerUp)) {
                applyPowerUp(player, powerUp);
                powerUp.setCollected(true);
            }
        }
    }

    private void applyPowerUp(Player player, PowerUp powerUp) {
        switch (powerUp.getType()) {
            case HEALTH:
                player.setLives(player.getLives() + 1);
                break;
            case SHIELD:
                player.setInvincible(true);
                break;
            case WEAPON_3WAY:
                player.setBulletLevel(3, 10000);
                break;
            case WEAPON_FAST:
                player.setFireRateMultiplier(2.0, 8000);
                break;
            case SPEED_UP:
                player.setSpeedMultiplier(1.5, 8000);
                break;
            case SUPER_DAMAGE:
                player.setDamageMultiplier(3, 15000);
                break;
        }
    }

    private void gameOver() {
        gameState.setGameOver(true);
        
        if (gameState.getScore() > gameState.getHighScore()) {
            gameState.setHighScore(gameState.getScore());
            HighScoreManager.saveHighScore(gameState.getScore());
        }
        
        gamePanel.repaintGame();
    }

    public void resetToMainMenu() {
        if (gameLoop != null) {
            gameLoop.stopGame();
        }
        gameState = new GameState();
        gameState.setHighScore(HighScoreManager.loadHighScore());
        gamePanel.setController(this);
        gamePanel.repaintGame();
    }

    public void togglePause() {
        gameState.setPaused(!gameState.isPaused());
        gamePanel.repaintGame();
    }

    public GameState getGameState() {
        return gameState;
    }

    public KeyController getKeyController() {
        return keyController;
    }

    public Image getHpImage() {
        return hpImage;
    }
}