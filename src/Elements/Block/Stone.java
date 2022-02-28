package Elements.Block;

import Enums.CollisionCheckStyle;

import java.awt.*;
import java.util.ArrayList;

public class Stone extends Block {
    public Stone() {
        super(CollisionCheckStyle.NONE, Color.GRAY, false, 0);
    }

    @Override
    public void initFilter() {
        setFilter(new ArrayList<>());
    }
}
