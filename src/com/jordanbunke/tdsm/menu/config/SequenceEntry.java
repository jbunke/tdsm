package com.jordanbunke.tdsm.menu.config;

import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.menu.Checkbox;
import com.jordanbunke.tdsm.menu.StaticLabel;
import com.jordanbunke.tdsm.menu.config.drag.DragIndicator;
import com.jordanbunke.tdsm.util.Colors;

/* TODO - draggable behaviour and visual
*   - Index is NECESSARY because draggable needs to have an idea of how
*     many indices it has moved
*   - Configuration menu will need to be rebuilt on drag release
* */
public final class SequenceEntry<T> extends MenuElementContainer {
    private final Checkbox checkbox;
    private final StaticLabel label;
    private final DragIndicator dragIndicator;
    // TODO - index is not final in case I find a solution (pinging sequencer?)
    //  that doesn't require rebuilding menu on drag release
    private int index;

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
        dragIndicator = DragIndicator.make(
                position.displace(dims.width(), 0), Anchor.RIGHT_TOP);

        this.index = index;
    }

    @Override
    public MenuElement[] getMenuElements() {
        return new MenuElement[] { checkbox, label, dragIndicator };
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return false;
    }
}
