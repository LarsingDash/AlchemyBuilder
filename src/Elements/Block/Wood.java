package Elements.Block;

import Elements.Element;
import Elements.Gas.Fire;
import Enums.CollisionCheckStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

public class Wood extends Element implements Block {
    public Wood() {
        super(CollisionCheckStyle.NONE, Color.decode("#a15b00"), true, 0);
    }

    @Override
    public void behave() {
        if (isLit() && getBurnCount() > 5) {
            ArrayList<Element> collisionElements = getEngine().collisionCheck(getPosition(), getEngine().invertFilter(Collections.singletonList(Wood.class)), CollisionCheckStyle.ROUND);
            if (!collisionElements.isEmpty()) {
                for (Element collisionElement : collisionElements) {
                    collisionElement.setLit(true);
                }
            }

            if (getBurnCount() == 20) {
                Fire fire = new Fire(new Point2D.Double(getPosition().x, getPosition().y + 10), 7);
                fire.setEngine(getEngine());
                getEngine().addElement(fire);
            }
        }
    }

    @Override
    public boolean collide(ArrayList<Element> collided) {
        return true;
    }

    @Override
    public void initFilter() {
        setFilter(new ArrayList<>());
    }
}
