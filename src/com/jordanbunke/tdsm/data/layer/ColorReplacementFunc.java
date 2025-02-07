package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.utility.math.Pair;

import java.awt.*;
import java.util.function.Function;

@FunctionalInterface
public interface ColorReplacementFunc {
    Pair<Integer, Function<Color, Color>> apply(final Color c);

    static ColorReplacementFunc trivial() {
        return c -> new Pair<>(-1, Function.identity());
    }
}
