package Elements.Fluid;

import Elements.Element;
import Enums.CollisionCheckStyle;
import Enums.GravityMovement;

import java.awt.*;
import java.util.ArrayList;

public abstract class Fluid extends Element {
    private GravityMovement movement = GravityMovement.DOWN;

    public Fluid(CollisionCheckStyle collisionCheckStyle, Color color, boolean isFlammable, int lifespan) {
        super(collisionCheckStyle, color, isFlammable, lifespan);
    }

    @Override
    abstract public void behave();

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

    public GravityMovement getMovement() {
        return movement;
    }

    public void setMovement(GravityMovement movement) {
        this.movement = movement;
    }
}
