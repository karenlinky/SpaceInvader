package ca.uwaterloo.cs349;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Text;
import javafx.scene.text.FontWeight;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SpaceInvaders extends Application {
//    float SCREEN_WIDTH = 300;
//    float SCREEN_HEIGHT = 300;
//    float BALL_RADIUS = 20;
//    float dx, dy;
//
//    enum STATE {STOP, RUN}
//    STATE gameState = STATE.RUN;
//
    public enum DIRECTION {LEFT, RIGHT, NONE};
    static DIRECTION PlayerDirection = DIRECTION.NONE;
//    float paddleSpeed = 1.1f;
    static int level = 1;
    final static int maxLevel = 3;
    final static int sceneWidth = 800;
    final static int sceneHeight = 600;
    static final float moveSpeed = 3.5f;
    static final float fireSpeed = 5f;

    static final float initSpeed1 = 3f;
    static final float initSpeed2 = 4f;
    static final float initSpeed3 = 5f;
    static ArrayList<Float> initSpeed = new ArrayList<Float>(SpaceInvaders.maxLevel);
    final static float addSpeedBy = 0.02f;
    final static int maxLives = 3;

    static float currentSpeed;
    static int score = 0;
    static int highScore = 0;
    static int lives = 3;

    static Label infoLabel = new Label("");

    static Enemies EnemiesObject;
    static Player Player;

    static boolean canFire = true; // to avoid pressing Space for a long time
    static Stage Stage;
    final static long playerFireDelay = 500;
    static long lastFiringTime = 0;
    static boolean playing = false;
    static int backgroundSound = 1;
    static long lastBackgroundPlayingTime = 0;
    static final long backgroundSoundOriginalDelay = 500;
    static long backgroundSoundDelay = backgroundSoundOriginalDelay;
    static long backgroundSoundDelayShortenBy = 10;
    static final long backgroundSoundShortestDelay = 300;

    static void lostShip() {
        SpaceInvaders.lives--;
        SpaceInvaders.updateInfoLabel();
        if (SpaceInvaders.lives == 0) {
            SpaceInvaders.endGame(false);
        }
    }

    static void endGame(boolean won) {
        SpaceInvaders.playing = false;
        SpaceInvaders.PlayerDirection = DIRECTION.NONE;
        Text Result = new Text(won ? "You Win!!!" : "Game Over :(");
        Result.setFont(Font.font("Impact", FontWeight.BOLD, 50));
        Text ScoreText = new Text("Score: " + SpaceInvaders.score);
        VBox.setMargin(ScoreText, new Insets(50.0, 0, 30, 0));
        ScoreText.setFont(Font.font("Impact", FontWeight.BOLD, 30));
        Text instructions = new Text("ENTER - Start Game\n" +
                "M - Main Menu\n" +
                "Q - Quit Game\n" +
                "1/2/3 - Start Game At A Specific Level");

        instructions.setTextAlignment(TextAlignment.CENTER);

        VBox welcomeGroup = new VBox(Result, ScoreText, instructions);
        welcomeGroup.setAlignment(Pos.CENTER);

        Scene endScene = new Scene(welcomeGroup, SpaceInvaders.sceneWidth, SpaceInvaders.sceneHeight);

        endScene.setOnKeyPressed(event -> {
            switch(event.getCode()) {
                case DIGIT1:
                    SpaceInvaders.setupGameScene(1);
                    break;
                case DIGIT2:
                    SpaceInvaders.setupGameScene(2);
                    break;
                case DIGIT3:
                    SpaceInvaders.setupGameScene(3);
                    break;
                case ENTER:
                    SpaceInvaders.setupGameScene(1);
                    break;
                case M:
                    SpaceInvaders.showMainMenu();
                    break;
                case Q:
                    System.exit(0);
                    break;
            }
        });

        SpaceInvaders.Stage.setScene(endScene);
    }

    static void updateInfoLabel() {
        String info = "\t";
        info += "Level: " + SpaceInvaders.level;
        info += "\t\t";
        info += "High Score: " + SpaceInvaders.highScore;
        info += "\n\t";
        info += "Ships: " + SpaceInvaders.lives;
        info += "\t\t";
        info += "Score: " + SpaceInvaders.score;
        SpaceInvaders.infoLabel.setText(info);
    }

    static void updateScore(int addScore) {
        SpaceInvaders.score += addScore;
        SpaceInvaders.highScore = Math.max(SpaceInvaders.highScore, SpaceInvaders.score);
        SpaceInvaders.currentSpeed += SpaceInvaders.addSpeedBy;
        SpaceInvaders.backgroundSoundDelay =
                SpaceInvaders.backgroundSoundDelay > SpaceInvaders.backgroundSoundShortestDelay ?
                        SpaceInvaders.backgroundSoundDelay - backgroundSoundDelayShortenBy :
                        SpaceInvaders.backgroundSoundDelay;

        if (Enemies.enemyKilled == Enemies.numRow * Enemies.numCol) {
            if (SpaceInvaders.level < SpaceInvaders.maxLevel) {
                SpaceInvaders.level++;
                EnemiesObject.spawnEnemies();
            } else {
                SpaceInvaders.endGame(true);
            }
        }

        SpaceInvaders.updateInfoLabel();
    }

    static void playSound(String fileName) {
        String sound = SpaceInvaders.class.getClassLoader().getResource("sounds/" + fileName).toString();
        AudioClip clip = new AudioClip(sound);
        clip.play();
    }

    static void attemptToFire() {
        Date currentDate = new Date();
        long currentTime = currentDate.getTime();
        SpaceInvaders.canFire = currentTime - SpaceInvaders.lastFiringTime >= playerFireDelay;
        if (SpaceInvaders.canFire) {

            SpaceInvaders.playSound("shoot.wav");

            SpaceInvaders.Player.fire(SpaceInvaders.fireSpeed);
            SpaceInvaders.canFire = false;
            SpaceInvaders.lastFiringTime = currentTime;
        }
    }

    static void setupGameScene(int level) {
        SpaceInvaders.playing = true;
        SpaceInvaders.backgroundSound = 1;
        SpaceInvaders.backgroundSoundDelay = SpaceInvaders.backgroundSoundOriginalDelay;
        SpaceInvaders.canFire = true;

        SpaceInvaders.level = level;
        SpaceInvaders.currentSpeed = SpaceInvaders.initSpeed.get(SpaceInvaders.level - 1);
        SpaceInvaders.score = 0;
        SpaceInvaders.lives = SpaceInvaders.maxLives;
        updateInfoLabel();
        SpaceInvaders.infoLabel.setFont(Font.font("Impact", FontWeight.BOLD, 20));
        SpaceInvaders.infoLabel.setTextFill(Color.WHITE);

        Group Group = new Group(SpaceInvaders.infoLabel);

        SpaceInvaders.EnemiesObject = new Enemies(Group);

        SpaceInvaders.Player = new Player(SpaceInvaders.EnemiesObject, Group);


        Scene gameScene = new Scene(Group, SpaceInvaders.sceneWidth, SpaceInvaders.sceneHeight, Color.BLACK);
        SpaceInvaders.Stage.setScene(gameScene);
        gameScene.setOnKeyPressed(event -> {
            switch(event.getCode()) {
                case LEFT:
                case A:
                    SpaceInvaders.PlayerDirection = DIRECTION.LEFT;
                    break;
                case RIGHT:
                case D:
                    SpaceInvaders.PlayerDirection = DIRECTION.RIGHT;
                    break;
                case SPACE:
                    SpaceInvaders.attemptToFire();
                    break;
            }
        });
        gameScene.setOnMouseClicked(event -> {
            switch(event.getButton()) {
                case PRIMARY:
                    SpaceInvaders.attemptToFire();
                    break;
            }
        });
        gameScene.setOnKeyReleased(event -> {
            switch(event.getCode()) {
                case LEFT:
                case A:
                case RIGHT:
                case D:
                    SpaceInvaders.PlayerDirection = DIRECTION.NONE;
                    break;
//                case SPACE:
//                    SpaceInvaders.canFire = true;
//                    break;
            }
        });
//        gameScene.setOnMouseReleased(event -> {
//            switch(event.getButton()) {
//                case PRIMARY:
//                    SpaceInvaders.canFire = true;
//                    break;
//            }
//        });
    }

    static void startGame(int level) {
//        SpaceInvaders.canFire = true;
//
//        SpaceInvaders.level = level;
//        SpaceInvaders.currentSpeed = SpaceInvaders.initSpeed.get(SpaceInvaders.level - 1);
//        System.out.println(currentSpeed);
//        SpaceInvaders.score = 0;
//        SpaceInvaders.lives = SpaceInvaders.maxLives;
//        updateInfoLabel();
//        SpaceInvaders.infoLabel.setFont(Font.font("Impact", FontWeight.BOLD, 20));
//
//        Group Group = new Group(SpaceInvaders.infoLabel);
//
//        SpaceInvaders.EnemiesObject = new Enemies(Group);
//
//        SpaceInvaders.Player = new Player(SpaceInvaders.EnemiesObject, Group);
//
//
//        Scene gameScene = new Scene(Group, SpaceInvaders.sceneWidth, SpaceInvaders.sceneHeight);
//        SpaceInvaders.Stage.setScene(gameScene);
//        gameScene.setOnKeyPressed(event -> {
//            switch(event.getCode()) {
//                case LEFT:
//                case A:
//                    SpaceInvaders.PlayerDirection = DIRECTION.LEFT;
//                    break;
//                case RIGHT:
//                case D:
//                    SpaceInvaders.PlayerDirection = DIRECTION.RIGHT;
//                    break;
//                case SPACE:
//                    if (SpaceInvaders.canFire) {
//                        SpaceInvaders.Player.fire(SpaceInvaders.fireSpeed);
//                        SpaceInvaders.canFire = false;
//                    }
//                    break;
//            }
//        });
//        gameScene.setOnKeyReleased(event -> {
//            switch(event.getCode()) {
//                case LEFT:
//                case A:
//                case RIGHT:
//                case D:
//                    SpaceInvaders.PlayerDirection = DIRECTION.NONE;
//                    break;
//                case SPACE:
//                    SpaceInvaders.canFire = true;
//                    break;
//            }
//        });
        SpaceInvaders.setupGameScene(level);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (PlayerDirection == DIRECTION.LEFT || PlayerDirection == DIRECTION.RIGHT) {
                    SpaceInvaders.Player.move();
                }
                if (SpaceInvaders.playing) {
                    Date currentDate = new Date();
                    long currentTime = currentDate.getTime();
                    if (currentTime - SpaceInvaders.lastBackgroundPlayingTime >= SpaceInvaders.backgroundSoundDelay) {
                        SpaceInvaders.playSound("fastinvader" + Integer.toString(SpaceInvaders.backgroundSound) + ".wav");
                        SpaceInvaders.backgroundSound = SpaceInvaders.backgroundSound < 4 ? SpaceInvaders.backgroundSound + 1 : 1;
                        SpaceInvaders.lastBackgroundPlayingTime = currentTime;
                    }

                    SpaceInvaders.EnemiesObject.move();
                    SpaceInvaders.EnemiesObject.fireEnemyBullet();

                    SpaceInvaders.Player.checkBulletHitEnemy();

                    SpaceInvaders.Player.moveBullet();

                    SpaceInvaders.EnemiesObject.moveBullet();
                }
            }
        };
        timer.start();
    }

    static boolean checkPlayerMoveRight () {
        return SpaceInvaders.PlayerDirection == DIRECTION.RIGHT;
    }

    static void showMainMenu() {
        Image logo = new Image("images/logo.png", 450, 175, true, true);
        ImageView logoImageView = new ImageView(logo);

        Text instructionTitle = new Text("Instructions");
        instructionTitle.setFont(Font.font("Impact", FontWeight.BOLD, 30));
        Text instructions = new Text("ENTER - Start Game\n" +
                "A or \u25C0, D or \u25B6 - Move Spaceship\n" +
                "SPACE or LEFT MOUSE CLICK - Fire\n" +
                "Q - Quit Game\n" +
                "1/2/3 - Start Game At A Specific Level");

        instructions.setTextAlignment(TextAlignment.CENTER);

        VBox welcomeGroup = new VBox(logoImageView, instructionTitle, instructions);
        welcomeGroup.setAlignment(Pos.CENTER);

        VBox.setMargin(instructionTitle, new Insets(80.0, 0, 10, 0));

        Scene welcomeScene = new Scene(welcomeGroup, SpaceInvaders.sceneWidth, SpaceInvaders.sceneHeight);

        welcomeScene.setOnKeyPressed(event -> {
            switch(event.getCode()) {
                case DIGIT1:
                    SpaceInvaders.startGame(1);
                    break;
                case DIGIT2:
                    SpaceInvaders.startGame(2);
                    break;
                case DIGIT3:
                    SpaceInvaders.startGame(3);
                    break;
                case ENTER:
                    SpaceInvaders.startGame(1);
                    break;
                case Q:
                    System.exit(0);
                    break;
            }
        });

        SpaceInvaders.Stage.setScene(welcomeScene);
    }

    @Override
    public void start(Stage stage) {
        this.Stage = stage;
        this.initSpeed.add(this.initSpeed1);
        this.initSpeed.add(this.initSpeed2);
        this.initSpeed.add(this.initSpeed3);


        this.Stage.setResizable(false);
//        Image logo = new Image("images/logo.png", 450, 175, true, true);
//        ImageView logoImageView = new ImageView(logo);
//
//        Text instructionTitle = new Text("Instructions");
//        instructionTitle.setFont(Font.font("Impact", FontWeight.BOLD, 30));
//        Text instructions = new Text("Enter - Start Game\n" +
//                "A/D or Left/Right Arrow Key - Move Spaceship\n" +
//                "Space - Fire\n" +
//                "Q - Quit Game\n" +
//                "1/2/3 - Switch Level");
//
//        instructions.setTextAlignment(TextAlignment.CENTER);
//
//        VBox welcomeGroup = new VBox(logoImageView, instructionTitle, instructions);
//        welcomeGroup.setAlignment(Pos.CENTER);
//
//        VBox.setMargin(instructionTitle, new Insets(80.0, 0, 10, 0));
//
//        Scene welcomeScene = new Scene(welcomeGroup, this.sceneWidth, this.sceneHeight);
//
//        welcomeScene.setOnKeyPressed(event -> {
//            switch(event.getCode()) {
//                case DIGIT1:
//                    this.startGame(1);
//                    break;
//                case DIGIT2:
//                    this.startGame(2);
//                    break;
//                case DIGIT3:
//                    this.startGame(3);
//                    break;
//                case ENTER:
//                    this.startGame(1);
//                    break;
//                case Q:
//                    System.exit(0);
//            }
//        });
//
//        this.Stage.setScene(welcomeScene);
        SpaceInvaders.showMainMenu();
        this.Stage.show();


//        // random drift for the ball
//        Random rand = new Random();
//        dx = -1 + (rand.nextFloat() * 2);
//        dy = 1 + rand.nextFloat();
//
//        // create ball, paddle
//        Circle ball = new Circle(SCREEN_WIDTH/2, 25, BALL_RADIUS);
//        ball.setFill(Color.BLUE);
//
//        Rectangle paddle = new Rectangle(75, 15);
//        paddle.setX(SCREEN_WIDTH/2);
//        paddle.setY(SCREEN_HEIGHT - 50);
//
//        Label gameOver = new Label("Game Over!");
//        gameOver.setFont(Font.font("Helvetica", 18));
//        gameOver.setLayoutX(SCREEN_WIDTH/2 - 50);
//        gameOver.setLayoutY(SCREEN_HEIGHT/2 - 100);
//        gameOver.setVisible(false);
//
//        // add everything to the group and the scene
//        Group root = new Group(ball, paddle, gameOver);
//        Scene scene = new Scene(root);
//
//        // the timer drives all of the movement
//        // reposition objects in the timer's handle event
//        AnimationTimer timer = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                // move ball
//                ball.setCenterX(ball.getCenterX() + dx);
//                ball.setCenterY(ball.getCenterY() + dy);
//
//                // if it strikes the sides or top of the screen, change direction
//                if (ball.getCenterX() < BALL_RADIUS
//                        || ball.getCenterX() > SCREEN_WIDTH - BALL_RADIUS) dx *= -1.0f;
//                if (ball.getCenterY() < BALL_RADIUS) dy *= -1.0f;
//
//                // if it strikes the bottom, game over!
//                if (ball.getCenterY() > SCREEN_HEIGHT - BALL_RADIUS - 25) {
//                    gameState = STATE.STOP;
//                    gameOver.setVisible(true);
//                    this.stop();
//                    return;
//                }
//
//                // if it strikes the paddle, change direction and speed up x movement
//                if (paddle.contains(ball.getCenterX(), ball.getCenterY() + BALL_RADIUS)) {
//                    dy *= -1.0f;
//                    if (paddleDirection == DIRECTION.LEFT) dx -= 0.1;
//                    if (paddleDirection == DIRECTION.RIGHT) dx += 0.1;
//                }
//
//                // move the paddle
//                // doing this here ensures that it's updated smoothly
//                if (paddleDirection == DIRECTION.LEFT) paddle.setX(paddle.getX() - paddleSpeed);
//                if (paddleDirection == DIRECTION.RIGHT) paddle.setX(paddle.getX() + paddleSpeed);
//            }
//        };
//        timer.start();
//
//        // use keypress to determine paddle direction
//        scene.setOnKeyPressed(event -> {
//            switch(event.getCode()) {
//                case LEFT:
//                    paddleDirection = DIRECTION.LEFT;
//                    break;
//                case RIGHT:
//                    paddleDirection = DIRECTION.RIGHT;
//                    break;
//                case Q:
//                    System.exit(0);
//            }
//        });
//        scene.setOnKeyReleased(event -> {
//            paddleDirection = DIRECTION.NONE;
//        });
//
//        // show the scene
//        stage.setScene(scene);
//        stage.setResizable(false);
//        stage.setTitle("Mini-Breakout");
//        stage.setWidth(SCREEN_WIDTH);
//        stage.setHeight(SCREEN_HEIGHT);
//        stage.setOnCloseRequest(event -> {
//            System.exit(0);
//        });
//        stage.show();
    }
}