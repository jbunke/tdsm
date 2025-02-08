package com.jordanbunke.tdsm.menu.sampler;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.Graphics;

import java.awt.*;

import static com.jordanbunke.tdsm.util.Layout.HUE_SLIDER_W;

public final class ColorPicker extends MenuElement implements ColorTransmitter {
    private Color color;
    private double hue, sat, val;
    private boolean active;

    private GameImage asset;

    public ColorPicker(
            final Coord2D position, final Bounds2D dimensions,
            final Anchor anchor, final Color initialColor
    ) {
        super(position, dimensions, anchor, true);

        setColor(initialColor);
    }

    public Coord2D localHuePos() {
        return new Coord2D(0, (int) (hue * getHeight()));
    }

    public Coord2D localSVPos() {
        final int w = getWidth() - HUE_SLIDER_W,
                h = getHeight(),
                x = HUE_SLIDER_W + ((int) (sat * w)),
                y = h - (int) (val * h);

        return new Coord2D(x, y);
    }

    public Color getHypothetical(
            final int localX, final int localY
    ) {
        final int w = getWidth(), h = getHeight();

        if (localX < HUE_SLIDER_W) {
            final double hypHue = localY / (double) h;
            return Colors.fromHSV(hypHue, sat, val);
        } else {
            final int x = localX - HUE_SLIDER_W,
                    matrixW = w - HUE_SLIDER_W;
            final double hypSat = x / (double) matrixW,
                    hypVal = 1.0 - (localY / (double) h);
            return Colors.fromHSV(hue, hypSat, hypVal);
        }
    }

    private void updateActive() {
        active = Sampler.get().isActive() &&
                Sampler.get().getSelection().isAnyColor();
    }

    private void checkForChange() {
        if (updateHSV())
            updateAsset();
    }

    private boolean updateHSV() {
        final double hue = Colors.rgbToHue(color),
                sat = Colors.rgbToSat(color),
                val = Colors.rgbToValue(color);

        final boolean change = this.hue != hue ||
                this.sat != sat || this.val != val;

        this.hue = hue;
        this.sat = sat;
        this.val = val;

        return change;
    }

    private void updateAsset() {
        asset = Graphics.drawColorPicker(this);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        if (active) {
            // TODO
        }
    }

    @Override
    public void update(final double deltaTime) {
        updateActive();
    }

    @Override
    public void render(final GameImage canvas) {
        if (active)
            draw(asset, canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    @Override
    public void receive(final Color color) {
        if (!this.color.equals(color)) {
            this.color = color;
            checkForChange();
        }
    }

    @Override
    public Color getColor() {
        return color;
    }

    private void setColor(final Color color) {
        this.color = color;

        updateHSV();
        updateAsset();
    }
}
