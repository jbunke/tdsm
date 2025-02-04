package com.jordanbunke.tdsm;

import com.jordanbunke.delta_time.OnStartup;
import com.jordanbunke.delta_time._core.Program;
import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.error.GameError;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.utility.Version;
import com.jordanbunke.delta_time.window.GameWindow;
import com.jordanbunke.tdsm.util.Constants;
import com.jordanbunke.tdsm.util.ParserUtils;

import java.nio.file.Path;
import java.util.Arrays;

public final class TDSM implements ProgramContext {
    public static String PROGRAM_NAME = "Top Down Sprite Maker";
    private static Version VERSION = new Version(1, 0, 0);
    private static boolean IS_DEVBUILD = false;

    private static final TDSM INSTANCE;

    public final Program program;
    public GameWindow window;

    static {
        OnStartup.run();
        readProgramFile();

        INSTANCE = new TDSM();
    }

    private static void readProgramFile() {
        final String[] programFile = FileIO.readResource(ResourceLoader
                .loadResource(Constants.PROGRAM_FILE), "prg").split("\n");

        for (String line : programFile) {
            final String[] codeAndValue = ParserUtils.splitIntoCodeAndValue(line);

            if (codeAndValue.length != ParserUtils.DESIRED)
                continue;

            final String code = codeAndValue[ParserUtils.CODE],
                    value = codeAndValue[ParserUtils.VALUE];

            switch (code) {
                case Constants.NAME_CODE -> PROGRAM_NAME = value;
                case Constants.VERSION_CODE -> {
                    try {
                        final Integer[] components = Arrays
                                .stream(value.split("\\."))
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
                        IS_DEVBUILD = Boolean.parseBoolean(value);
            }
        }

        if (IS_DEVBUILD) {
            VERSION.incrementBuild();

            final Path toSave = Path.of("res").resolve(Constants.PROGRAM_FILE);

            final String write = ParserUtils.encloseSetting(
                    Constants.NAME_CODE, PROGRAM_NAME) +
                    ParserUtils.encloseSetting(Constants.VERSION_CODE,
                            VERSION.toString()) +
                    ParserUtils.encloseSetting(Constants.IS_DEVBUILD_CODE,
                            String.valueOf(IS_DEVBUILD));

            FileIO.writeFile(toSave, write);
        }
    }

    private TDSM() {
        // TODO
        window = null; // here();
        program = null; // new Program(window, here);
    }

    public static TDSM get() {
        return INSTANCE;
    }

    public static void main(final String[] args) {

    }

    @Override
    public void process(final InputEventLogger eventLogger) {

    }

    @Override
    public void update(final double deltaTime) {

    }

    @Override
    public void render(final GameImage canvas) {

    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {

    }
}
