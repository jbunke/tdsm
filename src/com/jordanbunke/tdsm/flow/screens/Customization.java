package com.jordanbunke.tdsm.flow.screens;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.image.ImageProcessing;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.util.*;
import com.jordanbunke.tdsm.visual_misc.Playback;

import static com.jordanbunke.tdsm.util.Layout.*;
import static com.jordanbunke.tdsm.util.Layout.CustomizationBox.*;

public final class Customization {
    private static Menu menu = MenuAssembly.stub();

    public static void rebuildMenu() {
        menu = MenuAssembly.customization();
    }

    public static void process(final InputEventLogger eventLogger) {
        menu.process(eventLogger);
    }

    public static void update(final double deltaTime) {
        Playback.get().tick();
        menu.update(deltaTime);
    }

    public static void render(final GameImage canvas) {
        // TODO - draw boxes
        EnumUtils.stream(CustomizationBox.class).forEach(
                box -> renderCustomizationBox(box, canvas));

        final GameImage blueprint = Graphics.BLUEPRINT;
        final int bHalfW = blueprint.getWidth() / 2;
        canvas.draw(blueprint, PREVIEW.atX(0.5) - bHalfW, BUFFER);

        final GameImage sprite = ImageProcessing.scale(
                Sprite.get().renderSprite(),
                Sprite.get().getStyle().getPreviewScaleUp());

        canvas.draw(sprite, (PREVIEW.width - sprite.getWidth()) / 2,
                Layout.PREVIEW_RENDER_Y);

        menu.render(canvas);
    }

    private static void renderCustomizationBox(
            final CustomizationBox box, final GameImage canvas
    ) {
        canvas.drawRectangle(Colors.def(), 1f, box.x, box.y, box.width, box.height);
    }
}
