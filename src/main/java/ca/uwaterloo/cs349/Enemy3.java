package ca.uwaterloo.cs349;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Group;

public class Enemy3 extends Enemy {
    Enemy3(Group EnemyGroup, double posX, double posY, Group SceneGroup) {
        super("images/enemy3.png", EnemyGroup, posX, posY, SceneGroup);
    }

    @Override
    int updateScore() {
        return 10;
    }

    @Override
    EnemyBullet spawnBullet(double posX, double posY, double fireSpeed) {
        return new EnemyBullet("images/bullet3.png", this.SceneGroup, posX, posY, fireSpeed);
    }
}
