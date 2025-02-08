package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.data.style.Styles;
import com.jordanbunke.tdsm.flow.ProgramState;
import com.jordanbunke.tdsm.menu.Dropdown;
import com.jordanbunke.tdsm.menu.IconButton;
import com.jordanbunke.tdsm.menu.StaticLabel;
import com.jordanbunke.tdsm.menu.Veil;
import com.jordanbunke.tdsm.menu.layer.ColorSelectionButton;
import com.jordanbunke.tdsm.menu.sampler.Sampler;
import com.jordanbunke.tdsm.menu.text_button.StaticTextButton;
import com.jordanbunke.tdsm.visual_misc.Playback;

import java.awt.*;
import java.util.Arrays;

import static com.jordanbunke.tdsm.util.Layout.CustomizationBox.*;
import static com.jordanbunke.tdsm.util.Layout.labelPosFor;

public final class MenuAssembly {
    public static Menu stub() {
        return new Menu();
    }

    public static Menu customization() {
        final MenuBuilder mb = new MenuBuilder();

        // TODO - PREVIEW
        final StaticLabel animationLabel = StaticLabel.make(
                labelPosFor(PREVIEW.x, PREVIEW.atY(0.7)), "Animation:");

        final Animation[] anims = Sprite.get().getStyle().animations;
        final Dropdown animationDropdown = Dropdown.create(
                animationLabel.after(), MenuElement.Anchor.LEFT_TOP,
                Arrays.stream(anims).map(Animation::name)
                        .toArray(String[]::new),
                Arrays.stream(anims)
                        .map(a -> (Runnable) () -> Playback.get().setAnimation(a))
                        .toArray(Runnable[]::new),
                () -> Arrays.stream(anims).toList()
                        .indexOf(Playback.get().getAnimation()));

        final double ARROW_HEIGHT = 0.4;
        final MenuElement turnCWButton = IconButton.make(
                ResourceCodes.TURN_CLOCKWISE, PREVIEW.at(0.2, ARROW_HEIGHT),
                () -> true, () -> Sprite.get().turn(true)),
                turnCCWButton = IconButton.make(
                        ResourceCodes.TURN_COUNTERCLOCKWISE,
                        PREVIEW.at(0.8, ARROW_HEIGHT), () -> true,
                        () -> Sprite.get().turn(false));

        mb.addAll(animationLabel, animationDropdown,
                turnCWButton, turnCCWButton);

        // SAMPLER
        final Veil veil = new Veil(SAMPLER.pos(), SAMPLER.dims(),
                Sampler.get(), () -> Sampler.get().isActive());

        mb.addAll(veil);

        // TODO - TOP BAR
        final StaticLabel styleLabel = StaticLabel.make(
                labelPosFor(TOP.x, TOP.y), "Sprite style:");

        final Style[] styles = EnumUtils.stream(Styles.class)
                .map(Styles::get).toArray(Style[]::new);
        final Dropdown styleDropdown = Dropdown.create(
                styleLabel.after(), MenuElement.Anchor.LEFT_TOP,
                Arrays.stream(styles).map(Style::name)
                        .toArray(String[]::new),
                Arrays.stream(styles)
                        .map(s -> (Runnable) () -> Sprite.get().setStyle(s))
                        .toArray(Runnable[]::new),
                () -> Arrays.stream(styles).toList()
                        .indexOf(Sprite.get().getStyle())
        );

        final MenuElement randomSpriteButton = IconButton.make(
                ResourceCodes.RANDOM, ResourceCodes.RANDOM_SPRITE,
                TOP.at(0.95, 0.5), () -> true,
                () -> Sprite.get().getStyle().randomize());

        mb.addAll(styleLabel, styleDropdown, randomSpriteButton);

        // TODO - LAYER
        // TODO - temp dummy elements
        final ColorSelection ds1 = new ColorSelection("", true),
                ds2 = new ColorSelection("", false,
                        new Color(0x28, 0x28, 0x3c),
                        new Color(0x80, 0, 0));
        final ColorSelectionButton csb1 =
                new ColorSelectionButton(LAYERS.at(10, 10),
                        MenuElement.Anchor.LEFT_TOP, ds1),
                csb2 = new ColorSelectionButton(
                        LAYERS.at(LAYERS.width - 10, 10),
                        MenuElement.Anchor.RIGHT_TOP, ds2);

        mb.addAll(csb1, csb2);

        // TODO - BOTTOM BAR
        final MenuElement toMainButton = StaticTextButton.make(
                "< Main Menu", BOTTOM.at(0.0, 0.5).displace(4, 0),
                MenuElement.Anchor.LEFT_CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.MENU, main()));
        final MenuElement toSaveButton = StaticTextButton.make(
                "Configure... >", BOTTOM.at(1.0, 0.5).displace(-4, 0),
                MenuElement.Anchor.RIGHT_CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.CONFIGURATION, null));

        mb.addAll(toMainButton, toSaveButton);

        return mb.build();
    }

    public static Menu main() {
        // TODO

        return stub();
    }
}
