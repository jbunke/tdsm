package com.jordanbunke.tdsm.menu.anim;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.utility.math.Coord2D;

import java.nio.file.Path;

import static com.jordanbunke.tdsm.util.Constants.*;

public class LoadingAnimation extends MenuAnimation {
    private LoadingAnimation(
            final Coord2D position, final Anchor anchor,
            final GameImage... frames
    ) {
        super(position, anchor, ANIM_TICKS, frames);
    }

    public static LoadingAnimation make(
            final Coord2D position, final Anchor anchor
    ) {
        final GameImage[] frames = new GameImage[LOADING_FRAMES];

        for (int f = 0; f < frames.length; f++) {
            final Path path = LOADING_FOLDER
                    .resolve("loading-" + (f + 1) + ".png");
            frames[f] = ResourceLoader.loadImageResource(path);
        }

        return new LoadingAnimation(position, anchor, frames);
    }
}
