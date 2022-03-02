package Elements.Block;

import Enums.CollisionStyle;

import java.awt.*;
import java.util.ArrayList;

public class Wood extends Block {
    public Wood() {
        super(CollisionStyle.NONE, Color.decode("#a15b00"), true, true, 0, false);
    }

    @Override
    public void initFilter() {
        setFilter(new ArrayList<>());
    }
}
