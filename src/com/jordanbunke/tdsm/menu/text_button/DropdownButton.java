package com.jordanbunke.tdsm.menu.text_button;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.button.SimpleToggleMenuButton;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Cursor;

import java.util.function.Supplier;

import static com.jordanbunke.tdsm.util.Layout.*;

public final class DropdownButton extends SimpleToggleMenuButton {
    public DropdownButton(
            final Coord2D position, final int width, final Anchor anchor,
            final GameImage[] bases, final GameImage[] highlights,
            final Supplier<Integer> indexLogic, final Runnable behaviour
    ) {
        super(position, new Bounds2D(width, TEXT_BUTTON_H), anchor, true,
                bases, highlights, new Runnable[] { () -> {}, () -> {} },
                indexLogic, behaviour);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        if (isHighlighted())
            Cursor.ping(Cursor.POINTER);
    }
}
