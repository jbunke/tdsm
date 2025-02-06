package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.ext.dropdown.AbstractDropdownList;
import com.jordanbunke.delta_time.menu.menu_elements.ext.dropdown.SimpleItem;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.menu.scrollable.VertScrollBox;
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.Graphics;

import java.util.Arrays;
import java.util.function.Supplier;

import static com.jordanbunke.tdsm.util.Layout.*;

public class Dropdown extends AbstractDropdownList {
    private static final int DEFAULT_RENDER_ORDER = 1;

    private Dropdown(
            final Coord2D position, final Bounds2D dimensions,
            final Anchor anchor, final SimpleItem[] items,
            final Supplier<Integer> initialIndexFunction
    ) {
        super(position, dimensions, anchor, DEFAULT_RENDER_ORDER,
                items, initialIndexFunction);

        make();
    }

    public static Dropdown create(
            final Coord2D position,
            final String[] labels, final Runnable[] behaviours,
            final Supplier<Integer> initialIndexFunction
    ) {
        return create(position, Anchor.LEFT_CENTRAL, labels,
                behaviours, initialIndexFunction);
    }

    public static Dropdown create(
            final Coord2D position, final Anchor anchor,
            final String[] labels, final Runnable[] behaviours,
            final Supplier<Integer> initialIndexFunction
    ) {
        final int widest = Arrays.stream(labels)
                .map(l -> Graphics.uiText(Colors.def())
                        .addText(l).build().draw().getWidth())
                .reduce(1, Math::max);

        final Bounds2D dims = new Bounds2D(
                widest + DROPDOWN_EXTRA_W, TEXT_BUTTON_H);

        return new Dropdown(position, dims, anchor,
                composeItems(labels, behaviours), initialIndexFunction);
    }

    private static SimpleItem[] composeItems(
            final String[] labels, final Runnable[] behaviours
    ) {
        assert labels.length == behaviours.length;

        final SimpleItem[] items = new SimpleItem[labels.length];

        for (int i = 0; i < items.length; i++)
            items[i] = new SimpleItem(labels[i], behaviours[i]);

        return items;
    }

    @Override
    protected VertScrollBox makeDDContainer(final Coord2D position) {
        // TODO
        return null;
    }

    @Override
    protected MenuElement makeDDButton() {
        // TODO
        return null;
    }

    @Override
    protected Coord2D contentsDisplacement() {
        return new Coord2D(0, getHeight());
    }
}
