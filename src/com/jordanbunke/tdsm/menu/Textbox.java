package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.ext.AbstractTextbox;
import com.jordanbunke.delta_time.menu.menu_elements.ext.drawing_functions.TextboxDrawingFunction;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.util.Cursor;
import com.jordanbunke.tdsm.util.Graphics;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.jordanbunke.tdsm.util.Layout.ICON_TEXTBOX_RELATIVE_DIFF_Y;
import static com.jordanbunke.tdsm.util.Layout.TEXT_BUTTON_H;

public class Textbox extends AbstractTextbox {
    public Textbox(
            final Coord2D position, final int width, final Anchor anchor,
            final String prefix, String initialText, final String suffix,
            final Predicate<String> textValidator,
            final Consumer<String> setter, final int maxLength
    ) {
        super(position, new Bounds2D(width, TEXT_BUTTON_H), anchor,
                () -> prefix, initialText, () -> suffix,
                textValidator::test, setter, Textbox::draw, maxLength);
    }

    /**
     * The method signature reflects a mistake in Delta Time:
     * @param dims Should be of type {@code Bounds2D}
     * @see TextboxDrawingFunction
     * */
    public static GameImage draw(
            final Coord2D dims,
            final String prefix, final String text, final String suffix,
            final int cursorIndex, final int selectionIndex,
            final boolean valid, final boolean highlighted, final boolean typing
    ) {
        return Graphics.drawTextbox(dims.x, prefix, text, suffix,
                cursorIndex, selectionIndex, valid, highlighted, typing);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        if (isHighlighted())
            Cursor.ping(Cursor.TEXT);
    }

    public Coord2D followIcon17() {
        return getRenderPosition().displace(getWidth(), ICON_TEXTBOX_RELATIVE_DIFF_Y);
    }

    // TEXT VALIDATORS HERE

    public static boolean validFramesPerDim(final String s) {
        try {
            final int i = Integer.parseInt(s);

            return i >= Sprite.get().getStyle().longestAnimFrameCount();
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
