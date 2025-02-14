package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.button.SimpleToggleMenuButton;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Cursor;
import com.jordanbunke.tdsm.util.Graphics;
import com.jordanbunke.tdsm.util.ParserUtils;
import com.jordanbunke.tdsm.util.Tooltip;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public final class IconOptionsButton extends SimpleToggleMenuButton {
    private final String[] tooltips;

    private IconOptionsButton(
            final Coord2D position, final Bounds2D dimensions,
            final Anchor anchor, final GameImage[] bases,
            final Runnable[] behaviours, final String[] tooltips,
            final Supplier<Integer> indexFunc,
            final Runnable global
    ) {
        super(position, dimensions, anchor, true, bases,
                Arrays.stream(bases).map(Graphics::highlightIcon)
                        .toArray(GameImage[]::new),
                behaviours, indexFunc, global);

        this.tooltips = tooltips;
    }

    public static Builder init(
            final Coord2D position
    ) {
        return new Builder(position);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        if (isHighlighted()) {
            final Coord2D mousePos = eventLogger.getAdjustedMousePosition();
            Tooltip.get().ping(tooltips[getIndex()], mousePos);
            Cursor.ping(Cursor.POINTER);
        }
    }

    public static class Builder {
        private final Coord2D position;
        private Anchor anchor;
        private Runnable global;
        private Supplier<Integer> indexFunc;
        private Runnable[] behaviours;
        private String[] tooltips;
        private GameImage[] bases;
        private String[] codes;

        Builder(
                final Coord2D position
        ) {
            this.position = position;

            anchor = Anchor.LEFT_TOP;
            global = () -> {};
            indexFunc = () -> 0;

            codes = null;
            behaviours = null;
            tooltips = null;
            bases = null;
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setGlobal(final Runnable global) {
            this.global = global;
            return this;
        }

        public Builder setIndexFunc(final Supplier<Integer> indexFunc) {
            this.indexFunc = indexFunc;
            return this;
        }

        public Builder setCodes(final String... codes) {
            this.codes = codes;
            return this;
        }

        public Builder setBehaviours(final Runnable... behaviours) {
            this.behaviours = behaviours;
            return this;
        }

        public Builder setTooltips(final String... tooltips) {
            this.tooltips = tooltips;
            return this;
        }

        public Builder setBases(final GameImage... bases) {
            this.bases = bases;
            return this;
        }

        public IconOptionsButton build() {
            if (codes == null && bases == null)
                return null;

            final int options = codes != null ? codes.length : bases.length;

            final GameImage[] bases = this.bases != null
                    ? this.bases : Arrays.stream(codes)
                    .map(Graphics::readIcon).toArray(GameImage[]::new);
            final Bounds2D dims = new Bounds2D(bases[0].getWidth(),
                    bases[0].getHeight());
            final Runnable[] behaviours = this.behaviours != null
                    ? this.behaviours : IntStream.range(0, options)
                    .mapToObj(i -> (Runnable) () -> {})
                    .toArray(Runnable[]::new);
            final String[] tooltips = this.tooltips != null
                    ? this.tooltips : (codes != null
                    ? Arrays.stream(codes).map(ParserUtils::readTooltip)
                    .toArray(String[]::new)
                    : IntStream.range(0, options)
                    .mapToObj(i -> Tooltip.NONE).toArray(String[]::new));

            return new IconOptionsButton(position, dims, anchor,
                    bases, behaviours, tooltips, indexFunc, global);
        }
    }
}
