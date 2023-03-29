package ca.uwaterloo.cs349;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Group;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javafx.scene.transform.Translate;

public class Enemies {
    final static int numRow = 5;
    final static int numCol = 10;
    Group SceneGroup;
    Group EnemyGroup;
    ArrayList<Enemy> EnemyList = new ArrayList<Enemy>();
    ArrayList<EnemyBullet> BulletList = new ArrayList<EnemyBullet>();
    static boolean moveRight = true;

    final static double EnemySpace = 8f;
    final static double DescendBy = 8f;

    static int enemyKilled = 0;

    final static long enemyFireDelay1 = 2000;
    final static long enemyFireDelay2 = 1000;
    final static long enemyFireDelay3 = 500;
    static long lastFiringTime = 0;

    Enemies(Group SceneGroup) {
        this.SceneGroup = SceneGroup;

        this.spawnEnemies();

//        AnimationTimer fireEnemyBulletTimer = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                Random random = new Random();
//                long fireDelay =
//                        SpaceInvaders.level == 1 ? enemyFireDelay1 : SpaceInvaders.level == 2 ? enemyFireDelay2 : enemyFireDelay3;
//                Date currentDate = new Date();
//                long currentTime = currentDate.getTime();
//                boolean canFire = currentTime - lastFiringTime >= fireDelay;
//                if (canFire) {
//                    if (EnemyList.size() > 0) {
//                        BulletList.add(EnemyList.get(random.nextInt(EnemyList.size())).fire());
//                    }
//                    lastFiringTime = currentTime;
//                }
//            }
//        };
//        fireEnemyBulletTimer.start();
    }

    void fireEnemyBullet() {
        Random random = new Random();
        long fireDelay =
                SpaceInvaders.level == 1 ? enemyFireDelay1 : SpaceInvaders.level == 2 ? enemyFireDelay2 : enemyFireDelay3;
        Date currentDate = new Date();
        long currentTime = currentDate.getTime();
        boolean canFire = currentTime - lastFiringTime >= fireDelay;
        if (canFire) {
            if (EnemyList.size() > 0) {
                BulletList.add(EnemyList.get(random.nextInt(EnemyList.size())).fire());
            }
            lastFiringTime = currentTime;
        }
    }

    void spawnEnemies() {
        this.EnemyGroup = new Group();
        Enemies.enemyKilled = 0;
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                createEnemy(
                        i == 0 ? 1 : i < 3 ? 2 : 3,
                        SpaceInvaders.sceneWidth/2
                                - (numCol/2 - 0.5) * Enemies.EnemySpace
                                - numCol/2 * Enemy.EnemyWidth
                                + j * Enemy.EnemyWidth
                                + j * Enemies.EnemySpace,
                        this.getPosYByRow(i)
                );
            }
        }
        this.SceneGroup.getChildren().add(this.EnemyGroup);
    }

    double getPosYByRow(int rowIndex) {
        return 50 + rowIndex * (Enemy.EnemyHeight + EnemySpace);
    }

    void createEnemy(int enemyType, double posX, double posY) {
        Enemy NewEnemy = null;
        switch (enemyType) {
            case 1:
                NewEnemy = new Enemy1(this.EnemyGroup, posX, posY, this.SceneGroup);
                break;
            case 2:
                NewEnemy = new Enemy2(this.EnemyGroup, posX, posY, this.SceneGroup);
                break;
            case 3:
                NewEnemy = new Enemy3(this.EnemyGroup, posX, posY, this.SceneGroup);
                break;
        }
        this.EnemyList.add(NewEnemy);
    }

    void move() {
        float speed = SpaceInvaders.currentSpeed;
        Translate translate = new Translate();
        speed = Enemies.moveRight ? speed : -1 * speed;
        for (int i = 0; i < Enemies.numRow * Enemies.numCol - Enemies.enemyKilled; i++) {
            if (!this.EnemyList.get(i).checkKeepDirection(speed)) {
                speed *= -1;
                translate.setY(Enemies.DescendBy);
                break;
            }
        }
        translate.setX(speed);
        this.EnemyGroup.getTransforms().add(translate);
    }

    void moveBullet() {
        for (int i = 0; i < this.BulletList.size(); i++) {
            this.BulletList.get(i).moveBullet();
        }
    }

    static void startMovingRight() {
        Enemies.moveRight = true;
    }

    static void startMovingLeft() {
        Enemies.moveRight = false;
    }

    boolean checkHitEnemy(ImageView BulletImageView) {
        int score = 0;
        for (int i = 0; i < this.EnemyList.size(); i++) {
            score = this.EnemyList.get(i).checkHitEnemy(BulletImageView);
            if (score > 0) {
                SpaceInvaders.playSound("invaderkilled.wav");
                this.enemyKilled++;
                SpaceInvaders.updateScore(score);
                this.EnemyList.remove(this.EnemyList.get(i));
                return true;
            }
        }
        return false;
    }
    boolean checkHitPlayer(ImageView PlayerImageView) {
        for (int i = 0; i < BulletList.size(); i++) {
            if (this.BulletList.get(i).checkOutOfBound()) {
                // bullet goes out of screen
                this.BulletList.remove(i);
                i--;
            }
        }

        for (int i = 0; i < this.EnemyList.size(); i++) {
            if (this.EnemyList.get(i).checkOutOfBound()) {
                // alien hits bottom of screen
                SpaceInvaders.playSound("explosion.wav");
                this.EnemyList.remove(i);
                i--;
                Enemies.enemyKilled++;
                SpaceInvaders.lostShip();
            }
        }

        for (int i = 0; i < BulletList.size(); i++) {
            if (this.BulletList.get(i).checkHitPlayer(PlayerImageView)) {
                // bullet hits player
                SpaceInvaders.playSound("explosion.wav");
                this.BulletList.remove(i);
                i--;
                SpaceInvaders.lostShip();
                return true;
            }
        }

        for (int i = 0; i < this.EnemyList.size(); i++) {
            if (this.EnemyList.get(i).checkHitPlayer(PlayerImageView)) {
                // allien hits player
                SpaceInvaders.playSound("explosion.wav");
                this.EnemyList.remove(i);
                i--;
                Enemies.enemyKilled++;
                SpaceInvaders.lostShip();
                return true;
            }
        }

        return false;
    }

    boolean validatePlayerSpawnPosition(double posX, double posY) {
        for (int i = 0; i < this.EnemyList.size(); i++) {
            if (!this.EnemyList.get(i).validatePlayerSpawnPosition(posX, posY)) {
                return false;
            }
        }
        for (int i = 0; i < this.BulletList.size(); i++) {
            if (!this.BulletList.get(i).validatePlayerSpawnPosition(posX, posY)) {
                return false;
            }
        }
        return true;
    }

    double findPlayerSpawnPosition(double posX, double posY) {
        double center = posX;
        for (int i = 0; i < SpaceInvaders.sceneWidth - Player.PlayerWidth; i++) {
            posX = center + i;
            if (this.validatePlayerSpawnPosition(posX, posY)) {
                return posX;
            }
            posX = center - i;
            if (this.validatePlayerSpawnPosition(posX, posY)) {
                return posX;
            }
        }
        return center;
    }
}
