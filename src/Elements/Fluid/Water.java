package Elements.Fluid;

import Enums.CollisionStyle;

import java.awt.*;

public class Water extends Fluid {
    private int sleepClock = -1;

    public Water() {
        super(CollisionStyle.FLUID_SIMPLE, Color.BLUE, false, 0, CollisionStyle.FLUID_FULL);
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
                        getEngine().collisionCheck(this, getFilter(), CollisionStyle.FLUID_FULL);
                    }
                }
            } else {
                if (staticCounter % 100 == 0) {
                    getEngine().collisionCheck(this, getFilter(), CollisionStyle.FLUID_FULL);
                }
            }
        }

        switch (getMovement()) {
            case LEFT:
                getPosition().setLocation(getPosition().x - 10,getPosition().y);
                break;
            case RIGHT:
                getPosition().setLocation(getPosition().x + 10,getPosition().y);
                break;
            case DOWN:
                getPosition().setLocation(getPosition().x, getPosition().y - 10);
                break;
            default:
                break;
        }
    }
}
