package ca.uwaterloo.cs349;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Group;

public class Enemy2 extends Enemy {
    Enemy2(Group EnemyGroup, double posX, double posY, Group SceneGroup) {
        super("images/enemy2.png", EnemyGroup, posX, posY, SceneGroup);
    }

    @Override
    int updateScore() {
        return 20;
    }

    @Override
    EnemyBullet spawnBullet(double posX, double posY, double fireSpeed) {
        return new EnemyBullet("images/bullet2.png", this.SceneGroup, posX, posY, fireSpeed);
    }
}
