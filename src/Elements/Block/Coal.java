package Elements.Block;

import Elements.Gas.Fire;
import Enums.CollisionStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Coal extends Block {
    public Coal() {
        super(CollisionStyle.NONE, Color.BLACK, true, false, 0);
    }

    @Override
    public void behave() {
        Fire fire = new Fire(7);
        fire.setEngine(getEngine());
        fire.setPosition(new Point2D.Double(getPosition().x, getPosition().y + 10));
        if (isLit()) getEngine().addElement(fire);
    }

    @Override
    public void initFilter() {
        setFilter(new ArrayList<>());
    }
}
