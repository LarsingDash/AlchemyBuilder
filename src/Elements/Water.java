package Elements;

import Engine.AlchemyEngine;

import java.awt.*;
import java.awt.geom.Point2D;

public class Water extends Element {
    private int sleepClock = -1;
    public Water() {this(null);}

    public Water(AlchemyEngine engine) {
        super(engine, new Point2D.Double(0,0), "water", Color.BLUE, false, 0);
    }

    @Override
    public boolean behave() {
        sleepClock++;
        if (sleepClock % 2 == 0) return true;

        switch (getEngine().moveFluid(this)) {
            case LEFT:
                getPosition().setLocation(getPosition().x - 10,getPosition().y);
                setColor(Color.YELLOW);
                break;
            case RIGHT:
                getPosition().setLocation(getPosition().x + 10,getPosition().y);
                setColor(Color.YELLOW);
                break;
            case DOWN:
                getPosition().setLocation(getPosition().x, getPosition().y - 10);
                setColor(Color.BLUE);
                break;
            default:
                setColor(Color.RED);
        }

        return true;
    }
}
