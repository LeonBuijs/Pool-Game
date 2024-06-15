package utility;

import Server.Player;

import java.io.Serializable;

public class ClientData implements Serializable {
    private Player clientPlayer;
    private String nickname;
    private double rotation;
    private double power;
    private boolean fire;

//    public ClientData(Player clientPlayer, double rotation, double power, boolean fire) {
//        this.clientPlayer = clientPlayer;
//        this.rotation = rotation;
//        this.power = power;
//        this.fire = fire;
//    }

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

    public void setClientPlayer(Player clientPlayer) {
        this.clientPlayer = clientPlayer;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public boolean isFire() {
        return fire;
    }

    public void setFire(boolean fire) {
        this.fire = fire;
    }
}
