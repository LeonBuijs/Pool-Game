package Client;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.dyn4j.geometry.Transform;
import org.jfree.fx.FXGraphics2D;
import org.jfree.fx.ResizableCanvas;
import utility.ClientData;
import utility.ServerData;
import utility.TransformCarrier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
    private TransformCarrier cueTransform;
    private boolean showCue = false;
    private BufferedImage cueImage;
    private boolean running = true;
    private boolean isMyTurn = false;
    private boolean shoot = false;
    private Label currentTurnLabel = new Label();
    private Label playersLabel = new Label();
    private ServerData data;
    private ClientData otherPlayerData;

    Slider sliderRotation = new Slider(0, 360, 180);
    Slider sliderPower = new Slider(0, 100, 0);
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
        cueImage = ImageIO.read(getClass().getResource("res/cue.png"));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane mainPane = new BorderPane();
        canvas = new ResizableCanvas(g -> draw(g), mainPane);
        mainPane.setCenter(canvas);

        Label labelPower = new Label("Power: ");
        sliderPower.setShowTickLabels(true);
        HBox power = new HBox(labelPower, sliderPower);
        power.setSpacing(10);

        Label labelRotation = new Label("Rotation: ");
        HBox rotation = new HBox(labelRotation, sliderRotation);
        rotation.setSpacing(10);

        Button fireButton = new Button("Fire");
        fireButton.setOnAction(event -> {
            if (sliderPower.getValue() != 0) {
                this.shoot = true;
            }
        });

        HBox hBox = new HBox(power, rotation, fireButton, currentTurnLabel, playersLabel);
        hBox.setSpacing(100);

        mainPane.setTop(hBox);

        FXGraphics2D g2d = new FXGraphics2D(canvas.getGraphicsContext2D());

        Socket socket = new Socket("localhost", 2001);

        Thread threadSend = new Thread(() -> {
            while (running) {
                try {
                    send(socket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threadSend.start();

        Thread threadReceive = new Thread(() -> {
            while (running) {
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

        primaryStage.setScene(new Scene(mainPane, this.width, this.height));
        primaryStage.setTitle("Pool Game");
        primaryStage.show();
        draw(g2d);

        new AnimationTimer() {
            long last = -1;

            @Override
            public void handle(long now) {
                if (last == -1) {
                    last = now;
                }
                update((now - last) / 1000000000.0, primaryStage);
                last = now;
                draw(g2d);
            }
        }.start();
    }

    private void update(double deltaTime, Stage primaryStage) {
        //stopt de thread als het tabje gesloten wordt
        running = primaryStage.isShowing();
        currentTurnLabel.setText("Current turn: " + data.getCurrentPlayer().getNickName() + " " + data.getCurrentPlayer().getBallType());
        playersLabel.setText(data.getPlayer1Nickname() + " vs " + data.getPlayer2Nickname()); //TODO: deze methodes returnen null
    }

    private void draw(FXGraphics2D g) {
        g.setTransform(new AffineTransform());
        g.setBackground(Color.white);

        g.clearRect(0, 0, width, height);
        if (isMyTurn) {
            drawPowerBar(g, sliderPower.getValue());
        } else {
            if (otherPlayerData != null) {
                drawPowerBar(g, otherPlayerData.getPower());
            }

        }

        AffineTransform tx = new AffineTransform();
        tx.scale(7, 7);
        g.setTransform(tx);

        AffineTransform pooltable = new AffineTransform();
        pooltable.scale(0.1, 0.1);
        pooltable.translate(poolTable.getWidth() / 2 - 150, poolTable.getHeight() / 2 - 90);
        g.drawImage(poolTable, pooltable, null);

        for (int i = 0; i <= 15; i++) {
            TransformCarrier transform = transformList.get(i);

            AffineTransform tx1 = new AffineTransform();
            tx.rotate(transform.getRotation());
            tx1.translate(transform.getX(), transform.getY());
            tx1.scale(0.0115, 0.0115);
            g.drawImage(balls.get(i), tx1, null);
        }
        if (showCue) {
            AffineTransform cue = new AffineTransform();
            cue.translate(transformList.get(transformList.size() - 1).getX() + (balls.get(balls.size() - 1).getWidth() * 0.0115) / 2,
                    transformList.get(transformList.size() - 1).getY() + (balls.get(balls.size() - 1).getHeight() * 0.0115) / 2 - 0.5);
            cue.scale(0.1, 0.1);
            cue.scale(0.15, 0.15);
            cue.rotate(Math.toRadians(cueTransform.getRotation()));
            g.drawImage(cueImage, cue, null);
        }
    }

    private void receive(Socket socket) throws IOException, ClassNotFoundException {
        InputStream inputStream = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        data = (ServerData) objectInputStream.readObject();
        otherPlayerData = data.getOtherPlayerData();
//        System.out.println(data.getCurrentPlayer().getPlayerNumber());
//        System.out.println(data.getClientPlayer().getPlayerNumber());

        if (data.getCurrentPlayer().getPlayerNumber() == data.getClientPlayer().getPlayerNumber()) {
            isMyTurn = true;
        } else {
            isMyTurn = false;
        }
//        System.out.println(isMyTurn);
        if (!data.getTransforms().isEmpty()) {
            transformList = data.getTransforms();
        }

        cueTransform = data.getCue();
    }

    private void send(Socket socket) throws IOException, InterruptedException {
        //zonder sleep werkt dit niet fsr
        Thread.sleep(1);
        if (isMyTurn) {
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            objectOutputStream.writeObject(new ClientData("Naam", sliderRotation.getValue(), sliderPower.getValue(), this.shoot));
            if (shoot) {
                sliderPower.setValue(0);
            }
            shoot = false;
        }
    }

    private void drawPowerBar(FXGraphics2D g, double power) {
        Rectangle2D rectangle2D = new Rectangle2D.Double(1400, 99, 75, 501);
        Point2D.Double start = new Point2D.Double(1400, 100);
        Point2D.Double end = new Point2D.Double(1475, 600);

        // Define the colors at the start and end points
        float[] fractions = {0.0f, 1.0f};
        Color[] colors = {Color.RED, Color.YELLOW};

        // Create the LinearGradientPaint object
        LinearGradientPaint gradientPaint = new LinearGradientPaint(start, end, fractions, colors);

        g.setColor(Color.black);
        g.draw(rectangle2D);
        g.setPaint(gradientPaint);
        rectangle2D = new Rectangle2D.Double(1400, 599 - (power * 5), 74, power * 5);
        g.fill(rectangle2D);

    }
}
