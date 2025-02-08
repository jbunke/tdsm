package com.jordanbunke.tdsm.menu.scrollable;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.AbstractSlider;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.funke.core.Property;

// TODO
public sealed abstract class Slider extends AbstractSlider
        permits HorizontalSlider, VerticalSlider {
    private GameImage baseImage, highlightedImage, slidingImage;

    public Slider(
            Coord2D position, Bounds2D dimensions, Anchor anchor,
            int minValue, int maxValue, Property<Integer> property,
            boolean canSetImplicitly, int sliderBallDim
    ) {
        super(position, dimensions, anchor, minValue, maxValue,
                property, canSetImplicitly, sliderBallDim);
    }

    public abstract void drawSlider(final GameImage slider);
    public abstract Coord2D getSliderBallRenderPos(final int sliderBallRenderDim);

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    @Override
    protected void updateAssets() {
        // TODO
    }
}
