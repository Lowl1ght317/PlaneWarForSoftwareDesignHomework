package com.example.planewar.util;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class ImageLoader {
    private static final Logger logger = Logger.getLogger(ImageLoader.class.getName());
    private static final String IMAGE_DIR = "image";

    public static Image loadImage(String filename) {
        try {
            File file = new File(IMAGE_DIR, filename);
            if (file.exists()) {
                BufferedImage img = ImageIO.read(file);
                if (img == null) {
                    logger.warning("图片文件无法解码: " + filename);
                }
                return img;
            } else {
                logger.warning("图片文件不存在: " + filename);
            }
        } catch (IOException e) {
            logger.severe("加载图片失败: " + filename + ", 错误: " + e.getMessage());
        }
        return null;
    }

    public static Image loadPlayerImage() {
        return loadImage("player.png");
    }

    public static Image loadNormalEnemyImage() {
        return loadImage("normalenemy.png");
    }

    public static Image loadFastEnemyImage() {
        return loadImage("fastenemy.png");
    }

    public static Image loadEliteEnemyImage() {
        return loadImage("eliteenemy.png");
    }

    public static Image loadBossImage() {
        return loadImage("BOSS1.png");
    }

    public static Image loadFinalBossImage() {
        return loadImage("BOSS2.png");
    }

    public static Image loadHealthPowerUpImage() {
        return loadImage("HP.png");
    }

    public static Image loadShieldPowerUpImage() {
        return loadImage("shield.png");
    }

    public static Image loadThreeWayPowerUpImage() {
        return loadImage("3way.png");
    }

    public static Image loadFastShootPowerUpImage() {
        return loadImage("fastshoot.png");
    }

    public static Image loadSpeedUpPowerUpImage() {
        return loadImage("boost.png");
    }

    public static Image loadSuperDamagePowerUpImage() {
        return loadImage("HighDMG.png");
    }
}