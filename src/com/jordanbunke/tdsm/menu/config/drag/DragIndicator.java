package com.jordanbunke.tdsm.menu.config.drag;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Cursor;
import com.jordanbunke.tdsm.util.Graphics;
import com.jordanbunke.tdsm.util.ResourceCodes;
import com.jordanbunke.tdsm.util.Tooltip;

public final class DragIndicator extends StaticMenuElement {
    private DragIndicator(
            final Coord2D pos, final Anchor anchor, final GameImage img
    ) {
        super(pos, new Bounds2D(img.getWidth(), img.getHeight()), anchor, img);
    }

    public static DragIndicator make(final Coord2D pos, final Anchor anchor) {
        final GameImage icon = Graphics.readIcon(ResourceCodes.DRAG);
        return new DragIndicator(pos, anchor, icon);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        if (mouseIsWithinBounds(eventLogger.getAdjustedMousePosition()))
            Cursor.ping(Cursor.VERT_SCROLL);
    }
}
