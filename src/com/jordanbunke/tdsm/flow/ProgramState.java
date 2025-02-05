package com.jordanbunke.tdsm.flow;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.Menu;

public enum ProgramState implements ProgramContext {
    SPLASH, CUSTOMIZATION, SAVE, MENU;

    private static ProgramState state;
    private static Menu menu;

    public static ProgramState get() {
        return state;
    }

    public static void set(final ProgramState state, final Menu menu) {
        ProgramState.state = state;

        if (state == MENU)
            ProgramState.menu = menu;
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        // TODO
    }

    @Override
    public void update(final double deltaTime) {
        // TODO
    }

    @Override
    public void render(final GameImage canvas) {
        // TODO
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
