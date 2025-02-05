package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.menu.menu_elements.ext.AbstractTextbox;
import com.jordanbunke.delta_time.menu.menu_elements.ext.drawing_functions.TextboxDrawingFunction;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

// TODO
public class Textbox extends AbstractTextbox {

    public Textbox(
            Coord2D position, Bounds2D dimensions, Anchor anchor,
            Supplier<String> prefixGetter, String initialText,
            Supplier<String> suffixGetter,
            Function<String, Boolean> textValidator,
            Consumer<String> setter,
            TextboxDrawingFunction fDraw, int maxLength
    ) {
        super(position, dimensions, anchor, prefixGetter, initialText,
                suffixGetter, textValidator, setter, fDraw, maxLength);
    }
}
