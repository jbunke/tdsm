package com.jordanbunke.tdsm.menu.pre_export;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.image.ImageProcessing;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.menu.Dropdown;
import com.jordanbunke.tdsm.menu.IconButton;
import com.jordanbunke.tdsm.util.Graphics;
import com.jordanbunke.tdsm.util.ResourceCodes;
import com.jordanbunke.tdsm.visual_misc.Playback;

import java.util.Arrays;

public final class ReplacementPreview extends MenuElement {
    private static final Bounds2D DIMS = new Bounds2D(
            Graphics.BLUEPRINT.getWidth(), Graphics.BLUEPRINT.getHeight());

    private final Style style;
    private Directions.Dir dir;

    private final IconButton cwButton, ccwButton;
    private final Dropdown animDropdown;

    public ReplacementPreview(
            final Coord2D position, final Anchor anchor, final Style style
    ) {
        super(position, DIMS, anchor, true);

        this.style = style;

        dir = style.exportDirections()[0];

        final Animation[] anims = style.exportAnimations();

        Playback.get().setAnimation(anims[0]);

        final Coord2D middle = getRenderPosition()
                .displace(getWidth() / 2, getHeight() / 2);
        final int divergence = (int) (getWidth() * 0.5);
        cwButton = IconButton.init(ResourceCodes.TURN_CLOCKWISE,
                middle.displaceX(-divergence), () -> turn(false))
                .setAnchor(Anchor.CENTRAL).build();
        ccwButton = IconButton.init(ResourceCodes.TURN_COUNTERCLOCKWISE,
                middle.displaceX(divergence), () -> turn(true))
                .setAnchor(Anchor.CENTRAL).build();

        animDropdown = Dropdown.create(
                middle.displaceX(divergence * 2), Anchor.LEFT_CENTRAL,
                Arrays.stream(anims)
                        .map(Animation::name).toArray(String[]::new),
                Arrays.stream(anims)
                        .map(a -> (Runnable) () -> Playback.get().setAnimation(a))
                        .toArray(Runnable[]::new),
                () -> Arrays.stream(anims).toList()
                        .indexOf(Playback.get().getAnimation()));
    }

    private void turn(final boolean ccw) {
        final Directions.NumDirs numDirs = style.directions.numDirs();

        do {
            dir = ccw ? dir.ccw(numDirs) : dir.cw(numDirs);
        } while (!dirIncluded(dir));
    }

    private boolean dirIncluded(final Directions.Dir check) {
        final Directions.Dir[] dirs = style.exportDirections();

        for (Directions.Dir dir : dirs)
            if (check.equals(dir)) return true;

        return false;
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        cwButton.process(eventLogger);
        ccwButton.process(eventLogger);
        animDropdown.process(eventLogger);
    }

    @Override
    public void update(final double deltaTime) {
        Playback.get().tick();

        cwButton.update(deltaTime);
        ccwButton.update(deltaTime);
        animDropdown.update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        draw(drawPreview(), canvas);

        cwButton.render(canvas);
        ccwButton.render(canvas);
        animDropdown.render(canvas);
    }

    private GameImage drawPreview() {
        final GameImage preview = new GameImage(getWidth(), getHeight());

        final GameImage sprite = ImageProcessing.scale(
                style.settings.preExportTransform(
                        style.renderSprite(dir,
                                Playback.get().getAnimation(),
                                Playback.get().getFrame())),
                style.getPreviewScaleUp());
        final int x = (getWidth() - sprite.getWidth()) / 2,
                y = (getHeight() - sprite.getHeight()) / 2;
        preview.draw(sprite, x, y);

        return preview.submit();
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
