package com.jordanbunke.tdsm.menu.pre_export;

import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;

// TODO
public final class ReplacementOptions extends MenuElementContainer {
    public ReplacementOptions(
            final Coord2D position, final Bounds2D dimensions
    ) {
        super(position, dimensions, Anchor.LEFT_TOP, true);
    }

    @Override
    public MenuElement[] getMenuElements() {
        return new MenuElement[0];
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return true;
    }
}
