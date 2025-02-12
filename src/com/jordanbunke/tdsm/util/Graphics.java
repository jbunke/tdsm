package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.text.Text;
import com.jordanbunke.delta_time.text.TextBuilder;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.menu.Button;
import com.jordanbunke.tdsm.menu.sampler.ColorPicker;
import com.jordanbunke.tdsm.menu.text_button.ButtonType;
import com.jordanbunke.tdsm.menu.text_button.TextButton;

import java.awt.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;

import static com.jordanbunke.tdsm.util.Layout.*;
import static com.jordanbunke.tdsm.util.Layout.ScreenBox.PREVIEW;

public final class Graphics {
    private static final Path ICONS_FOLDER = Path.of("icons"),
            CURSORS_FOLDER = Path.of("cursors");

    private static final GameImage HUE_SLIDER, SV_MATRIX;
    public static final GameImage BLUEPRINT, CHECKERBOARD, H_LINE, V_LINE;

    static {
        HUE_SLIDER = readIcon(ResourceCodes.HUE_SLIDER);
        SV_MATRIX = readIcon(ResourceCodes.SV_MATRIX);
        BLUEPRINT = readIcon(ResourceCodes.BLUEPRINT);
        CHECKERBOARD = drawCheckerboard();
        H_LINE = drawLine(true);
        V_LINE = drawLine(false);
    }

    // IO

    public static GameImage readIcon(final String code) {
        final Path iconFile = ICONS_FOLDER.resolve(code.toLowerCase() + ".png");
        return ResourceLoader.loadImageResource(iconFile);
    }

    public static GameImage readCursor(final Cursor cursor) {
        final Path cursorFile = CURSORS_FOLDER.resolve(
                cursor.name().toLowerCase() + ".png");
        return ResourceLoader.loadImageResource(cursorFile);
    }

    // Text
    public static TextBuilder uiText(final Color color, final double textSize) {
        return new TextBuilder(textSize, Text.Orientation.CENTER,
                color, ProgramFont.getLarge());
    }

    public static TextBuilder uiText(final Color color) {
        return uiText(color, 1.0);
    }

    public static TextBuilder miniText(final Color color) {
        return new TextBuilder(1.0, 0.3, Text.Orientation.CENTER,
                color, ProgramFont.getMini());
    }

    // UI Elements
    public static int naiveButtonWidth(final String label) {
        final GameImage textImage = uiText(Colors.darkSystem())
                .addText(label).build().draw();

        return textImage.getWidth() + TEXT_BUTTON_EXTRA_W;
    }

    public static GameImage drawTextButton(final TextButton tb) {
        // TODO - temp MVP implementation
        // TODO - account for button type and state (stub, highlighted, dropdown)
        final ButtonType type = tb.getButtonType();

        final GameImage textImage = uiText(Colors.darkSystem())
                .addText(tb.getLabel()).build().draw();
        final GameImage button = new GameImage(tb.getWidth(), TEXT_BUTTON_H);

        button.drawRectangle(Colors.darkSystem(), 2f, 0, 0,
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

            final int iconX = button.getWidth() -
                    (icon.getWidth() + DD_ICON_LEFT_NUDGE),
                    iconY = (TEXT_BUTTON_H - icon.getHeight()) / 2;
            button.draw(icon, iconX, iconY);
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

        // pre-processing
        final int left = Math.min(cursorIndex, selectionIndex),
                right = Math.max(cursorIndex, selectionIndex),
                INC = TEXTBOX_SEG_INC;

        final boolean hasSelection = left != right,
                cursorAtRight = cursorIndex == right;

        // setup
        final Color mainColor = valid ? Colors.darkSystem() : Colors.invalid(),
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

    public static GameImage drawCheckbox(
            final boolean highlighted, final boolean checked
    ) {
        final GameImage icon = readIcon(checked
                ? ResourceCodes.CHECKED : ResourceCodes.UNCHECKED);

        return highlighted ? highlightIcon(icon) : icon;
    }

    public static GameImage drawSwatchButton(
            final Color color, final Button b
    ) {
        // TODO - temp MVP implementation
        final GameImage button = new GameImage(
                SWATCH_BUTTON_DIM, SWATCH_BUTTON_DIM);

        button.fill(color);

        final Color outline = b.outcomes(Colors.selected(),
                Colors.highlight(), Colors.darkSystem());

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

    public static GameImage drawVertScrollSlider(
            final int w, final int h, final int barH,
            final int barY, final Button b
    ) {
        final GameImage slider = new GameImage(w, h);

        // TODO - temp MVP implementation
        final Color fill = b.outcomes(
                Colors.selected(), Colors.highlight(), Colors.darkSystem());
        slider.fillRectangle(fill, 0, barY, w, barH);

        return slider.submit();
    }

    public static GameImage drawColorPicker(
            final ColorPicker picker
    ) {
        // TODO - temp MVP implementation
        // pre-processing
        final int w = picker.getWidth(), h = picker.getHeight();

        final GameImage asset = new GameImage(w, h);

        // hue spectrum
        for (int y = 0; y < h; y++) {
            final Color at = Colors.fromHSV(picker.getHypothetical(0, y));
            asset.fillRectangle(at, 0, y, HUE_SLIDER_W, 1);
        }

        // SV matrix
        for (int x = HUE_SLIDER_W; x < w; x++) {
            for (int y = 0; y < h; y++) {
                final Color at = Colors.fromHSV(picker.getHypothetical(x, y));
                asset.setRGB(x, y, at.getRGB());
            }
        }

        // outlines
        final Color outline = Colors.darkSystem();
        asset.drawRectangle(outline, 2f, 0, 0, w, h);
        asset.drawLine(outline, 1f, HUE_SLIDER_W, 0, HUE_SLIDER_W, h);

        // selection indicators
        final Coord2D hsp = picker.localHuePos()
                .displace(1, -(HUE_SLIDER.getHeight() / 2)),
                svp = centerOn(picker.localSVPos(), SV_MATRIX);
        asset.draw(HUE_SLIDER, hsp.x, hsp.y);
        asset.draw(SV_MATRIX, svp.x, svp.y);

        return asset.submit();
    }

    // Draw additional

    public static GameImage drawTooltip(final String text) {
        final Color textColor = Colors.darkSystem();
        final String[] lines = text.split("\n");
        final GameImage[] lineImages = Arrays.stream(lines)
                .map(l -> miniText(textColor).addText(l).build().draw())
                .toArray(GameImage[]::new);
        final int ls = lines.length,
                w = Arrays.stream(lineImages)
                        .map(GameImage::getWidth)
                        .reduce(1, Math::max) + TOOLTIP_PADDING_W,
                h = TOOLTIP_LINE_INC_Y * ls;

        final GameImage tooltip = new GameImage(w, h);

        // background
        tooltip.fill(Colors.lightSystem());

        for (int l = 0; l < ls; l++) {
            final GameImage line = lineImages[l];
            final int x = (w - line.getWidth()) / 2,
                    y = TOOLTIP_INITIAL_OFFSET_Y + (l * TOOLTIP_LINE_INC_Y);
            tooltip.draw(line, x, y);
        }

        return tooltip.submit();
    }

    public static void renderScreenBox(final GameImage canvas) {
        renderScreenBox(canvas, 0, 0, CANVAS_W, CANVAS_H);
    }

    public static void renderScreenBox(
            final GameImage canvas, final ScreenBox box
    ) {
        renderScreenBox(canvas, box.x, box.y, box.width, box.height);
    }

    public static void renderScreenBox(
            final GameImage canvas,
            final int x, final int y, final int w, final int h
    ) {
        final Color outer = Colors.darkSystem(), inner = Colors.lightAccent();

        final GameImage box = new GameImage(w, h);

        box.drawRectangle(outer, 2f, 1, 1, w - 2, h - 2);
        box.drawRectangle(inner, 1f, 2, 2, w - 5, h - 5);

        box.dot(outer, 2, 2);
        box.dot(outer, 2, h - 3);
        box.dot(outer, w - 3, 2);
        box.dot(outer, w - 3, h - 3);

        canvas.draw(box.submit(), x, y);
    }

    private static GameImage drawCheckerboard() {
        final int w = PREVIEW.width, h = PREVIEW.height,
                px = CHECKERBOARD_SQUARE;
        final GameImage checkerboard = new GameImage(w, h);

        for (int x = 0; x < w / px; x++)
            for (int y = 0; y < h / px; y++)
                checkerboard.fillRectangle(
                        Colors.checkerboard((x + y) % 2 == 0),
                        x * px, y * px, px, px);

        return checkerboard.submit();
    }

    private static GameImage drawLine(final boolean horz) {
        final int l = horz ? PREVIEW.width : PREVIEW.height;
        final GameImage line = new GameImage(horz ? l : 1, horz ? 1 : l);

        for (int px = 0; px < l; px++)
            line.dot(Colors.line(px % 8 < 4), horz ? px : 0, horz ? 0 : px);

        return line.submit();
    }

    // Algo
    public static GameImage highlightIcon(
            final GameImage icon
    ) {
        final int w = icon.getWidth(), h = icon.getHeight();
        final GameImage highlight = new GameImage(icon);

        for (int x = 1; x < w - 1; x++) {
            for (int y = 1; y < h - 1; y++) {
                final Color c = icon.getColorAt(x, y);

                if (c.getAlpha() == 0 && hasAdjacent(icon, x, y))
                    highlight.dot(Colors.highlight(), x, y);
            }
        }

        return highlight.submit();
    }

    private static boolean hasAdjacent(
            final GameImage image, final int x, final int y
    ) {
        return notTransparent(image, x - 1, y) ||
                notTransparent(image, x + 1, y) ||
                notTransparent(image, x, y - 1) ||
                notTransparent(image, x, y + 1);
    }

    private static boolean notTransparent(
            final GameImage image, final int x, final int y
    ) {
        return image.getColorAt(x, y).getAlpha() > 0;
    }

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
