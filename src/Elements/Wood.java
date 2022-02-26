package Elements;

import Engine.AlchemyEngine;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

public class Wood extends Element {
    public Wood() {this(null);}

    public Wood(AlchemyEngine engine) {
        super(engine, new Point2D.Double(0,0), "wood", Color.decode("#a15b00"), true, 0);
    }

    @Override
    public boolean behave() {
        if (this.isLit() && this.getBurnCount() > 5) {
            ArrayList<Element> collisionElements = super.getEngine().detectCollision(getPosition(), Collections.singletonList(Wood.class), true, new Point2D.Double(-1,-1), 25);
            if (!collisionElements.isEmpty()) {
                for (Element collisionElement : collisionElements) {
                    collisionElement.setLit(true);
                }
            }

            if (this.getBurnCount() == 20) {
                this.getEngine().addElement(new Fire(this.getEngine(), new Point2D.Double(getPosition().x, getPosition().y + 10), 7));
            }
        }

        return true;
    }
}
