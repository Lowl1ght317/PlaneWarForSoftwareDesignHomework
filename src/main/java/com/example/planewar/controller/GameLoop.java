package com.example.planewar.controller;

public class GameLoop extends Thread {
    private GameController controller;
    private boolean running;
    private static final int FPS = 60;
    private static final long TARGET_TIME = 1000 / FPS;

    public GameLoop(GameController controller) {
        this.controller = controller;
        this.running = true;
    }

    @Override
    public void run() {
        long lastTime = System.currentTimeMillis();
        
        while (running) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastTime;
            
            if (elapsedTime >= TARGET_TIME) {
                controller.update();
                lastTime = currentTime;
            }
            
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stopGame() {
        running = false;
        try {
            join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}