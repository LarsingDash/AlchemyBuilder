package Elements.Gas;

import Elements.Element;
import Enums.CollisionCheckStyle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Fire extends Gas {
    @SuppressWarnings("unused")
    public Fire() {
        super(CollisionCheckStyle.UP, Color.RED, false);
    }

    public Fire(int customLifespan) {
        super(CollisionCheckStyle.UP, Color.RED, false, customLifespan);
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
