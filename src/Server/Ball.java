package Server;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;

public class Ball {
    private BallType ballType;
    private Body ball;
    private GameObject ballObject;
    private boolean running = false;
    private boolean potted = false;
    private int whichPocket = -1;

    public Ball(BallType ballType, Body ball, GameObject ballObject) {
        this.ballType = ballType;
        this.ball = ball;
        this.ballObject = ballObject;
    }

    public void update() {
        Vector2 vector2 = ball.getLinearVelocity();
        if (!running) {
            ball.applyForce(new Vector2(0, 0));
            running = true;
        } else {
            ball.applyImpulse(new Vector2(-vector2.x * 0.2, -vector2.y * 0.2));
        }


    }

    public boolean checkRolling() {
        Vector2 vector2 = ball.getChangeInPosition();

        //test sout
//        if (ballType.equals(BallType.WHITE)) {
//            System.out.println(ball.getChangeInPosition());
//        }

        if (vector2.x < 1.0e-3 && vector2.x > -1.0e-3 && vector2.y < 1.0e-3 && vector2.y > -1.0e-3) {
            return false;
        }
        return true;
    }

    public boolean checkInPocket(ArrayList<Body> corners){
        for (Body corner : corners) {
            if (ball.isInContact(corner)){
                this.whichPocket = corners.indexOf(corner) + 1;
                return true;
            }
        }
        return false;
    }

    public int getWhichPocket() {
        return whichPocket;
    }

    public BallType getBallType() {
        return ballType;
    }

    public Body getBall() {
        return ball;
    }

    public GameObject getBallObject() {
        return ballObject;
    }

    public enum BallType {HALF, WHOLE, BLACK, WHITE}

    public boolean isPotted() {
        return potted;
    }

    public void setPotted(boolean potted) {
        this.potted = potted;
    }
}
