package utility;

import org.dyn4j.geometry.Transform;

import java.awt.geom.AffineTransform;
import java.io.Serializable;

public class TransformCarrier implements Serializable {
    private double x;
    private double y;
    private double rotation;

    public TransformCarrier(Transform transform) {
        this.x = transform.getTranslationX();
        this.y = transform.getTranslationY();
        this.rotation = transform.getRotation();
    }

    public TransformCarrier(AffineTransform transform, double rotation) {
        this.x = transform.getTranslateX();
        this.y = transform.getTranslateY();
        this.rotation = rotation;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
}
