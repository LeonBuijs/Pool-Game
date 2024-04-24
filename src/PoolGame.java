import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.jfree.fx.FXGraphics2D;
import org.jfree.fx.ResizableCanvas;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PoolGame extends Application {
    private ResizableCanvas canvas;
    private World world;
    private List<GameObject> gameObjectList = new ArrayList<>();
    private boolean debugSelected = true;//fixme op false zetten
    private BufferedImage image;
    private List<GameObject> balls = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane mainPane = new BorderPane();
        canvas = new ResizableCanvas(g -> draw(g), mainPane);
        mainPane.setCenter(canvas);
        FXGraphics2D g2d = new FXGraphics2D(canvas.getGraphicsContext2D());

        javafx.scene.control.CheckBox showDebug = new CheckBox("Show debug");
        showDebug.setOnAction(e -> {
            debugSelected = showDebug.isSelected();
        });
        mainPane.setTop(showDebug);

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.world = new World();
        world.setGravity(new Vector2(0, 0));

        createBalls();

        // Wall Left
        Body wallLeft = new Body();
        wallLeft.addFixture(Geometry.createRectangle(50, 350));
        wallLeft.getTransform().setTranslation(355, 450);
        wallLeft.setMass(MassType.INFINITE);
        world.addBody(wallLeft);
        // Wall Right
        Body wallRight = new Body();
        wallRight.addFixture(Geometry.createRectangle(50, 350));
        wallRight.getTransform().setTranslation(1245, 450);
        wallRight.setMass(MassType.INFINITE);
        world.addBody(wallRight);
    }

    private void draw(FXGraphics2D g) {
        g.drawImage(image, (1600 - image.getWidth()) / 2, (900 - image.getHeight()) / 2, null);
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
    }

    private void createBalls() {
//        ballWhite.setImage(ImageIO.read(getClass().getResource("balls/ball_white.png")));
//        balls.get(0).setImage(ImageIO.read(getClass().getResource("balls/ball_1.png")));
//        balls.get(1).setImage(ImageIO.read(getClass().getResource("balls/ball_2.png")));
//        balls.get(2).setImage(ImageIO.read(getClass().getResource("balls/ball_3.png")));
//        balls.get(3).setImage(ImageIO.read(getClass().getResource("balls/ball_4.png")));
//        balls.get(4).setImage(ImageIO.read(getClass().getResource("balls/ball_5.png")));
//        balls.get(5).setImage(ImageIO.read(getClass().getResource("balls/ball_6.png")));
//        balls.get(6).setImage(ImageIO.read(getClass().getResource("balls/ball_7.png")));
//        balls.get(7).setImage(ImageIO.read(getClass().getResource("balls/ball_8.png")));
//        balls.get(8).setImage(ImageIO.read(getClass().getResource("balls/ball_9.png")));
//        balls.get(9).setImage(ImageIO.read(getClass().getResource("balls/ball_10.png")));
//        balls.get(10).setImage(ImageIO.read(getClass().getResource("balls/ball_11.png")));
//        balls.get(11).setImage(ImageIO.read(getClass().getResource("balls/ball_12.png")));
//        balls.get(12).setImage(ImageIO.read(getClass().getResource("balls/ball_13.png")));
//        balls.get(13).setImage(ImageIO.read(getClass().getResource("balls/ball_14.png")));
//        balls.get(14).setImage(ImageIO.read(getClass().getResource("balls/ball_15.png")));

        for (int i = 0; i < 16; i++) {
            Body ball = new Body();
            BodyFixture ballFix = new BodyFixture(Geometry.createCircle(10));//todo radius goed zetten
            ballFix.setRestitution(0.3);
            ball.addFixture(ballFix);
            ball.setGravityScale(0);
            ball.setMass(MassType.NORMAL);
            ball.setBullet(true);//voorkomt clipping

            GameObject ballObject;
            if (i == 0) {
                ballObject = new GameObject("balls/ball_white.png", ball, new Vector2(0, 0), 1);
//                world.addBody(ball);
//                gameObjectList.add(ballObject);
//                balls.add(ballObject);
            } else {
                ballObject = new GameObject("balls/ball_" + i + ".png", ball, new Vector2(0, 0), 0.1);
            }
//            ball.translate(new Vector2(1, 1));
            ball.translateToOrigin();
            world.addBody(ball);
            gameObjectList.add(ballObject);
            balls.add(ballObject);
        }
    }
}
