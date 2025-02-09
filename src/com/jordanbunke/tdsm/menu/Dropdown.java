package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.button.SimpleToggleMenuButton;
import com.jordanbunke.delta_time.menu.menu_elements.ext.dropdown.AbstractDropdownList;
import com.jordanbunke.delta_time.menu.menu_elements.ext.dropdown.SimpleItem;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.menu.scrollable.VertScrollBox;
import com.jordanbunke.tdsm.menu.text_button.Alignment;
import com.jordanbunke.tdsm.menu.text_button.ButtonType;
import com.jordanbunke.tdsm.menu.text_button.StaticTextButton;
import com.jordanbunke.tdsm.menu.text_button.TextButton;
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.Graphics;

import java.util.Arrays;
import java.util.function.Supplier;

import static com.jordanbunke.tdsm.util.Layout.*;

public final class Dropdown extends AbstractDropdownList {
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
        final int size = getSize();
        final MenuElement[] scrollables = new MenuElement[size];

        final Bounds2D dimensions = new Bounds2D(getWidth(),
                TEXT_BUTTON_H * Math.min(DD_ELEMENT_ALLOWANCE, size));
        final int realBottomY = position.y + (size * TEXT_BUTTON_H);

        final boolean canScroll = realBottomY > position.y + dimensions.height();
        final int buttonWidth = getWidth() - (canScroll ? VERT_SCROLL_BAR_W : 0);

        for (int i = 0; i < size; i++) {
            final int index = i;

            scrollables[i] = StaticTextButton.make(
                    getLabelTextFor(i), ButtonType.DD_OPTION, Alignment.LEFT,
                    buttonWidth, position.displace(0, i * TEXT_BUTTON_H),
                    Anchor.LEFT_TOP, () -> true, () -> select(index));
        }

        return new VertScrollBox(position, dimensions,
                Arrays.stream(scrollables).map(Scrollable::new)
                        .toArray(Scrollable[]::new), realBottomY, 0);
    }

    @Override
    protected SimpleToggleMenuButton makeDDButton() {
        final int width = getWidth();
        final String text = getCurrentLabelText();
        final TextButton base = TextButton.of(text, width,
                Alignment.LEFT, ButtonType.DD_HEAD);

        final GameImage[] bases = new GameImage[] {
                Graphics.drawTextButton(base),
                Graphics.drawTextButton(base.sim(true, false))
        };

        final GameImage[] highlighted = new GameImage[] {
                Graphics.drawTextButton(base.sim(false, true)),
                Graphics.drawTextButton(base.sim(true, true))
        };

        return new SimpleToggleMenuButton(new Coord2D(getX(), getY()),
                new Bounds2D(getWidth(), TEXT_BUTTON_H),
                getAnchor(), true, bases, highlighted,
                new Runnable[] { () -> {}, () -> {} },
                () -> isDroppedDown() ? 1 : 0, this::toggleDropDown);
    }

    @Override
    protected Coord2D contentsDisplacement() {
        return new Coord2D(0, getHeight());
    }
}
