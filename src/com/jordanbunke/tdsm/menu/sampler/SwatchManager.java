package com.jordanbunke.tdsm.menu.sampler;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.InvisibleMenuElement;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.util.Colors;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class SwatchManager extends InvisibleMenuElement
        implements ColorTransmitter {
    private ColorSelection selection;
    private Color color;

    private final List<SwatchButton> swatchButtons;

    public SwatchManager() {
        this.selection = null;
        this.color = Colors.black();

        swatchButtons = new ArrayList<>();
    }

    void newColorSelection(final ColorSelection selection) {
        if (this.selection != null && this.selection.equals(selection))
            return;

        this.selection = selection;

        final Color[] swatches = selection.getSwatches();
        swatchButtons.clear();

        for (int i = 0; i < swatches.length; i++)
            swatchButtons.add(SwatchButton.make(swatches[i], i, this));
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        for (SwatchButton button : swatchButtons)
            button.process(eventLogger);
    }

    @Override
    public void render(final GameImage canvas) {
        for (SwatchButton button : swatchButtons)
            button.render(canvas);
    }

    @Override
    public void update(final double deltaTime) {
        for (SwatchButton button : swatchButtons)
            button.update(deltaTime);
    }

    @Override
    public void receive(final Color color) {}

    @Override
    public Color getColor() {
        return color;
    }

    public void setColor(final Color color) {
        this.color = color;
        send();
    }

    ColorSelection getSelection() {
        return selection;
    }
}
