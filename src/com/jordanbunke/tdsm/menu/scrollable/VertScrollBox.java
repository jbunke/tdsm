package com.jordanbunke.tdsm.menu.scrollable;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.AbstractVerticalScrollBox;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;

import static com.jordanbunke.tdsm.util.Layout.*;

public class VertScrollBox extends AbstractVerticalScrollBox {
    public VertScrollBox(
            final Coord2D position, final Bounds2D dimensions,
            final Scrollable[] menuElements,
            final int realBottomY, final int initialOffsetY
    ) {
        super(position, dimensions, menuElements, GameImage::new,
                PX_PER_SCROLL, realBottomY, initialOffsetY);
    }

    @Override
    protected VertScrollSlider makeSlider(final int maxOffsetY) {
        final Coord2D position = new Coord2D(getX(), getY())
                .displace(getWidth() - getScrollBarWidth(), 0);
        final int h = getHeight(),
                scrollBarH = Math.max(MIN_SCROLL_BAR_DIM,
                        (int) (Math.pow(h, 2) / (double) (maxOffsetY + h)));

        return new VertScrollSlider(position,
                new Bounds2D(getScrollBarWidth(), h), maxOffsetY,
                () -> -getOffset().y, o -> setOffsetY(-o), scrollBarH);
    }

    public int getScrollBarWidth() {
        return VERT_SCROLL_BAR_W;
    }

    /**
     * Included for access; superclass method is protected -
     * {@link AbstractVerticalScrollBox#getOffset()}
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
        final GameImage box = new GameImage(getWidth(), getHeight());
        box.draw(capture, -pos.x, -pos.y);
        canvas.draw(box.submit(), pos.x, pos.y);
    }

    @Override
    protected boolean renderAndProcessChild(final Scrollable child) {
        final Coord2D rp = getRenderPosition(),
                childRP = child.getRenderPosition();
        final int childH = child.getHeight(), bottom = rp.y + getHeight();

        return childRP.y <= bottom && childRP.y + childH >= rp.y;
    }
}
