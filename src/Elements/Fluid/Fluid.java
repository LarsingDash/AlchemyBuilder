package Elements.Fluid;

import Elements.Elements;
import Enums.GravityMovement;

public interface Fluid extends Elements {
    void setMovement(GravityMovement movement);
    GravityMovement getMovement();
}
