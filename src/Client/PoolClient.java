package Client;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.dyn4j.geometry.Transform;
import org.jfree.fx.FXGraphics2D;
import org.jfree.fx.ResizableCanvas;
import utility.ServerData;
import utility.TransformCarrier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PoolClient extends Application {
    private int width = 1600;
    private int height = 900;
    private ResizableCanvas canvas;
    private BufferedImage poolTable;
    private List<TransformCarrier> transformList = new ArrayList<>();
    private List<BufferedImage> balls = new ArrayList<>();
//    private ArrayList<Ball> balls = new ArrayList<>();
//    private Ball ballWhite;

    public void init() throws IOException {
        for (int i = 1; i <= 15; i++) {
            balls.add(ImageIO.read(getClass().getResource("res/ball_" + i + ".png")));
        }

        try {
            poolTable = ImageIO.read(getClass().getResource("res/Pooltafel.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        balls.add(ImageIO.read(getClass().getResource("res/ball_white.png")));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane mainPane = new BorderPane();
        canvas = new ResizableCanvas(g -> draw(g), mainPane);
        mainPane.setCenter(canvas);
        FXGraphics2D g2d = new FXGraphics2D(canvas.getGraphicsContext2D());

        Socket socket = new Socket("localhost", 2001);

        Thread threadSend = new Thread(() -> {
            while (true) {
                try {
                    send(socket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threadSend.start();

        Thread threadReceive = new Thread(() -> {
            while (true) {
                try {
                    receive(socket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threadReceive.start();

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

        primaryStage.setScene(new Scene(mainPane, this.width, this.height));
        primaryStage.setTitle("Pool Game");
        primaryStage.show();
        draw(g2d);
    }

    private void update(double deltaTime) {

    }

    private void draw(FXGraphics2D g) {
        g.setTransform(new AffineTransform());
        g.setBackground(Color.white);

        g.clearRect(0, 0, width, height);

        AffineTransform tx = new AffineTransform();
        tx.scale(7, 7);
        g.setTransform(tx);

        AffineTransform pooltable = new AffineTransform();
        pooltable.scale(0.1, 0.1);
        pooltable.translate(poolTable.getWidth()/2 - 150, poolTable.getHeight()/2 - 90);
        g.drawImage(poolTable, pooltable, null);

        for (int i = 0; i <= 15; i++) {
            TransformCarrier transform = transformList.get(i);

            AffineTransform tx1 = new AffineTransform();
//                tx.rotate(transform.getRotation()); fixme
            tx1.translate(transform.getX(), transform.getY());
            tx1.scale(0.0115, 0.0115);
            g.drawImage(balls.get(i), tx1, null);
        }
    }

    private void receive(Socket socket) throws IOException, ClassNotFoundException {
        InputStream inputStream = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        ServerData data = (ServerData) objectInputStream.readObject();
        if (!data.getTransforms().isEmpty()) {
            transformList = data.getTransforms();
        }
    }

    private void send(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
    }
}
