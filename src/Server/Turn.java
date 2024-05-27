package Server;

public class Turn {
    private boolean isTurnActive = false;
    private boolean noBallPotted = true;
    private boolean whiteBallPotted = false;
    private boolean toChangePlayer = false;
    private boolean turnStarted = false;

    public boolean isTurnActive() {
        return isTurnActive;
    }

    public void setTurnActive(boolean turnActive) {
        isTurnActive = turnActive;
    }

    public boolean isNoBallPotted() {
        return noBallPotted;
    }

    public void setNoBallPotted(boolean noBallPotted) {
        this.noBallPotted = noBallPotted;
    }

    public boolean isWhiteBallPotted() {
        return whiteBallPotted;
    }

    public void setWhiteBallPotted(boolean whiteBallPotted) {
        this.whiteBallPotted = whiteBallPotted;
    }

    public boolean isToChangePlayer() {
        return toChangePlayer;
    }

    public void setToChangePlayer(boolean toChangePlayer) {
        this.toChangePlayer = toChangePlayer;
    }

    public boolean isTurnStarted() {
        return turnStarted;
    }

    public void setTurnStarted(boolean turnStarted) {
        this.turnStarted = turnStarted;
    }
}
