package com.jordanbunke.tdsm.menu.scrollable;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.AbstractVerticalScrollBox;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Layout;

import static com.jordanbunke.tdsm.util.Layout.VERT_SCROLL_BAR_W;
import static com.jordanbunke.tdsm.util.Layout.MIN_VERT_SCROLL_BAR_H;

public class VertScrollBox extends AbstractVerticalScrollBox {
    public VertScrollBox(
            final Coord2D position, final Bounds2D dimensions,
            final Scrollable[] menuElements,
            final int realBottomY, final int initialOffsetY
    ) {
        super(position, dimensions, menuElements, GameImage::new,
                Layout.PX_PER_SCROLL, realBottomY, initialOffsetY);
    }

    @Override
    protected VertScrollSlider makeSlider(final int maxOffsetY) {
        final Coord2D position = new Coord2D(getX(), getY())
                .displace(getWidth() - getScrollBarWidth(), 0);
        final int h = getHeight(),
                scrollBarH = Math.max(MIN_VERT_SCROLL_BAR_H,
                        (int) (Math.pow(h, 2) / (double) (maxOffsetY + h)));

        return new VertScrollSlider(position,
                new Bounds2D(getScrollBarWidth(), h), maxOffsetY,
                () -> -getOffset().y, o -> setOffsetY(-o), scrollBarH);
    }

    public int getScrollBarWidth() {
        return VERT_SCROLL_BAR_W;
    }
}
