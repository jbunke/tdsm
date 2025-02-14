package com.jordanbunke.tdsm.menu.scrollable;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.AbstractSlider;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.funke.core.Property;

public abstract class Slider extends AbstractSlider {
    public Slider(
            final Coord2D position, final Bounds2D dimensions,
            final int maxValue, final Property<Integer> property,
            final int sliderBallDim
    ) {
        super(position, dimensions, Anchor.LEFT_TOP, 0, maxValue,
                property, true, sliderBallDim);
    }

    @Override
    public final void debugRender(
            final GameImage canvas, final GameDebugger debugger
    ) {}
}
