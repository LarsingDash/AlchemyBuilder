package Elements.Fluid;

import Enums.CollisionStyle;

import java.awt.*;

public class Water extends Fluid {
    private int sleepClock = -1;

    public Water() {
        super(CollisionStyle.FLUID_FIRST, Color.BLUE, false, 0, CollisionStyle.FLUID_SECOND);
    }

    @Override
    public void behave() {
        sleepClock++;
        if (sleepClock % 2 == 0) return;

        if (staticStage != 0) {
            staticCounter++;

            if (staticStage == 1) {
                if (staticCounter > 300) {
                    staticStage = 2;
                } else {
                    if (staticCounter % 10 == 0) {
                        getEngine().collisionCheck(this, getFilter(), CollisionStyle.FLUID_SECOND);
                    }
                }
            } else {
                if (staticCounter % 100 == 0) {
                    getEngine().collisionCheck(this, getFilter(), CollisionStyle.FLUID_SECOND);
                }
            }
        }

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
                setColor(Color.RED);
                break;
        }
    }
}
