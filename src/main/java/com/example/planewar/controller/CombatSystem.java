package com.example.planewar.controller;

import com.example.planewar.model.Bullet;
import com.example.planewar.model.Enemy;
import com.example.planewar.model.Enemy.EnemyType;
import com.example.planewar.model.GameMode;
import com.example.planewar.model.GameState;
import com.example.planewar.model.Player;
import com.example.planewar.model.PowerUp;
import com.example.planewar.util.CollisionDetector;
import com.example.planewar.view.GameFrame;

import java.util.ArrayList;
import java.util.List;

public class CombatSystem {
    private GameState gameState;
    
    public CombatSystem(GameState gameState) {
        this.gameState = gameState;
    }

    public void checkCollisions() {
        Player player = gameState.getPlayer();
        if (player == null) return;

        checkPlayerEnemyCollisions(player);
        checkBulletEnemyCollisions(player);
        checkBulletPlayerCollisions(player);
        checkPlayerPowerUpCollisions(player);
    }

    private void checkPlayerEnemyCollisions(Player player) {
        for (Enemy enemy : gameState.getEnemies()) {
            if (CollisionDetector.checkPlayerEnemyCollision(player, enemy)) {
                player.loseLife();
                if (enemy.getType() != EnemyType.BOSS && enemy.getType() != EnemyType.FINAL_BOSS) {
                    enemy.destroy();
                }
            }
        }
    }

    private void checkBulletEnemyCollisions(Player player) {
        for (Enemy enemy : gameState.getEnemies()) {
            for (Bullet bullet : gameState.getBullets()) {
                if (bullet.isPlayerBullet() && CollisionDetector.checkBulletEnemyCollision(bullet, enemy)) {
                    enemy.takeDamage(bullet.getDamage() * player.getDamageMultiplier());
                    bullet.setDestroyed(true);
                }
            }
        }
    }

    private void checkBulletPlayerCollisions(Player player) {
        for (Bullet bullet : gameState.getBullets()) {
            if (!bullet.isPlayerBullet() && CollisionDetector.checkBulletPlayerCollision(bullet, player)) {
                player.loseLife();
                bullet.setDestroyed(true);
            }
        }
    }

    private void checkPlayerPowerUpCollisions(Player player) {
        for (PowerUp powerUp : gameState.getPowerUps()) {
            if (CollisionDetector.checkPlayerPowerUpCollision(player, powerUp)) {
                applyPowerUp(player, powerUp);
                powerUp.setCollected(true);
            }
        }
    }

    public void applyPowerUp(Player player, PowerUp powerUp) {
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

    public void updateEnemies(EnemySpawner spawner) {
        List<Enemy> enemiesToRemove = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        
        for (Enemy enemy : gameState.getEnemies()) {
            if (enemy.getType() == EnemyType.BOSS || enemy.getType() == EnemyType.FINAL_BOSS) {
                spawner.updateBossMovement(enemy);
            } else {
                enemy.update();
            }
            
            if (enemy.getY() > GameFrame.HEIGHT + 50) {
                enemiesToRemove.add(enemy);
            }
            
            if (enemy.isDestroyed()) {
                spawner.spawnPowerUp(enemy);
                enemiesToRemove.add(enemy);
                gameState.addScore(enemy.getScore());
                
                if (enemy.getType() == EnemyType.BOSS || enemy.getType() == EnemyType.FINAL_BOSS) {
                    spawner.setBossSpawned(false);
                    if (gameState.getGameMode() == GameMode.STORY && gameState.getLevel() >= 4) {
                        gameState.setWin(true);
                        gameState.setGameOver(true);
                    }
                } else {
                    gameState.incrementEnemiesKilled();
                }
            }
            
            if (enemy.getY() > 0 && currentTime - enemy.getLastShootTime() > enemy.getType().getShootInterval()) {
                spawner.shootEnemyBullet(enemy);
                enemy.setLastShootTime(currentTime);
            }
        }
        
        for (Enemy enemy : enemiesToRemove) {
            gameState.removeEnemy(enemy);
        }
    }

    public void updateBullets() {
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

    public void updatePowerUps() {
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
}