package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.ext.AbstractDynamicLabel;
import com.jordanbunke.delta_time.menu.menu_elements.ext.drawing_functions.LabelDrawingFunction;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.Graphics;

import java.awt.*;
import java.util.function.Supplier;

import static com.jordanbunke.tdsm.util.Layout.POST_LABEL_BUFFER_X;
import static com.jordanbunke.tdsm.util.Layout.POST_LABEL_OFFSET_Y;

public final class DynamicLabel extends AbstractDynamicLabel {
    private DynamicLabel(
            final Coord2D position, final Bounds2D dimensions,
            final Anchor anchor, final Color textColor,
            final Supplier<String> getter, final LabelDrawingFunction fDraw
    ) {
        super(position, dimensions, anchor, textColor, getter, fDraw);
    }

    public static Builder init(
            final Coord2D position, final Supplier<String> getter,
            final String widestCase
    ) {
        return new Builder(position, getter, widestCase);
    }

    static DynamicLabel make(
            final Coord2D position, final Anchor anchor,
            final Supplier<String> getter, final String widestCase,
            final Color color, final LabelDrawingFunction drawFunc
    ) {
        final GameImage max = drawFunc.draw(widestCase, color);
        final Bounds2D dims = new Bounds2D(max.getWidth(), max.getHeight());

        return new DynamicLabel(position, dims, anchor, color, getter, drawFunc);
    }

    public static class Builder {
        private final Supplier<String> getter;
        private final String widestCase;
        private final Coord2D position;
        private Anchor anchor;
        private Color color;
        private LabelDrawingFunction drawFunc;

        Builder(
                final Coord2D position, final Supplier<String> getter,
                final String widestCase
        ) {
            this.position = position;
            this.getter = getter;
            this.widestCase = widestCase;

            this.anchor = Anchor.LEFT_TOP;
            this.color = Colors.darkSystem();
            this.drawFunc = (t, c) -> Graphics.uiText(c).addText(t).build().draw();
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setColor(final Color color) {
            this.color = color;
            return this;
        }

        public Builder setMini() {
            this.drawFunc = (t, c) -> Graphics.miniText(c).addText(t).build().draw();
            return this;
        }

        public DynamicLabel build() {
            return DynamicLabel.make(position, anchor,
                    getter, widestCase, color, drawFunc);
        }
    }

    public Coord2D followTB() {
        return getRenderPosition().displace(
                getWidth() + POST_LABEL_BUFFER_X, POST_LABEL_OFFSET_Y);
    }
}
