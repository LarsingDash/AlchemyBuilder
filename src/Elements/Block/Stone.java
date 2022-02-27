package Elements.Block;

import Elements.Element;
import Enums.CollisionCheckStyle;

import java.awt.*;
import java.util.ArrayList;

public class Stone extends Element implements Block {
    public Stone() {
        super(CollisionCheckStyle.NONE, Color.GRAY, false, 0);
    }

    @Override
    public void behave() {}

    @Override
    public boolean collide(ArrayList<Element> collided) {
        return true;
    }

    @Override
    public void initFilter() {
        setFilter(new ArrayList<>());
    }
}
