package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Graphics;
import com.jordanbunke.tdsm.util.ParserUtils;
import com.jordanbunke.tdsm.util.ResourceCodes;
import com.jordanbunke.tdsm.util.Tooltip;

public final class Indicator extends StaticMenuElement {
    private final String tooltip;

    private Indicator(
            final Coord2D position, final Anchor anchor,
            final GameImage image, final String tooltip
    ) {
        super(position, new Bounds2D(image.getWidth(), image.getHeight()),
                anchor, image);

        this.tooltip = tooltip;
    }

    public static Builder init(final Coord2D position) {
        return new Builder(position);
    }

    public static Indicator make(
            final String iconCode, final String tooltipCode,
            final Coord2D position, final Anchor anchor
    ) {
        final GameImage icon = Graphics.readIcon(iconCode);
        final String tooltip = ParserUtils.readResourceText(tooltipCode);

        return new Indicator(position, anchor, icon, tooltip);
    }

    public static Indicator make(
            final String tooltipCode,
            final Coord2D position, final Anchor anchor
    ) {
        return make(ResourceCodes.INFO, tooltipCode, position, anchor);
    }

    public static class Builder {
        private final Coord2D position;
        private Anchor anchor;
        private String tooltip, iconCode;

        Builder(final Coord2D position) {
            this.position = position;

            anchor = Anchor.CENTRAL;
            tooltip = " ";
            iconCode = ResourceCodes.INFO;
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setIconCode(final String iconCode) {
            this.iconCode = iconCode;
            return this;
        }

        public Builder setTooltip(final String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder setTooltipCode(final String tooltipCode) {
            tooltip = ParserUtils.readResourceText(tooltipCode);
            return this;
        }

        public Indicator build() {
            return new Indicator(position, anchor,
                    Graphics.readIcon(iconCode), tooltip);
        }
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        final Coord2D mousePos = eventLogger.getAdjustedMousePosition();

        if (mouseIsWithinBounds(mousePos))
            Tooltip.get().ping(tooltip, mousePos);
    }

    public Coord2D following() {
        return getRenderPosition().displace(getWidth(), 0);
    }
}
