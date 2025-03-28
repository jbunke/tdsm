package com.jordanbunke.tdsm;

import com.jordanbunke.delta_time.OnStartup;
import com.jordanbunke.delta_time._core.GameManager;
import com.jordanbunke.delta_time._core.Program;
import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.error.GameError;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.utility.Version;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.delta_time.window.GameWindow;
import com.jordanbunke.stip_parser.ParserSerializer;
import com.jordanbunke.stip_parser.SerialBlock;
import com.jordanbunke.tdsm.flow.ProgramState;
import com.jordanbunke.tdsm.util.*;

import java.nio.file.Path;
import java.util.Arrays;

public final class TDSM implements ProgramContext {
    public static String PROGRAM_NAME = "Top Down Sprite Maker";
    private static Version VERSION = new Version(1, 0, 0);
    private static boolean IS_DEVBUILD = false;

    public final Program program;
    public final GameWindow window;

    static {
        OnStartup.run();
        readProgramFile();
    }

    private static void readProgramFile() {
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

    private TDSM() {
        ProgramState.set(ProgramState.MENU, MenuAssembly.main());

        window = makeWindow();
        window.hideCursor();
        program = new Program(window, new GameManager(0, this),
                Constants.TICK_HZ, Constants.FPS);
        program.setCanvasSize(Layout.CANVAS_W, Layout.CANVAS_H);
        program.setScheduleUpdates(false);
        program.getDebugger().muteChannel(GameDebugger.FRAME_RATE);
    }

    public static void main(final String[] args) {
        new TDSM();
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        ProgramState.get().process(eventLogger);
    }

    @Override
    public void update(final double deltaTime) {
        ProgramState.get().update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        ProgramState.get().render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    private GameWindow makeWindow() {
        return new GameWindow(PROGRAM_NAME + " " + getVersion(),
                Layout.width(), Layout.height(),
                Graphics.readIcon(ResourceCodes.ICON), true, false, false);
    }

    public static void quitProgram() {
        // TODO - potential write settings
        System.exit(0);
    }
}
