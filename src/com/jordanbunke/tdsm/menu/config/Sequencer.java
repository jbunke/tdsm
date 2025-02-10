package com.jordanbunke.tdsm.menu.config;

import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Layout;

import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class Sequencer<T> extends MenuElementContainer {
    final BiConsumer<T, Boolean> inclusionUpdate;
    final BiConsumer<T, Integer> orderUpdate;
    final Function<T, Boolean> inclusionCheck;
    final Function<T, String> nameFunc;

    final double relW;

    private final MenuElement[] menuElements;

    Sequencer(
            final Coord2D position, final T[] inputData,
            final BiConsumer<T, Boolean> inclusionUpdate,
            final BiConsumer<T, Integer> orderUpdate,
            final Function<T, Boolean> inclusionCheck,
            final Function<T, String> nameFunc, final double relW
    ) {
        super(position, new Bounds2D(1, 1), Anchor.LEFT_TOP, false);

        this.inclusionUpdate = inclusionUpdate;
        this.orderUpdate = orderUpdate;
        this.inclusionCheck = inclusionCheck;
        this.nameFunc = nameFunc;

        this.relW = relW;

        menuElements = generateMenuElements(inputData);
    }

    private MenuElement[] generateMenuElements(final T[] data) {
        final MenuBuilder mb = new MenuBuilder();

        // TODO - temp implementation -- consider wrapping in vert scroll box
        Coord2D pos = getRenderPosition();

        for (int i = 0; i < data.length; i++) {
            final SequenceEntry<T> entry =
                    new SequenceEntry<>(pos, data[i], this, i);
            mb.add(entry);
            pos = pos.displace(0, Layout.SEQUENCE_ENTRY_INC_Y);
        }

        return mb.build().getMenuElements();
    }

    @Override
    public MenuElement[] getMenuElements() {
        return menuElements;
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return false;
    }
}
