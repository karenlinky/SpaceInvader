package ca.uwaterloo.cs349;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class EnemyBullet {
    final static int bulletWidth = 7;
    final static int bulletHeight = 20;
    static final float fireSpeed1 = 5f;

    ImageView BulletImageView;
    Group SceneGroup;

    double fireSpeed;

    EnemyBullet(String imageFile, Group SceneGroup, double posX, double posY, double fireSpeed) {
        Image Bullet = new Image(imageFile, EnemyBullet.bulletWidth, EnemyBullet.bulletHeight, false, true);
        this.BulletImageView = new ImageView(Bullet);

        this.BulletImageView.setX(posX);
        this.BulletImageView.setY(posY);
        this.SceneGroup = SceneGroup;
        this.SceneGroup.getChildren().add(this.BulletImageView);
        this.fireSpeed = fireSpeed;

//        AnimationTimer enemyBulletTimer = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                BulletImageView.setY(BulletImageView.getY() + fireSpeed);
//            }
//        };
//        enemyBulletTimer.start();
    }

    void moveBullet() {
        this.BulletImageView.setY(this.BulletImageView.getY() + this.fireSpeed);
    }

    boolean checkOutOfBound() {
        double bulletPosY = this.BulletImageView.localToScene(this.BulletImageView.getBoundsInLocal()).getMinY();
        if (SpaceInvaders.sceneHeight <= bulletPosY) {
            this.SceneGroup.getChildren().remove(this.BulletImageView);
            return true;
        }
        return false;
    }

    boolean bulletHitPlayer(double playerPosX, double playerPosY, double bulletPosX, double bulletPosY) {
        if (playerPosX - EnemyBullet.bulletWidth <= bulletPosX &&
                bulletPosX <= playerPosX + Player.PlayerWidth - EnemyBullet.bulletWidth &&
                playerPosY <= bulletPosY + EnemyBullet.bulletHeight - 10) {
            return true;
        }
        return false;
    }

    boolean checkHitPlayer(ImageView PlayerImageView) {
        double playerPosX = PlayerImageView.getX() + EnemyBullet.bulletWidth/2;
        double playerPosY = PlayerImageView.getY();
        double bulletPosX = this.BulletImageView.localToScene(this.BulletImageView.getBoundsInLocal()).getMinX();
        double bulletPosY = this.BulletImageView.localToScene(this.BulletImageView.getBoundsInLocal()).getMinY();
//        if (playerPosX <= bulletPosX &&
//                bulletPosX <= playerPosX + Player.PlayerWidth &&
//                playerPosY <= bulletPosY + EnemyBullet.bulletHeight - 25) {
//            this.SceneGroup.getChildren().remove(this.BulletImageView);
//            return true;
//        }
        if (bulletHitPlayer(playerPosX, playerPosY, bulletPosX, bulletPosY)) {
            this.SceneGroup.getChildren().remove(this.BulletImageView);
            return true;
        }
        return false;
    }

    boolean validatePlayerSpawnPosition(double playerPosX, double playerPosY) {
        double bulletPosX = this.BulletImageView.localToScene(this.BulletImageView.getBoundsInLocal()).getMinX();
        double bulletPosY = this.BulletImageView.localToScene(this.BulletImageView.getBoundsInLocal()).getMinY();

        return !bulletHitPlayer(playerPosX, playerPosY, bulletPosX, bulletPosY);

    }
}
