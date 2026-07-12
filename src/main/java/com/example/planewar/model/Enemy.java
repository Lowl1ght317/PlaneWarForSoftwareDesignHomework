package com.example.planewar.model;

import java.awt.Image;
import java.util.Random;

public class Enemy {
    private int x;
    private int y;
    private int width;
    private int height;
    private int speed;
    private int health;
    private int maxHealth;
    private int score;
    private Image image;
    private EnemyType type;
    private boolean isDestroyed;

    public enum EnemyType {
        NORMAL(50, 50, 1, 50, 4000),
        FAST(50, 50, 1, 100, 3000),
        ELITE(50, 50, 2, 200, 1500),
        BOSS(100, 100, 50, 5000, 1200),
        FINAL_BOSS(120, 120, 100, 10000, 800);

        private final int width;
        private final int height;
        private final int health;
        private final int score;
        private final int shootInterval;

        EnemyType(int width, int height, int health, int score, int shootInterval) {
            this.width = width;
            this.height = height;
            this.health = health;
            this.score = score;
            this.shootInterval = shootInterval;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getHealth() {
            return health;
        }

        public int getScore() {
            return score;
        }

        public int getShootInterval() {
            return shootInterval;
        }
    }

    private long lastShootTime;

    public Enemy(int x, int y, EnemyType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.width = type.getWidth();
        this.height = type.getHeight();
        this.health = type.getHealth();
        this.maxHealth = type.getHealth();
        this.score = type.getScore();
        this.speed = 2;
        this.isDestroyed = false;
        this.lastShootTime = System.currentTimeMillis() - type.getShootInterval();
    }

    public static Enemy createRandomEnemy(int screenWidth) {
        Random random = new Random();
        int rand = random.nextInt(100);
        EnemyType type;
        
        if (rand < 70) {
            type = EnemyType.NORMAL;
        } else if (rand < 90) {
            type = EnemyType.FAST;
        } else {
            type = EnemyType.ELITE;
        }
        
        int x = random.nextInt(screenWidth - type.getWidth());
        return new Enemy(x, -type.getHeight(), type);
    }

    public void update() {
        y += speed;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            isDestroyed = true;
        }
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setDestroyed(boolean destroyed) {
        isDestroyed = destroyed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSpeed() {
        return speed;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getScore() {
        return score;
    }

    public Image getImage() {
        return image;
    }

    public EnemyType getType() {
        return type;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public long getLastShootTime() {
        return lastShootTime;
    }

    public void setLastShootTime(long lastShootTime) {
        this.lastShootTime = lastShootTime;
    }
}