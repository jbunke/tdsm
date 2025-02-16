package com.jordanbunke.tdsm.flow;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.flow.screens.Configuration;
import com.jordanbunke.tdsm.flow.screens.Customization;
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.Cursor;
import com.jordanbunke.tdsm.util.Graphics;
import com.jordanbunke.tdsm.util.Tooltip;

import static com.jordanbunke.tdsm.util.Layout.*;

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

    public static void to(final Menu menu) {
        if (state != MENU)
            return;

        ProgramState.menu = menu;
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        final Coord2D mousePos = eventLogger.getAdjustedMousePosition();
        Tooltip.get().ping(Tooltip.NONE, mousePos);
        Cursor.reset(mousePos);

        switch (state) {
            case CUSTOMIZATION -> Customization.get().process(eventLogger);
            case CONFIGURATION -> Configuration.get().process(eventLogger);
            case MENU -> menu.process(eventLogger);
        }

        Tooltip.get().check();
    }

    @Override
    public void update(final double deltaTime) {
        switch (state) {
            case CUSTOMIZATION -> Customization.get().update(deltaTime);
            case CONFIGURATION -> Configuration.get().update(deltaTime);
            case MENU -> menu.update(deltaTime);
        }
    }

    @Override
    public void render(final GameImage canvas) {
        canvas.fillRectangle(Colors.bg(), 0, 0, CANVAS_W, CANVAS_H);

        switch (state) {
            case CUSTOMIZATION -> Customization.get().render(canvas);
            case CONFIGURATION -> Configuration.get().render(canvas);
            case MENU -> {
                menu.render(canvas);
                Graphics.renderScreenBox(canvas);
            }
        }

        Tooltip.get().render(canvas);
        Cursor.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
