import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;
import org.jfree.fx.ResizableCanvas;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class PoolClient extends Application {
    private ResizableCanvas canvas;
    private BufferedImage image;
    private ArrayList<Ball> balls = new ArrayList<>();

    public void init() throws IOException {
        Ball ballWhite = new Ball();
        for (int i = 0; i < 15; i++) {
            Ball ball = new Ball();
            balls.add(ball);
        }
        ballWhite.setImage(ImageIO.read(getClass().getResource("ball_white.png")));
        balls.get(0).setImage(ImageIO.read(getClass().getResource("ball_1.png")));
        balls.get(1).setImage(ImageIO.read(getClass().getResource("ball_2.png")));
        balls.get(2).setImage(ImageIO.read(getClass().getResource("ball_3.png")));
        balls.get(3).setImage(ImageIO.read(getClass().getResource("ball_4.png")));
        balls.get(4).setImage(ImageIO.read(getClass().getResource("ball_5.png")));
        balls.get(5).setImage(ImageIO.read(getClass().getResource("ball_6.png")));
        balls.get(6).setImage(ImageIO.read(getClass().getResource("ball_7.png")));
        balls.get(7).setImage(ImageIO.read(getClass().getResource("ball_8.png")));
        balls.get(8).setImage(ImageIO.read(getClass().getResource("ball_9.png")));
        balls.get(9).setImage(ImageIO.read(getClass().getResource("ball_10.png")));
        balls.get(10).setImage(ImageIO.read(getClass().getResource("ball_11.png")));
        balls.get(11).setImage(ImageIO.read(getClass().getResource("ball_12.png")));
        balls.get(12).setImage(ImageIO.read(getClass().getResource("ball_13.png")));
        balls.get(13).setImage(ImageIO.read(getClass().getResource("ball_14.png")));
        balls.get(14).setImage(ImageIO.read(getClass().getResource("ball_15.png")));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            image = ImageIO.read(getClass().getResource("Pooltafel.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BorderPane mainPane = new BorderPane();
        canvas = new ResizableCanvas(g -> draw(g), mainPane);
        mainPane.setCenter(canvas);
        FXGraphics2D g2d = new FXGraphics2D(canvas.getGraphicsContext2D());

        Socket socket = new Socket("localhost", 2001);

        Thread threadSend = new Thread(() -> {
            try {
                send(socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        threadSend.start();

        Thread threadReceive = new Thread(() -> {
            try {
                receive(socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        threadReceive.start();
        primaryStage.setScene(new Scene(mainPane, 1600, 900));
        primaryStage.setTitle("Pool Game");
        primaryStage.show();
        draw(g2d);
    }

    private void draw(FXGraphics2D g) {
        g.drawImage(image, 0,0, null);
    }

    private void receive(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String input = bufferedReader.readLine();
        String type = input.substring(0, input.indexOf(" "));
        String data = input.substring(input.indexOf(" ")+1);

        switch (type){
            case "ball1":
                // Bal 1
            case "ball2":
                // Bal 2
            case "ball3":
                // Bal 3
            case "ball4":
                // Bal 4
            case "ball5":
                // Bal 5
            case "ball6":
                // Bal 6
            case "ball7":
                // Bal 7
            case "ball8":
                // Bal 8
            case "ball9":
                // Bal 9
            case "ball10":
                // Bal 10
            case "ball11":
                // Bal 11
            case "ball12":
                // Bal 12
            case "ball13":
                // Bal 13
            case "ball14":
                // Bal 14
            case "ball15":
                // Bal 15
            case "ballw":
                // Bal wit
        }
    }

    private void send(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
    }
}
