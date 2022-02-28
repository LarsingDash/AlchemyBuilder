package Elements.Fluid;

import Enums.CollisionCheckStyle;

import java.awt.*;

public class Water extends Fluid {
    private int sleepClock = -1;

    public Water() {
        super(CollisionCheckStyle.GRAVITY_FIRST, Color.BLUE, false, 0);
    }

    @Override
    public void behave() {
        sleepClock++;
        if (sleepClock % 2 == 0) return;

        switch (getMovement()) {
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
                setColor(Color.BLUE);
        }
    }
}
