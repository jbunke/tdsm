package com.jordanbunke.tdsm.menu.sampler;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.Cursor;
import com.jordanbunke.tdsm.util.Graphics;

import java.awt.*;
import java.util.List;

import static com.jordanbunke.tdsm.util.Layout.HUE_SLIDER_W;

public final class ColorPicker extends MenuElement implements ColorTransmitter {
    private Color color;
    private double hue, sat, val;
    private boolean active, interacting;

    private GameImage asset;

    public ColorPicker(
            final Coord2D position, final Bounds2D dimensions,
            final Anchor anchor, final Color initialColor
    ) {
        super(position, dimensions, anchor, true);

        interacting = false;
        setColor(initialColor);
    }

    private enum Freeze {
        NONE, HUE, SV;

        boolean canUpdateHue() {
            return this != HUE;
        }

        boolean canUpdateSV() {
            return this != SV;
        }
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

    public double[] getHypothetical(
            int localX, int localY
    ) {
        final int w = getWidth(), h = getHeight();
        localX = MathPlus.bounded(0, localX, w);
        localY = MathPlus.bounded(0, localY, h);

        if (localX < HUE_SLIDER_W) {
            final double hypHue = localY / (double) h;
            return new double[] { hypHue, sat, val };
        } else {
            final int x = localX - HUE_SLIDER_W,
                    matrixW = w - HUE_SLIDER_W;
            final double hypSat = x / (double) matrixW,
                    hypVal = 1.0 - (localY / (double) h);
            return new double[] { hue, hypSat, hypVal };
        }
    }

    public Freeze getFreeze(final int localX) {
        return localX < HUE_SLIDER_W ? Freeze.SV : Freeze.HUE;
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

        return updateHSV(hue, sat, val, Freeze.NONE);
    }

    private boolean updateHSV(
            final double hue, final double sat,
            final double val, final Freeze freeze
    ) {
        final boolean change = this.hue != hue ||
                this.sat != sat || this.val != val;

        if (freeze.canUpdateHue())
            this.hue = hue;

        if (freeze.canUpdateSV()) {
            this.sat = sat;
            this.val = val;
        }

        return change;
    }

    private void updateAsset() {
        asset = Graphics.drawColorPicker(this);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        if (active) {
            final Coord2D mp = eventLogger.getAdjustedMousePosition();
            final boolean mouseInBounds = mouseIsWithinBounds(mp);
            final Coord2D localMP = mp.displace(getRenderPosition().scale(-1));

            // stop interacting on release
            if (interacting) {
                final List<GameEvent> unprocessed = eventLogger.getUnprocessedEvents();
                for (GameEvent e : unprocessed) {
                    if (e instanceof GameMouseEvent me &&
                            me.matchesAction(GameMouseEvent.Action.UP)) {
                        interacting = false;
                        me.markAsProcessed();
                        break;
                    }
                }
            }

            if (mouseInBounds && Cursor.get() != Cursor.POINTER) {
                // check for mouse down and for click
                final List<GameEvent> unprocessed = eventLogger.getUnprocessedEvents();
                for (GameEvent e : unprocessed) {
                    if (e instanceof GameMouseEvent me &&
                            !me.matchesAction(GameMouseEvent.Action.UP)) {
                        switch (me.action) {
                            case DOWN -> interacting = true;
                            case CLICK -> {
                                pickColor(localMP);
                                interacting = false;
                            }
                        }

                        me.markAsProcessed();
                        break;
                    }
                }
            }

            // adjustment logic
            if (interacting)
                pickColor(localMP);

            // cursor
            if (mouseInBounds)
                Cursor.ping(interacting ? Cursor.NONE : Cursor.RETICLE);
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

    private void pickColor(final Coord2D localPos) {
        final double[] hsv = getHypothetical(localPos.x, localPos.y);

        this.color = Colors.fromHSV(hsv);

        updateHSV(hsv[0], hsv[1], hsv[2], getFreeze(localPos.x));
        updateAsset();
        send();
    }
}
