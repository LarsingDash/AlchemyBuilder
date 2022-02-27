package Elements.Fluid;

import Elements.Element;
import Engine.AlchemyEngine;
import Enums.CollisionCheckStyle;
import Enums.GravityMovement;

import java.awt.*;
import java.util.ArrayList;

public class Water extends Element implements Fluid {
    private int sleepClock = -1;
    private GravityMovement movement = GravityMovement.DOWN;

    public Water() {this(null);}

    public Water(AlchemyEngine engine) {
        super(engine, CollisionCheckStyle.GRAVITY_FIRST, Color.BLUE, false, 0);
    }

    @Override
    public void behave() {
        sleepClock++;
        if (sleepClock % 2 == 0) return;

        switch (movement) {
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

        if (movement != GravityMovement.DOWN) {
            movement = GravityMovement.DOWN;
        }
    }

    @Override
    public boolean collide(ArrayList<Element> collided) {
        for (Element other : collided) {
            if (collided.get(0).getClass().getName().equals("Elements.Fluid.Water")) {
                Water otherWater = (Water) other;
                if (otherWater.movement == GravityMovement.BLOCKED) {
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
