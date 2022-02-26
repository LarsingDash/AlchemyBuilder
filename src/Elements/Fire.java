package Elements;

import Engine.AlchemyEngine;
import Enums.CollisionCheckStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

public class Fire extends Element {
    public Fire() {
        this(null);
    }

    public Fire(AlchemyEngine engine) {
        this(engine, new Point2D.Double(0, 0), 30);
    }

    public Fire(AlchemyEngine engine, Point2D.Double customStartingPoint, int customLifespan) {
        super(engine, CollisionCheckStyle.UP, "fire", Color.RED, false, customLifespan);
        setPosition(customStartingPoint);
    }

    @Override
    public void behave() {
        setPosition(new Point2D.Double(getPosition().x, getPosition().y + 10));
    }

    @Override
    public boolean collide(ArrayList<Element> collided) {
        for (Element collisionElement : collided) {
            collisionElement.setLit(true);
        }
        return false;
    }

    @Override
    public void initFilter() {
        setFilter(Collections.singletonList(Fire.class));
    }
}
