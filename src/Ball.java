import java.awt.image.BufferedImage;

public class Ball {
    private int x;
    private int y;
    private boolean potted;
    private BufferedImage image;

    public Ball() {
        this.x = 0;
        this.y = 0;
        this.potted = false;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isPotted() {
        return potted;
    }

    public void setPotted(boolean potted) {
        this.potted = potted;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
