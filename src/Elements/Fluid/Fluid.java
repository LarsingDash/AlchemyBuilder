package Elements.Fluid;

import Elements.Element;
import Elements.Gas.Gas;
import Enums.CollisionStyle;
import Enums.FluidMovement;

import java.awt.*;
import java.util.LinkedList;

public abstract class Fluid extends Element {
    private FluidMovement movement = FluidMovement.DOWN;
    private final CollisionStyle fluidStyle;

    int staticStage = 0;
    int staticCounter = 0;

    public Fluid(CollisionStyle collisionStyle, Color color, boolean isFlammable, int lifespan, CollisionStyle fluidStyle) {
        super(collisionStyle, color, isFlammable, false, lifespan);
        this.fluidStyle = fluidStyle;
    }

    @Override
    abstract public void behave();

    @Override
    public boolean collide(LinkedList<Element> collided) {
        if (fluidStyle == CollisionStyle.FLUID_SIMPLE) {
            movement = FluidMovement.BLOCKED;
        } else if (staticStage < 1) {
            if (collided.get(0).getClass().getSuperclass() == Fluid.class) {
                Fluid other = (Fluid) collided.get(0);
                if (other.getMovement() == FluidMovement.DOWN) return true;
                getEngine().collisionCheck(this, getFilter(), CollisionStyle.FLUID_FULL);
            } else {
                getEngine().collisionCheck(this, getFilter(), CollisionStyle.FLUID_FULL);
            }
        }

        extraCollide(collided);

        return true;
    }

    public void extraCollide(LinkedList<Element> collided) {};

    @Override
    public void initFilter() {
        setFilter(getEngine().getElementsUnder(Gas.class));
    }

    public FluidMovement getMovement() {
        return movement;
    }

    public void setMovement(FluidMovement movement) {
        if (movement == FluidMovement.BLOCKED) {
            staticStage = 1;
            staticCounter++;
        } else {
            staticStage = 0;
        }
        this.movement = movement;
    }
}
