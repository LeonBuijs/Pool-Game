package Server;

import utility.*;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Force;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.jfree.fx.FXGraphics2D;
import org.jfree.fx.ResizableCanvas;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PoolGame extends Application {
    private ResizableCanvas canvas;
    private World world;
    private List<GameObject> gameObjectList = new ArrayList<>();
    private boolean debugSelected = false;
    private BufferedImage image;
    private BufferedImage imageCue;
    //    private List<Body> balls = new ArrayList<>();
    private List<Ball> ballObjectList = new ArrayList<>();
    private List<Ball> ballsWhole = new ArrayList<>();
    private List<Ball> ballsHalf = new ArrayList<>();
    private Ball ballWhite;
    private Ball ballBlack;
    Slider sliderRotation = new Slider(0, 360, 180);
    Slider sliderPower = new Slider(0, 100, 0);
    private Camera camera;
    private MousePicker mousePicker;
    private boolean showCue = true;
    private AffineTransform cueTransform = new AffineTransform();
    private ArrayList<Body> checkingCorners = new ArrayList<>();
    private int lastPottedWhole = -1;
    private int lastPottedHalf = -1;
    private Player player1 = null;
    private Player player2 = null;
    private Player currentPlayer = player1;
    private Turn turn = new Turn();
    private int playerCount = 0;
    private Label currentTurnLabel = new Label();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread serverThread = new Thread(() -> {
            System.out.println("Server");
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(2001);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Er is een verbinding");

                Socket finalSocket = socket;
                Thread threadSend = new Thread(() -> {
                    boolean running = true;
                    Player player = null;
                    if (player1 == null) {
                        player = new Player(1, "p1");
                        player1 = player;
                        currentPlayer = player1;
                    } else if (player2 == null) {
                        player = new Player(2, "p2");
                        player2 = player;
                    }
                    while (running) {
                        try {
                            send(finalSocket, player);
                        } catch (IOException e) {
                            System.out.println("client disconnected");
                            running = false;
                        }
                    }
                });
                threadSend.start();

                Socket finalSocket1 = socket;
                Thread threadReceive = new Thread(() -> {
                    boolean running = true;
                    while (running) {
                        try {
                            receive(finalSocket1);
                        } catch (IOException e) {
                            System.out.println("client disconnected");
                            running = false;
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                threadReceive.start();
            }
        });
        serverThread.start();

        BorderPane mainPane = new BorderPane();
        canvas = new ResizableCanvas(g -> draw(g), mainPane);
        mainPane.setCenter(canvas);
        FXGraphics2D g2d = new FXGraphics2D(canvas.getGraphicsContext2D());

        camera = new Camera(canvas, g -> draw(g), g2d);

        mousePicker = new MousePicker(canvas);

        sliderPower.setShowTickLabels(true);
//        HBox hbox = getHBox(); //Test
//        mainPane.setTop(hbox);

        new AnimationTimer() {
            long last = -1;

            @Override
            public void handle(long now) {
                if (last == -1) {
                    last = now;
                }
                update((now - last) / 1000000000.0);
                last = now;
                draw(g2d);
            }
        }.start();

        primaryStage.setScene(new Scene(mainPane, 1600, 900));
        primaryStage.setTitle("Pool Game");
        primaryStage.show();
        draw(g2d);
    }

    private void receive(Socket socket) throws IOException, ClassNotFoundException {
        InputStream inputStream = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        ClientData data = (ClientData) objectInputStream.readObject();
        sliderPower.setValue(data.getPower());
        sliderRotation.setValue(data.getRotation());
    }

    private void send(Socket socket, Player player) throws IOException {
        if (ballObjectList.size() == 16) {
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            objectOutputStream.writeObject(new ServerData(ballObjectList, player, currentPlayer, player1, player2, cueTransform, sliderRotation.getValue(), showCue));
        }
    }

    private HBox getHBox() {
        Label labelPower = new Label("Power: ");
        HBox power = new HBox(labelPower, sliderPower);
        power.setSpacing(10);

        Label labelRotation = new Label("Rotation: ");
        HBox rotation = new HBox(labelRotation, sliderRotation);
        rotation.setSpacing(10);

        Button fireButton = new Button("Fire");
        fireButton.setOnAction(event -> {
            if (showCue && player1 != null && player2 != null) {
                shootBall();
                turn = new Turn();
                turn.setTurnActive(true);
            }
        });

        CheckBox showDebug = new CheckBox("Show debug");
        showDebug.setOnAction(e -> debugSelected = showDebug.isSelected());

        HBox hbox = new HBox(showDebug, power, rotation, fireButton, currentTurnLabel, new Label(""), new Label("VS"), new Label(""));
        hbox.setSpacing(100);
        return hbox;
    }

    public void init() {
        try {
            image = ImageIO.read(getClass().getResource("res/Pooltafel.png"));
            imageCue = ImageIO.read(getClass().getResource("res/cue.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.world = new World();
        world.setGravity(new Vector2(0, 0));

        createBalls();
        createWalls();
        createCheckers();

        //todo dit door server verbinding uiteindelijk afhandelen
//        player1 = new Player(1, "p1");
//        player2 = new Player(2, "p2");

        currentPlayer = player1;

//        currentTurnLabel.setText("Current turn: " + player1.getNickName());
    }

    private void draw(FXGraphics2D g) {
        g.setTransform(new AffineTransform());
        g.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());
        g.setBackground(Color.white);

        g.setTransform(camera.getTransform((int) canvas.getWidth(), (int) canvas.getHeight()));

        AffineTransform txZoom = new AffineTransform(g.getTransform());
        txZoom.scale(0.1, 0.1);
        g.setTransform(txZoom);
        g.drawImage(image, (1600 - image.getWidth()) / 2, (900 - image.getHeight()) / 2, null);

        cueTransform = new AffineTransform(txZoom);
        cueTransform.translate(ballObjectList.get(ballObjectList.indexOf(ballWhite)).getBall().getTransform().getTranslationX() / 0.1,
                ballObjectList.get(ballObjectList.indexOf(ballWhite)).getBall().getTransform().getTranslationY() / 0.1);
        cueTransform.rotate(Math.toRadians(sliderRotation.getValue()));
        cueTransform.scale(0.15, 0.15);

        g.setColor(Color.white);

        g.setTransform(camera.getTransform((int) canvas.getWidth(), (int) canvas.getHeight()));

        gameObjectList.stream().distinct().forEach(gameObject -> gameObject.draw(g));

        g.setTransform(cueTransform);

        if (showCue & !ballWhite.isPotted()) {
            g.drawImage(imageCue, 75, -40, null);
            g.setStroke(new BasicStroke(10));
            g.drawLine(0, 0, (int) (-sliderPower.getValue() / 0.03), 0);
        }

        g.setTransform(camera.getTransform((int) canvas.getWidth(), (int) canvas.getHeight()));

        if (debugSelected) {
            g.setColor(Color.blue);
            DebugDraw.draw(g, world, 1);
        }
    }

    private void update(double deltaTime) {
        world.update(deltaTime);
        mousePicker.update(world, camera.getTransform((int) canvas.getWidth(), (int) canvas.getHeight()), 1);

        boolean toShowCue = true;
        boolean isRolling = false;

        for (Ball ball : ballObjectList) {
            if (ball.isPotted() && !ball.equals(ballWhite)) {
                continue;
            }

            ball.update();
            if (ball.checkRolling()) {
                toShowCue = false;
                if (!isRolling) {
                    isRolling = ball.checkRolling();
                }
            }
        }
        if (ballWhite.checkRolling()) {
            toShowCue = false;
        }
        this.showCue = toShowCue;

        List<Ball> temp = new ArrayList<>(ballObjectList);
        for (Ball ball : temp) {
            if (!ball.isPotted()) {
                if (ball.checkInPocket(checkingCorners)) {
                    turn.setNoBallPotted(false);
                    if (ball.getBallType() != Ball.BallType.WHITE) {
                        ball.setPotted(true);
                        if (ball.getBallType() == Ball.BallType.BLACK) {
                            boolean allPotted = true;

                            for (Ball anBall : ballObjectList) {
                                if (!anBall.isPotted() && anBall.getBallType().equals(currentPlayer.getBallType())) {
                                    allPotted = false;
                                    break;
                                }
                            }
                            if (!allPotted || currentPlayer.getBallType() == null) {
                                resetGame();
                            } else if (checkInCorrectPocket()) {
                                //todo iets doen dat je gewonnen hebt fzo
                                System.out.println("yippeeee");
                            } else {
                                resetGame();
                            }
                        }

                        checkWholeAndHalfPocketed(ball);
                    } else {
                        ball.setPotted(true);
                    }
                }
            } else {
                if (ball.getBallType() != Ball.BallType.WHITE) {
                    Transform transform = new Transform();
                    transform.setTranslation(0, 0);
                    ball.getBall().setTransform(transform);
                } else {
                    turn.setNoBallPotted(false);
                    turn.setWhiteBallPotted(true);
                    turn.setToChangePlayer(true);

                    resetWhiteBall();
                    ball.setPotted(false);
                }
            }
            if (turn.isNoBallPotted() && !isRolling && turn.isTurnActive() && turn.isTurnStarted()) {
                turn.setToChangePlayer(true);
                changeTurn();
            } else if (!isRolling && turn.isTurnActive() && turn.isTurnStarted()) {
                changeTurn();
            }
        }
        turn.setTurnStarted(true);
    }

    private void checkWholeAndHalfPocketed(Ball ball) {
        boolean hasBallType = true;
        if (ball.getBallType() == Ball.BallType.WHOLE) {
            hasBallType = checkPlayerHasBallType(ball);
            lastPottedWhole = ball.getWhichPocket();
        } else if (ball.getBallType() == Ball.BallType.HALF) {
            hasBallType = checkPlayerHasBallType(ball);
            lastPottedHalf = ball.getWhichPocket();
        }

        if (!hasBallType) {
            turn.setToChangePlayer(true);
        }
    }

    private boolean checkPlayerHasBallType(Ball ball) {
        Player otherPlayer = getOtherPlayer();
        //todo
        if (currentPlayer.getBallType() == null && otherPlayer.getBallType() == null) {
            currentPlayer.setBallType(ball.getBallType());
            if (ball.getBallType().equals(Ball.BallType.WHOLE)) {
                otherPlayer.setBallType(Ball.BallType.HALF);
            } else {
                otherPlayer.setBallType(Ball.BallType.WHOLE);
            }
            return true;
        } else if (currentPlayer.getBallType().equals(ball.getBallType())) {
            return true;
        } else {
            return false;
        }
    }

    private void changeTurn() {

        if (!turn.isTurnActive()) {
            return;
        }

        if (turn.isToChangePlayer()) {
            currentPlayer = getOtherPlayer();
            currentTurnLabel.setText("Current turn: " + currentPlayer.getNickName() + " " + currentPlayer.getBallType());
        }
        turn.setTurnActive(false);
    }

    private Player getOtherPlayer() {
        if (currentPlayer.equals(player1)) {
            return player2;
        } else {
            return player1;
        }
    }

    private void createBalls() {
        for (int i = 1; i < 17; i++) {
            Body ballBody = new Body();
            BodyFixture ballFix = new BodyFixture(Geometry.createCircle(0.8));
            ballFix.setDensity(10);
            ballFix.setRestitution(0.1);
            ballBody.setAngularDamping(1);
            ballBody.addFixture(ballFix);
            ballBody.setGravityScale(0);
            ballBody.setMass(MassType.NORMAL);

            GameObject ballObject;

            if (i == 16) {
                ballObject = new GameObject("res/ball_white.png", ballBody, new Vector2(0, 0), 0.0115);
                ballBody.setBullet(true);
            } else {
                ballObject = new GameObject("res/ball_" + i + ".png", ballBody, new Vector2(0, 0), 0.0115);
            }

            //toevoegen aan lijsten
            Ball ball;
            if (i == 16) {
                ball = new Ball(Ball.BallType.WHITE, ballBody, ballObject);
                ballWhite = ball;
            } else if (i < 8) {
                ball = new Ball(Ball.BallType.WHOLE, ballBody, ballObject);
                ballsWhole.add(ball);
            } else if (i == 8) {
                ball = new Ball(Ball.BallType.BLACK, ballBody, ballObject);
                ballBlack = ball;
            } else {
                ball = new Ball(Ball.BallType.HALF, ballBody, ballObject);
                ballsHalf.add(ball);
            }

            ballObjectList.add(ball);
            world.addBody(ballBody);
            gameObjectList.add(ballObject);
        }
        resetBalls();
    }

    private void resetWhiteBall() {
        world.removeBody(ballObjectList.get(ballObjectList.indexOf(ballWhite)).getBall());
        gameObjectList.remove(ballObjectList.get(ballObjectList.indexOf(ballWhite)).getBallObject());
        ballObjectList.remove(ballObjectList.get(ballObjectList.indexOf(ballWhite)));

        Body ballBody = new Body();
        BodyFixture ballFix = new BodyFixture(Geometry.createCircle(0.8));
        ballFix.setDensity(10);
        ballFix.setRestitution(0.1);
        ballBody.setAngularDamping(1);
        ballBody.addFixture(ballFix);
        ballBody.setGravityScale(0);
        ballBody.setMass(MassType.NORMAL);

        GameObject ballObject = new GameObject("res/ball_white.png", ballBody, new Vector2(0, 0), 0.0115);
        Ball ball = new Ball(Ball.BallType.WHITE, ballBody, ballObject);
        ballWhite = ball;

        ballObjectList.add(ball);
        world.addBody(ballBody);
        gameObjectList.add(ballObject);
        ballObjectList.get(ballObjectList.indexOf(ballWhite)).getBall().translate(new Vector2(59, 45));
        ballWhite.setPotted(false);
    }

    private void resetBalls() {
        ballObjectList.stream().distinct().forEach(ball -> ball.setPotted(false));

        lastPottedHalf = -1;
        lastPottedWhole = -1;

        //milde random afwijking van de witte bal om afstoten wat meer random te maken
        Random random = new Random();
        double deviationBall = random.nextDouble() - 0.5;

        ballObjectList.get(ballObjectList.indexOf(ballWhite)).getBall().translate(new Vector2(59, 45 + deviationBall));

        double baseX = 98;
        double baseY = 45;
        double offsetX = 1.43;
        double offsetY = 0.85;

        ballObjectList.get(0).getBall().translate(new Vector2(baseX, baseY));
        ballObjectList.get(1).getBall().translate(new Vector2(baseX + offsetX, baseY + offsetY));
        ballObjectList.get(2).getBall().translate(new Vector2(baseX + offsetX, baseY - offsetY));
        ballObjectList.get(3).getBall().translate(new Vector2(baseX + offsetX * 2, baseY + offsetY * 2));
        ballObjectList.get(7).getBall().translate(new Vector2(baseX + offsetX * 2, baseY));
        ballObjectList.get(4).getBall().translate(new Vector2(baseX + offsetX * 2, baseY - offsetY * 2));
        ballObjectList.get(5).getBall().translate(new Vector2(baseX + offsetX * 3, baseY + offsetY * 3));
        ballObjectList.get(6).getBall().translate(new Vector2(baseX + offsetX * 3, baseY + offsetY * 1));
        ballObjectList.get(8).getBall().translate(new Vector2(baseX + offsetX * 3, baseY - offsetY * 1));
        ballObjectList.get(9).getBall().translate(new Vector2(baseX + offsetX * 3, baseY - offsetY * 3));
        ballObjectList.get(10).getBall().translate(new Vector2(baseX + offsetX * 4, baseY + offsetY * 4));
        ballObjectList.get(11).getBall().translate(new Vector2(baseX + offsetX * 4, baseY + offsetY * 2));
        ballObjectList.get(12).getBall().translate(new Vector2(baseX + offsetX * 4, baseY));
        ballObjectList.get(13).getBall().translate(new Vector2(baseX + offsetX * 4, baseY - offsetY * 2));
        ballObjectList.get(14).getBall().translate(new Vector2(baseX + offsetX * 4, baseY - offsetY * 4));
    }

    private void shootBall() {
        int rotation = (int) sliderRotation.getValue() + 180;
        int power = (int) sliderPower.getValue();

        double x = (Math.cos(Math.toRadians(rotation)) * power * 2000);
        double y = (Math.sin(Math.toRadians(rotation)) * power * 2000);

        ballObjectList.get(ballObjectList.indexOf(ballWhite)).getBall().applyForce(new Force(x, y));
    }

    private boolean checkInCorrectPocket() {
        int lastPocketed;

        if (currentPlayer.getBallType().equals(Ball.BallType.WHOLE)) {
            lastPocketed = lastPottedWhole;
        } else if (currentPlayer.getBallType().equals(Ball.BallType.HALF)) {
            lastPocketed = lastPottedHalf;
        } else {
            return false;
        }

        switch (lastPocketed) {
            case 1:
                if (ballBlack.getWhichPocket() == 4) {
                    return true;
                }
            case 2:
                if (ballBlack.getWhichPocket() == 5) {
                    return true;
                }
            case 3:
                if (ballBlack.getWhichPocket() == 6) {
                    return true;
                }
            case 4:
                if (ballBlack.getWhichPocket() == 1) {
                    return true;
                }
            case 5:
                if (ballBlack.getWhichPocket() == 2) {
                    return true;
                }
            case 6:
                if (ballBlack.getWhichPocket() == 3) {
                    return true;
                }
        }

        return false;
    }

    private void resetGame() {
        world.removeAllBodies();

        ballObjectList.clear();
        gameObjectList.clear();
        ballsHalf.clear();
        ballsWhole.clear();
        checkingCorners.clear();

        createWalls();
        createCheckers();
        createBalls();

        player1.setBallType(null);
        player2.setBallType(null);

        lastPottedHalf = -1;
        lastPottedWhole = -1;

        turn = new Turn();

        currentPlayer = player1;

        currentTurnLabel.setText("Current turn: " + player1.getNickName());
    }

    private void createWalls() {
        // Wall Left
        Body wallLeft = new Body();
        wallLeft.addFixture(Geometry.createRectangle(5, 35));
        wallLeft.getTransform().setTranslation(35.5, 45.0);
        wallLeft.setMass(MassType.INFINITE);
        world.addBody(wallLeft);
        // Wall Right
        Body wallRight = new Body();
        wallRight.addFixture(Geometry.createRectangle(5, 35));
        wallRight.getTransform().setTranslation(124.5, 45);
        wallRight.setMass(MassType.INFINITE);
        world.addBody(wallRight);

        // Wall Up1
        Body wallUp1 = new Body();
        wallUp1.addFixture(Geometry.createRectangle(34.7, 5));
        wallUp1.getTransform().setTranslation(59.5, 21.2);
        wallUp1.setMass(MassType.INFINITE);
        world.addBody(wallUp1);
        // Wall Up2
        Body wallUp2 = new Body();
        wallUp2.addFixture(Geometry.createRectangle(35.5, 5));
        wallUp2.getTransform().setTranslation(101, 21.2);
        wallUp2.setMass(MassType.INFINITE);
        world.addBody(wallUp2);

        // Wall Down1
        Body wallDown1 = new Body();
        wallDown1.addFixture(Geometry.createRectangle(34.7, 5));
        wallDown1.getTransform().setTranslation(59.5, 68.8);
        wallDown1.setMass(MassType.INFINITE);
        world.addBody(wallDown1);
        // Wall Down2
        Body wallDown2 = new Body();
        wallDown2.addFixture(Geometry.createRectangle(35.5, 5));
        wallDown2.getTransform().setTranslation(101, 68.8);
        wallDown2.setMass(MassType.INFINITE);
        world.addBody(wallDown2);

        // Corner1.1
        Body corner11 = new Body();
        corner11.addFixture(Geometry.createTriangle(new Vector2(0, 0), new Vector2(5, 5), new Vector2(0, 5)));
        corner11.getTransform().setTranslation(33, 22.5);
        corner11.setMass(MassType.INFINITE);
        world.addBody(corner11);
        // Corner1.2
        Body corner12 = new Body();
        corner12.addFixture(Geometry.createTriangle(new Vector2(5, 5), new Vector2(0, 0), new Vector2(5, 0)));
        corner12.getTransform().setTranslation(37.1, 18.7);
        corner12.setMass(MassType.INFINITE);
        world.addBody(corner12);
        // Corner1.3
        Body corner13 = new Body();
        corner13.addFixture(Geometry.createTriangle(new Vector2(5, 5), new Vector2(0, 0), new Vector2(5, 0)));
        corner13.getTransform().setTranslation(37.1, 18.7);
        corner13.setMass(MassType.INFINITE);
        world.addBody(corner13);

        // Corner2.1
        Body corner21 = new Body();
        corner21.addFixture(Geometry.createTriangle(new Vector2(0, 0), new Vector2(1, 0), new Vector2(0, 1.7)));
        corner21.getTransform().setTranslation(76.9, 22);
        corner21.setMass(MassType.INFINITE);
        world.addBody(corner21);
        // Corner2.2
        Body corner22 = new Body();
        corner22.addFixture(Geometry.createTriangle(new Vector2(0, 0), new Vector2(1, 0), new Vector2(1, 1.7)));
        corner22.getTransform().setTranslation(82.2, 22);
        corner22.setMass(MassType.INFINITE);
        world.addBody(corner22);

        // Corner3.1
        Body corner31 = new Body();
        corner31.addFixture(Geometry.createTriangle(new Vector2(0, 0), new Vector2(3, 0), new Vector2(0, 5)));
        corner31.getTransform().setTranslation(118.8, 18.7);
        corner31.setMass(MassType.INFINITE);
        world.addBody(corner31);
        // Corner3.2
        Body corner32 = new Body();
        corner32.addFixture(Geometry.createTriangle(new Vector2(0, 5), new Vector2(5, 0), new Vector2(5, 5)));
        corner32.getTransform().setTranslation(122.0, 22.5);
        corner32.setMass(MassType.INFINITE);
        world.addBody(corner32);

        // Corner4.1
        Body corner41 = new Body();
        corner41.addFixture(Geometry.createTriangle(new Vector2(0, 0), new Vector2(5, 0), new Vector2(5, 5)));
        corner41.getTransform().setTranslation(122, 62.6);
        corner41.setMass(MassType.INFINITE);
        world.addBody(corner41);
        // Corner4.2
        Body corner42 = new Body();
        corner42.addFixture(Geometry.createTriangle(new Vector2(0, 0), new Vector2(3, 5), new Vector2(0, 5)));
        corner42.getTransform().setTranslation(118.8, 66.3);
        corner42.setMass(MassType.INFINITE);
        world.addBody(corner42);

        // Corner5.1
        Body corner51 = new Body();
        corner51.addFixture(Geometry.createTriangle(new Vector2(0, 0), new Vector2(1, 1.7), new Vector2(0, 1.7)));
        corner51.getTransform().setTranslation(76.9, 66.3);
        corner51.setMass(MassType.INFINITE);
        world.addBody(corner51);
        // Corner5.2
        Body corner52 = new Body();
        corner52.addFixture(Geometry.createTriangle(new Vector2(0, 1.7), new Vector2(1, 0), new Vector2(1, 1.7)));
        corner52.getTransform().setTranslation(82.2, 66.3);
        corner52.setMass(MassType.INFINITE);
        world.addBody(corner52);

        // Corner6.1
        Body corner61 = new Body();
        corner61.addFixture(Geometry.createTriangle(new Vector2(0, 0), new Vector2(5, 0), new Vector2(0, 5)));
        corner61.getTransform().setTranslation(33, 62.6);
        corner61.setMass(MassType.INFINITE);
        world.addBody(corner61);
        // Corner6.2
        Body corner62 = new Body();
        corner62.addFixture(Geometry.createTriangle(new Vector2(0, 5), new Vector2(5, 0), new Vector2(5, 5)));
        corner62.getTransform().setTranslation(37.1, 66.3);
        corner62.setMass(MassType.INFINITE);
        world.addBody(corner62);
    }

    private void createCheckers() {
        // Checker1
        Body checker1 = new Body();
        checker1.addFixture(Geometry.createCircle(2.2));
        checker1.getTransform().setTranslation(37.5, 23.0);
        checker1.setMass(MassType.INFINITE);
        world.addBody(checker1);
        checkingCorners.add(checker1);

        // Checker2
        Body checker2 = new Body();
        checker2.addFixture(Geometry.createCircle(2.2));
        checker2.getTransform().setTranslation(80.0, 20.2);
        checker2.setMass(MassType.INFINITE);
        world.addBody(checker2);
        checkingCorners.add(checker2);

        // Checker3
        Body checker3 = new Body();
        checker3.addFixture(Geometry.createCircle(2.2));
        checker3.getTransform().setTranslation(122.8, 22.8);
        checker3.setMass(MassType.INFINITE);
        world.addBody(checker3);
        checkingCorners.add(checker3);

        // Checker4
        Body checker4 = new Body();
        checker4.addFixture(Geometry.createCircle(2.2));
        checker4.getTransform().setTranslation(122.6, 66.9);
        checker4.setMass(MassType.INFINITE);
        world.addBody(checker4);
        checkingCorners.add(checker4);

        // Checker5
        Body checker5 = new Body();
        checker5.addFixture(Geometry.createCircle(2.2));
        checker5.getTransform().setTranslation(80.0, 69.7);
        checker5.setMass(MassType.INFINITE);
        world.addBody(checker5);
        checkingCorners.add(checker5);

        // Checker6
        Body checker6 = new Body();
        checker6.addFixture(Geometry.createCircle(2.2));
        checker6.getTransform().setTranslation(37.4, 66.9);
        checker6.setMass(MassType.INFINITE);
        world.addBody(checker6);
        checkingCorners.add(checker6);
    }
}
