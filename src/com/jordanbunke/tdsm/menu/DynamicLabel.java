package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.ext.AbstractDynamicLabel;
import com.jordanbunke.delta_time.menu.menu_elements.ext.drawing_functions.LabelDrawingFunction;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Graphics;

import java.awt.*;
import java.util.function.Supplier;

public final class DynamicLabel extends AbstractDynamicLabel {
    private DynamicLabel(
            final Coord2D position, final Bounds2D dimensions,
            final Anchor anchor, final Color textColor,
            final Supplier<String> getter, final LabelDrawingFunction fDraw
    ) {
        super(position, dimensions, anchor, textColor, getter, fDraw);
    }

    public static DynamicLabel mini(
            final Coord2D position, final Anchor anchor,
            final Supplier<String> getter, final String widestCase,
            final Color color
    ) {
        final LabelDrawingFunction fDraw = (t, c) ->
                Graphics.miniText(c).addText(t).build().draw();
        final GameImage max = fDraw.draw(widestCase, color);
        final Bounds2D dims = new Bounds2D(max.getWidth(), max.getHeight());

        return new DynamicLabel(position, dims, anchor, color, getter, fDraw);
    }
}
