package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.ext.dropdown.AbstractDropdownList;
import com.jordanbunke.delta_time.menu.menu_elements.ext.dropdown.SimpleItem;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.AbstractVerticalScrollBox;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;

import java.util.function.Supplier;

// TODO
public class Dropdown extends AbstractDropdownList {

    public Dropdown(
            Coord2D position, Bounds2D dimensions, Anchor anchor,
            int renderOrder, SimpleItem[] items,
            Supplier<Integer> initialIndexFunction
    ) {
        super(position, dimensions, anchor, renderOrder, items, initialIndexFunction);
    }

    @Override
    protected AbstractVerticalScrollBox makeDDContainer(final Coord2D position) {
        return null;
    }

    @Override
    protected MenuElement makeDDButton() {
        return null;
    }

    @Override
    protected Coord2D contentsDisplacement() {
        return null;
    }
}
