package com.jordanbunke.tdsm.menu.sampler;

import com.jordanbunke.delta_time.menu.menu_elements.invisible.ThinkingMenuElement;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.menu.text_button.Alignment;
import com.jordanbunke.tdsm.menu.text_button.ButtonType;
import com.jordanbunke.tdsm.menu.text_button.StaticTextButton;
import com.jordanbunke.tdsm.util.Graphics;

import java.util.function.Consumer;

public final class SamplerTextButton extends StaticTextButton {
    private SamplerTextButton(
            final String label, final int width, final Alignment alignment,
            final Coord2D position, final Anchor anchor, final Runnable behaviour
    ) {
        super(label, ButtonType.STANDARD, width, alignment, position, anchor, behaviour);
    }

    public static ThinkingMenuElement make(final Coord2D position) {
        final String SUBMIT_LABEL = "Submit", CLOSE_LABEL = "Close";
        final int width = Math.max(
                Graphics.naiveButtonWidth(SUBMIT_LABEL),
                Graphics.naiveButtonWidth(CLOSE_LABEL));
        final Anchor anchor = Anchor.RIGHT_BOTTOM;
        final Alignment alignment = Alignment.CENTER;

        final SamplerTextButton submitButton = new SamplerTextButton(
                SUBMIT_LABEL, width, alignment, position, anchor,
                SamplerTextButton::submit),
                closeButton = new SamplerTextButton(CLOSE_LABEL, width,
                        alignment, position, anchor, SamplerTextButton::close);

        return new ThinkingMenuElement(() ->
                canSubmit() ? submitButton : closeButton);
    }

    private static boolean canSubmit() {
        final Sampler s = Sampler.get();

        return s != null && s.hasUnsubmitted();
    }

    private static void submit() {
        safeSamplerAccess(Sampler::submit);
    }

    private static void close() {
        safeSamplerAccess(Sampler::close);
    }

    private static void safeSamplerAccess(final Consumer<Sampler> action) {
        final Sampler s = Sampler.get();

        if (s != null)
            action.accept(s);
    }
}
