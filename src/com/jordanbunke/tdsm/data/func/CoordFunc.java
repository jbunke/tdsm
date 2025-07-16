package com.jordanbunke.tdsm.data.func;

import com.jordanbunke.delta_time.utility.math.Coord2D;

@FunctionalInterface
public interface CoordFunc {
    Coord2D apply(final Integer frame);

    static CoordFunc simple(
            final Coord2D firstFrame, final boolean orientation
    ) {
        return f -> new Coord2D(
                firstFrame.x + (orientation ? f : 0),
                firstFrame.y + (orientation ? 0 : f));
    }
}
