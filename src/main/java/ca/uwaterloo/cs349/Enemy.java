package ca.uwaterloo.cs349;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Enemy {
    final static int EnemyWidth = 45;
    final static int EnemyHeight = 30;

    Group SceneGroup;
    Group EnemyGroup;
    ImageView EnemyImageView;

    Enemy(String imageFile, Group EnemyGroup, double posX, double posY, Group SceneGroup) {
        Image EnemyImage = new Image(imageFile, Enemy.EnemyWidth, Enemy.EnemyHeight, false, true);
        this.EnemyImageView = new ImageView(EnemyImage);

        this.SceneGroup = SceneGroup;
        this.EnemyGroup = EnemyGroup;

        this.EnemyImageView.setX(posX);
        this.EnemyImageView.setY(posY);
        this.EnemyGroup.getChildren().add(this.EnemyImageView);
    }

    boolean checkKeepDirection(float speed) {
        double posX = this.EnemyImageView.localToScene(this.EnemyImageView.getBoundsInLocal()).getMinX();
        if (posX + speed <= 0) {
            Enemies.startMovingRight();
            return false;
        } else if (SpaceInvaders.sceneWidth <= posX + Enemy.EnemyWidth + speed - 5) {
            Enemies.startMovingLeft();
            return false;
        }
        return true;
    }

    int checkHitEnemy(ImageView BulletImageView) {
        double bulletPosX = BulletImageView.getX() + PlayerBullet.bulletWidth/2;
        double bulletPosY = BulletImageView.getY();
        double enemyPosX = this.EnemyImageView.localToScene(this.EnemyImageView.getBoundsInLocal()).getMinX();
        double enemyPosY = this.EnemyImageView.localToScene(this.EnemyImageView.getBoundsInLocal()).getMinY();
        if (enemyPosX <= bulletPosX &&
                bulletPosX <= enemyPosX + Enemy.EnemyWidth &&
                enemyPosY <= bulletPosY &&
                bulletPosY <= enemyPosY + Enemy.EnemyHeight) {
            this.EnemyGroup.getChildren().remove(this.EnemyImageView);
            return this.updateScore();
        }
        return 0;
    }

    int updateScore() {return 0;}

    boolean EnemyHitPlayer(double playerPosX, double playerPosY, double enemyPosX, double enemyPosY) {
        if ((playerPosX <= enemyPosX &&
                enemyPosX <= playerPosX + Player.PlayerWidth &&
                playerPosY <= enemyPosY + Enemy.EnemyHeight)
                ||
                (playerPosX <= enemyPosX + Enemy.EnemyWidth &&
                        enemyPosX + Enemy.EnemyWidth <= playerPosX + Player.PlayerWidth &&
                        playerPosY <= enemyPosY + Enemy.EnemyHeight - 25)) {
            return true;
        }
        return false;
    }

    boolean checkHitPlayer(ImageView PlayerImageView) {
        double playerPosX = PlayerImageView.getX();
        double playerPosY = PlayerImageView.getY();
        double enemyPosX = this.EnemyImageView.localToScene(this.EnemyImageView.getBoundsInLocal()).getMinX();
        double enemyPosY = this.EnemyImageView.localToScene(this.EnemyImageView.getBoundsInLocal()).getMinY();
//        if ((playerPosX <= enemyPosX &&
//                enemyPosX <= playerPosX + Player.PlayerWidth &&
//                playerPosY <= enemyPosY + Enemy.EnemyHeight)
//                ||
//                (playerPosX <= enemyPosX + Enemy.EnemyWidth &&
//                        enemyPosX + Enemy.EnemyWidth <= playerPosX + Player.PlayerWidth &&
//                        playerPosY <= enemyPosY + Enemy.EnemyHeight)) {
//            this.EnemyGroup.getChildren().remove(this.EnemyImageView);
//            return true;
//        }
        if (EnemyHitPlayer(playerPosX, playerPosY, enemyPosX, enemyPosY)) {
            this.EnemyGroup.getChildren().remove(this.EnemyImageView);
            return true;
        }
        return false;
    }

    boolean checkOutOfBound() {
        double enemyPosY = this.EnemyImageView.localToScene(this.EnemyImageView.getBoundsInLocal()).getMinY();
        if (SpaceInvaders.sceneHeight <= enemyPosY + Enemy.EnemyHeight - 5) {
            this.EnemyGroup.getChildren().remove(this.EnemyImageView);
            return true;
        }
        return false;
    }

    EnemyBullet fire() {
        double posX = this.EnemyImageView.localToScene(this.EnemyImageView.getBoundsInLocal()).getMinX() + Enemy.EnemyWidth/2;
        double posY = this.EnemyImageView.localToScene(this.EnemyImageView.getBoundsInLocal()).getMinY() + Enemy.EnemyHeight;
        SpaceInvaders.playSound("shoot.wav");
        return this.spawnBullet(posX, posY, EnemyBullet.fireSpeed1 + SpaceInvaders.level * 0.5);
    }

    EnemyBullet spawnBullet(double posX, double posY, double fireSpeed) {
        return new EnemyBullet("images/bullet1.png", this.EnemyGroup, posX, posY, fireSpeed);
    }

    boolean validatePlayerSpawnPosition(double playerPosX, double playerPosY) {
        double enemyPosX = this.EnemyImageView.localToScene(this.EnemyImageView.getBoundsInLocal()).getMinX() + Enemy.EnemyWidth/2;
        double enemyPosY = this.EnemyImageView.localToScene(this.EnemyImageView.getBoundsInLocal()).getMinY() + Enemy.EnemyHeight;

        return !EnemyHitPlayer(playerPosX, playerPosY, enemyPosX, enemyPosY);

    }
}
