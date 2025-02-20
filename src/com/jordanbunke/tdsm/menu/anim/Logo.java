package com.jordanbunke.tdsm.menu.anim;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.utility.math.Coord2D;

import java.nio.file.Path;

import static com.jordanbunke.tdsm.util.Constants.*;

public final class Logo extends MenuAnimation {
    private Logo(
            final Coord2D position, final Anchor anchor,
            final GameImage... frames
    ) {
        super(position, anchor, ANIM_TICKS, frames);
    }

    public static Logo make(
            final Coord2D position, final Anchor anchor
    ) {
        final GameImage[] frames = new GameImage[LOGO_FRAMES];

        for (int f = 0; f < frames.length; f++) {
            final Path path = LOGO_FOLDER
                    .resolve("logo-" + (f + 1) + ".png");
            frames[f] = ResourceLoader.loadImageResource(path);
        }

        return new Logo(position, anchor, frames);
    }
}
