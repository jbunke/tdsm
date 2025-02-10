package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.tdsm.TDSM;
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
import com.jordanbunke.tdsm.menu.layer.ColorSelectionElement;
import com.jordanbunke.tdsm.menu.sampler.Sampler;
import com.jordanbunke.tdsm.menu.text_button.Alignment;
import com.jordanbunke.tdsm.menu.text_button.ButtonType;
import com.jordanbunke.tdsm.menu.text_button.StaticTextButton;
import com.jordanbunke.tdsm.visual_misc.Playback;

import java.awt.*;
import java.util.Arrays;

import static com.jordanbunke.tdsm.util.Layout.ScreenBox.*;
import static com.jordanbunke.tdsm.util.Layout.*;

public final class MenuAssembly {
    public static Menu stub() {
        return new Menu();
    }

    public static Menu customization() {
        final MenuBuilder mb = new MenuBuilder();

        // TODO - PREVIEW
        final StaticLabel animationLabel = StaticLabel.make(
                labelPosFor(PREVIEW.x, PREVIEW.atY(0.75)), "Animation:");

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

        final double ARROW_HEIGHT = 0.4, DIVERGENCE = 0.3;
        final MenuElement turnCWButton = IconButton.make(
                ResourceCodes.TURN_CLOCKWISE,
                PREVIEW.at(0.5 - DIVERGENCE, ARROW_HEIGHT),
                () -> true, () -> Sprite.get().turn(true)),
                turnCCWButton = IconButton.make(
                        ResourceCodes.TURN_COUNTERCLOCKWISE,
                        PREVIEW.at(0.5 + DIVERGENCE, ARROW_HEIGHT),
                        () -> true, () -> Sprite.get().turn(false));

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
        final ColorSelection ds1 = new ColorSelection("Test 1", true),
                ds2 = new ColorSelection("Test 2 @ Middle", false,
                        new Color(0x28, 0x28, 0x3c),
                        new Color(0x80, 0, 0));
        final ColorSelectionElement
                cse1 = ColorSelectionElement.of(ds1, LAYERS.pos()),
                cse2 = ColorSelectionElement.of(ds2, LAYERS.at(0.5, 0.0));

        final StaticLabel test = StaticLabel.mini(
                miniLabelPosFor(LAYERS.x, LAYERS.atY(0.5)),
                "The quick brown fox jumped over the lazy dog.", Colors.darkSystem(),
                MenuElement.Anchor.LEFT_TOP);

        mb.addAll(cse1, cse2, test);

        // BOTTOM BAR
        final MenuElement toMainButton = StaticTextButton.make(
                "< Main Menu", BOTTOM.at(0.0, 0.5).displace(4, 0),
                MenuElement.Anchor.LEFT_CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.MENU, main()));
        final MenuElement toConfigButton = StaticTextButton.make(
                "Configure... >", BOTTOM.at(1.0, 0.5).displace(-4, 0),
                MenuElement.Anchor.RIGHT_CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.CONFIGURATION, null));

        mb.addAll(toMainButton, toConfigButton);

        return mb.build();
    }

    public static Menu configuration() {
        final MenuBuilder mb = new MenuBuilder();

        // TODO - INCLUSION
        final StaticLabel inclusionLabel = StaticLabel.make(
                labelPosFor(INCLUSION.x, INCLUSION.y),
                "Output Sequence & Inclusion");

        mb.addAll(inclusionLabel);

        // TODO - LAYOUT

        // BOTTOM BAR
        final MenuElement toCustomButton = StaticTextButton.make(
                "< Customize...", BOTTOM.at(0.0, 0.5).displace(4, 0),
                MenuElement.Anchor.LEFT_CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.CUSTOMIZATION, null));
        final MenuElement toExportButton = StaticTextButton.make(
                "Export... >", BOTTOM.at(1.0, 0.5).displace(-4, 0),
                MenuElement.Anchor.RIGHT_CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.MENU, export()));

        mb.addAll(toCustomButton, toExportButton);

        return mb.build();
    }

    public static Menu main() {
        final MenuBuilder mb = new MenuBuilder();

        // TODO - logo

        final int buttonW = screenWidth(0.3);
        final MenuElement startButton = StaticTextButton.make(
                "Start", ButtonType.STANDARD, Alignment.CENTER,
                buttonW, canvasAt(0.5, 0.65),
                MenuElement.Anchor.CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.CUSTOMIZATION, null)),
                aboutButton = StaticTextButton.make(
                        "About", ButtonType.STANDARD, Alignment.CENTER,
                        buttonW, textButtonBelow(startButton),
                        MenuElement.Anchor.CENTRAL, () -> true,
                        () -> {} /* TODO */),
                quitButton = StaticTextButton.make(
                        "Quit", ButtonType.STANDARD, Alignment.CENTER,
                        buttonW, textButtonBelow(aboutButton),
                        MenuElement.Anchor.CENTRAL, () -> true,
                        TDSM::quitProgram);

        mb.addAll(startButton, aboutButton, quitButton);

        // Version and credits
        final StaticLabel programLabel = StaticLabel.make(
                canvasAt(0.5, 0.98),
                MenuElement.Anchor.CENTRAL_BOTTOM,
                Graphics.miniText(Colors.darkSystem())
                        .addText(TDSM.getVersion()).addLineBreak()
                        .addText("(c) 2025 Jordan Bunke").build());

        mb.add(programLabel);

        return mb.build();
    }

    public static Menu export() {
        final MenuBuilder mb = new MenuBuilder();

        // TODO

        return mb.build();
    }
}
