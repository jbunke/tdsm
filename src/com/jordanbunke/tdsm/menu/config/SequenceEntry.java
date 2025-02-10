package com.jordanbunke.tdsm.menu.config;

import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.menu.Checkbox;
import com.jordanbunke.tdsm.menu.StaticLabel;
import com.jordanbunke.tdsm.menu.config.drag.DragLogic;
import com.jordanbunke.tdsm.menu.config.drag.DragNode;
import com.jordanbunke.tdsm.util.Colors;

/* TODO - draggable behaviour and visual
*   - Index is NECESSARY because draggable needs to have an idea of how
*     many indices it has moved
*   - Configuration menu will need to be rebuilt on drag release
* */
public final class SequenceEntry<T> extends MenuElementContainer {
    private final Checkbox checkbox;
    private final StaticLabel label;
    private final DragNode<T> dragNode;

    public final Sequencer<T> sequencer;
    public final T data;
    public final int index;

    private Coord2D lockedPos;

    SequenceEntry(
            final Coord2D position, final Bounds2D dims, final T data,
            final Sequencer<T> sequencer, final int index
    ) {
        super(position, dims, Anchor.LEFT_TOP, false);

        checkbox = new Checkbox(position, Anchor.LEFT_TOP,
                () -> sequencer.inclusionCheck.apply(data),
                b -> sequencer.inclusionUpdate.accept(data, b));
        label = StaticLabel.mini(checkbox.followMiniLabel(),
                sequencer.nameFunc.apply(data), Colors.darkSystem(),
                Anchor.LEFT_TOP);

        this.sequencer = sequencer;
        this.data = data;
        this.index = index;

        dragNode = new DragNode<>(position.displace(dims.width(), 0),
                Anchor.RIGHT_TOP, this);
    }

    @Override
    public MenuElement[] getMenuElements() {
        return new MenuElement[] { checkbox, label, dragNode};
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return false;
    }

    // dragging

    public void drag() {
        final DragLogic<T> logic = sequencer.dragLogic;
        final int DIM = getHeight(), movingIndex = logic.getMovingIndex();
        final int deltaY = (int) Math.round(
                logic.deltaMousePosition().y / (double) DIM),
                diff;

        if (movingIndex == index) {
            logic.setWouldBeIndex(index + deltaY);

            diff = (logic.getWouldBeIndex() - index) * DIM;
        } else {
            final int adjustedIndex = index + (movingIndex > index
                    ? (logic.getWouldBeIndex() > index ? 0 : 1)
                    : (logic.getWouldBeIndex() < index ? 0 : -1));

            diff = (adjustedIndex - index) * DIM;
        }

        setY(lockedPos.y + diff);
    }

    public void lockPosition() {
        lockedPos = getPosition();
    }

    public void releasedAt(int destIndex) {
        sequencer.orderUpdate.accept(data, destIndex);
    }

    @Override
    public void incrementX(final int deltaX) {
        if (sequencer.dragLogic.isMoving())
            lockedPos = lockedPos.displace(deltaX, 0);
        else
            super.incrementX(deltaX);
    }

    @Override
    public void incrementY(final int deltaY) {
        if (sequencer.dragLogic.isMoving())
            lockedPos = lockedPos.displace(0, deltaY);
        else
            super.incrementY(deltaY);
    }
}
