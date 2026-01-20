package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;

import static com.jordanbunke.tdsm.util.Layout.ScreenBox.SAMPLER;

import java.util.function.Supplier;

public final class Veil extends MenuElement {
    private boolean passing;
    private final Supplier<Boolean> condition;
    private final MenuElement content;
    private final StaticLabel disabled;

    public Veil(
            final Coord2D position, final Bounds2D dimensions,
            final MenuElement content, final Supplier<Boolean> condition
    ) {
        super(position, dimensions, Anchor.LEFT_TOP, true);

        this.content = content;
        this.condition = condition;

        disabled = StaticLabel
                .init(SAMPLER.at(0.5, 0.5), "No active color selection")
                .setMini().setAnchor(Anchor.CENTRAL).build();

        passing = condition.get();
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        if (passing)
            content.process(eventLogger);
    }

    @Override
    public void update(final double deltaTime) {
        passing = condition.get();

        if (passing)
            content.update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        if (passing)
            content.render(canvas);
        else
            disabled.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
