package Elements.Block;

import Elements.Element;
import Elements.Gas.Fire;
import Enums.CollisionStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.LinkedList;

public abstract class Block extends Element {
    private final boolean isEmitter;

    public Block(CollisionStyle collisionStyle, Color color, boolean isFlammable, boolean isBurnable, int lifespan, boolean isEmitter) {
        super(collisionStyle, color, isFlammable, isBurnable, lifespan);
        this.isEmitter = isEmitter;
    }

    @Override
    public void behave() {
        if  (isFlammable()) {
            if (isLit() && getBurnCount() > 5) {
                LinkedList<Element> collisionElements = getEngine().collisionCheck(getPosition(), getEngine().invertFilter(Collections.singletonList(this.getClass())), CollisionStyle.ROUND);
                if (!collisionElements.isEmpty()) {
                    for (Element collisionElement : collisionElements) {
                        collisionElement.setLit(true);
                    }
                }

                if (getBurnCount() == 20 || (getBurnCount() >= 20) && isEmitter) {
                    Fire fire = new Fire(7);
                    fire.setEngine(getEngine());
                    fire.setPosition(new Point2D.Double(getPosition().x, getPosition().y + 10));
                    getEngine().addElement(fire);
                }
            }
        }
    }

    @Override
    public boolean collide(LinkedList<Element> collided) {
        return true;
    }

    abstract public void initFilter();
}
