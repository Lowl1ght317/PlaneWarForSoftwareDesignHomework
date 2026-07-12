package com.example.planewar.model;

public class Bullet {
    private int x;
    private int y;
    private int width;
    private int height;
    private int speed;
    private int damage;
    private boolean isPlayerBullet;
    private boolean isDestroyed;

    public Bullet(int x, int y, boolean isPlayerBullet) {
        this.x = x;
        this.y = y;
        this.width = 6;
        this.height = 15;
        this.speed = isPlayerBullet ? 12 : 3;
        this.damage = 1;
        this.isPlayerBullet = isPlayerBullet;
        this.isDestroyed = false;
    }

    public void update() {
        if (isPlayerBullet) {
            y -= speed;
        } else {
            y += speed;
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

    public void setDamage(int damage) {
        this.damage = damage;
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

    public int getDamage() {
        return damage;
    }

    public boolean isPlayerBullet() {
        return isPlayerBullet;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }
}