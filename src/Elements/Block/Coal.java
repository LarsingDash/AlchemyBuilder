package Elements.Block;

import Enums.CollisionStyle;

import java.awt.*;
import java.util.ArrayList;

public class Coal extends Block {
    public Coal() {
        super(CollisionStyle.NONE, Color.BLACK, true, false, 0, true);
    }

    @Override
    public void initFilter() {
        setFilter(new ArrayList<>());
    }
}
