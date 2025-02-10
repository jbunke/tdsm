package com.jordanbunke.tdsm.menu.config;

import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.jordanbunke.tdsm.util.Layout.*;
import static com.jordanbunke.tdsm.util.Layout.ScreenBox.SEQUENCING;

public abstract class Sequencer<T> extends MenuElementContainer {
    final BiConsumer<T, Boolean> inclusionUpdate;
    final BiConsumer<T, Integer> orderUpdate;
    final Function<T, Boolean> inclusionCheck;
    final Function<T, String> nameFunc;

    final double relW;

    private final SequencerScrollBox scrollBox;

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

        scrollBox = makeScrollBox(inputData);
    }

    private SequencerScrollBox makeScrollBox(final T[] data) {
        final MenuBuilder mb = new MenuBuilder();

        Coord2D pos = getRenderPosition();
        final Coord2D overall = pos;
        final int INC = SEQUENCE_ENTRY_INC_Y,
                w = (int) (SEQUENCING.width * relW),
                HEIGHT = (int) (SEQUENCING.height * SEQUENCER_REL_H),
                realBottomY = pos.y + (data.length * INC);
        final boolean canScroll = realBottomY > pos.y + HEIGHT;
        final int entryW = w - (canScroll ? SEQUENCER_SCROLL_BAR_W : 0);

        for (int i = 0; i < data.length; i++) {
            final SequenceEntry<T> entry =
                    new SequenceEntry<>(pos, new Bounds2D(entryW, INC),
                            data[i], this, i);
            mb.add(entry);
            pos = pos.displace(0, INC);
        }

        return new SequencerScrollBox(overall, new Bounds2D(w, HEIGHT),
                Arrays.stream(mb.build().getMenuElements())
                        .map(Scrollable::new)
                        .toArray(Scrollable[]::new), realBottomY);
    }

    @Override
    public MenuElement[] getMenuElements() {
        return new MenuElement[] { scrollBox };
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return true;
    }
}
