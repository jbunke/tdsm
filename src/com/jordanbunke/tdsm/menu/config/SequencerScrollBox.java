package com.jordanbunke.tdsm.menu.config;

import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.menu.scrollable.VertScrollBox;
import com.jordanbunke.tdsm.util.Layout;

public final class SequencerScrollBox extends VertScrollBox {
    public SequencerScrollBox(
            final Coord2D position, final Bounds2D dimensions,
            final Scrollable[] menuElements, final int realBottomY,
            final int initialOffsetY
    ) {
        super(position, dimensions, menuElements, realBottomY, initialOffsetY);
    }

    @Override
    public int getScrollBarWidth() {
        return Layout.SEQUENCER_SCROLL_BAR_W;
    }

    @Override
    public Coord2D getOffset() {
        return super.getOffset();
    }
}
