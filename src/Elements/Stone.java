package Elements;

import Engine.AlchemyEngine;

import java.awt.*;
import java.awt.geom.Point2D;

public class Stone extends Element {
    public Stone() {this(null);}

    public Stone(AlchemyEngine engine) {
        super(engine, new Point2D.Double(0,0), "stone", Color.GRAY, false, 0);
    }

    @Override
    public boolean behave() {
        return true;
    }
}
