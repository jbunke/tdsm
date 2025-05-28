package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.ResourceLoader;

import java.nio.file.Path;

public final class ParserUtils {
    public static String readResourceText(final String code) {
        return read(Constants.TEXT_FOLDER.resolve(code + ".txt"));
    }

    public static String read(final Path resource) {
        return FileIO.readResource(ResourceLoader.loadResource(resource), "");
    }
}
