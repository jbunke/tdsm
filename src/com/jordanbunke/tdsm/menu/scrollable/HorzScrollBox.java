package com.jordanbunke.tdsm.menu.scrollable;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.AbstractHorizontalScrollBox;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;

import static com.jordanbunke.tdsm.util.Layout.*;

public final class HorzScrollBox extends AbstractHorizontalScrollBox {
    public HorzScrollBox(
            final Coord2D position, final Bounds2D dimensions,
            final Scrollable[] menuElements,
            final int realRightX, final int initialOffsetX
    ) {
        super(position, dimensions, menuElements, GameImage::new,
                PX_PER_SCROLL, realRightX, initialOffsetX);
    }

    @Override
    protected HorzScrollSlider makeSlider(final int maxOffsetX) {
        final Coord2D position = new Coord2D(getX(), getY())
                .displace(0, getHeight() - getScrollBarHeight());
        final int w = getWidth(),
                scrollBarW = Math.max(MIN_SCROLL_BAR_DIM,
                        (int) (Math.pow(w, 2) / (double) (maxOffsetX + w)));

        return new HorzScrollSlider(position,
                new Bounds2D(w, getScrollBarHeight()), maxOffsetX,
                () -> -getOffset().x, o -> setOffsetX(-o), scrollBarW);
    }

    public int getScrollBarHeight() {
        return HORZ_SCROLL_BAR_H;
    }

    /**
     * Included for access; superclass method is protected -
     * {@link AbstractHorizontalScrollBox#getOffset()}
     * */
    @Override
    public Coord2D getOffset() {
        return super.getOffset();
    }

    @Override
    public void render(final GameImage canvas) {
        final int cw = canvas.getWidth(), ch = canvas.getHeight();
        final GameImage capture = new GameImage(cw, ch);

        super.render(capture);

        final Coord2D pos = getPosition();
        final GameImage box = capture.section(pos,
                pos.displace(getWidth(), getHeight()));
        canvas.draw(box.submit(), pos.x, pos.y);
    }

    @Override
    protected boolean renderAndProcessChild(final Scrollable child) {
        final Coord2D rp = getRenderPosition(),
                childRP = child.getRenderPosition();
        final int childW = child.getWidth(), right = rp.x + getWidth();

        return childRP.x <= right && childRP.x + childW >= rp.x;
    }
}
