package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Force;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import javax.management.monitor.GaugeMonitor;

public class PoolServer {
    private World world;
    private ArrayList<GameObject> gameObjects = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("Server");
        ServerSocket serverSocket = new ServerSocket(2001);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Er is een verbinding");

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
    }

    private static void receive(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
    }

    private static void send(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
    }

    private void init() {
        world = new World();
        world.setGravity(new Vector2(0, 0));
    }
}