package com.jordanbunke.tdsm.menu.scrollable;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.funke.core.Property;

// TODO
public final class VerticalSlider extends Slider {
    public VerticalSlider(
            Coord2D position, Bounds2D dimensions, Anchor anchor,
            int minValue, int maxValue, Property<Integer> property,
            boolean canSetImplicitly, int sliderBallDim
    ) {
        super(position, dimensions, anchor, minValue, maxValue,
                property, canSetImplicitly, sliderBallDim);
    }

    @Override
    public void drawSlider(final GameImage slider) {
        // TODO
    }

    @Override
    public Coord2D getSliderBallRenderPos(final int sliderBallRenderDim) {
        return new Coord2D(0, sliderBallRenderDim);
    }

    @Override
    protected GameImage drawSliding() {
        return null;
    }

    @Override
    protected GameImage drawHighlighted() {
        return null;
    }

    @Override
    protected GameImage drawBasic() {
        return null;
    }

    @Override
    protected int getCoordDimension(final Coord2D position) {
        return position.y;
    }

    @Override
    protected int getSizeDimension() {
        return getHeight();
    }
}
