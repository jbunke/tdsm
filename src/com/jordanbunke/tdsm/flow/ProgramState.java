package com.jordanbunke.tdsm.flow;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.tdsm.flow.screens.Customization;

public enum ProgramState implements ProgramContext {
    CUSTOMIZATION, CONFIGURATION, MENU;

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
        switch (state) {
            case CUSTOMIZATION -> Customization.process(eventLogger);
            case MENU -> {
                // TODO - global
                menu.process(eventLogger);
            }
        }
    }

    @Override
    public void update(final double deltaTime) {
        switch (state) {
            case CUSTOMIZATION -> Customization.update(deltaTime);
        }
    }

    @Override
    public void render(final GameImage canvas) {
        switch (state) {
            case CUSTOMIZATION -> Customization.render(canvas);
        }
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
