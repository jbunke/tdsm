package com.jordanbunke.tdsm.flow.screens;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.tdsm.util.Graphics;
import com.jordanbunke.tdsm.util.MenuAssembly;

import java.util.Arrays;

import static com.jordanbunke.tdsm.util.Layout.ScreenBox.*;

public final class Configuration implements ProgramContext {
    private static final Configuration INSTANCE;
    private Menu menu;

    static {
        INSTANCE = new Configuration();
    }

    private Configuration() {
        menu = MenuAssembly.stub();
    }

    public static Configuration get() {
        return INSTANCE;
    }

    public void rebuildMenu() {
        menu = MenuAssembly.configuration();
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        menu.process(eventLogger);
    }

    @Override
    public void update(final double deltaTime) {
        menu.update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        Arrays.stream(configurationBoxes()).forEach(
                box -> Graphics.drawScreenBox(box, canvas));

        // TODO - render preview of first sprite

        menu.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
