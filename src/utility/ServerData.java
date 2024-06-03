package utility;

import Server.Ball;
import Server.Player;
import org.dyn4j.geometry.Transform;
import java.io.Serializable;
import java.util.ArrayList;

public class ServerData implements Serializable {
    ArrayList<Transform> transforms = new ArrayList<>();
    Player currentPlayer;
    Player player1;
    Player player2;
    public ServerData(ArrayList<Ball> balls, Player currentlyPlaying, Player player1, Player player2) {
        for (Ball ball : balls) {
            transforms.add(ball.getBall().getTransform());
        }
        this.currentPlayer = currentlyPlaying;
        this.player1 = player1;
        this.player2 = player2;
    }
}
