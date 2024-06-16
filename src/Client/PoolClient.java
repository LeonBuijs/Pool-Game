package Client;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;
import org.jfree.fx.ResizableCanvas;
import utility.*;

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
import java.util.function.UnaryOperator;

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
    private String nickname = "Guest";
    private NameChecker nameChecker = new NameChecker();

    Slider sliderRotation = new Slider(0, 360, 180);
    Slider sliderPower = new Slider(0, 100, 0);

    private String playerNames = "";

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
        HBox hBox = getHBox(labelPower);

        mainPane.setTop(hBox);

        FXGraphics2D g2d = new FXGraphics2D(canvas.getGraphicsContext2D());

        Thread threadReceive = getThreads();
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

    private Thread getThreads() throws IOException {
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
        return threadReceive;
    }

    private HBox getHBox(Label labelPower) {
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

        Label nameLabel = new Label("nickname: ");

        TextField textField = new TextField("Guest");
        textField.setMinWidth(200);

        UnaryOperator<TextFormatter.Change> rejectChange = change -> {
            if (change.isContentChange()) {
                if (change.getControlNewText().length() > 12) {
                    return null;
                }
            }
            return change;
        };

        textField.setTextFormatter(new TextFormatter<>(rejectChange));

        Button changeNameButton = new Button("Change");
        changeNameButton.setOnAction(e -> {
            if (data == null) {
                return;
            }

            if (nameChecker.checkNickname(textField.getText())) {
                this.nickname = textField.getText();
                data.getClientPlayer().setNickName(nickname);
            } else {
                this.nickname = "naughty";
                data.getClientPlayer().setNickName("naughty");
                textField.setText("naughty");
            }
        });

        HBox hBox = new HBox(power, rotation, fireButton, currentTurnLabel, playersLabel, new HBox(nameLabel, textField, changeNameButton));
        hBox.setSpacing(50);
        return hBox;
    }

    private void update(double deltaTime, Stage primaryStage) {
        //stopt de thread als het tabje gesloten wordt
        running = primaryStage.isShowing();

        //los object zodat thread niet tussendoor het data-object aanpast
        ServerData data1 = data;

        data1.getClientPlayer().setNickName(nickname);
        data1.applyNickname(data1.getClientPlayer());

        currentTurnLabel.setText("Current turn: " + data1.getCurrentPlayer().getNickName());
        playersLabel.setText(data1.getPlayer1Nickname() + " VS " + data1.getPlayer2Nickname());
        playerNames = data1.getPlayer1Nickname() + " VS " + data1.getPlayer2Nickname();
    }

    private void draw(FXGraphics2D g) {
        g.setTransform(new AffineTransform());
        g.setBackground(Color.white);

        g.clearRect(0, 0, width, height);
        drawPlayerNames(g);
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

        AffineTransform poolTable = new AffineTransform();
        poolTable.scale(0.1, 0.1);
        poolTable.translate(this.poolTable.getWidth() / 2 - 150, this.poolTable.getHeight() / 2 - 90);
        g.drawImage(this.poolTable, poolTable, null);

        for (int i = 0; i <= 15; i++) {
            TransformCarrier transform = transformList.get(i);

            AffineTransform tx1 = new AffineTransform();
            tx.rotate(transform.getRotation());
            tx1.translate(transform.getX(), transform.getY());
            tx1.scale(0.0115, 0.0115);
            g.drawImage(balls.get(i), tx1, null);
        }
        if (showCue) {
            //todo afstand fixen
            AffineTransform cueTransform = new AffineTransform();
            cueTransform.translate(transformList.get(transformList.size() - 1).getX() + (balls.get(balls.size() - 1).getWidth() * 0.0115 - 1.5 + Math.cos(Math.toRadians(this.cueTransform.getRotation())) * 2.5),
                    transformList.get(transformList.size() - 1).getY() + (balls.get(balls.size() - 1).getHeight() * 0.0115) - 1.5 + Math.sin(Math.toRadians(this.cueTransform.getRotation())) * 2.5);
            cueTransform.scale(0.1, 0.1);
            cueTransform.scale(0.15, 0.15);
            cueTransform.rotate(Math.toRadians(this.cueTransform.getRotation()), cueTransform.getTranslateX(), cueTransform.getTranslateY());
            g.drawImage(cueImage, cueTransform, null);
        }
    }

    private void receive(Socket socket) throws IOException, ClassNotFoundException {
        InputStream inputStream = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        data = (ServerData) objectInputStream.readObject();
        otherPlayerData = data.getOtherPlayerData();

        if (data.getCurrentPlayer().getPlayerNumber() == data.getClientPlayer().getPlayerNumber()) {
            isMyTurn = true;
        } else {
            isMyTurn = false;
        }
        if (!data.getTransforms().isEmpty()) {
            transformList = data.getTransforms();
        }

        cueTransform = data.getCue();
        showCue = data.isShowCue();
    }

    private void send(Socket socket) throws IOException, InterruptedException {
        //zonder sleep werkt dit niet fsr
        Thread.sleep(1);
        if (isMyTurn) {
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            objectOutputStream.writeObject(new ClientData(data.getClientPlayer(), nickname, sliderRotation.getValue(), sliderPower.getValue(), this.shoot));
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

    private void drawPlayerNames(FXGraphics2D g) {
        g.setColor(Color.black);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
        g.drawString(this.playerNames, this.width / 2 - ((int) (this.playerNames.length() * 7.5)), 50);
        g.drawLine(this.width / 2, 0, this.width / 2, this.height);

        AffineTransform transform = new AffineTransform();
        AffineTransform transform2 = new AffineTransform();
        transform.translate(550, 75);
        transform2.translate(825, 75);
        transform.scale(0.2, 0.2);
        transform2.scale(0.2, 0.2);

        if (data == null) {
            return;
        }

        if (data.getPlayer1().getBallType() == null) {
            return;
        }

        if (data.getPlayer1().getBallType().equals(BallType.WHOLE)) {
            // Half links & Heel rechts
            for (int i = 0; i < 7; i++) {
                g.drawImage(balls.get(i), transform, null);
                g.drawImage(balls.get(i + 8), transform2, null);
                transform.translate(balls.get(i).getWidth() + 20, 0);
                transform2.translate(balls.get(i).getWidth() + 20, 0);
            }
        } else if (data.getPlayer1().getBallType().equals(BallType.HALF)) {
            // Heel links & Half rechts
            for (int i = 0; i < 7; i++) {
                g.drawImage(balls.get(i + 8), transform, null);
                g.drawImage(balls.get(i), transform2, null);
                transform.translate(balls.get(i).getWidth() + 20, 0);
                transform2.translate(balls.get(i).getWidth() + 20, 0);
            }
        }
    }
}
