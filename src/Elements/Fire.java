package Elements;

import Engine.AlchemyEngine;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

public class Fire extends Element {
    public Fire() {this(null);}

    public Fire(AlchemyEngine engine) {
        this(engine, new Point2D.Double(0,0), 30);
    }

    public Fire(AlchemyEngine engine, Point2D.Double customStartingPoint, int customLifespan) {
        super(engine, customStartingPoint, "fire", Color.RED, false, customLifespan);
    }

    @Override
    public boolean behave() {
        super.getPosition().setLocation(super.getPosition().x, super.getPosition().y + 10);

        ArrayList<Element> collisionElements = super.getEngine().detectCollision(getPosition(), Collections.singletonList(Fire.class), false);
        if (!collisionElements.isEmpty()) {
            for (Element collisionElement : collisionElements) {
                collisionElement.setLit(true);
            }
            return false;
        }

        return true;
    }
}
