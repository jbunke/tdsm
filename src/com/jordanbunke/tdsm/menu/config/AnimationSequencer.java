package com.jordanbunke.tdsm.menu.config;

import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Sprite;

public final class AnimationSequencer extends Sequencer<Animation> {
    public AnimationSequencer(final Coord2D position) {
        super(position, Sprite.get().getStyle()::updateAnimationInclusion,
                Sprite.get().getStyle()::reorderAnimation,
                Sprite.get().getStyle()::isAnimationIncluded,
                Sprite.get().getStyle()::animationExportOrder,
                a -> a.name() + " (" + a.frameCount() + ")", 0.55);
    }
}
