package com.jordanbunke.tdsm.menu.sampler;

import com.jordanbunke.delta_time.error.GameError;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.menu.Textbox;

import java.awt.*;

import static com.jordanbunke.tdsm.util.Layout.*;

public final class ColorTextbox extends Textbox implements ColorTransmitter {
    private static final String PFX = "#", SFX = "";
    private static final int MAX = 6;

    private Color color;
    private boolean active;

    public ColorTextbox(
            final Coord2D position, final Anchor anchor,
            final Color initialColor
    ) {
        super(position, COLOR_TEXTBOX_W, anchor,
                PFX, colorToHexCode(initialColor), SFX,
                ColorTextbox::validateAsHexCode,
                ColorTextbox::transmit, MAX);

        color = initialColor;
    }

    private void updateActive() {
        active = Sampler.get().isActive() &&
                Sampler.get().getSelection().isAnyColor();
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        if (active)
            super.process(eventLogger);
    }

    @Override
    public void render(final GameImage canvas) {
        if (active)
            super.render(canvas);
    }

    @Override
    public void update(final double deltaTime) {
        updateActive();

        if (active)
            super.update(deltaTime);
    }

    @Override
    public void receive(final Color color) {
        if (!this.color.equals(color)) {
            this.color = color;
            setText(colorToHexCode(color));
        }
    }

    public static void transmit(final String text) {
        final Color color = hexCodetoColor(text);

        if (color == null)
            return;

        Sampler.get().setColor(color, null);
    }

    @Override
    public Color getColor() {
        return color;
    }

    // Color math helpers

    private static Color hexCodetoColor(final String hexCode) {
        if (!validateAsHexCode(hexCode))
            return null;

        final int LENGTH_OF_SECTION = 2, R = 0, G = 2, B = 4;

        final int r = hexToInt(hexCode.substring(R, R + LENGTH_OF_SECTION)),
                g = hexToInt(hexCode.substring(G, G + LENGTH_OF_SECTION)),
                b = hexToInt(hexCode.substring(B, B + LENGTH_OF_SECTION));

        return new Color(r, g, b);
    }

    private static int hexToInt(final String hexSequence) {
        if (hexSequence.isEmpty() || !validHexSequence(hexSequence)) {
            GameError.send("String \"" + hexSequence +
                    "\" is not a valid hex sequence.");
            return 0;
        }

        final int BASE = 16;
        int accumulator = 0, power = 0;

        for (int i = hexSequence.length() - 1; i >= 0; i--) {
            final char c = hexSequence.toLowerCase().charAt(i);
            final int placeValue = isNumeric(c)
                    ? c - '0' : 10 + (c - 'a');

            accumulator += placeValue * Math.pow(BASE, power);
            power++;
        }

        return accumulator;
    }

    private static String colorToHexCode(final Color c) {
        final String r = Integer.toHexString(c.getRed());
        final String g = Integer.toHexString(c.getGreen());
        final String b = Integer.toHexString(c.getBlue());

        return (r.length() == 1 ? ("0") + r : r) +
                (g.length() == 1 ? ("0") + g : g) +
                (b.length() == 1 ? ("0") + b : b);
    }

    private static boolean validateAsHexCode(final String hexColorCode) {
        return hexColorCode.length() == MAX &&
                validHexSequence(hexColorCode);
    }

    private static boolean validHexSequence(final String hexSequence) {
        for (char c : hexSequence.toCharArray())
            if (!(isNumeric(c) || isAlpha(c)))
                return false;

        return true;
    }

    private static boolean isNumeric(final char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isAlpha(final char c) {
        return (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }
}
