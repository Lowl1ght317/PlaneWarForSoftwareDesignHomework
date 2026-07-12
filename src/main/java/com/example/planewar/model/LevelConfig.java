package com.example.planewar.model;

public class LevelConfig {
    private int levelNumber;
    private String name;
    private String description;
    private WinCondition winCondition;
    private int targetScore;
    private int targetKillCount;
    private boolean hasBoss;
    private int enemySpawnInterval;
    private int maxEnemiesOnScreen;

    public enum WinCondition {
        SCORE,
        KILL_COUNT,
        DEFEAT_BOSS,
        SURVIVE
    }

    public LevelConfig(int levelNumber, String name, String description, WinCondition winCondition,
                       int targetScore, int targetKillCount, boolean hasBoss,
                       int enemySpawnInterval, int maxEnemiesOnScreen) {
        this.levelNumber = levelNumber;
        this.name = name;
        this.description = description;
        this.winCondition = winCondition;
        this.targetScore = targetScore;
        this.targetKillCount = targetKillCount;
        this.hasBoss = hasBoss;
        this.enemySpawnInterval = enemySpawnInterval;
        this.maxEnemiesOnScreen = maxEnemiesOnScreen;
    }

    public static LevelConfig[] getLevels() {
        return new LevelConfig[]{
                new LevelConfig(1, "初出茅庐", "击败15个敌人通关",
                        WinCondition.KILL_COUNT, 0, 15, false, 1000, 5),
                new LevelConfig(2, "分数挑战", "达到5000分通关",
                        WinCondition.SCORE, 5000, 0, false, 800, 6),
                new LevelConfig(3, "精英围剿", "击败25个敌人通关",
                        WinCondition.KILL_COUNT, 0, 25, false, 600, 8),
                new LevelConfig(4, "BOSS降临", "击败BOSS获得高分通关",
                        WinCondition.SCORE, 5000, 0, true, 500, 10),
                new LevelConfig(5, "最终决战", "击败最终BOSS获得高分通关",
                        WinCondition.SCORE, 10000, 0, true, 400, 12),
                new LevelConfig(6, "无尽挑战", "无限模式，挑战你的极限",
                        WinCondition.SURVIVE, 0, 0, false, 800, 8)
        };
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public WinCondition getWinCondition() {
        return winCondition;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public int getTargetKillCount() {
        return targetKillCount;
    }

    public boolean hasBoss() {
        return hasBoss;
    }

    public int getEnemySpawnInterval() {
        return enemySpawnInterval;
    }

    public int getMaxEnemiesOnScreen() {
        return maxEnemiesOnScreen;
    }
}