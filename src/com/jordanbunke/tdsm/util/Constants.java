package com.jordanbunke.tdsm.util;

import java.nio.file.Path;

public final class Constants {
    public static final String
            NAME_CODE = "name", VERSION_CODE = "version",
            IS_DEVBUILD_CODE = "devbuild";

    public static final Path PROGRAM_FILE = Path.of("program");

    public static final double TICK_HZ = 30d, FPS = 30d;

    public static final int FRAME_TICKS = 5;
}
