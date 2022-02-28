package Elements.Fluid;

import Enums.CollisionStyle;
import Enums.FluidMovement;

import java.awt.*;

public class Sand extends Fluid {
    private int sleepClock = -1;

    public Sand() {
        super(CollisionStyle.FLUID_FIRST, Color.YELLOW, false, 0, CollisionStyle.FLUID_FIRST);
    }

    @Override
    public void behave() {
        sleepClock++;
        if (sleepClock % 2 == 0) return;

        if (getMovement() == FluidMovement.DOWN) {
            getPosition().setLocation(getPosition().x, getPosition().y - 10);
        }
    }
}
