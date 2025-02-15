package com.jordanbunke.tdsm.menu.sampler;

import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.util.Colors;

import java.awt.*;

import static com.jordanbunke.tdsm.util.Layout.*;
import static com.jordanbunke.tdsm.util.Layout.ScreenBox.SAMPLER;

public final class Sampler extends MenuElementContainer {
    private static final Sampler INSTANCE;

    private final MenuElement[] menuElements;
    private final SwatchManager swatchManager;

    private ColorSelection selection;
    private Color color;

    static {
        INSTANCE = new Sampler();
    }

    private Sampler() {
        super(new Coord2D(), new Bounds2D(1, 1), Anchor.LEFT_TOP, false);

        selection = null;
        color = Colors.black();

        swatchManager = new SwatchManager();
        menuElements = setUpMenuElements();
    }

    private MenuElement[] setUpMenuElements() {
        final MenuBuilder mb = new MenuBuilder();

        // textbox
        final int postSwatchesX = BUFFER + (2 * SWATCH_BUTTON_INC);
        final ColorTextbox colorTextbox = new ColorTextbox(
                SAMPLER.at(postSwatchesX, SAMPLER.height - BUFFER),
                Anchor.LEFT_BOTTOM, color);

        // color picker
        final ColorPicker colorPicker = new ColorPicker(
                SAMPLER.at(postSwatchesX, BUFFER),
                new Bounds2D(SAMPLER.width - (BUFFER + postSwatchesX),
                        (SWATCH_BUTTON_INC * 5) -
                                (SWATCH_BUTTON_INC - SWATCH_BUTTON_DIM)),
                Anchor.LEFT_TOP, color);

        // submit button
        final MenuElement submitButton = SubmitColorButton.make(
                SAMPLER.at(1.0, 1.0).displace(-BUFFER, -BUFFER),
                this::isActive, this::submit);

        mb.addAll(swatchManager, submitButton, colorTextbox, colorPicker);

        return mb.build().getMenuElements();
    }

    public static Sampler get() {
        return INSTANCE;
    }

    public boolean isActive() {
        return selection != null;
    }

    public boolean hasUnsubmitted() {
        return isActive() && !getSelection().getColor().equals(getColor());
    }

    public Color getColor() {
        return color;
    }

    public void setColor(final Color color, final ColorTransmitter sender) {
        this.color = color;

        for (MenuElement e : menuElements)
            if (e instanceof ColorTransmitter ct && !ct.equals(sender))
                ct.receive(color);

        if (sender != null && sender.submits())
            submit();
    }

    public ColorSelection getSelection() {
        return selection;
    }

    public void setSelection(final ColorSelection selection) {
        this.selection = selection;

        swatchManager.newColorSelection(selection);
        setColor(selection.getColor(), null);
    }

    public void submit() {
        if (isActive())
            selection.setColor(color, true);
    }

    public void close() {
        selection = null;
    }

    public void jolt() {
        if (isActive())
            setColor(selection.getColor(), null);
    }

    @Override
    public MenuElement[] getMenuElements() {
        return menuElements;
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return false;
    }
}
