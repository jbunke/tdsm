package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.fonts.Font;
import com.jordanbunke.delta_time.fonts.FontBuilder;
import com.jordanbunke.delta_time.text.Text;
import com.jordanbunke.delta_time.text.TextBuilder;

import java.awt.*;
import java.nio.file.Path;

public enum ProgramFont {
    LARGE("tdsm", 1.0, 0.5),
    MINI("mini", 0.3, 0.35);

    private final Font font;
    private final double lineSpacing;

    ProgramFont(
            final String baseName, final double lineSpacing,
            final double whitespaceMult
    ) {
        final Path FOLDER = Path.of("font");
        final FontBuilder builder = new FontBuilder().setPixelSpacing(1)
                .setWhitespaceBreadthMultiplier(whitespaceMult);

        font = builder.build(FOLDER, true, baseName);
        this.lineSpacing = lineSpacing;
    }

    public TextBuilder getBuilder(final Text.Orientation orientation) {
        return getBuilder(1.0, orientation, Colors.darkSystem());
    }

    public TextBuilder getBuilder(
            final double textSize, final Text.Orientation orientation,
            final Color color
    ) {
        return new TextBuilder(textSize,
                lineSpacing, orientation, color, font);
    }
}
