package com.jordanbunke.tdsm.util;

import java.nio.file.Path;

public final class Constants {
    public static final String
            NAME_CODE = "name", VERSION_CODE = "version",
            IS_DEVBUILD_CODE = "devbuild",
            SCRIPT_EXT = "tds", STYLE_FILE_EXT = "tdsm",
            STYLE_MANIFEST_FILENAME = "manifest." + SCRIPT_EXT,
            DEFAULT_STYLE_NAME = "default";

    public static final Path PROGRAM_FILE = Path.of("program"),
            VERSION_FILE = Path.of("version"),
            ASSET_ROOT_FOLDER = Path.of("sprite_assets"),
            TEXT_FOLDER = Path.of("text"),
            LOGO_FOLDER = Path.of("logo"),
            LOADING_FOLDER = Path.of("loading"),
            STYLES_FOLDER = Path.of("styles");

    public static final double TICK_HZ = 60d, FPS = 60d;

    public static final int
            FRAME_TICKS = 12, TOOLTIP_TICKS = 24,
            MIN_SPRITE_EXPORT_W = 1, MIN_SPRITE_EXPORT_H = 1,
            MAX_SPRITE_EXPORT_W = 128, MAX_SPRITE_EXPORT_H = 128,
            DEF_FRAMES_PER_DIM = 8, DEF_TEXTBOX_CHAR_MAX = 12,
            FILE_NAME_MAX_LENGTH = 40, LOGO_FRAMES = 16,
            LOADING_FRAMES = 16, ANIM_TICKS = 6,
            GBA_SPRITE_COL_LIMIT = 15, STYLE_NAME_CUTOFF = 16;
}
