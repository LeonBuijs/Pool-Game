package utility;

import Server.Player;

import java.io.Serializable;

public class ClientData implements Serializable {
    private Player clientPlayer;
    private String nickname;
    private double rotation;
    private double power;
    private boolean fire;

    public ClientData(Player clientPlayer, String nickname, double rotation, double power, boolean fire) {
        this.clientPlayer = clientPlayer;
        this.nickname = nickname;
        this.rotation = rotation;
        this.power = power;
        this.fire = fire;
    }

    public Player getClientPlayer() {
        return clientPlayer;
    }

    public String getNickname() {
        return nickname;
    }

    public double getRotation() {
        return rotation;
    }

    public double getPower() {
        return power;
    }

    public boolean isFire() {
        return fire;
    }
}
