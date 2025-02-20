package com.jordanbunke.tdsm.menu.anim;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.visual.AnimationMenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;

public class MenuAnimation extends AnimationMenuElement {
    public MenuAnimation(
            final Coord2D position, final Anchor anchor,
            final int ticksPerFrame, final GameImage... frames
    ) {
        super(position, new Bounds2D(frames[0].getWidth(),
                frames[0].getHeight()), anchor, ticksPerFrame, frames);
    }
}
