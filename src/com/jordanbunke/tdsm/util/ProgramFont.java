package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.fonts.Font;
import com.jordanbunke.delta_time.fonts.FontBuilder;

import java.nio.file.Path;

public class ProgramFont {
    private static final Font FONT;

    static {
        final FontBuilder builder = new FontBuilder().setPixelSpacing(1)
                .setWhitespaceBreadthMultiplier(0.5);
        final Path FOLDER = Path.of("font");
        FONT = builder.build(FOLDER, true, "tdsm-clipped");
    }

    public static Font get() {
        return FONT;
    }
}
