package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.events.GameMouseScrollEvent;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.util.Cursor;
import com.jordanbunke.tdsm.util.Graphics;

import static com.jordanbunke.tdsm.util.Layout.*;

public final class SpriteSheetPreview extends MenuElement {
    private static final Coord2D CENTER_SCREEN = canvasAt(0.5, 0.5);
    private static final int ZOOM_TO_SCALE = 1, ZOOM_OUT = 2;

    private final GameImage spriteSheet;
    private GameImage spriteSheetCanvas;

    private boolean moving;
    private int zoom;
    private Coord2D lastMousePos;

    private SpriteSheetPreview(final GameImage spriteSheet) {
        super(CENTER_SCREEN,
                new Bounds2D(spriteSheet.getWidth(), spriteSheet.getHeight()),
                Anchor.CENTRAL, true);

        this.spriteSheet = spriteSheet;

        moving = false;
        zoom(ZOOM_TO_SCALE, CENTER_SCREEN);
    }

    public SpriteSheetPreview() {
        this(generateSpriteSheet());
    }

    private static GameImage generateSpriteSheet() {
        final Style style = Sprite.get().getStyle();
        final Bounds2D spriteDims = style.getExportSpriteDims();
        final GameImage spriteSheet = style.renderSpriteSheet(),
                checkerboard = Graphics.drawSpriteSheetPreviewCheckerboard(
                        spriteSheet.getWidth(), spriteSheet.getHeight(),
                        spriteDims.width(), spriteDims.height());

        checkerboard.draw(spriteSheet);
        return checkerboard.submit();
    }

    @Override
    public void update(double deltaTime) {}

    @Override
    public void render(final GameImage canvas) {
        draw(spriteSheetCanvas, canvas);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        final Coord2D mousePos = eventLogger.getAdjustedMousePosition();

        if (moving) {
            Cursor.ping(Cursor.HAND_GRAB);

            final Coord2D deltaMousePos =
                    mousePos.displace(lastMousePos.scale(-1));
            move(deltaMousePos);

            lastMousePos = mousePos;
        } else if (mouseIsWithinBounds(mousePos))
            Cursor.ping(Cursor.HAND_OPEN);

        for (GameEvent e : eventLogger.getUnprocessedEvents()) {
            if (e instanceof GameMouseEvent me) {
                switch (me.action) {
                    case UP -> moving = false;
                    case DOWN -> {
                        if (mouseIsWithinBounds(mousePos)) {
                            moving = true;
                            lastMousePos = mousePos;
                        }
                    }
                }
            } else if (e instanceof GameMouseScrollEvent sme) {
                final int zoom = sme.clicksScrolled < 0 ? ZOOM_TO_SCALE : ZOOM_OUT;

                if (this.zoom != zoom)
                    zoom(zoom, mousePos);
            }
        }
    }

    private void move(final Coord2D deltaMousePos) {
        incrementX(deltaMousePos.x);
        incrementY(deltaMousePos.y);

        final Coord2D rp = getRenderPosition(),
                br = rp.displace(getWidth(), getHeight());

        if (rp.x > CENTER_SCREEN.x)
            incrementX(CENTER_SCREEN.x - rp.x);
        if (br.x < CENTER_SCREEN.x)
            incrementX(CENTER_SCREEN.x - br.x);
        if (rp.y > CENTER_SCREEN.y)
            incrementY(CENTER_SCREEN.y - rp.y);
        if (br.y < CENTER_SCREEN.y)
            incrementY(CENTER_SCREEN.y - br.y);
    }

    private void zoom(final int zoom, final Coord2D mousePos) {
        this.zoom = zoom;

        final int w = spriteSheet.getWidth() / zoom,
                h = spriteSheet.getHeight() / zoom;

        spriteSheetCanvas = new GameImage(w, h);
        spriteSheetCanvas.draw(spriteSheet, 0, 0, w, h);
        spriteSheetCanvas.free();

        final Coord2D mouseToPos = getPosition().displace(mousePos.scale(-1)),
                zoomShift = zoom == ZOOM_TO_SCALE ? mouseToPos :
                        new Coord2D(mouseToPos.x / 2, mouseToPos.y / 2).scale(-1);

        incrementX(zoomShift.x);
        incrementY(zoomShift.y);

        setWidth(w);
        setHeight(h);

        move(new Coord2D());
    }

    @Override
    public void debugRender(
            final GameImage canvas, final GameDebugger debugger
    ) {}
}
