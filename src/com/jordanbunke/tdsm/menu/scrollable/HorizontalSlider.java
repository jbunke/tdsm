package com.jordanbunke.tdsm.menu.scrollable;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.funke.core.Property;

// TODO
public final class HorizontalSlider extends Slider {
    public HorizontalSlider(
            Coord2D position, Bounds2D dimensions, Anchor anchor,
            int minValue, int maxValue, Property<Integer> property,
            boolean canSetImplicitly, int sliderBallDim
    ) {
        super(position, dimensions, anchor, minValue, maxValue,
                property, canSetImplicitly, sliderBallDim);
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
        return 0;
    }

    @Override
    protected int getSizeDimension() {
        return 0;
    }

    @Override
    protected void updateAssets() {

    }
}
