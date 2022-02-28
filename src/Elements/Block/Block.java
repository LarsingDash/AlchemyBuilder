package Elements.Block;

import Elements.Element;
import Enums.CollisionStyle;

import java.awt.*;
import java.util.LinkedList;

public abstract class Block extends Element {
    public Block(CollisionStyle collisionStyle, Color color, boolean isFlammable, int lifespan) {
        super(collisionStyle, color, isFlammable, lifespan);
    }

    @Override
    public void behave() {}

    @Override
    public boolean collide(LinkedList<Element> collided) {
        return true;
    }

    abstract public void initFilter();
}
