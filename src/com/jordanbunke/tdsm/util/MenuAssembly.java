package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.TDSM;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Edge;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.data.style.Styles;
import com.jordanbunke.tdsm.flow.ProgramState;
import com.jordanbunke.tdsm.menu.*;
import com.jordanbunke.tdsm.menu.config.AnimationSequencer;
import com.jordanbunke.tdsm.menu.config.DirectionSequencer;
import com.jordanbunke.tdsm.menu.config.PaddingTextbox;
import com.jordanbunke.tdsm.menu.layer.ColorSelectionElement;
import com.jordanbunke.tdsm.menu.sampler.Sampler;
import com.jordanbunke.tdsm.menu.text_button.Alignment;
import com.jordanbunke.tdsm.menu.text_button.ButtonType;
import com.jordanbunke.tdsm.menu.text_button.StaticTextButton;
import com.jordanbunke.tdsm.visual_misc.Playback;

import java.awt.*;
import java.util.Arrays;

import static com.jordanbunke.tdsm.util.Constants.*;
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
                animationLabel.followTB(), MenuElement.Anchor.LEFT_TOP,
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
                styleLabel.followTB(), MenuElement.Anchor.LEFT_TOP,
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

        // INCLUSION
        final StaticLabel sequencingLabel = StaticLabel.make(
                labelPosFor(SEQUENCING.x, SEQUENCING.y), "Sequencing");
        final Indicator sequencingInfo = Indicator.make(ResourceCodes.INCLUSION,
                sequencingLabel.followIcon17(), MenuElement.Anchor.LEFT_TOP);

        final double SEQUENCER_Y = 0.17;
        final DirectionSequencer dirSequencer = new DirectionSequencer(
                new Coord2D(BUFFER, SEQUENCING.atY(SEQUENCER_Y)));
        final AnimationSequencer animSequencer = new AnimationSequencer(
                SEQUENCING.at(0.5, SEQUENCER_Y).displace(-BUFFER, 0));

        mb.addAll(sequencingLabel, sequencingInfo, dirSequencer, animSequencer);

        // LAYOUT
        final StaticLabel paddingLabel = StaticLabel.make(
                labelPosFor(LAYOUT.x, LAYOUT.y), "Padding");
        final Indicator paddingInfo = Indicator.make(ResourceCodes.PADDING,
                paddingLabel.followIcon17(), MenuElement.Anchor.LEFT_TOP);

        final double EDGES_Y = 0.12, EDGES_Y_INC = 0.08;
        EnumUtils.stream(Edge.class).forEach(e -> {
            final Coord2D edgeLabelPos = LAYOUT.at(
                    0.0 + (e.ordinal() / 2 == 0 ? 0.0 : 0.5),
                    EDGES_Y + (e.ordinal() % 2 == 0 ? 0.0 : EDGES_Y_INC))
                    .displace(BUFFER, 0);
            final StaticLabel edgeLabel = StaticLabel.make(edgeLabelPos,
                    StringUtils.nameFromID(e.name().toLowerCase()) + ":");
            final PaddingTextbox edgeTextbox =
                    new PaddingTextbox(edgeLabel.followTBStandard(), e);
            mb.addAll(edgeLabel, edgeTextbox);
        });

        final double SPRITE_SIZE_Y = 0.32;
        final String spriteSizePrefix = "Individual sprite size: ";
        final DynamicLabel spriteSizeLabel = DynamicLabel.mini(
                miniLabelPosFor(LAYOUT.x, LAYOUT.atY(SPRITE_SIZE_Y)),
                MenuElement.Anchor.LEFT_TOP, () -> {
                    final Bounds2D dims = Sprite.get()
                            .getStyle().getExportSpriteDims();
                    return spriteSizePrefix + dims.width() + "x" + dims.height();
                },
                spriteSizePrefix + MAX_SPRITE_EXPORT_W + "x" +
                        MAX_SPRITE_EXPORT_H, Colors.darkSystem());

        // TODO - horizontal / vertical

        // TODO - animations per dimension -- boundless?

        mb.addAll(paddingLabel, paddingInfo, spriteSizeLabel);

        // BOTTOM BAR
        final MenuElement toCustomButton = StaticTextButton.make(
                "< Customize...", BOTTOM.at(0.0, 0.5).displace(4, 0),
                MenuElement.Anchor.LEFT_CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.CUSTOMIZATION, null));
        final MenuElement toExportButton = StaticTextButton.make(
                "Export... >", BOTTOM.at(1.0, 0.5).displace(-4, 0),
                MenuElement.Anchor.RIGHT_CENTRAL,
                () -> Sprite.get().getStyle().exportsASprite(),
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
