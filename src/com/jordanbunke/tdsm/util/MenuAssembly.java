package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.menu.IconButton;
import com.jordanbunke.tdsm.menu.StaticLabel;

import static com.jordanbunke.tdsm.util.Layout.CustomizationBox.PREVIEW;
import static com.jordanbunke.tdsm.util.Layout.CustomizationBox.TOP;
import static com.jordanbunke.tdsm.util.Layout.labelPosFor;

public final class MenuAssembly {
    public static Menu stub() {
        return new Menu();
    }

    public static Menu customization() {
        final MenuBuilder mb = new MenuBuilder();

        // TODO - Preview
        final StaticLabel animationLabel = StaticLabel.make(
                labelPosFor(PREVIEW.x, PREVIEW.atY(0.7)), "Animation: ");

        final double ARROW_HEIGHT = 0.4;
        final MenuElement turnCWButton = IconButton.make(
                ResourceCodes.TURN_CLOCKWISE, PREVIEW.at(0.2, ARROW_HEIGHT),
                () -> true, () -> Sprite.get().turn(true)),
                turnCCWButton = IconButton.make(
                        ResourceCodes.TURN_COUNTERCLOCKWISE,
                        PREVIEW.at(0.8, ARROW_HEIGHT), () -> true,
                        () -> Sprite.get().turn(false));

        mb.addAll(animationLabel, turnCWButton, turnCCWButton);

        // TODO - Sampler

        // TODO - Top bar
        final StaticLabel styleLabel = StaticLabel.make(
                labelPosFor(TOP.x, TOP.y), "Sprite style: ");

        final MenuElement randomSpriteButton = IconButton.make(
                ResourceCodes.RANDOM, ResourceCodes.RANDOM_SPRITE,
                TOP.at(0.95, 0.5), () -> true,
                () -> Sprite.get().getStyle().randomize());

        mb.addAll(styleLabel, randomSpriteButton);

        // TODO - Layers

        // TODO - Bottom bar

        return mb.build();
    }
}
