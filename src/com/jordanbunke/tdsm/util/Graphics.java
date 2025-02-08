package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.text.Text;
import com.jordanbunke.delta_time.text.TextBuilder;
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

    public static GameImage drawSwatchButton(
            final Color color, final Button b
    ) {
        // TODO - temp MVP implementation
        final GameImage button = new GameImage(
                SWATCH_BUTTON_DIM, SWATCH_BUTTON_INC);

        button.fill(color);

        final Color outline = b.outcomes(Colors.selected(),
                Colors.highlight(), Colors.def());

        button.drawRectangle(outline, 2f, 0, 0,
                button.getWidth(), button.getHeight());

        return button.submit();
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
