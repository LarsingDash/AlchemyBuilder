package Elements.Gas;

import Elements.Element;
import Enums.CollisionStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public abstract class Gas extends Element {
    public Gas(CollisionStyle collisionStyle, Color color, boolean isFlammable, int customLifespan) {
        super(collisionStyle, color, isFlammable, customLifespan);
    }

    public Gas(CollisionStyle collisionStyle, Color color, boolean isFlammable) {
        this(collisionStyle, color, isFlammable, 15);
    }

    @Override
    public void behave() {
        setPosition(new Point2D.Double(getPosition().x, getPosition().y + 10));
    }

    @Override
    abstract public boolean collide(LinkedList<Element> collided);

    @Override
    public void initFilter() {

    }
}
