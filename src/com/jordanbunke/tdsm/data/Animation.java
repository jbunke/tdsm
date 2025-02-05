package com.jordanbunke.tdsm.data;

import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Constants;

import java.util.function.Function;

public final class Animation {
    private final String id;
    private final int[] frameTimings;
    private final Function<Integer, Coord2D> coordFunc;
    private final PlaybackMode playbackMode;

    public enum PlaybackMode {
        LOOP, PONG
    }

    private Animation(
            final String id, final int[] frameTimings,
            final Function<Integer, Coord2D> coordFunc,
            final PlaybackMode playbackMode
    ) {
        this.id = id;
        this.frameTimings = frameTimings;
        this.playbackMode = playbackMode;
        this.coordFunc = coordFunc;
    }

    public static Animation make(
            final String id, final int frameCount,
            final Coord2D firstFrame, final boolean horizontal,
            final PlaybackMode playbackMode
    ) {
        final Function<Integer, Coord2D> coordFunc = f -> new Coord2D(
                firstFrame.x + (horizontal ? f : 0),
                firstFrame.y + (horizontal ? 0 : f)
        );

        final int[] frameTimings = new int[frameCount];

        for (int i = 0; i < frameCount; i++)
            frameTimings[i] = Constants.FRAME_TICKS;

        return new Animation(id, frameTimings, coordFunc, playbackMode);
    }

    public int frameCount() {
        return frameTimings.length;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof Animation that && this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return frameCount();
    }
}
