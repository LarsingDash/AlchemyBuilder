package Elements.Block;

import Elements.Element;
import Enums.CollisionCheckStyle;

import java.awt.*;
import java.util.ArrayList;

public abstract class Block extends Element {
    public Block(CollisionCheckStyle collisionCheckStyle, Color color, boolean isFlammable, int lifespan) {
        super(collisionCheckStyle, color, isFlammable, lifespan);
    }

    @Override
    public void behave() {}

    @Override
    public boolean collide(ArrayList<Element> collided) {
        return true;
    }

    abstract public void initFilter();
}
