package com.jordanbunke.tdsm.data;

import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.func.CoordFunc;
import com.jordanbunke.tdsm.util.Constants;
import com.jordanbunke.tdsm.util.StringUtils;

import java.util.Arrays;

public final class Animation {
    public final String id;
    private final int[] ticksPerFrame;
    public final CoordFunc coordFunc;
    public final PlaybackMode playbackMode;

    public enum PlaybackMode {
        LOOP, PONG
    }

    private Animation(
            final String id, final int[] ticksPerFrame,
            final CoordFunc coordFunc,
            final PlaybackMode playbackMode
    ) {
        this.id = id;
        this.ticksPerFrame = ticksPerFrame;
        this.playbackMode = playbackMode;
        this.coordFunc = coordFunc;
    }

    public static Builder init(final String id, final int frameCount) {
        return new Builder(id, frameCount);
    }

    public static class Builder {
        private final String id;
        private final int[] ticksPerFrame;

        private CoordFunc coordFunc;
        private PlaybackMode playbackMode;

        Builder(final String id, final int frameCount) {
            this.id = id;
            ticksPerFrame = new int[frameCount];
            Arrays.fill(ticksPerFrame, Constants.FRAME_TICKS);

            coordFunc = f -> new Coord2D(0, f);
            playbackMode = PlaybackMode.LOOP;
        }

        public Builder setPlaybackMode(final PlaybackMode playbackMode) {
            this.playbackMode = playbackMode;
            return this;
        }

        public Builder setCoordFunc(final CoordFunc coordFunc) {
            this.coordFunc = coordFunc;
            return this;
        }

        public Builder setCoordFunc(
                final Coord2D firstFrame, final boolean horizontal
        ) {
            coordFunc = f -> new Coord2D(
                    firstFrame.x + (horizontal ? f : 0),
                    firstFrame.y + (horizontal ? 0 : f)
            );
            return this;
        }

        public Builder setFrameTiming(final int ticks) {
            Arrays.fill(ticksPerFrame, ticks);
            return this;
        }

        public Builder setTicksPerFrame(final int... ticksPerFrame) {
            if (ticksPerFrame.length == this.ticksPerFrame.length)
                System.arraycopy(ticksPerFrame, 0,
                        this.ticksPerFrame, 0, ticksPerFrame.length);

            return this;
        }

        public Animation build() {
            return new Animation(id, ticksPerFrame, coordFunc, playbackMode);
        }
    }

    public int frameCount() {
        return ticksPerFrame.length;
    }

    public int ticksForFrame(final int frameIndex) {
        return ticksPerFrame[frameIndex];
    }

    @Override
    public String toString() {
        return id;
    }

    public String name() {
        return StringUtils.nameFromID(id);
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
