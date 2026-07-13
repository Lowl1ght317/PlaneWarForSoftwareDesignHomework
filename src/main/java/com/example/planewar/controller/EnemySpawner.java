package com.example.planewar.controller;

import com.example.planewar.model.Bullet;
import com.example.planewar.model.Enemy;
import com.example.planewar.model.Enemy.EnemyType;
import com.example.planewar.model.GameMode;
import com.example.planewar.model.GameState;
import com.example.planewar.model.LevelConfig;
import com.example.planewar.model.PowerUp;
import com.example.planewar.view.GameFrame;

import java.awt.Image;
import java.util.Random;

public class EnemySpawner {
    private GameState gameState;
    
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
    
    private long lastEnemySpawnTime;
    private boolean bossSpawned;
    private long gameTime;
    
    private static final Random RANDOM = new Random();
    
    private static final long BOSS_MOVEMENT_PERIOD = 500;
    private static final int BOSS_MOVEMENT_RANGE = 150;
    private static final int BOSS_SPAWN_KILL_THRESHOLD = 10;
    private static final int BOSS_BULLET_LEFT_OFFSET = 20;
    private static final int BOSS_BULLET_RIGHT_OFFSET = 26;
    private static final int BULLET_CENTER_OFFSET_X = 3;
    private static final int ENEMY_BULLET_OFFSET_X = 3;
    
    private static final int SURVIVAL_BOSS_THRESHOLD_BASE = 12000;
    private static final int SURVIVAL_SPAWN_INTERVAL_BASE = 800;
    private static final int SURVIVAL_SPAWN_INTERVAL_MIN = 300;
    private static final int SURVIVAL_SPAWN_INTERVAL_DECREASE = 30;
    private static final int SURVIVAL_MAX_ENEMIES_BASE = 8;
    private static final int SURVIVAL_MAX_ENEMIES_INCREMENT = 2;
    private static final int SURVIVAL_MAX_ENEMIES_LIMIT = 15;
    private static final int SURVIVAL_NORMAL_RATE_BASE = 70;
    private static final int SURVIVAL_NORMAL_RATE_DECREASE = 5;
    private static final int SURVIVAL_NORMAL_RATE_MIN = 30;
    private static final int SURVIVAL_FAST_RATE_BASE = 20;
    private static final int SURVIVAL_FAST_RATE_INCREMENT = 3;
    private static final int SURVIVAL_FAST_RATE_MAX = 40;

    public EnemySpawner(GameState gameState) {
        this.gameState = gameState;
    }

    public void setImages(Image normalEnemyImage, Image fastEnemyImage, Image eliteEnemyImage,
                          Image bossImage, Image finalBossImage,
                          Image healthPowerUpImage, Image shieldPowerUpImage,
                          Image threeWayPowerUpImage, Image fastShootPowerUpImage,
                          Image speedUpPowerUpImage, Image superDamagePowerUpImage) {
        this.normalEnemyImage = normalEnemyImage;
        this.fastEnemyImage = fastEnemyImage;
        this.eliteEnemyImage = eliteEnemyImage;
        this.bossImage = bossImage;
        this.finalBossImage = finalBossImage;
        this.healthPowerUpImage = healthPowerUpImage;
        this.shieldPowerUpImage = shieldPowerUpImage;
        this.threeWayPowerUpImage = threeWayPowerUpImage;
        this.fastShootPowerUpImage = fastShootPowerUpImage;
        this.speedUpPowerUpImage = speedUpPowerUpImage;
        this.superDamagePowerUpImage = superDamagePowerUpImage;
    }

    public void init() {
        lastEnemySpawnTime = System.currentTimeMillis();
        bossSpawned = false;
        gameTime = 0;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }

    public void spawnBoss(boolean isFinal) {
        bossSpawned = true;
        EnemyType bossType = isFinal ? EnemyType.FINAL_BOSS : EnemyType.BOSS;
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

    public void spawnEnemies() {
        LevelConfig levelConfig = LevelConfig.getLevels()[Math.min(gameState.getLevel() - 1, 4)];

        long currentTime = System.currentTimeMillis();
        int spawnInterval = levelConfig.getEnemySpawnInterval();
        int maxEnemies = levelConfig.getMaxEnemiesOnScreen();
        
        if (gameState.getGameMode() == GameMode.SURVIVAL) {
            int bossWave = gameState.getBossWave();
            spawnInterval = Math.max(SURVIVAL_SPAWN_INTERVAL_MIN, SURVIVAL_SPAWN_INTERVAL_BASE - bossWave * SURVIVAL_SPAWN_INTERVAL_DECREASE);
            maxEnemies = Math.min(SURVIVAL_MAX_ENEMIES_LIMIT, SURVIVAL_MAX_ENEMIES_BASE + bossWave * SURVIVAL_MAX_ENEMIES_INCREMENT);
        }
        
        if (currentTime - lastEnemySpawnTime > spawnInterval) {
            int enemyCount = 0;
            for (Enemy enemy : gameState.getEnemies()) {
                if (enemy.getType() != EnemyType.BOSS && enemy.getType() != EnemyType.FINAL_BOSS) {
                    enemyCount++;
                }
            }
            
            if (enemyCount < maxEnemies) {
                Enemy enemy = gameState.getGameMode() == GameMode.SURVIVAL 
                        ? createSurvivalEnemy(GameFrame.WIDTH) 
                        : Enemy.createRandomEnemy(GameFrame.WIDTH);
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

    private Enemy createSurvivalEnemy(int screenWidth) {
        int bossWave = gameState.getBossWave();
        int rand = RANDOM.nextInt(100);
        EnemyType type;
        
        int normalRate = Math.max(SURVIVAL_NORMAL_RATE_MIN, SURVIVAL_NORMAL_RATE_BASE - bossWave * SURVIVAL_NORMAL_RATE_DECREASE);
        int fastRate = normalRate + Math.min(SURVIVAL_FAST_RATE_MAX, SURVIVAL_FAST_RATE_BASE + bossWave * SURVIVAL_FAST_RATE_INCREMENT);
        
        if (rand < normalRate) {
            type = EnemyType.NORMAL;
        } else if (rand < fastRate) {
            type = EnemyType.FAST;
        } else {
            type = EnemyType.ELITE;
        }
        
        int x = RANDOM.nextInt(screenWidth - type.getWidth());
        return new Enemy(x, -type.getHeight(), type);
    }

    public void spawnPowerUp(Enemy enemy) {
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
        if (RANDOM.nextInt(100) < dropRate) {
            PowerUp.PowerUpType type;
            int randType = RANDOM.nextInt(6);
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

    public void updateBossMovement(Enemy boss) {
        if (boss.getY() < 50) {
            boss.setY(boss.getY() + 1);
        } else {
            long time = gameTime / BOSS_MOVEMENT_PERIOD;
            boss.setX((int) (GameFrame.WIDTH / 2 + Math.sin(time) * BOSS_MOVEMENT_RANGE - boss.getWidth() / 2));
        }
    }

    public void shootEnemyBullet(Enemy enemy) {
        if (enemy.getType() == EnemyType.BOSS || enemy.getType() == EnemyType.FINAL_BOSS) {
            Bullet bullet1 = new Bullet(
                    enemy.getX() + BOSS_BULLET_LEFT_OFFSET,
                    enemy.getY() + enemy.getHeight(),
                    false
            );
            Bullet bullet2 = new Bullet(
                    enemy.getX() + enemy.getWidth() / 2 - BULLET_CENTER_OFFSET_X,
                    enemy.getY() + enemy.getHeight(),
                    false
            );
            Bullet bullet3 = new Bullet(
                    enemy.getX() + enemy.getWidth() - BOSS_BULLET_RIGHT_OFFSET,
                    enemy.getY() + enemy.getHeight(),
                    false
            );
            gameState.addBullet(bullet1);
            gameState.addBullet(bullet2);
            gameState.addBullet(bullet3);
        } else {
            Bullet bullet = new Bullet(
                    enemy.getX() + enemy.getWidth() / 2 - ENEMY_BULLET_OFFSET_X,
                    enemy.getY() + enemy.getHeight(),
                    false
            );
            gameState.addBullet(bullet);
        }
    }

    public boolean isBossSpawned() {
        return bossSpawned;
    }

    public void setBossSpawned(boolean bossSpawned) {
        this.bossSpawned = bossSpawned;
    }

    public int getBossSpawnKillThreshold() {
        return BOSS_SPAWN_KILL_THRESHOLD;
    }

    public int getSurvivalBossThresholdBase() {
        return SURVIVAL_BOSS_THRESHOLD_BASE;
    }
}