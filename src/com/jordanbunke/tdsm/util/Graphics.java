package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.text.Text;
import com.jordanbunke.delta_time.text.TextBuilder;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.menu.Button;
import com.jordanbunke.tdsm.menu.text_button.ButtonType;
import com.jordanbunke.tdsm.menu.text_button.TextButton;

import java.awt.*;
import java.nio.file.Path;
import java.util.function.Function;

import static com.jordanbunke.tdsm.util.Layout.*;

public final class Graphics {
    private static final Path ICONS_FOLDER = Path.of("icons");

    // IO

    public static GameImage readIcon(final String code) {
        final Path iconFile = ICONS_FOLDER.resolve(code.toLowerCase() + ".png");
        return ResourceLoader.loadImageResource(iconFile);
    }

    // Text
    public static TextBuilder uiText(final Color color, final double textSize) {
        return new TextBuilder(textSize, Text.Orientation.CENTER,
                color, ProgramFont.get());
    }

    public static TextBuilder uiText(final Color color) {
        return uiText(color, 1.0);
    }

    // UI Elements
    public static int naiveButtonWidth(final String label) {
        final GameImage textImage = uiText(Colors.def())
                .addText(label).build().draw();

        return textImage.getWidth() + TEXT_BUTTON_EXTRA_W;
    }

    public static GameImage drawTextButton(final TextButton tb) {
        // TODO - temp MVP implementation
        // TODO - account for button type and state (stub, highlighted, dropdown)
        final ButtonType type = tb.getButtonType();

        final GameImage textImage = uiText(Colors.def())
                .addText(tb.getLabel()).build().draw();
        final GameImage button = new GameImage(tb.getWidth(), TEXT_BUTTON_H);

        button.drawRectangle(Colors.def(), 2f, 0, 0,
                button.getWidth(), button.getHeight());

        final int x = switch (tb.getAlignment()) {
            case LEFT -> TEXT_BUTTON_RENDER_BUFFER_X;
            case CENTER -> (button.getWidth() - textImage.getWidth()) / 2;
            case RIGHT -> button.getWidth() -
                    (TEXT_BUTTON_RENDER_BUFFER_X + textImage.getWidth());
        };

        button.draw(textImage, x, TEXT_IN_BUTTON_OFFSET_Y);

        // dropdown list button
        if (type == ButtonType.DD_HEAD) {
            final GameImage icon = readIcon(tb.isSelected()
                    ? ResourceCodes.COLLAPSE : ResourceCodes.EXPAND);

            button.draw(icon, button.getWidth() -
                    (icon.getWidth() + TEXT_BUTTON_RENDER_BUFFER_X), 0);
        }

        return button.submit();
    }

    public static GameImage drawTextbox(
            final int width,
            final String prefix, final String text, final String suffix,
            final int cursorIndex, final int selectionIndex,
            final boolean valid, final boolean highlighted, final boolean typing
    ) {
        // TODO - temp MVP implementation
//        final TextButton tb = TextButton.of(prefix + text + suffix, width,
//                        Alignment.LEFT, ButtonType.STANDARD)
//                .sim(typing, highlighted);
//        return drawTextButton(tb);

        // pre-processing
        final int left = Math.min(cursorIndex, selectionIndex),
                right = Math.max(cursorIndex, selectionIndex),
                INC = TEXTBOX_SEG_INC;

        final boolean hasSelection = left != right,
                cursorAtRight = cursorIndex == right;

        // setup
        final Color mainColor = valid ? Colors.black() : Colors.invalid(),
                affixColor = Colors.shiftRGB(mainColor, 0x60),
                highlightOverlay = Colors.highlightOverlay(),
                outlineColor = typing ? Colors.selected() :
                        (highlighted ? Colors.highlight() : mainColor);

        // text and cursor

        final String preSel = text.substring(0, left),
                sel = text.substring(left, right),
                postSel = text.substring(right);
        final GameImage prefixImage = uiText(affixColor)
                .addText(prefix).build().draw(),
                suffixImage = uiText(affixColor)
                        .addText(suffix).build().draw(),
                preSelImage = uiText(mainColor)
                        .addText(preSel).build().draw(),
                selImage = uiText(mainColor)
                        .addText(sel).build().draw(),
                postSelImage = uiText(mainColor)
                        .addText(postSel).build().draw();

        final GameImage box = new GameImage(width, TEXT_BUTTON_H);

        Coord2D textPos = new Coord2D(TEXT_BUTTON_RENDER_BUFFER_X,
                TEXT_IN_BUTTON_OFFSET_Y);

        // possible prefix
        box.draw(prefixImage, textPos.x, textPos.y);
        if (!prefix.isEmpty())
            textPos = textPos.displace(prefixImage.getWidth() + INC, 0);

        // main text prior to possible selection
        box.draw(preSelImage, textPos.x, textPos.y);
        if (!preSel.isEmpty())
            textPos = textPos.displace(preSelImage.getWidth() + INC, 0);

        // possible selection text
        if (hasSelection) {
            if (!cursorAtRight)
                textPos = textPos.displace(2 * INC, 0);

            box.draw(selImage, textPos.x, textPos.y);
            box.fillRectangle(highlightOverlay, textPos.x - INC, 0,
                    selImage.getWidth() + (2 * INC), box.getHeight());
            textPos = textPos.displace(selImage.getWidth() + INC, 0);
        }

        // cursor
        box.fillRectangle(mainColor,
                textPos.x - (cursorAtRight ? 0
                        : selImage.getWidth() + (3 * INC)),
                0, INC, box.getHeight());
        if (cursorAtRight)
            textPos = textPos.displace(2 * INC, 0);

        // main text following possible selection
        box.draw(postSelImage, textPos.x, textPos.y);
        if (!postSel.isEmpty())
            textPos = textPos.displace(postSelImage.getWidth() + INC, 0);

        // possible suffix
        box.draw(suffixImage, textPos.x, textPos.y);

        box.drawRectangle(outlineColor, 2f, 0, 0,
                box.getWidth(), box.getHeight());

        return box.submit();
    }

    public static GameImage drawSwatchButton(
            final Color color, final Button b
    ) {
        // TODO - temp MVP implementation
        final GameImage button = new GameImage(
                SWATCH_BUTTON_DIM, SWATCH_BUTTON_DIM);

        button.fill(color);

        final Color outline = b.outcomes(Colors.selected(),
                Colors.highlight(), Colors.def());

        button.drawRectangle(outline, 2f, 0, 0,
                button.getWidth(), button.getHeight());

        return button.submit();
    }

    public static GameImage drawColSelButton(
            final Color color, final Button b
    ) {
        // TODO - temp MVP implementation
        return drawSwatchButton(color, b);
    }

    // Algo

    public static GameImage pixelWiseTransformation(
            final GameImage input, final Function<Color, Color> f
    ) {
        final GameImage output = new GameImage(input);

        final int w = output.getWidth(), h = output.getHeight();

        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                output.setRGB(x, y, f.apply(input.getColorAt(x, y)).getRGB());

        return output.submit();
    }

    public static Color greyscale(final Color in) {
        final int avg = (in.getRed() + in.getGreen() + in.getBlue()) / 3;
        return new Color(avg, avg, avg, in.getAlpha());
    }
}
