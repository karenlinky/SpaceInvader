package ca.uwaterloo.cs349;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Group;

public class PlayerBullet {
    final static int bulletWidth = 7;
    final static int bulletHeight = 20;
    Enemies Enemies;
    ImageView BulletImageView;
    Group Group;
    float speed;
    PlayerBullet(Enemies Enemies, Group Group, double posX, double posY, float speed) {
        this.Enemies = Enemies;
        Image Bullet = new Image("images/player_bullet.png", PlayerBullet.bulletWidth, PlayerBullet.bulletHeight, false, true);
        this.BulletImageView = new ImageView(Bullet);

        this.BulletImageView.setX(posX);
        this.BulletImageView.setY(posY);
        this.Group = Group;
        this.Group.getChildren().add(this.BulletImageView);
        this.speed = speed;

//        AnimationTimer playerBulletTimer = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                BulletImageView.setY(BulletImageView.getY() - speed);
//            }
//        };
//        playerBulletTimer.start();
    }

    void moveBullet() {
        BulletImageView.setY(BulletImageView.getY() - this.speed);
    }

    boolean checkHitEnemy() {
        boolean hitEnemy = this.Enemies.checkHitEnemy(this.BulletImageView);
        if (hitEnemy) {
            this.Group.getChildren().remove(this.BulletImageView);
            return true;
        }
        return false;
    }

    boolean checkOutOfBound() {
        double bulletPosY = this.BulletImageView.localToScene(this.BulletImageView.getBoundsInLocal()).getMinY();
        if (bulletPosY + PlayerBullet.bulletHeight < -20) {
            this.Group.getChildren().remove(this.BulletImageView);
            return true;
        }
        return false;
    }
}
