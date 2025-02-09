package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.text.Text;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.Graphics;

import java.awt.*;

import static com.jordanbunke.tdsm.util.Layout.*;

public final class StaticLabel extends StaticMenuElement {
    private StaticLabel(
            final Coord2D position, final Anchor anchor, final GameImage image
    ) {
        super(position, anchor, image);
    }

    public static StaticLabel make(
            final Coord2D position, final String text
    ) {
        return make(position, text, Colors.darkSystem(), 1.0);
    }

    public static StaticLabel make(
            final Coord2D position, final String text,
            final Color color, final double textSize
    ) {
        return new StaticLabel(position, Anchor.LEFT_TOP,
                Graphics.uiText(color, textSize).addText(text).build().draw());
    }

    public static StaticLabel mini(
            final Coord2D position, final String text,
            final Color color, final Anchor anchor
    ) {
        return new StaticLabel(position, anchor,
                Graphics.miniText(color).addText(text).build().draw());
    }

    public static StaticLabel make(
            final Coord2D position, final Anchor anchor, final Text text
    ) {
        return new StaticLabel(position, anchor, text.draw());
    }

    public Coord2D after() {
        return getRenderPosition().displace(
                getWidth() + POST_LABEL_BUFFER_X, POST_LABEL_OFFSET_Y);
    }
}
