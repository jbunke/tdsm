package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.button.SimpleMenuButton;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.ThinkingMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Graphics;

import java.util.function.Supplier;

public final class TextButton extends SimpleMenuButton {

    private TextButton(
            final Coord2D position, final Anchor anchor,
            final Runnable behaviour, final GameImage base, final GameImage highlight
    ) {
        super(position, new Bounds2D(base.getWidth(), base.getHeight()),
                anchor, true, behaviour, base, highlight);
    }

    public static ThinkingMenuElement make(
            final String text, final Coord2D position, final Anchor anchor,
            final Supplier<Boolean> precondition, final Runnable behaviour
    ) {
        final GameImage base = Graphics.drawTextButtonBase(text);

        // TODO - unique highlight and stub image

        final TextButton button = new TextButton(position, anchor, behaviour, base, base /* TODO */);
        final StaticMenuElement stub = new StaticMenuElement(position, anchor, base /* TODO */);

        return new ThinkingMenuElement(
                () -> precondition.get() ? button : stub);
    }
}
