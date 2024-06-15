package Server;

import utility.BallType;

import java.io.Serializable;

public class Player implements Serializable {
    private int playerNumber;
    private String nickName;
    private BallType ballType;

    public Player(int playerNumber, String nickName, BallType ballType) {
        this.playerNumber = playerNumber;
        this.nickName = nickName;
        this.ballType = ballType;
    }

    public Player(int playerNumber, String nickName) {
        this.playerNumber = playerNumber;
        this.nickName = nickName;
        this.ballType = null;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public BallType getBallType() {
        return ballType;
    }

    public void setBallType(BallType ballType) {
        this.ballType = ballType;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "Server.Player{" +
                "playerNumber=" + playerNumber +
                ", nickName='" + nickName + '\'' +
                ", ballType=" + ballType +
                '}';
    }
}