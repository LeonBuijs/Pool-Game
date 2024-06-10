package utility;

import Server.Ball;
import Server.Player;

import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServerData implements Serializable {
    List<TransformCarrier> transforms = new ArrayList<>();
    TransformCarrier cue;
    boolean showCue;
    Player clientPlayer;
    Player currentPlayer;
    Player player1;
    Player player2;
    String player1Nickname;
    String player2Nickname;
    public ServerData(List<Ball> balls, Player clientPlayer, Player currentlyPlaying, Player player1, Player player2, AffineTransform cue, double cueRotation, boolean showCue) {
        for (Ball ball : balls) {
            transforms.add(new TransformCarrier(ball.getBall().getTransform()));
        }
        this.clientPlayer = clientPlayer;
        this.currentPlayer = currentlyPlaying;
        this.player1 = player1;
        this.player2 = player2;
        this.cue = new TransformCarrier(cue, cueRotation);
        this.showCue = showCue;
    }

    public List<TransformCarrier> getTransforms() {
        return transforms;
    }

    public void setTransforms(List<TransformCarrier> transforms) {
        this.transforms = transforms;
    }

    public TransformCarrier getCue() {
        return cue;
    }

    public void setCue(TransformCarrier cue) {
        this.cue = cue;
    }

    public boolean isShowCue() {
        return showCue;
    }

    public void setShowCue(boolean showCue) {
        this.showCue = showCue;
    }

    public Player getClientPlayer() {
        return clientPlayer;
    }

    public void setClientPlayer(Player clientPlayer) {
        this.clientPlayer = clientPlayer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
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
