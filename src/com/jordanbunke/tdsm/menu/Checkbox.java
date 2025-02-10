package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.ext.AbstractCheckbox;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.funke.core.ConcreteProperty;
import com.jordanbunke.tdsm.util.Cursor;
import com.jordanbunke.tdsm.util.Graphics;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.jordanbunke.tdsm.util.Layout.CHECKBOX_DIM;

public final class Checkbox extends AbstractCheckbox {
    public Checkbox(
            final Coord2D position, final Anchor anchor,
            final Supplier<Boolean> getter,
            final Consumer<Boolean> setter
    ) {
        super(position, new Bounds2D(CHECKBOX_DIM, CHECKBOX_DIM), anchor,
                new ConcreteProperty<>(getter, setter), Graphics::drawCheckbox);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        if (isHighlighted())
            Cursor.ping(Cursor.POINTER);
    }

    public Coord2D followMiniLabel() {
        return getRenderPosition().displace(getWidth(), 0);
    }
}
