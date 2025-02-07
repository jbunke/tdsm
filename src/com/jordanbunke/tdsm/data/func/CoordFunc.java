package com.jordanbunke.tdsm.data.func;

import com.jordanbunke.delta_time.utility.math.Coord2D;

@FunctionalInterface
public interface CoordFunc {
    Coord2D apply(final Integer frame);
}
