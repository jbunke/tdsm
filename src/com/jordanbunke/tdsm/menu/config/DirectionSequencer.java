package com.jordanbunke.tdsm.menu.config;

import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.Sprite;

public final class DirectionSequencer extends Sequencer<Directions.Dir> {
    public DirectionSequencer(final Coord2D position) {
        super(position, Sprite.get().getStyle().directionExportOrder(),
                Sprite.get().getStyle()::updateDirectionInclusion,
                Sprite.get().getStyle()::reorderDirection,
                Sprite.get().getStyle()::isDirectionIncluded,
                Sprite.get().getStyle().directions::name, 0.3);
    }
}
