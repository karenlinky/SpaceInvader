package ca.uwaterloo.cs349;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Text;
import javafx.scene.text.FontWeight;

public class Player {

    ImageView PlayerImageView = null;
    static final int PlayerWidth = 50;
    static final int PlayerHeight = 30;
    Group Group;
    Enemies Enemies;
    ArrayList<PlayerBullet> Bullets = new ArrayList<PlayerBullet>();

    Player(Enemies Enemies, Group Group) {
        this.Enemies = Enemies;
        this.Group = Group;

        this.spawnPlayer(true);

//        AnimationTimer checkBulletHitEnemytimer = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
////                for (int i = 0; i < Bullets.size(); i++) {
////                    if (Bullets.get(i).checkHitEnemy()) {
////                        Bullets.remove(i);
////                        i--;
////                    } else if (Bullets.get(i).checkOutOfBound()) {
////                        Bullets.remove(i);
////                        i--;
////                    }
////                }
////                checkBeingHit();
//                checkBulletHitEnemy();
//            }
//        };
//        checkBulletHitEnemytimer.start();
    }

    void checkBulletHitEnemy() {
        for (int i = 0; i < this.Bullets.size(); i++) {
            if (this.Bullets.get(i).checkHitEnemy()) {
                this.Bullets.remove(i);
                i--;
            } else if (this.Bullets.get(i).checkOutOfBound()) {
                this.Bullets.remove(i);
                i--;
            }
        }
       this.checkBeingHit();
    }

    void spawnPlayer(boolean firstStart) {
        if (this.PlayerImageView != null && this.Group.getChildren().contains(this.PlayerImageView)) {
            this.Group.getChildren().remove(this.PlayerImageView);
        }

        Image PlayerImage = new Image("images/player.png", Player.PlayerWidth, Player.PlayerHeight, false, true);
        this.PlayerImageView = new ImageView(PlayerImage);

        double posX = SpaceInvaders.sceneWidth/2 - Player.PlayerWidth/2;
        double posY = SpaceInvaders.sceneHeight - Player.PlayerHeight;

        if (!firstStart) {
            posX = this.Enemies.findPlayerSpawnPosition(posX, posY);
        }

        this.PlayerImageView.setX(posX);
        this.PlayerImageView.setY(posY);
        this.Group.getChildren().add(this.PlayerImageView);
    }

    void move() {
        float speed = SpaceInvaders.checkPlayerMoveRight() ? SpaceInvaders.moveSpeed : SpaceInvaders.moveSpeed * -1;
        if (0 <= this.PlayerImageView.getX() + speed &&
                this.PlayerImageView.getX() + speed <= SpaceInvaders.sceneWidth - this.PlayerWidth) {
            this.PlayerImageView.setX(this.PlayerImageView.getX() + speed);
        }
    }

    void moveBullet() {
        for (int i = 0; i < this.Bullets.size(); i++) {
            this.Bullets.get(i).moveBullet();
        }
    }

    void fire(float speed) {
        double posX = this.PlayerImageView.getX() + this.PlayerWidth/2 - PlayerBullet.bulletWidth/2;
        double posY = this.PlayerImageView.getY();

        PlayerBullet bullet = new PlayerBullet(this.Enemies, this.Group, posX, posY, speed);
        this.Bullets.add(bullet);
    }

    void checkBeingHit() {
        if (this.Enemies.checkHitPlayer(this.PlayerImageView)) {
            spawnPlayer(false);
        }
    }
}
