package Elements;

import Engine.AlchemyEngine;
import Enums.CollisionCheckStyle;
import Enums.GravityMovement;

import java.awt.*;
import java.util.ArrayList;

public class Sand extends Element {
    private int sleepClock = -1;
    private GravityMovement movement = GravityMovement.DOWN;

    public Sand() {this(null);}

    public Sand(AlchemyEngine engine) {
        super(engine, CollisionCheckStyle.GRAVITY_FIRST, "water", Color.YELLOW, false, 0);
    }

    @Override
    public void behave() {
        sleepClock++;
        if (sleepClock % 2 == 0) return;

        if (movement == GravityMovement.DOWN) {
            getPosition().setLocation(getPosition().x, getPosition().y - 10);
        } else {
            movement = GravityMovement.DOWN;
        }
    }

    @Override
    public boolean collide(ArrayList<Element> collided) {
        for (Element other : collided) {
            if (collided.get(0).getClass() == Sand.class) {
                Sand otherWater = (Sand) other;
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
}
