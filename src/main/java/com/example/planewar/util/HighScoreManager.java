package com.example.planewar.util;

import java.io.*;
import java.util.logging.Logger;

public class HighScoreManager {
    private static final Logger logger = Logger.getLogger(HighScoreManager.class.getName());
    private static final String HIGH_SCORE_FILE = "highscore.dat";

    public static int loadHighScore() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGH_SCORE_FILE))) {
            return ois.readInt();
        } catch (FileNotFoundException e) {
            logger.info("最高分文件不存在，将创建新文件");
            return 0;
        } catch (IOException e) {
            logger.warning("读取最高分失败: " + e.getMessage());
            return 0;
        }
    }

    public static void saveHighScore(int score) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGH_SCORE_FILE))) {
            oos.writeInt(score);
        } catch (IOException e) {
            logger.severe("保存最高分失败: " + e.getMessage());
        }
    }
}