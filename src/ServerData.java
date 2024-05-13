import org.dyn4j.geometry.Transform;
import java.io.Serializable;
import java.util.ArrayList;

public class ServerData implements Serializable {
    ArrayList<Transform> transforms = new ArrayList<>();
    Turn currentTurn;
    public ServerData(ArrayList<Ball> balls, Turn turn) {
        for (Ball ball : balls) {
            transforms.add(ball.getBall().getTransform());
        }
        this.currentTurn = turn;
    }
    public enum Turn {PLAYER_1, PLAYER_2}
}
