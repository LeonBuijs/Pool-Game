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
    }

    private void send(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
    }
}
