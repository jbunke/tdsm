package com.jordanbunke.tdsm.menu.sampler;

import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.menu.text_button.StaticTextButton;
import com.jordanbunke.tdsm.util.Colors;

import java.awt.*;

import static com.jordanbunke.tdsm.util.Layout.CustomizationBox.SAMPLER;

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

        // TODO - textbox

        // submit button - TODO - potentially illegal precondition
        final MenuElement submitButton = StaticTextButton.make(
                "Submit", SAMPLER.at(10, 10), Anchor.LEFT_TOP,
                this::isActive, () -> submit(true));

        // TODO - sampler itself
        // - hue bar
        // - SV matrix

        mb.addAll(swatchManager, submitButton);

        return mb.build().getMenuElements();
    }

    public static Sampler get() {
        return INSTANCE;
    }

    public boolean isActive() {
        return selection != null;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(final Color color, final ColorTransmitter sender) {
        this.color = color;

        for (MenuElement e : menuElements)
            if (e instanceof ColorTransmitter ct && !ct.equals(sender))
                ct.receive(color);

        submit(false);
    }

    public ColorSelection getSelection() {
        return selection;
    }

    public void setSelection(final ColorSelection selection) {
        this.selection = selection;

        swatchManager.newColorSelection(selection);
        setColor(selection.getColor(), null);
    }

    public void submit(final boolean close) {
        if (isActive())
            selection.setColor(color);

        if (close)
            close();
    }

    public void close() {
        selection = null;
    }

    @Override
    public MenuElement[] getMenuElements() {
        return menuElements;
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        // TODO - test
        return false;
    }
}
