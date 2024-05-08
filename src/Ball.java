import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.jfree.fx.FXGraphics2D;

import java.awt.image.BufferedImage;

public class Ball {
    private BallType ballType;
    private Body ball;
    private GameObject ballObject;
    private boolean running = false;

    public Ball(BallType ballType, Body ball, GameObject ballObject) {
        this.ballType = ballType;
        this.ball = ball;
        this.ballObject = ballObject;
    }

    public void update() {
        Vector2 vector2 = ball.getLinearVelocity();
        if (!running) {
            ball.applyForce(new Vector2(0,0));
            running = true;
        } else {
            ball.applyImpulse(new Vector2(-vector2.x * 0.2, -vector2.y * 0.2));
        }

//          todo dit als checker te gebruiken om te kijken of alle ballen stil liggen.
//        if (vector2.x < -1.0e-3 || vector2.x > 1.0e-3 && vector2.y < -1.0e-3 || vector2.y> 1.0e-3) {

//        test sout
//        if (ballType.equals(BallType.WHITE)) {
//            System.out.println(ball.getChangeInOrientation());
//        }
    }

    public boolean checkRolling(){
        Vector2 vector2 = ball.getChangeInPosition();
        if (vector2.x < 1.0e-3 && vector2.y < 1.0e-3) {
//            System.out.println("test");
            return false;
        }
        return true;
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
}
