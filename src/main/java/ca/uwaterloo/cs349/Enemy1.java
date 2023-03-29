package ca.uwaterloo.cs349;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Group;

public class Enemy1 extends Enemy {
    Enemy1(Group EnemyGroup, double posX, double posY, Group SceneGroup) {
        super("images/enemy1.png", EnemyGroup, posX, posY, SceneGroup);
    }

    @Override
    int updateScore() {
        return 30;
    }

    @Override
    EnemyBullet spawnBullet(double posX, double posY, double fireSpeed) {
        return new EnemyBullet("images/bullet1.png", this.SceneGroup, posX, posY, fireSpeed);
    }
}
