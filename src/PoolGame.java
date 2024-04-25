import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Force;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.jfree.fx.FXGraphics2D;
import org.jfree.fx.ResizableCanvas;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PoolGame extends Application {
    private ResizableCanvas canvas;
    private World world;
    private List<GameObject> gameObjectList = new ArrayList<>();
    private boolean debugSelected = false;
    private BufferedImage image;
    private BufferedImage imageCue;
    private List<Body> balls = new ArrayList<Body>();
    private List<Ball> Ballz = new ArrayList<>();//todo balls vervangen met deze en naam aanpassen
    private List<Ball> ballsWhole = new ArrayList<>();
    private List<Ball> ballsHalf = new ArrayList<>();
    private Ball ballWhite;
    private Ball ballBlack;
    Slider sliderRotation;
    Slider sliderPower;
    private Camera camera;
    private MousePicker mousePicker;

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane mainPane = new BorderPane();
        canvas = new ResizableCanvas(g -> draw(g), mainPane);
        mainPane.setCenter(canvas);
        FXGraphics2D g2d = new FXGraphics2D(canvas.getGraphicsContext2D());

        camera = new Camera(canvas, g -> draw(g), g2d);

        mousePicker = new MousePicker(canvas);

        Slider sliderPower = new Slider(0, 100, 0);
        sliderPower = new Slider(0, 100, 0);
        sliderPower.setShowTickLabels(true);
        Label labelPower = new Label("Power: ");
        HBox power = new HBox(labelPower, sliderPower);
        power.setSpacing(10);

        sliderRotation = new Slider(0, 360, 180);
        Label labelRotation = new Label("Rotation: ");
        HBox rotation = new HBox(labelRotation, sliderRotation);
        rotation.setSpacing(10);

        Button fireButton = new Button("Fire");
        fireButton.setOnAction(event -> shootBall());

        javafx.scene.control.CheckBox showDebug = new CheckBox("Show debug");
        showDebug.setOnAction(e -> {
            debugSelected = showDebug.isSelected();
        });
        HBox hbox = new HBox(showDebug, power, rotation, fireButton);
        hbox.setSpacing(100);
        mainPane.setTop(hbox);

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

    public void init() {
        try {
            image = ImageIO.read(getClass().getResource("Pooltafel.png"));
            imageCue = ImageIO.read(getClass().getResource("cue.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.world = new World();
        world.setGravity(new Vector2(0, 0));

        createBalls();

        createWalls();
    }

    private void draw(FXGraphics2D g) {
        g.setTransform(new AffineTransform());
        g.clearRect(0,0, (int) canvas.getWidth(), (int) canvas.getHeight());
        g.setBackground(Color.white);

        g.setTransform(camera.getTransform((int) canvas.getWidth(), (int) canvas.getHeight()));

        AffineTransform txZoom = new AffineTransform(g.getTransform());
        txZoom.scale(0.1, 0.1);
        g.setTransform(txZoom);
        g.drawImage(image, (1600 - image.getWidth()) / 2, (900 - image.getHeight()) / 2, null);

        AffineTransform cueTransform = new AffineTransform(txZoom);
        cueTransform.translate(balls.get(0).getTransform().getTranslationX()/0.1, balls.get(0).getTransform().getTranslationY()/0.1);
        cueTransform.rotate(Math.toRadians(sliderRotation.getValue()));
        cueTransform.scale(0.15, 0.15);
//        cueTransform.scale(0.01, 0.01);
        g.setTransform(cueTransform);
        g.drawImage(imageCue, 75,-40, null);

        g.setTransform(camera.getTransform((int) canvas.getWidth(), (int) canvas.getHeight()));

        for (GameObject gameObject : gameObjectList) {
            gameObject.draw(g);
        }
        if (debugSelected) {
            g.setColor(Color.blue);
            DebugDraw.draw(g, world, 1);
        }
    }

    private void update(double deltaTime) {
        world.update(deltaTime);
        mousePicker.update(world, camera.getTransform((int) canvas.getWidth(), (int) canvas.getHeight()), 1);
    }

    private void createBalls() {
        for (int i = 0; i < 16; i++) {
            Body ballBody = new Body();
            BodyFixture ballFix = new BodyFixture(Geometry.createCircle(1));//todo radius goed zetten
            ballFix.setRestitution(0.3);
            ballBody.addFixture(ballFix);
            ballBody.setGravityScale(0);
            ballBody.setMass(MassType.NORMAL);

            //gameobjects maken
            GameObject ballObject;

            if (i == 0) {
                ballObject = new GameObject("balls/ball_white.png", ballBody, new Vector2(0, 0), 0.0143);
            } else {
                ballObject = new GameObject("balls/ball_" + i + ".png", ballBody, new Vector2(0, 0), 0.0143);
            }

            //toevoegen aan lijsten
            if (i == 0) {
                Ball ball = new Ball(Ball.BallType.WHITE, ballBody, ballObject);
                Ballz.add(ball);
                ballWhite = ball;
            } else if (i < 8) {
                Ball ball = new Ball(Ball.BallType.WHOLE, ballBody, ballObject);
                Ballz.add(ball);
                ballWhite = ball;
            } else if (i == 8) {
                Ball ball = new Ball(Ball.BallType.BLACK, ballBody, ballObject);
                Ballz.add(ball);
                ballWhite = ball;
            } else {
                Ball ball = new Ball(Ball.BallType.HALF, ballBody, ballObject);
                Ballz.add(ball);
                ballWhite = ball;
            }

            world.addBody(ballBody);
            gameObjectList.add(ballObject);
            balls.add(ballBody);
        }
        resetBalls();
    }

    private void resetBalls() {
        //todo alle ballen goed zetten
        //witte ball
        balls.get(0).translate(new Vector2(59, 45));
        //de rest van de ballen
        double baseX = 98;
        double baseY = 45;
        double offsetX = 1.72;
        double offsetY = 2;

        balls.get(1).translate(new Vector2(baseX, baseY));
        balls.get(2).translate(new Vector2(baseX + offsetX, baseY + offsetY * 0.5));
        balls.get(3).translate(new Vector2(baseX + offsetX, baseY - offsetY * 0.5));
        balls.get(4).translate(new Vector2());
        balls.get(5).translate(new Vector2());
        balls.get(6).translate(new Vector2());
        balls.get(7).translate(new Vector2());
        balls.get(8).translate(new Vector2(baseX + offsetX * 2, baseY));
        balls.get(9).translate(new Vector2());
        balls.get(10).translate(new Vector2());
        balls.get(11).translate(new Vector2());
        balls.get(12).translate(new Vector2());
        balls.get(13).translate(new Vector2());
        balls.get(14).translate(new Vector2());
        balls.get(15).translate(new Vector2());
    }

    private void shootBall() {
        int rotation = (int) sliderRotation.getValue();
        int power = (int) sliderPower.getValue();

        balls.get(0).applyForce(new Force(balls.get(0).getTransform().getTranslationX(),
                balls.get(0).getTransform().getTranslationY()));
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
        corner11.addFixture(Geometry.createTriangle(new Vector2(0,0), new Vector2(5,5), new Vector2(0,5)));
        corner11.getTransform().setTranslation(33, 22.5);
        corner11.setMass(MassType.INFINITE);
        world.addBody(corner11);
        // Corner1.2
        Body corner12 = new Body();
        corner12.addFixture(Geometry.createTriangle(new Vector2(5,5), new Vector2(0,0), new Vector2(5,0)));
        corner12.getTransform().setTranslation(37.1, 18.7);
        corner12.setMass(MassType.INFINITE);
        world.addBody(corner12);
        // Corner1.3
        Body corner13 = new Body();
        corner13.addFixture(Geometry.createTriangle(new Vector2(5,5), new Vector2(0,0), new Vector2(5,0)));
        corner13.getTransform().setTranslation(37.1, 18.7);
        corner13.setMass(MassType.INFINITE);
        world.addBody(corner13);

        // Corner2.1
        Body corner21 = new Body();
        corner21.addFixture(Geometry.createTriangle(new Vector2(0,0), new Vector2(1,0), new Vector2(0,1.7)));
        corner21.getTransform().setTranslation(76.9, 22);
        corner21.setMass(MassType.INFINITE);
        world.addBody(corner21);
        // Corner2.2
        Body corner22 = new Body();
        corner22.addFixture(Geometry.createTriangle(new Vector2(0,0), new Vector2(1,0), new Vector2(1,1.7)));
        corner22.getTransform().setTranslation(82.2, 22);
        corner22.setMass(MassType.INFINITE);
        world.addBody(corner22);

        // Corner3.1
        Body corner31 = new Body();
        corner31.addFixture(Geometry.createTriangle(new Vector2(0,0), new Vector2(3,0), new Vector2(0,5)));
        corner31.getTransform().setTranslation(118.8, 18.7);
        corner31.setMass(MassType.INFINITE);
        world.addBody(corner31);
        // Corner3.2
        Body corner32 = new Body();
        corner32.addFixture(Geometry.createTriangle(new Vector2(0,5), new Vector2(5,0), new Vector2(5,5)));
        corner32.getTransform().setTranslation(122.0, 22.5);
        corner32.setMass(MassType.INFINITE);
        world.addBody(corner32);

        // Corner4.1
        Body corner41 = new Body();
        corner41.addFixture(Geometry.createTriangle(new Vector2(0,0), new Vector2(5,0), new Vector2(5,5)));
        corner41.getTransform().setTranslation(122, 62.6);
        corner41.setMass(MassType.INFINITE);
        world.addBody(corner41);
        // Corner4.2
        Body corner42 = new Body();
        corner42.addFixture(Geometry.createTriangle(new Vector2(0,0), new Vector2(3,5), new Vector2(0,5)));
        corner42.getTransform().setTranslation(118.8, 66.3);
        corner42.setMass(MassType.INFINITE);
        world.addBody(corner42);

        // Corner5.1
        Body corner51 = new Body();
        corner51.addFixture(Geometry.createTriangle(new Vector2(0,0), new Vector2(1,1.7), new Vector2(0,1.7)));
        corner51.getTransform().setTranslation(76.9, 66.3);
        corner51.setMass(MassType.INFINITE);
        world.addBody(corner51);
        // Corner5.2
        Body corner52 = new Body();
        corner52.addFixture(Geometry.createTriangle(new Vector2(0,1.7), new Vector2(1,0), new Vector2(1,1.7)));
        corner52.getTransform().setTranslation(82.2, 66.3);
        corner52.setMass(MassType.INFINITE);
        world.addBody(corner52);

        // Corner6.1
        Body corner61 = new Body();
        corner61.addFixture(Geometry.createTriangle(new Vector2(0,0), new Vector2(5,0), new Vector2(0,5)));
        corner61.getTransform().setTranslation(33, 62.6);
        corner61.setMass(MassType.INFINITE);
        world.addBody(corner61);
        // Corner6.2
        Body corner62 = new Body();
        corner62.addFixture(Geometry.createTriangle(new Vector2(0,5), new Vector2(5,0), new Vector2(5,5)));
        corner62.getTransform().setTranslation(37.1, 66.3);
        corner62.setMass(MassType.INFINITE);
        world.addBody(corner62);
    }
}
