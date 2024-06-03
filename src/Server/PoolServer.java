package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Force;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import utility.ServerData;


import javax.management.monitor.GaugeMonitor;

public class PoolServer {
    private World world;
    private List<GameObject> gameObjectList = new ArrayList<>();

    private Ball ballWhite;
    private Ball ballBlack;
    private List<Ball> ballsWhole = new ArrayList<>();
    private List<Ball> ballsHalf = new ArrayList<>();
    private List<Ball> ballObjectList = new ArrayList<>();
    private ArrayList<Body> checkingCorners = new ArrayList<>();

    private int lastPottedWhole = -1;
    private int lastPottedHalf = -1;

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

        createBalls();
        createWalls();
        createCheckers();
    }

    private void createBalls() {
        for (int i = 0; i < 16; i++) {
            Body ballBody = new Body();
            BodyFixture ballFix = new BodyFixture(Geometry.createCircle(0.8));
            ballFix.setDensity(10);
            ballFix.setRestitution(0.1);
            ballBody.setAngularDamping(1);
            ballBody.addFixture(ballFix);
            ballBody.setGravityScale(0);
            ballBody.setMass(MassType.NORMAL);

            GameObject ballObject;

            if (i == 0) {
                ballObject = new GameObject("res.balls/balls/ball_white.png", ballBody, new Vector2(0, 0), 0.0115);
                ballBody.setBullet(true);
            } else {
                ballObject = new GameObject("res.balls/balls/ball_" + i + ".png", ballBody, new Vector2(0, 0), 0.0115);
            }

            //toevoegen aan lijsten
            Ball ball;
            if (i == 0) {
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

        ballObjectList.get(1).getBall().translate(new Vector2(baseX, baseY));
        ballObjectList.get(2).getBall().translate(new Vector2(baseX + offsetX, baseY + offsetY));
        ballObjectList.get(3).getBall().translate(new Vector2(baseX + offsetX, baseY - offsetY));
        ballObjectList.get(4).getBall().translate(new Vector2(baseX + offsetX * 2, baseY + offsetY * 2));
        ballObjectList.get(8).getBall().translate(new Vector2(baseX + offsetX * 2, baseY));
        ballObjectList.get(5).getBall().translate(new Vector2(baseX + offsetX * 2, baseY - offsetY * 2));
        ballObjectList.get(6).getBall().translate(new Vector2(baseX + offsetX * 3, baseY + offsetY * 3));
        ballObjectList.get(7).getBall().translate(new Vector2(baseX + offsetX * 3, baseY + offsetY * 1));
        ballObjectList.get(9).getBall().translate(new Vector2(baseX + offsetX * 3, baseY - offsetY * 1));
        ballObjectList.get(10).getBall().translate(new Vector2(baseX + offsetX * 3, baseY - offsetY * 3));
        ballObjectList.get(11).getBall().translate(new Vector2(baseX + offsetX * 4, baseY + offsetY * 4));
        ballObjectList.get(12).getBall().translate(new Vector2(baseX + offsetX * 4, baseY + offsetY * 2));
        ballObjectList.get(13).getBall().translate(new Vector2(baseX + offsetX * 4, baseY));
        ballObjectList.get(14).getBall().translate(new Vector2(baseX + offsetX * 4, baseY - offsetY * 2));
        ballObjectList.get(15).getBall().translate(new Vector2(baseX + offsetX * 4, baseY - offsetY * 4));
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