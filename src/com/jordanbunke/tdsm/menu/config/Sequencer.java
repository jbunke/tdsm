package com.jordanbunke.tdsm.menu.config;

import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.menu.config.drag.DragLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.jordanbunke.tdsm.util.Layout.*;
import static com.jordanbunke.tdsm.util.Layout.ScreenBox.SEQUENCING;

public abstract class Sequencer<T> extends MenuElementContainer {
    final BiConsumer<T, Boolean> inclusionUpdate;
    final BiConsumer<T, Integer> orderUpdate;
    final Function<T, Boolean> inclusionCheck;
    final Supplier<T[]> orderGetter;
    final Function<T, String> nameFunc;
    public final DragLogic<T> dragLogic;

    final double relW;

    private SequencerScrollBox scrollBox;
    private final List<SequenceEntry<T>> elements;
    private int offset;

    Sequencer(
            final Coord2D position,
            final BiConsumer<T, Boolean> inclusionUpdate,
            final BiConsumer<T, Integer> orderUpdate,
            final Function<T, Boolean> inclusionCheck,
            final Supplier<T[]> orderGetter,
            final Function<T, String> nameFunc, final double relW
    ) {
        super(position, new Bounds2D(1, 1), Anchor.LEFT_TOP, false);

        this.inclusionUpdate = inclusionUpdate;
        this.orderUpdate = orderUpdate;
        this.inclusionCheck = inclusionCheck;
        this.orderGetter = orderGetter;
        this.nameFunc = nameFunc;
        this.dragLogic = new DragLogic<>(this);

        this.relW = relW;

        this.offset = 0;
        elements = new ArrayList<>();
        refreshScrollBox();
    }

    public void refreshScrollBox() {
        scrollBox = makeScrollBox();
    }

    private SequencerScrollBox makeScrollBox() {
        final MenuBuilder mb = new MenuBuilder();
        elements.clear();

        final T[] data = orderGetter.get();

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
            elements.add(entry);
            pos = pos.displace(0, INC);
        }

        return new SequencerScrollBox(overall, new Bounds2D(w, HEIGHT),
                Arrays.stream(mb.build().getMenuElements())
                        .map(Scrollable::new)
                        .toArray(Scrollable[]::new), realBottomY, offset);
    }

    @Override
    public MenuElement[] getMenuElements() {
        return new MenuElement[] { scrollBox };
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return true;
    }

    @Override
    public void update(final double deltaTime) {
        super.update(deltaTime);

        offset = -scrollBox.getOffset().y;
    }

    public SequenceEntry<T> getElement(final int index) {
        try {
            return elements.get(index);
        } catch (IndexOutOfBoundsException ioobe) {
            return null;
        }
    }

    public List<SequenceEntry<T>> getElements() {
        return elements;
    }
}
