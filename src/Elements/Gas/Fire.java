package Elements.Gas;

import Elements.Element;
import Enums.CollisionCheckStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

public class Fire extends Element implements Gas {
    public Fire() {
        this(new Point2D.Double(0, 0), 15);
    }

    public Fire(Point2D.Double customStartingPoint, int customLifespan) {
        super(CollisionCheckStyle.UP, Color.RED, false, customLifespan);
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
