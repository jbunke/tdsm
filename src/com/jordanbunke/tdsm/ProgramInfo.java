package com.jordanbunke.tdsm;

import com.jordanbunke.delta_time.error.GameError;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.utility.Version;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.stip_parser.ParserSerializer;
import com.jordanbunke.stip_parser.SerialBlock;
import com.jordanbunke.tdsm.util.Constants;

import java.nio.file.Path;
import java.util.Arrays;

public final class ProgramInfo {
    public static String PROGRAM_NAME = "Top Down Sprite Maker";
    private static Version VERSION = new Version(1, 0, 0);
    private static boolean IS_DEVBUILD = false;

    static void readProgramFile() {
        final String programFile = FileIO.readResource(ResourceLoader
                .loadResource(Constants.PROGRAM_FILE), "prg");

        final SerialBlock[] blocks = ParserSerializer
                .deserializeBlocksAtDepthLevel(programFile);

        for (SerialBlock block : blocks) {
            switch (block.tag()) {
                case Constants.NAME_CODE -> PROGRAM_NAME = block.value();
                case Constants.VERSION_CODE -> {
                    try {
                        final Integer[] components = Arrays
                                .stream(block.value().split("\\."))
                                .map(Integer::parseInt).toArray(Integer[]::new);

                        final int MAJOR = 0, MINOR = 1, PATCH = 2,
                                BUILD = 3, HAS_BUILD_LENGTH = 4;

                        if (components.length == HAS_BUILD_LENGTH)
                            VERSION = new Version(components[MAJOR],
                                    components[MINOR], components[PATCH],
                                    components[BUILD]);
                        else if (components.length > PATCH)
                            VERSION = new Version(components[MAJOR],
                                    components[MINOR], components[PATCH]);
                    } catch (NumberFormatException e) {
                        GameError.send("Could not read program version from data file.");
                    }
                }
                case Constants.IS_DEVBUILD_CODE ->
                        IS_DEVBUILD = Boolean.parseBoolean(block.value());
            }
        }

        final Path RES_ROOT = Path.of("res");

        if (IS_DEVBUILD) {
            VERSION.incrementBuild();

            final Path toSave = RES_ROOT.resolve(Constants.PROGRAM_FILE);

            final StringBuilder updated = new StringBuilder();

            ParserSerializer.serializeSimpleAttributes(updated, -1,
                    new Pair<>(Constants.NAME_CODE, PROGRAM_NAME),
                    new Pair<>(Constants.VERSION_CODE, VERSION.toString()),
                    new Pair<>(Constants.IS_DEVBUILD_CODE,
                            String.valueOf(IS_DEVBUILD)));

            FileIO.writeFile(toSave, updated.toString());
        }

        final Path versionFile = RES_ROOT.resolve(Constants.VERSION_FILE);
        FileIO.writeFile(versionFile, VERSION.toString());
    }

    public static String getVersion() {
        return "v" + VERSION + (IS_DEVBUILD ? " (devbuild)" : "");
    }
}
