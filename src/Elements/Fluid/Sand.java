package Elements.Fluid;

import Enums.CollisionCheckStyle;
import Enums.GravityMovement;

import java.awt.*;

public class Sand extends Fluid {
    private int sleepClock = -1;

    public Sand() {
        super(CollisionCheckStyle.GRAVITY_FIRST, Color.YELLOW, false, 0);
    }

    @Override
    public void behave() {
        sleepClock++;
        if (sleepClock % 2 == 0) return;

        if (getMovement() == GravityMovement.DOWN) {
            getPosition().setLocation(getPosition().x, getPosition().y - 10);
        }
    }
}
