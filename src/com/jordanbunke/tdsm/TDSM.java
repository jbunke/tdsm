package com.jordanbunke.tdsm;

import com.jordanbunke.delta_time.OnStartup;
import com.jordanbunke.delta_time._core.GameManager;
import com.jordanbunke.delta_time._core.Program;
import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.window.GameWindow;
import com.jordanbunke.tdsm.flow.ProgramState;
import com.jordanbunke.tdsm.util.*;

import static com.jordanbunke.tdsm.ProgramInfo.*;

public final class TDSM implements ProgramContext {
    public final Program program;
    public final GameWindow window;

    private TDSM() {
        ProgramState.set(ProgramState.MENU, MenuAssembly.mainMenu());

        window = makeWindow();
        window.hideCursor();
        program = new Program(window, new GameManager(0, this),
                Constants.TICK_HZ, Constants.FPS);
        program.setCanvasSize(Layout.CANVAS_W, Layout.CANVAS_H);
        program.setScheduleUpdates(false);
        program.getDebugger().muteChannel(GameDebugger.FRAME_RATE);
    }

    public static void main(final String[] args) {
        OnStartup.run();
        readProgramFile();

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
