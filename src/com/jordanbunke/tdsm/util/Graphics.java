package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.text.Text;
import com.jordanbunke.delta_time.text.TextBuilder;

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
    public static GameImage drawTextButtonBase(final String text) {
        // TODO - temp MVP implementation

        final GameImage textImage = uiText(Colors.def())
                .addText(text).build().draw();
        final GameImage button = new GameImage(
                textImage.getWidth() + TEXT_BUTTON_EXTRA_W, TEXT_BUTTON_H);

        button.drawRectangle(Colors.def(), 2f, 0, 0,
                button.getWidth(), button.getHeight());
        button.draw(textImage, TEXT_BUTTON_EXTRA_W / 2, TEXT_IN_BUTTON_OFFSET_Y);

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
