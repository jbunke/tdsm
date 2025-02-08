package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.fonts.Font;
import com.jordanbunke.delta_time.fonts.FontBuilder;

import java.nio.file.Path;

public final class ProgramFont {
    private static final Font LARGE, MINI;

    static {
        final Path FOLDER = Path.of("font");
        final FontBuilder builder = new FontBuilder().setPixelSpacing(1)
                .setWhitespaceBreadthMultiplier(0.5);
        LARGE = builder.build(FOLDER, true, "tdsm");

        builder.setWhitespaceBreadthMultiplier(0.2);
        MINI = builder.build(FOLDER, true, "mini");
    }

    public static Font getLarge() {
        return LARGE;
    }

    public static Font getMini() {
        return MINI;
    }
}
