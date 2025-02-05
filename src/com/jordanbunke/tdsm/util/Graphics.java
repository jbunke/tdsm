package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;

import java.nio.file.Path;

public final class Graphics {
    private static final Path ICONS_FOLDER = Path.of("icons");

    public static GameImage readIcon(final String code) {
        final Path iconFile = ICONS_FOLDER.resolve(code.toLowerCase() + ".png");
        return ResourceLoader.loadImageResource(iconFile);
    }
}
