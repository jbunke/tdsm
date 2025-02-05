package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.utility.math.Coord2D;

// TODO
public class StaticLabel extends StaticMenuElement {
    private StaticLabel(
            final Coord2D position, final Anchor anchor, final GameImage image
    ) {
        super(position, anchor, image);
    }

    public static StaticLabel make(
            final Coord2D position, final String text
    ) {
        // TODO
        return null;
    }
}
