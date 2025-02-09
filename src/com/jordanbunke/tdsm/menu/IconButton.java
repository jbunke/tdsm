package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.button.SimpleMenuButton;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.ThinkingMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Graphics;
import com.jordanbunke.tdsm.util.ParserUtils;
import com.jordanbunke.tdsm.util.ResourceCodes;

import java.util.function.Supplier;

public final class IconButton extends SimpleMenuButton {
    private final String tooltip;

    private IconButton(
            final Coord2D position, final Runnable behaviour,
            final GameImage base, final String tooltip
    ) {
        super(position, new Bounds2D(base.getWidth(), base.getHeight()),
                Anchor.CENTRAL, true, behaviour,
                base, Graphics.highlightIcon(base));

        this.tooltip = tooltip;
    }

    public static ThinkingMenuElement make(
            final String code, final Coord2D position,
            final Supplier<Boolean> precondition, final Runnable behaviour
    ) {
        return make(code, code, position, precondition, behaviour);
    }

    public static ThinkingMenuElement make(
            final String code, final String tooltipCode, final Coord2D position,
            final Supplier<Boolean> precondition, final Runnable behaviour
    ) {
        final GameImage iconImage = Graphics.readIcon(code),
                greyedOut = Graphics.pixelWiseTransformation(
                        iconImage, Graphics::greyscale);

        final String tooltip = tooltipCode.equals(ResourceCodes.NO_TOOLTIP)
                ? null : ParserUtils.readTooltip(tooltipCode);

        final IconButton icon = new IconButton(
                position, behaviour, iconImage, tooltip);
        final StaticMenuElement stub =
                new StaticMenuElement(position, Anchor.CENTRAL, greyedOut);

        return new ThinkingMenuElement(
                () -> precondition.get() ? icon : stub);
    }

    // TODO - tooltip functionality
}
