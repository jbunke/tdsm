package com.jordanbunke.tdsm.visual_misc;

import com.jordanbunke.tdsm.data.Animation;

import static com.jordanbunke.tdsm.data.Animation.PlaybackMode.*;

public class Playback {
    private Animation animation;

    private boolean playing, forwards;
    private int frame, ticks;

    private static final Playback INSTANCE;

    private Playback() {

    }

    static {
        INSTANCE = new Playback();
    }

    public static Playback get() {
        return INSTANCE;
    }

    public void setAnimation(final Animation animation) {
        if (!animation.equals(this.animation)) {
            frame = 0;
            ticks = 0;
            playing = true;
            forwards = true;
        }

        this.animation = animation;
    }

    public void tick() {
        if (!playing || animation == null)
            return;

        final int frameTicks = animation.ticksForFrame(frame),
                frameCount = animation.frameCount();
        ticks++;

        if (ticks >= frameTicks) {
            ticks = 0;

            if (forwards) {
                if (frame + 1 >= frameCount) {
                    if (animation.playbackMode == LOOP)
                        frame = 0;
                    else
                        forwards = false;
                } else
                    frame++;
            } else if (animation.playbackMode == PONG) {
                if (frame <= 0)
                    forwards = true;
                else
                    frame--;
            } else {
                frame = 0;
                forwards = true;
            }
        }
    }

    public Animation getAnimation() {
        return animation;
    }

    public int getFrame() {
        return frame;
    }
}
