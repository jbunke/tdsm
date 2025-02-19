package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.text.Text;
import com.jordanbunke.delta_time.text.TextBuilder;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.tdsm.util.ParserUtils;
import com.jordanbunke.tdsm.util.ProgramFont;
import com.jordanbunke.tdsm.util.ResourceCodes;

public final class Blinker extends StaticLabel {
    private static final int ON = 40, OFF = 15;

    private int tick;
    private boolean showing = true;

    private Blinker(
            final Coord2D position, final Anchor anchor, final GameImage image
    ) {
        super(position, anchor, image);
    }

    public static Blinker make(
            final Coord2D position, final Anchor anchor
    ) {
        final String[] splashTexts = ParserUtils
                .readTooltip(ResourceCodes.SPLASH)
                .split("\n\n");
        final String choice =
                splashTexts[RNG.randomInRange(0, splashTexts.length)];
        final TextBuilder tb = ProgramFont.MINI
                .getBuilder(Text.Orientation.CENTER);
        final String[] lines = choice.split("\n");

        for (int i = 0; i < lines.length; i++) {
            tb.addText(lines[i]);

            if (i + 1 < lines.length) tb.addLineBreak();
        }

        return new Blinker(position, anchor, tb.build().draw());
    }

    @Override
    public void update(final double deltaTime) {
        super.update(deltaTime);

        final int threshold = showing ? ON : OFF;
        tick++;

        if (tick >= threshold) {
            tick = 0;
            showing = !showing;
        }
    }

    @Override
    public void render(final GameImage canvas) {
        if (showing)
            super.render(canvas);
    }
}
