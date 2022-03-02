package Elements.Block;

import Enums.CollisionStyle;

import java.awt.*;
import java.util.ArrayList;

public class Stone extends Block {
    public Stone() {
        super(CollisionStyle.NONE, Color.GRAY, false, false, 0, false);
    }

    @Override
    public void initFilter() {
        setFilter(new ArrayList<>());
    }
}
