package com.example.planewar.util;

import com.example.planewar.model.Bullet;
import com.example.planewar.model.Enemy;
import com.example.planewar.model.Player;
import com.example.planewar.model.PowerUp;

public class CollisionDetector {
    private static boolean checkAABBCollision(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        return x1 + w1 > x2 && x1 < x2 + w2 && y1 + h1 > y2 && y1 < y2 + h2;
    }

    public static boolean checkPlayerEnemyCollision(Player player, Enemy enemy) {
        return checkAABBCollision(
                player.getX(), player.getY(), player.getWidth(), player.getHeight(),
                enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()
        );
    }

    public static boolean checkBulletEnemyCollision(Bullet bullet, Enemy enemy) {
        return checkAABBCollision(
                bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight(),
                enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()
        );
    }

    public static boolean checkBulletPlayerCollision(Bullet bullet, Player player) {
        return checkAABBCollision(
                bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight(),
                player.getX(), player.getY(), player.getWidth(), player.getHeight()
        );
    }

    public static boolean checkPlayerPowerUpCollision(Player player, PowerUp powerUp) {
        return checkAABBCollision(
                player.getX(), player.getY(), player.getWidth(), player.getHeight(),
                powerUp.getX(), powerUp.getY(), powerUp.getWidth(), powerUp.getHeight()
        );
    }
}