package Elements.Gas;

import Elements.Element;
import Enums.CollisionStyle;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;

public class Fire extends Gas {
    @SuppressWarnings("unused")
    public Fire() {
        super(CollisionStyle.UP, Color.RED, false);
    }

    public Fire(int customLifespan) {
        super(CollisionStyle.UP, Color.RED, false, customLifespan);
    }

    @Override
    public boolean collide(LinkedList<Element> collided) {
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
