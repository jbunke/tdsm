package com.jordanbunke.tdsm.flow.screens;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.Graphics;
import com.jordanbunke.tdsm.util.Layout;
import com.jordanbunke.tdsm.util.MenuAssembly;

import java.awt.*;
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
        canvas.draw(drawPreview(), PREVIEW.x, PREVIEW.y);

        Arrays.stream(configurationBoxes()).forEach(
                box -> Graphics.drawScreenBox(box, canvas));

        menu.render(canvas);
    }

    private GameImage drawPreview() {
        final Color shadow = Colors.shadow();
        final GameImage preview = new GameImage(Graphics.CHECKERBOARD);
        final int w = preview.getWidth(), h = preview.getHeight();

        final GameImage sprite = Sprite.get()
                .getStyle().firstIncludedSpritePreview();

        if (sprite == null) {
            preview.fill(shadow);
            return preview.submit();
        }

        final Coord2D tl = Layout.centerWithin(preview, sprite),
                br = tl.displace(sprite.getWidth(), sprite.getHeight());

        preview.draw(sprite, tl.x, tl.y);

        // shadows
        preview.fillRectangle(shadow, 0, 0, tl.x, h);
        preview.fillRectangle(shadow, br.x, 0, w - br.x, h);
        preview.fillRectangle(shadow, tl.x, 0, br.x - tl.x, tl.y);
        preview.fillRectangle(shadow, tl.x, br.y, br.x - tl.x, h - br.y);

        // lines
        preview.draw(Graphics.H_LINE, 0, tl.y - 1);
        preview.draw(Graphics.V_LINE, tl.x - 1, 0);
        preview.draw(Graphics.H_LINE, 0, br.y);
        preview.draw(Graphics.V_LINE, br.x, 0);

        return preview.submit();
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
