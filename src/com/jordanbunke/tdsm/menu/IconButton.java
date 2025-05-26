package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.button.SimpleMenuButton;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.ThinkingMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.*;

import java.util.function.Supplier;

public final class IconButton extends SimpleMenuButton {
    private final String tooltip;

    private IconButton(
            final Coord2D position, final Anchor anchor,
            final Runnable behaviour, final GameImage base,
            final String tooltip
    ) {
        super(position, new Bounds2D(base.getWidth(), base.getHeight()),
                anchor, true, behaviour, base, Graphics.highlightIcon(base));

        this.tooltip = tooltip;
    }

    public static Builder init(
            final String code, final Coord2D position,
            final Runnable behaviour
    ) {
        return new Builder(code, position, behaviour);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        if (isHighlighted()) {
            final Coord2D mousePos = eventLogger.getAdjustedMousePosition();
            Tooltip.get().ping(tooltip, mousePos);
            Cursor.ping(Cursor.POINTER);
        }
    }

    public static class Builder {
        private final String code;
        private final Coord2D position;
        private final Runnable behaviour;

        private String tooltipCode;
        private Anchor anchor;

        Builder(
                final String code, final Coord2D position,
                final Runnable behaviour
        ) {
            this.code = code;
            this.position = position;
            this.behaviour = behaviour;

            tooltipCode = code;
            anchor = Anchor.LEFT_TOP;
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setTooltipCode(final String tooltipCode) {
            this.tooltipCode = tooltipCode;
            return this;
        }

        public IconButton build() {
            final GameImage iconImage = Graphics.readIcon(code);

            final String tooltip = tooltipCode.equals(ResourceCodes.NO_TOOLTIP)
                    ? Tooltip.NONE : ParserUtils.readResourceText(tooltipCode);

            return new IconButton(position, anchor, behaviour, iconImage, tooltip);
        }

        @SuppressWarnings("unused")
        public ThinkingMenuElement buildForWhen(
                final Supplier<Boolean> precondition
        ) {
            final GameImage greyedOut = Graphics.pixelWiseTransformation(
                    Graphics.readIcon(code), Graphics::greyscale);

            final StaticMenuElement stub =
                    new StaticMenuElement(position, anchor, greyedOut);

            return new ThinkingMenuElement(
                    () -> precondition.get() ? build() : stub);
        }
    }
}
