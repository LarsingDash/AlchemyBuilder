package Elements.Fluid;

import Elements.Element;
import Enums.CollisionCheckStyle;
import Enums.GravityMovement;

import java.awt.*;
import java.util.ArrayList;

public class Sand extends Element implements Fluid {
    private int sleepClock = -1;
    private GravityMovement movement = GravityMovement.DOWN;

    public Sand() {
        super(CollisionCheckStyle.GRAVITY_FIRST, Color.YELLOW, false, 0);
    }

    @Override
    public void behave() {
        sleepClock++;
        if (sleepClock % 2 == 0) return;

        if (movement == GravityMovement.DOWN) {
            getPosition().setLocation(getPosition().x, getPosition().y - 10);
        }
    }

    @Override
    public boolean collide(ArrayList<Element> collided) {
        for (Element other : collided) {
            if (getEngine().getElementsUnder(Fluid.class).contains(other.getClass())) {
                Fluid otherWater = (Fluid) other;
                if (otherWater.getMovement() == GravityMovement.BLOCKED) {
                    this.movement = GravityMovement.BLOCKED;
                } else {
                    this.movement = GravityMovement.DOWN;
                }
            } else {
                movement = GravityMovement.BLOCKED;
            }
        }

        return true;
    }

    @Override
    public void initFilter() {
        setFilter(new ArrayList<>());
    }

    @Override
    public void setMovement(GravityMovement movement) {
        this.movement = movement;
    }

    @Override
    public GravityMovement getMovement() {
        return movement;
    }
}
