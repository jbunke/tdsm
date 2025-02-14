package com.jordanbunke.tdsm.menu.scrollable;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.funke.core.ConcreteProperty;
import com.jordanbunke.tdsm.menu.Button;
import com.jordanbunke.tdsm.util.Cursor;
import com.jordanbunke.tdsm.util.Graphics;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class VertScrollSlider extends Slider {
    private GameImage base, highlight, sliding;

    public VertScrollSlider(
            final Coord2D position, final Bounds2D dimensions,
            final int maxValue, final Supplier<Integer> getter,
            final Consumer<Integer> setter, final int sliderBallDim
    ) {
        super(position, dimensions, maxValue,
                new ConcreteProperty<>(getter, setter), sliderBallDim);
        updateAssets();
    }

    @Override
    protected GameImage drawSliding() {
        return sliding;
    }

    @Override
    protected GameImage drawHighlighted() {
        return highlight;
    }

    @Override
    protected GameImage drawBasic() {
        return base;
    }

    @Override
    protected int getCoordDimension(final Coord2D position) {
        return position.y;
    }

    @Override
    protected int getSizeDimension() {
        return getHeight();
    }

    @Override
    protected void updateAssets() {
        // pre-processing
        final int barH = sliderBallDim, rangeY = getHeight() - barH,
                barY = (int) (getSliderFraction() * rangeY);

        base = Graphics.drawVertScrollBar(getWidth(), getHeight(),
                barH, barY, Button.sim(false, false));
        highlight = Graphics.drawVertScrollBar(getWidth(), getHeight(),
                barH, barY, Button.sim(false, true));
        sliding = Graphics.drawVertScrollBar(getWidth(), getHeight(),
                barH, barY, Button.sim(true, false));
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        if (isSliding())
            Cursor.force(Cursor.VERT_SCROLL);
        else if (isHighlighted())
            Cursor.ping(Cursor.VERT_SCROLL);
    }
}
