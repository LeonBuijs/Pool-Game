package utility;

import Server.Ball;
import Server.Player;

import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServerData implements Serializable {
    private List<TransformCarrier> transforms = new ArrayList<>();
    private TransformCarrier cue;
    private boolean showCue;
    private Player clientPlayer;
    private Player currentPlayer;
    private Player player1;
    private Player player2;
    private String player1Nickname;
    private String player2Nickname;
    private ClientData otherPlayerData;
    private boolean disconnected;

    public ServerData(List<Ball> balls, Player clientPlayer, Player currentlyPlaying, Player player1, Player player2, AffineTransform cue, double cueRotation, boolean showCue, ClientData otherPlayerData, boolean disconnected) {
        for (Ball ball : balls) {
            transforms.add(new TransformCarrier(ball.getBall().getTransform()));
        }
        this.clientPlayer = clientPlayer;
        this.currentPlayer = currentlyPlaying;
        this.player1 = player1;
        this.player2 = player2;
        this.cue = new TransformCarrier(cue, cueRotation);
        this.showCue = showCue;
        this.otherPlayerData = otherPlayerData;
        this.disconnected = disconnected;

        applyNickname(clientPlayer);
    }

    public void applyNickname(Player clientPlayer) {
        if (clientPlayer.getPlayerNumber() == 1) {
            player1Nickname = clientPlayer.getNickName();
            if (player2 != null) {
                player2Nickname = player2.getNickName();
            }
        } else if (clientPlayer.getPlayerNumber() == 2) {
            player2Nickname = clientPlayer.getNickName();
            if (player1 != null) {
                player1Nickname = player1.getNickName();
            }
        }
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

    public ClientData getOtherPlayerData() {
        return otherPlayerData;
    }

    public void setOtherPlayerData(ClientData otherPlayerData) {
        this.otherPlayerData = otherPlayerData;
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }
}
