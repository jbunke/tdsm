package com.jordanbunke.tdsm.menu.config.drag;

import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.menu.config.SequenceEntry;
import com.jordanbunke.tdsm.util.Cursor;
import com.jordanbunke.tdsm.util.Graphics;
import com.jordanbunke.tdsm.util.ResourceCodes;

public final class DragNode<T> extends StaticMenuElement {
    private final SequenceEntry<T> entry;

    private DragNode(
            final Coord2D pos, final Anchor anchor, final GameImage img,
            final SequenceEntry<T> entry
    ) {
        super(pos, new Bounds2D(img.getWidth(), img.getHeight()), anchor, img);

        this.entry = entry;
    }

    public DragNode(
            final Coord2D pos, final Anchor anchor, final SequenceEntry<T> entry
    ) {
        this(pos, anchor, Graphics.readIcon(ResourceCodes.DRAG), entry);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        final Coord2D mousePos = eventLogger.getAdjustedMousePosition();

        if (mouseIsWithinBounds(mousePos)) {
            Cursor.ping(Cursor.VERT_SCROLL);

            // initialize dragging
            for (GameEvent e : eventLogger.getUnprocessedEvents()) {
                if (e instanceof GameMouseEvent me) {
                    if (me.action == GameMouseEvent.Action.DOWN) {
                        me.markAsProcessed();
                        getLogic().prepare(mousePos, entry.index);
                    }
                }
            }
        }

        // drag logic
        getLogic().process(eventLogger, entry.index);
    }

    private DragLogic<T> getLogic() {
        return entry.sequencer.dragLogic;
    }
}
