package com.jordanbunke.tdsm.flow.screens;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.image.ImageProcessing;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.util.*;
import com.jordanbunke.tdsm.visual_misc.Playback;

import java.util.Arrays;

import static com.jordanbunke.tdsm.util.Layout.*;
import static com.jordanbunke.tdsm.util.Layout.ScreenBox.*;

public final class Customization implements ProgramContext {
    private static final Customization INSTANCE;
    private Menu menu;

    static {
        INSTANCE = new Customization();
    }

    private Customization() {
        menu = MenuAssembly.stub();
    }

    public static Customization get() {
        return INSTANCE;
    }

    public void rebuildMenu() {
        menu = MenuAssembly.customization();
    }

    public void process(final InputEventLogger eventLogger) {
        menu.process(eventLogger);
    }

    public void update(final double deltaTime) {
        Playback.get().tick();
        menu.update(deltaTime);
    }

    public void render(final GameImage canvas) {
        final GameImage blueprint = Graphics.BLUEPRINT;
        final int bHalfW = blueprint.getWidth() / 2;
        canvas.draw(blueprint, PREVIEW.atX(0.5) - bHalfW, BUFFER);

        final GameImage sprite = ImageProcessing.scale(
                Sprite.get().renderSprite(),
                Sprite.get().getStyle().getPreviewScaleUp());

        canvas.draw(sprite, (PREVIEW.width - sprite.getWidth()) / 2,
                Layout.PREVIEW_RENDER_Y);

        Arrays.stream(customizationBoxes())
                .forEach(box -> Graphics.renderScreenBox(canvas, box));

        menu.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
