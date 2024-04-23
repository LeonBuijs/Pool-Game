import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PoolClient extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
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
    }

    private void receive(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
    }

    private void send(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
    }
}
