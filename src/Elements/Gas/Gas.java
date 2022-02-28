package Elements.Gas;

import Elements.Element;
import Enums.CollisionCheckStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public abstract class Gas extends Element {
    public Gas(CollisionCheckStyle collisionCheckStyle, Color color, boolean isFlammable, int customLifespan) {
        super(collisionCheckStyle, color, isFlammable, customLifespan);
    }

    public Gas(CollisionCheckStyle collisionCheckStyle, Color color, boolean isFlammable) {
        this(collisionCheckStyle, color, isFlammable, 15);
    }

    @Override
    public void behave() {
        setPosition(new Point2D.Double(getPosition().x, getPosition().y + 10));
    }

    @Override
    abstract public boolean collide(ArrayList<Element> collided);

    @Override
    public void initFilter() {

    }
}
