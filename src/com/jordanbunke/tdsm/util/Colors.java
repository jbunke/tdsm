package com.jordanbunke.tdsm.util;

import com.jordanbunke.color_proc.ColorAlgo;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.stip_parser.ParserSerializer;
import com.jordanbunke.tdsm.data.func.ColorReplacementFunc;
import com.jordanbunke.tdsm.data.style.Style;

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

    public static String hexCode(final Color c) {
        return "#" + ParserSerializer.serializeColor(c, true);
    }

    public static Map<Color, Integer> colorOccurrences(final Style style) {
        final GameImage spriteSheet = style.renderSpriteSheet();
        final int w = spriteSheet.getWidth(), h = spriteSheet.getHeight();
        final Map<Color, Integer> cs = new HashMap<>();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                final Color c = spriteSheet.getColorAt(x, y);

                if (c.getAlpha() == 0) continue;

                if (cs.containsKey(c))
                    cs.put(c, cs.get(c) + 1);
                else
                    cs.put(c, 1);
            }
        }

        return cs;
    }

    public static GameImage runColorReplacement(
            final GameImage ref, final Color[] colors,
            final ColorReplacementFunc colorReplacementFunc
    ) {
        final Function<Color, Color> replacement = c -> {
            final Pair<Integer, Function<Color, Color>> out =
                    colorReplacementFunc.apply(c);
            final int index = out.a();

            return index < 0 || index >= colors.length ? c
                    : out.b().apply(colors[index]);
        };

        return ColorAlgo.run(replacement, ref);
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
}
