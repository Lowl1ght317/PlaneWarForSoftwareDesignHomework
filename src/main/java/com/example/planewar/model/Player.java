package com.example.planewar.model;

import java.awt.Image;

public class Player {
    private int x;
    private int y;
    private int width;
    private int height;
    private int speed;
    private int lives;
    private long invincibleTime;
    private boolean isInvincible;
    private Image image;
    
    private int bulletLevel;
    private long bulletLevelEndTime;
    private double fireRateMultiplier;
    private long fireRateEndTime;
    private int damageMultiplier;
    private long damageEndTime;
    private double speedMultiplier;
    private long speedEndTime;

    public Player(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = 6;
        this.lives = 3;
        this.isInvincible = false;
        this.invincibleTime = 0;
        this.bulletLevel = 1;
        this.bulletLevelEndTime = 0;
        this.fireRateMultiplier = 1.0;
        this.fireRateEndTime = 0;
        this.damageMultiplier = 1;
        this.damageEndTime = 0;
        this.speedMultiplier = 1.0;
        this.speedEndTime = 0;
    }

    public void moveLeft() {
        x -= speed * getSpeedMultiplier();
    }

    public void moveRight() {
        x += speed * getSpeedMultiplier();
    }

    public void moveUp() {
        y -= speed * getSpeedMultiplier();
    }

    public void moveDown() {
        y += speed * getSpeedMultiplier();
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

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setInvincible(boolean isInvincible) {
        this.isInvincible = isInvincible;
        if (isInvincible) {
            this.invincibleTime = System.currentTimeMillis();
        }
    }

    public void setImage(Image image) {
        this.image = image;
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

    public int getLives() {
        return lives;
    }

    /**
     * 每帧更新，统一处理所有计时器过期逻辑。
     */
    public void update() {
        long now = System.currentTimeMillis();

        if (isInvincible && now - invincibleTime > 2000) {
            isInvincible = false;
        }
        if (bulletLevelEndTime > 0 && now > bulletLevelEndTime) {
            bulletLevel = 1;
            bulletLevelEndTime = 0;
        }
        if (fireRateEndTime > 0 && now > fireRateEndTime) {
            fireRateMultiplier = 1.0;
            fireRateEndTime = 0;
        }
        if (damageEndTime > 0 && now > damageEndTime) {
            damageMultiplier = 1;
            damageEndTime = 0;
        }
        if (speedEndTime > 0 && now > speedEndTime) {
            speedMultiplier = 1.0;
            speedEndTime = 0;
        }
    }

    public boolean isInvincible() {
        return isInvincible;
    }

    public int getBulletLevel() {
        return bulletLevel;
    }

    public void setBulletLevel(int bulletLevel, int durationMs) {
        this.bulletLevel = bulletLevel;
        this.bulletLevelEndTime = System.currentTimeMillis() + durationMs;
    }

    public double getFireRateMultiplier() {
        return fireRateMultiplier;
    }

    public void setFireRateMultiplier(double multiplier, int durationMs) {
        this.fireRateMultiplier = multiplier;
        this.fireRateEndTime = System.currentTimeMillis() + durationMs;
    }

    public int getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(int multiplier, int durationMs) {
        this.damageMultiplier = multiplier;
        this.damageEndTime = System.currentTimeMillis() + durationMs;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(double multiplier, int durationMs) {
        this.speedMultiplier = multiplier;
        this.speedEndTime = System.currentTimeMillis() + durationMs;
    }

    public Image getImage() {
        return image;
    }

    public void loseLife() {
        if (!isInvincible) {
            lives--;
            setInvincible(true);
        }
    }

    public boolean isAlive() {
        return lives > 0;
    }
}