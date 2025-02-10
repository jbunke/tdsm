package com.jordanbunke.tdsm.menu.config;

import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Edge;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.menu.Textbox;
import com.jordanbunke.tdsm.util.Layout;

import java.util.function.Consumer;
import java.util.function.Function;

public final class PaddingTextbox extends Textbox {
    private static final int MAX = 4;

    public PaddingTextbox(final Coord2D position, final Edge edge) {
        super(position, Layout.PADDING_TEXTBOX_W, Anchor.LEFT_TOP, "",
                String.valueOf(Sprite.get().getStyle().getEdgePadding(edge)),
                "px", makeValidator(edge), makeSetter(edge), MAX);
    }

    private static Function<String, Boolean> makeValidator(final Edge edge) {
        return s -> {
            try {
                final int px = Integer.parseInt(s);
                return Sprite.get().getStyle().validateEdgePadding(edge, px);
            } catch (NumberFormatException nfe) {
                return false;
            }
        };
    }

    private static Consumer<String> makeSetter(final Edge edge) {
        return s -> {
            final int px = Integer.parseInt(s);
            Sprite.get().getStyle().setEdgePadding(edge, px);
        };
    }
}
