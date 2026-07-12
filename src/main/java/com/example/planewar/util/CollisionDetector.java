package com.example.planewar.util;

import com.example.planewar.model.Bullet;
import com.example.planewar.model.Enemy;
import com.example.planewar.model.Player;
import com.example.planewar.model.PowerUp;

public class CollisionDetector {
    public static boolean checkPlayerEnemyCollision(Player player, Enemy enemy) {
        int playerLeft = player.getX();
        int playerRight = player.getX() + player.getWidth();
        int playerTop = player.getY();
        int playerBottom = player.getY() + player.getHeight();

        int enemyLeft = enemy.getX();
        int enemyRight = enemy.getX() + enemy.getWidth();
        int enemyTop = enemy.getY();
        int enemyBottom = enemy.getY() + enemy.getHeight();

        return playerRight > enemyLeft && playerLeft < enemyRight
                && playerBottom > enemyTop && playerTop < enemyBottom;
    }

    public static boolean checkBulletEnemyCollision(Bullet bullet, Enemy enemy) {
        int bulletLeft = bullet.getX();
        int bulletRight = bullet.getX() + bullet.getWidth();
        int bulletTop = bullet.getY();
        int bulletBottom = bullet.getY() + bullet.getHeight();

        int enemyLeft = enemy.getX();
        int enemyRight = enemy.getX() + enemy.getWidth();
        int enemyTop = enemy.getY();
        int enemyBottom = enemy.getY() + enemy.getHeight();

        return bulletRight > enemyLeft && bulletLeft < enemyRight
                && bulletBottom > enemyTop && bulletTop < enemyBottom;
    }

    public static boolean checkBulletPlayerCollision(Bullet bullet, Player player) {
        int bulletLeft = bullet.getX();
        int bulletRight = bullet.getX() + bullet.getWidth();
        int bulletTop = bullet.getY();
        int bulletBottom = bullet.getY() + bullet.getHeight();

        int playerLeft = player.getX();
        int playerRight = player.getX() + player.getWidth();
        int playerTop = player.getY();
        int playerBottom = player.getY() + player.getHeight();

        return bulletRight > playerLeft && bulletLeft < playerRight
                && bulletBottom > playerTop && bulletTop < playerBottom;
    }

    public static boolean checkPlayerPowerUpCollision(Player player, PowerUp powerUp) {
        int playerLeft = player.getX();
        int playerRight = player.getX() + player.getWidth();
        int playerTop = player.getY();
        int playerBottom = player.getY() + player.getHeight();

        int powerUpLeft = powerUp.getX();
        int powerUpRight = powerUp.getX() + powerUp.getWidth();
        int powerUpTop = powerUp.getY();
        int powerUpBottom = powerUp.getY() + powerUp.getHeight();

        return playerRight > powerUpLeft && playerLeft < powerUpRight
                && playerBottom > powerUpTop && playerTop < powerUpBottom;
    }
}