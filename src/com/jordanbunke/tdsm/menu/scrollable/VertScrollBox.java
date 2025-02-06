package com.jordanbunke.tdsm.menu.scrollable;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.ext.drawing_functions.ScrollBoxDrawingFunction;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.AbstractVerticalScrollBox;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Layout;

public final class VertScrollBox extends AbstractVerticalScrollBox {
    public VertScrollBox(
            final Coord2D position, final Bounds2D dimensions,
            final Scrollable[] menuElements,
            final int realBottomY, final int initialOffsetY,
            final ScrollBoxDrawingFunction fDraw
    ) {
        super(position, dimensions, menuElements, fDraw,
                Layout.PX_PER_SCROLL, realBottomY, initialOffsetY);
    }

    public VertScrollBox(
            final Coord2D position, final Bounds2D dimensions,
            final Scrollable[] menuElements
    ) {
        super(position, dimensions, menuElements, GameImage::new, 0,
                position.y + dimensions.height(), 0);
    }

    @Override
    protected VerticalSlider makeSlider(final int maxOffsetY) {
        // TODO
        return null;
    }
}
