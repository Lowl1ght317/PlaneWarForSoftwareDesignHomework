package com.example.planewar.util;

import java.io.*;

public class HighScoreManager {
    private static final String HIGH_SCORE_FILE = "highscore.dat";

    public static int loadHighScore() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGH_SCORE_FILE))) {
            return ois.readInt();
        } catch (FileNotFoundException e) {
            return 0;
        } catch (IOException e) {
            return 0;
        }
    }

    public static void saveHighScore(int score) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGH_SCORE_FILE))) {
            oos.writeInt(score);
        } catch (IOException e) {
            System.err.println("无法保存最高分");
        }
    }
}