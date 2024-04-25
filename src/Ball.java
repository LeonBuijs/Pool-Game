import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.jfree.fx.FXGraphics2D;

import java.awt.image.BufferedImage;

public class Ball {
//    private int x;
//    private int y;
//    private boolean potted;
//    private BufferedImage image;
//
//    public Ball() {
//        this.x = 0;
//        this.y = 0;
//        this.potted = false;
//    }
//
//    public int getX() {
//        return x;
//    }
//
//    public void setX(int x) {
//        this.x = x;
//    }
//
//    public int getY() {
//        return y;
//    }
//
//    public void setY(int y) {
//        this.y = y;
//    }
//
//    public boolean isPotted() {
//        return potted;
//    }
//
//    public void setPotted(boolean potted) {
//        this.potted = potted;
//    }
//
//    public BufferedImage getImage() {
//        return image;
//    }
//
//    public void setImage(BufferedImage image) {
//        this.image = image;
//    }
//
//    public void draw(FXGraphics2D g){
//        g.scale(0.2,0.2);
//        g.drawImage(this.image, (this.x*5)-(this.image.getWidth()/2), (this.y*5)-(this.image.getHeight()/2), null);
//        g.scale(5,5);
//    }
    private BallType ballType;
    private Body ball;
    private GameObject ballObject;

    public Ball(BallType ballType, Body ball, GameObject ballObject) {
        this.ballType = ballType;
        this.ball = ball;
        this.ballObject = ballObject;
    }

    public void update() {
//        if (ballType.equals(BallType.WHITE)) {
            System.out.println(ball.getChangeInPosition());
            ball.setMass(MassType.NORMAL);
            Vector2 vector2 = ball.getChangeInPosition();
//            ball.applyImpulse(new Vector2(-vector2.x * 1, -vector2.y * 1));
//        }
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
