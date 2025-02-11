package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.utility.math.Coord2D;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DynamicTextbox extends Textbox {
    private final Supplier<String> getter;

    public DynamicTextbox(
            final Coord2D position, final int width, final Anchor anchor,
            final String prefix, final String suffix,
            final Function<String, Boolean> textValidator,
            final Supplier<String> getter, final Consumer<String> setter,
            final int maxLength
    ) {
        super(position, width, anchor, prefix, getter.get(),
                suffix, textValidator, setter, maxLength);

        this.getter = getter;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        if (!isTyping())
            setText(getter.get());
    }
}
