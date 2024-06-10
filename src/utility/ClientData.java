package utility;

import java.io.Serializable;

public class ClientData implements Serializable {
    private String nickName;
    private double rotation;
    private double power;
    private boolean fire;

    public ClientData(String nickName, double rotation, double power, boolean fire) {
        this.nickName = nickName;
        this.rotation = rotation;
        this.power = power;
        this.fire = fire;
    }

    public ClientData(double rotation, double power, boolean fire) {
        this.nickName = "";
        this.rotation = rotation;
        this.power = power;
        this.fire = fire;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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
