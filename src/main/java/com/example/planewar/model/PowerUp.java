package com.example.planewar.model;

import java.awt.Color;
import java.awt.Image;

public class PowerUp {
    private int x;
    private int y;
    private int width;
    private int height;
    private int speed;
    private PowerUpType type;
    private boolean isCollected;
    private Image image;

    public enum PowerUpType {
        HEALTH(Color.RED, "生命"),
        SHIELD(Color.BLUE, "护盾"),
        WEAPON_3WAY(Color.GREEN, "三发"),
        WEAPON_FAST(Color.CYAN, "射速"),
        SPEED_UP(Color.YELLOW, "加速"),
        SUPER_DAMAGE(new Color(255, 100, 255), "超伤");

        private final Color color;
        private final String name;

        PowerUpType(Color color, String name) {
            this.color = color;
            this.name = name;
        }

        public Color getColor() {
            return color;
        }

        public String getName() {
            return name;
        }
    }

    public PowerUp(int x, int y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.width = 30;
        this.height = 30;
        this.speed = 2;
        this.type = type;
        this.isCollected = false;
    }

    public void update() {
        y += speed;
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

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
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

    public PowerUpType getType() {
        return type;
    }

    public boolean isCollected() {
        return isCollected;
    }
}