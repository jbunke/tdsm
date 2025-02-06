package com.jordanbunke.tdsm.menu.scrollable;

import com.jordanbunke.delta_time.image.GameImage;

@FunctionalInterface
public interface ScrollBoxDrawFunc {
    GameImage draw(final int width, final int height);
}
