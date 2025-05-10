package com.jordanbunke.tdsm.data.func;

import com.jordanbunke.tdsm.data.Replacement;

import java.awt.*;
import java.util.function.Function;

@FunctionalInterface
public interface ColorReplacementFunc {
    Replacement apply(final Color c);

    static ColorReplacementFunc trivial() {
        return c -> new Replacement(-1, Function.identity());
    }
}
