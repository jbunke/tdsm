package com.jordanbunke.tdsm.menu.sampler;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.ThinkingMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.menu.text_button.Alignment;
import com.jordanbunke.tdsm.menu.text_button.ButtonType;
import com.jordanbunke.tdsm.menu.text_button.StaticTextButton;
import com.jordanbunke.tdsm.menu.text_button.TextButton;
import com.jordanbunke.tdsm.util.Graphics;

import java.util.function.Supplier;

public final class SubmitColorButton extends StaticTextButton {
    private boolean active;

    private SubmitColorButton(
            final String label, final int width, final Alignment alignment,
            final Coord2D position, final Anchor anchor, final Runnable behaviour
    ) {
        super(label, ButtonType.STANDARD, width, alignment, position, anchor, behaviour);
    }

    public static ThinkingMenuElement make(
            final Coord2D position, final Supplier<Boolean> precondition,
            final Runnable behaviour
    ) {
        final String LABEL = "Submit";
        final int width = Graphics.naiveButtonWidth(LABEL);
        final Anchor anchor = Anchor.RIGHT_BOTTOM;
        final Alignment alignment = Alignment.CENTER;

        final SubmitColorButton button = new SubmitColorButton(
                LABEL, width, alignment, position, anchor, behaviour);
        final StaticMenuElement stub = new StaticMenuElement(
                position, anchor, Graphics.drawTextButton(TextButton.of(
                        LABEL, width, alignment, ButtonType.STUB)));

        return new ThinkingMenuElement(() -> precondition.get() ? button : stub);
    }

    private void updateActive() {
        active = Sampler.get().hasUnsubmitted() &&
                Sampler.get().getSelection().isAnyColor();
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        if (active)
            super.process(eventLogger);
    }

    @Override
    public void render(final GameImage canvas) {
        if (active)
            super.render(canvas);
    }

    @Override
    public void update(final double deltaTime) {
        updateActive();

        if (active)
            super.update(deltaTime);
    }
}
