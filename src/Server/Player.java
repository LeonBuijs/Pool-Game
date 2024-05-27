package Server;

public class Player {
    private int playerNumber;
    private String nickName;
    private Ball.BallType ballType;

    public Player(int playerNumber, String nickName, Ball.BallType ballType) {
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

    public Ball.BallType getBallType() {
        return ballType;
    }

    public void setBallType(Ball.BallType ballType) {
        this.ballType = ballType;
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