package com.example.planewar.controller;

public class GameLoop extends Thread {
    private GameController controller;
    private volatile boolean running;
    private static final int FPS = 60;
    private static final long TARGET_TIME = 1000 / FPS;
    private static final long PAUSE_SLEEP_TIME = 50;

    public GameLoop(GameController controller) {
        this.controller = controller;
        this.running = true;
    }

    @Override
    public void run() {
        long lastTime = System.currentTimeMillis();
        
        while (running) {
            if (controller.isPaused()) {
                try {
                    Thread.sleep(PAUSE_SLEEP_TIME);
                    continue;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastTime;
            
            if (elapsedTime >= TARGET_TIME) {
                controller.update();
                lastTime = currentTime;
            }
            
            try {
                long sleepTime = TARGET_TIME - (System.currentTimeMillis() - lastTime);
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
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