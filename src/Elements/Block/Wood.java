package Elements.Block;

import Elements.Element;
import Elements.Gas.Fire;
import Enums.CollisionStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Wood extends Block {
    public Wood() {
        super(CollisionStyle.NONE, Color.decode("#a15b00"), true, true, 0);
    }

    @Override
    public void behave() {
        if (isLit() && getBurnCount() > 5) {
            LinkedList<Element> collisionElements = getEngine().collisionCheck(getPosition(), getEngine().invertFilter(Collections.singletonList(Wood.class)), CollisionStyle.ROUND);
            if (!collisionElements.isEmpty()) {
                for (Element collisionElement : collisionElements) {
                    collisionElement.setLit(true);
                }
            }

            if (getBurnCount() == 20) {
                Fire fire = new Fire(7);
                fire.setEngine(getEngine());
                fire.setPosition(new Point2D.Double(getPosition().x, getPosition().y + 10));
                getEngine().addElement(fire);
            }
        }
    }

    @Override
    public void initFilter() {
        setFilter(new ArrayList<>());
    }
}
