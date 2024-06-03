package utility;

import Server.Ball;
import Server.Player;
import org.dyn4j.geometry.Transform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServerData implements Serializable {
    List<TransformCarrier> transforms = new ArrayList<>();
    int currentPlayer;
    int player1;
    int player2;
    String player1Nickname;
    String player2Nickname;
    public ServerData(List<Ball> balls, int currentlyPlaying, int player1, int player2) {
        for (Ball ball : balls) {
            transforms.add(new TransformCarrier(ball.getBall().getTransform()));
        }
        this.currentPlayer = currentlyPlaying;
        this.player1 = player1;
        this.player2 = player2;
    }

    public List<TransformCarrier> getTransforms() {
        return transforms;
    }

    public void setTransforms(List<TransformCarrier> transforms) {
        this.transforms = transforms;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getPlayer1() {
        return player1;
    }

    public void setPlayer1(int player1) {
        this.player1 = player1;
    }

    public int getPlayer2() {
        return player2;
    }

    public void setPlayer2(int player2) {
        this.player2 = player2;
    }

    public String getPlayer1Nickname() {
        return player1Nickname;
    }

    public void setPlayer1Nickname(String player1Nickname) {
        this.player1Nickname = player1Nickname;
    }

    public String getPlayer2Nickname() {
        return player2Nickname;
    }

    public void setPlayer2Nickname(String player2Nickname) {
        this.player2Nickname = player2Nickname;
    }
}
