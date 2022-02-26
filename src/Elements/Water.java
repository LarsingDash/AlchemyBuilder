package Elements;

import Engine.AlchemyEngine;
import Enums.CollisionCheckStyle;

import java.awt.*;
import java.util.ArrayList;

public class Water extends Element {
    private int sleepClock = -1;
    public Water() {this(null);}

    public Water(AlchemyEngine engine) {
        super(engine, CollisionCheckStyle.FLUID, "water", Color.BLUE, false, 0);
    }

    @Override
    public void behave() {
        sleepClock++;
        if (sleepClock % 2 == 0) return;

        switch (getEngine().moveFluid(this)) {
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
    }

    @Override
    public boolean collide(ArrayList<Element> collided) {
        //todo
        return false;
    }

    @Override
    public void initFilter() {
        setFilter(new ArrayList<>());
    }
}
