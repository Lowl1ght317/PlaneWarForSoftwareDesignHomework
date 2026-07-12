package com.example.planewar.model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private int score;
    private int highScore;
    private int lives;
    private boolean isPaused;
    private boolean isGameOver;
    private boolean isGameStarted;
    private boolean isWin;
    private int level;
    private int enemiesKilled;
    private int enemiesPerLevel;
    private GameMode gameMode;

    private List<Enemy> enemies;
    private List<Bullet> bullets;
    private List<PowerUp> powerUps;
    private Player player;

    public GameState() {
        this.score = 0;
        this.highScore = 0;
        this.lives = 3;
        this.isPaused = false;
        this.isGameOver = false;
        this.isGameStarted = false;
        this.level = 1;
        this.enemiesKilled = 0;
        this.enemiesPerLevel = 10;
        this.gameMode = null;
        
        this.enemies = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.powerUps = new ArrayList<>();
    }

    public void reset(GameMode mode) {
        this.score = 0;
        this.lives = 3;
        this.isPaused = false;
        this.isGameOver = false;
        this.isGameStarted = true;
        this.isWin = false;
        this.level = 1;
        this.enemiesKilled = 0;
        this.gameMode = mode;
        this.enemies.clear();
        this.bullets.clear();
        this.powerUps.clear();
    }

    public void addScore(int points) {
        score += points;
        if (score > highScore) {
            highScore = score;
        }
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }

    public void removeBullet(Bullet bullet) {
        bullets.remove(bullet);
    }

    public void addPowerUp(PowerUp powerUp) {
        powerUps.add(powerUp);
    }

    public void removePowerUp(PowerUp powerUp) {
        powerUps.remove(powerUp);
    }

    public void incrementEnemiesKilled() {
        enemiesKilled++;
    }

    public void setEnemiesKilled(int enemiesKilled) {
        this.enemiesKilled = enemiesKilled;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public void setWin(boolean win) {
        isWin = win;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setGameStarted(boolean gameStarted) {
        isGameStarted = gameStarted;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public int getLives() {
        return lives;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public int getLevel() {
        return level;
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }

    public int getEnemiesPerLevel() {
        return enemiesPerLevel;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    public Player getPlayer() {
        return player;
    }
}