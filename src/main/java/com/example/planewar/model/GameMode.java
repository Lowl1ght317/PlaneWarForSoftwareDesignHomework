package com.example.planewar.model;

public enum GameMode {
    STORY("闯关模式"),
    SURVIVAL("无限模式");

    private final String name;

    GameMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}