package Engine.Saving;

import java.io.Serializable;

public class SavablePoint implements Serializable {
    private double x = 0d;
    private double y = 0d;

    @SuppressWarnings("unused")
    public SavablePoint() {
    }

    public SavablePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
