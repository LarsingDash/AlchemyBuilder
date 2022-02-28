package Elements.Fluid;

import Elements.Element;
import Enums.CollisionStyle;
import Enums.FluidMovement;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public abstract class Fluid extends Element {
    private FluidMovement movement = FluidMovement.DOWN;
    private final CollisionStyle fluidStyle;

    int staticStage = 0;
    int staticCounter = 0;

    public Fluid(CollisionStyle collisionStyle, Color color, boolean isFlammable, int lifespan, CollisionStyle fluidStyle) {
        super(collisionStyle, color, isFlammable, lifespan);
        this.fluidStyle = fluidStyle;
    }

    @Override
    abstract public void behave();

    @Override
    public boolean collide(LinkedList<Element> collided) {
        if (fluidStyle == CollisionStyle.FLUID_FIRST) {
            movement = FluidMovement.BLOCKED;
        } else if (staticStage < 1) {
            if (collided.get(0).getClass().getSuperclass() == Fluid.class) {
                Fluid other = (Fluid) collided.get(0);
                if (other.getMovement() == FluidMovement.DOWN) return true;
                getEngine().collisionCheck(this, getFilter(), CollisionStyle.FLUID_SECOND);
            } else {
                getEngine().collisionCheck(this, getFilter(), CollisionStyle.FLUID_SECOND);
            }
        }

        return true;
    }

    @Override
    public void initFilter() {
        setFilter(new ArrayList<>());
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
