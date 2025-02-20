package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.tdsm.data.func.ColorReplacementFunc;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class Colors {
    private static final Color BLACK = new Color(0, 0, 0),
            WHITE = new Color(0xff, 0xff, 0xff),
            GREY = new Color(0x80, 0x80, 0x80),
            RED = new Color(0xe0, 0x20, 0x20),
            GREEN = new Color(0x20, 0xe0, 0x20),
            BLUE = new Color(0x20, 0x20, 0xe0),
            ORANGE = new Color(0xc0, 0x60, 0),
            BROWN = new Color(0x60, 0x30, 0x10),
            YELLOW = new Color(0xe0, 0xc0, 0),
            PURPLE = new Color(0xb0, 0, 0xb0),
            PINK = new Color(0xff, 0x80, 0xc0),
            CYAN = new Color(0, 0xb0, 0xb0),
            VEIL = new Color(0x80, 0x80, 0x80, 0x80),
            HIGHLIGHT_OVERLAY = new Color(0x80, 0x80, 0xff, 0x80),
            SHADOW = new Color(0x20, 0x20, 0x20, 0x60),
            LINE_1 = new Color(0x20, 0x20, 0x20),
            TRANSPARENT = new Color(0, 0, 0, 0),
            CB_1 = new Color(0xa0, 0xa0, 0xa0),
            CB_2 = new Color(0xc0, 0xc0, 0xc0),
            INVALID = new Color(0x80, 0, 0),
            DARK_SYSTEM = new Color(0x291f66),
            LIGHT_SYSTEM = new Color(0xe8e8e8),
            LIGHT_ACCENT = new Color(0xb7cbe6),
            DARK_ACCENT = new Color(0x6b8ebc),
            BACKGROUND = new Color(0x88, 0x9f, 0xbc);

    public static final Color[] DEFAULT_SWATCHES = new Color[] {
            BLACK, WHITE, GREY, RED, GREEN, BLUE,
            ORANGE, BROWN, YELLOW, PURPLE, PINK, CYAN
    };

    public static Color darkSystem() {
        return DARK_SYSTEM;
    }

    public static Color lightSystem() {
        return LIGHT_SYSTEM;
    }

    public static Color lightAccent() {
        return LIGHT_ACCENT;
    }

    public static Color darkAccent() {
        return DARK_ACCENT;
    }

    public static Color bg() {
        return BACKGROUND;
    }

    public static Color transparent() {
        return TRANSPARENT;
    }

    public static Color checkerboard(final boolean _1) {
        return _1 ? CB_1 : CB_2;
    }

    public static Color line(final boolean _1) {
        return _1 ? LINE_1 : TRANSPARENT;
    }

    public static Color shadow() {
        return SHADOW;
    }

    public static Color highlight() {
        return LIGHT_SYSTEM;
    }

    public static Color selected() {
        return BLUE;
    }

    public static Color black() {
        return BLACK;
    }

    public static Color veil() {
        return VEIL;
    }

    public static Color highlightOverlay() {
        return HIGHLIGHT_OVERLAY;
    }

    public static Color invalid() {
        return INVALID;
    }

    public static GameImage runColorReplacement(
            final GameImage ref, final Color[] colors,
            final ColorReplacementFunc colorReplacementFunc
    ) {
        final int w = ref.getWidth(), h = ref.getHeight();
        final GameImage img = new GameImage(w, h);
        final Map<Color, Color> replacements = new HashMap<>();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                final Color c = ref.getColorAt(x, y);

                if (replacements.containsKey(c))
                    img.setRGB(x, y, replacements.get(c).getRGB());
                else {
                    final Pair<Integer, Function<Color, Color>> out =
                            colorReplacementFunc.apply(c);
                    final int index = out.a();

                    if (index < 0 || index >= colors.length) {
                        replacements.put(c, c);
                        img.setRGB(x, y, c.getRGB());
                    } else {
                        final Color set = out.b().apply(colors[index]);
                        replacements.put(c, set);
                        img.setRGB(x, y, set.getRGB());
                    }
                }
            }
        }

        return img.submit();
    }

    public static Color shiftRGB(final Color base, final int shift) {
        return new Color(
                shiftChannel(base.getRed(), Math.abs(shift)),
                shiftChannel(base.getGreen(), Math.abs(shift)),
                shiftChannel(base.getBlue(), Math.abs(shift)));
    }

    private static int shiftChannel(final int c, final int shift) {
        final int MIDDLE = 0x80;
        final boolean increase = Math.signum((double) (MIDDLE - c)) >= 0.0;

        return c + (increase ? shift : -shift);
    }

    public static Color fromHSV(
            final double[] hsv
    ) {
        if (hsv.length != 3)
            return darkSystem();

        return fromHSV(hsv[0], hsv[1], hsv[2]);
    }

    public static Color rgbOnly(final Color c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue());
    }

    public static Color fromHSV(
            final double hue, final double sat,
            final double val
    ) {
        return fromHSV(hue, sat, val, Constants.RGB_SCALE);
    }

    public static Color fromHSV(
            final double hue, final double sat,
            final double val, final int alpha
    ) {
        final double SIX = 6d, c = sat * val,
                x = c * (1d - Math.abs(((SIX * hue) % 2) - 1)),
                m = val - c, r, g, b;

        if (hue < 1 / SIX) {
            r = c;
            g = x;
            b = 0d;
        } else if (hue < 2 / SIX) {
            r = x;
            g = c;
            b = 0d;
        } else if (hue < 3 / SIX) {
            r = 0d;
            g = c;
            b = x;
        } else if (hue < 4 / SIX) {
            r = 0d;
            g = x;
            b = c;
        } else if (hue < 5 / SIX) {
            r = x;
            g = 0d;
            b = c;
        } else {
            r = c;
            g = 0d;
            b = x;
        }

        return new Color(scaleUpChannel(r + m),
                scaleUpChannel(g + m), scaleUpChannel(b + m), alpha);
    }

    public static double rgbToHue(final Color c) {
        final int R = 0, G = 1, B = 2;
        final double[] rgb = rgbAsArray(c);
        final double max = getMaxOfRGB(rgb), range = getRangeOfRGB(rgb),
                multiplier = 1 / 6d;

        if (range == 0d)
            return 0d;

        if (max == rgb[R]) {
            // red maximum case
            double value = (rgb[G] - rgb[B]) / range;

            while (value < 0)
                value += 6;
            while (value >= 6)
                value -= 6;

            return multiplier * value;
        } else if (max == rgb[G]) {
            // green maximum case
            return multiplier * (((rgb[B] - rgb[R]) / range) + 2);

        } else if (max == rgb[B]) {
            // blue maximum case
            return multiplier * (((rgb[R] - rgb[G]) / range) + 4);
        }

        return 0d;
    }

    public static double rgbToSat(final Color c) {
        final double[] rgb = rgbAsArray(c);
        final double max = getMaxOfRGB(rgb);

        if (max == 0d)
            return 0;
        else
            return getRangeOfRGB(rgb) / max;
    }

    public static double rgbToValue(final Color c) {
        return getMaxOfRGB(rgbAsArray(c));
    }

    private static int scaleUpRGBAHSV(final double n, final int scale) {
        return MathPlus.bounded(0, (int) Math.round(n * scale), scale);
    }

    public static int hue(final Color c) {
        return scaleUpRGBAHSV(rgbToHue(c), Constants.HUE_SCALE);
    }

    public static int sat(final Color c) {
        return scaleUpChannel(rgbToSat(c));
    }

    public static int val(final Color c) {
        return scaleUpChannel(rgbToValue(c));
    }

    public static int scaleUpChannel(final double n) {
        return scaleUpRGBAHSV(n, Constants.RGB_SCALE);
    }

    public static double normalizeHue(double hue) {
        while (hue > 1.0) hue -= 1.0;
        while (hue < 0.0) hue += 1.0;

        return hue;
    }

    private static double getRangeOfRGB(final double[] rgb) {
        return getMaxOfRGB(rgb) - getMinOfRGB(rgb);
    }

    private static double getMaxOfRGB(final double[] rgb) {
        return MathPlus.max(rgb);
    }

    private static double getMinOfRGB(final double[] rgb) {
        return MathPlus.min(rgb);
    }

    private static double[] rgbAsArray(final Color c) {
        return new double[] {
                c.getRed() / (double) Constants.RGB_SCALE,
                c.getGreen() / (double) Constants.RGB_SCALE,
                c.getBlue() / (double) Constants.RGB_SCALE
        };
    }
}
