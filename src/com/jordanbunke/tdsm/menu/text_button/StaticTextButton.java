package com.jordanbunke.tdsm.menu.text_button;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.button.MenuButton;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.ThinkingMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Graphics;
import com.jordanbunke.tdsm.util.Layout;

import java.util.function.Supplier;

public final class StaticTextButton extends MenuButton implements TextButton {
    private final String label;
    private final Alignment alignment;
    private final ButtonType buttonType;

    private final GameImage base, highlight;

    private StaticTextButton(
            final String label, final ButtonType buttonType,
            final int width, final Alignment alignment,
            final Coord2D position, final Anchor anchor,
            final Runnable behaviour
    ) {
        super(position, new Bounds2D(width, Layout.TEXT_BUTTON_H),
                anchor, true, behaviour);

        this.label = label;
        this.alignment = alignment;
        this.buttonType = buttonType;

        base = Graphics.drawTextButton(sim(false, false));
        highlight = Graphics.drawTextButton(sim(false, true));
    }

    public static ThinkingMenuElement make(
            final String label,
            final Coord2D position, final Anchor anchor,
            final Supplier<Boolean> precondition, final Runnable behaviour
    ) {
        return make(label, ButtonType.STANDARD, Alignment.CENTER,
                position, anchor, precondition, behaviour);
    }

    public static ThinkingMenuElement make(
            final String label, final ButtonType buttonType,
            final Alignment alignment,
            final Coord2D position, final Anchor anchor,
            final Supplier<Boolean> precondition, final Runnable behaviour
    ) {
        final int width = Graphics.naiveButtonWidth(label);

        return make(label, buttonType, alignment, width,
                position, anchor, precondition, behaviour);
    }

    public static ThinkingMenuElement make(
            final String label, final ButtonType buttonType,
            final Alignment alignment, final int width,
            final Coord2D position, final Anchor anchor,
            final Supplier<Boolean> precondition, final Runnable behaviour
    ) {
        final StaticTextButton button =
                new StaticTextButton(label, buttonType, width,
                        alignment, position, anchor, behaviour);
        final StaticMenuElement stub =
                new StaticMenuElement(position, anchor,
                        Graphics.drawTextButton(TextButton.of(
                                label, width, alignment, ButtonType.STUB)));

        return new ThinkingMenuElement(
                () -> precondition.get() ? button : stub);
    }

    @Override
    public void render(final GameImage canvas) {
        draw(isHighlighted() ? highlight : base, canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    @Override
    public void update(final double deltaTime) {}

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Alignment getAlignment() {
        return alignment;
    }

    @Override
    public ButtonType getButtonType() {
        return buttonType;
    }
}
