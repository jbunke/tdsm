package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement.Anchor;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementGrouping;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.GatewayMenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.TDSM;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Edge;
import com.jordanbunke.tdsm.data.Orientation;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.data.style.Styles;
import com.jordanbunke.tdsm.flow.ProgramState;
import com.jordanbunke.tdsm.menu.*;
import com.jordanbunke.tdsm.menu.Checkbox;
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
import java.nio.file.Paths;
import java.util.Arrays;

import static com.jordanbunke.tdsm.util.Constants.MAX_SPRITE_EXPORT_H;
import static com.jordanbunke.tdsm.util.Constants.MAX_SPRITE_EXPORT_W;
import static com.jordanbunke.tdsm.util.Layout.*;
import static com.jordanbunke.tdsm.util.Layout.ScreenBox.*;

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
                animationLabel.followTB(),
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
                styleLabel.followTB(),
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
                Anchor.LEFT_TOP);

        mb.addAll(cse1, cse2, test);

        // BOTTOM BAR
        final MenuElement toMainButton = StaticTextButton.make(
                "< Main Menu", BOTTOM.at(0.0, 0.5).displace(4, 0),
                Anchor.LEFT_CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.MENU, main()));
        final MenuElement toConfigButton = StaticTextButton.make(
                "Configure... >", BOTTOM.at(1.0, 0.5).displace(-4, 0),
                Anchor.RIGHT_CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.CONFIGURATION, null));

        mb.addAll(toMainButton, toConfigButton);

        return mb.build();
    }

    public static Menu configuration() {
        final MenuBuilder mb = new MenuBuilder();

        // PREVIEW
        final Indicator firstSpriteInfo = Indicator.make(
                ResourceCodes.FIRST_SPRITE, PREVIEW.pos(),
                Anchor.LEFT_TOP);
        mb.add(firstSpriteInfo);

        // SEQUENCING
        final StaticLabel sequencingLabel = StaticLabel.make(
                labelPosFor(SEQUENCING.x, SEQUENCING.y), "Sequencing");
        final Indicator sequencingInfo = Indicator.make(ResourceCodes.INCLUSION,
                sequencingLabel.followIcon17(), Anchor.LEFT_TOP);

        final double TYPE_Y = 0.17, SEQUENCER_Y = 0.29;
        final StaticLabel dirLabel = StaticLabel.mini(
                SEQUENCING.at(0.22, TYPE_Y), "Directions",
                Colors.darkSystem(), Anchor.CENTRAL_TOP),
                animLabel = StaticLabel.mini(
                        SEQUENCING.at(0.675, TYPE_Y), "Animations",
                        Colors.darkSystem(), Anchor.CENTRAL_TOP);

        final DirectionSequencer dirSequencer = new DirectionSequencer(
                new Coord2D(SEQUENCING.x + BUFFER, SEQUENCING.atY(SEQUENCER_Y)));
        final AnimationSequencer animSequencer = new AnimationSequencer(
                SEQUENCING.at(0.4, SEQUENCER_Y));

        final MenuElement resetSequencingButton = IconButton.make(
                ResourceCodes.RESET, sequencingInfo.following(),
                Anchor.LEFT_TOP, () -> true, () -> {
                    Sprite.get().getStyle().resetSequencing();
                    dirSequencer.refreshScrollBox();
                    animSequencer.refreshScrollBox();
                });

        final String NO_FRAMES = "Configuration produces no frames!";
        final DynamicLabel frameCountLabel = DynamicLabel.init(
                SEQUENCING.at(0.5, 0.98),
                () -> Sprite.get().getStyle().exportsASprite()
                        ? (Sprite.get().getStyle().exportFrameCount() +
                        " animation frames") : NO_FRAMES, NO_FRAMES)
                .setAnchor(Anchor.CENTRAL_BOTTOM)
                .setMini().build();

        mb.addAll(sequencingLabel, sequencingInfo, resetSequencingButton,
                dirLabel, animLabel, dirSequencer, animSequencer, frameCountLabel);

        // LAYOUT
        final StaticLabel paddingLabel = StaticLabel.make(
                labelPosFor(LAYOUT.x, LAYOUT.y), "Padding");
        final Indicator paddingInfo = Indicator.make(ResourceCodes.PADDING,
                paddingLabel.followIcon17(), Anchor.LEFT_TOP);
        final MenuElement resetPaddingButton = IconButton.make(
                ResourceCodes.RESET, paddingInfo.following(),
                Anchor.LEFT_TOP, () -> true,
                Sprite.get().getStyle()::resetPadding);

        final double EDGES_Y = 0.1, EDGES_Y_INC = 0.08;
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

        final double SPRITE_SIZE_Y = EDGES_Y + 0.2;
        final String SPRITE_SIZE_PREFIX = "Individual sprite size: ";
        final DynamicLabel spriteSizeLabel = DynamicLabel.init(
                miniLabelPosFor(LAYOUT.x, LAYOUT.atY(SPRITE_SIZE_Y)), () -> {
                    final Bounds2D dims = Sprite.get()
                            .getStyle().getExportSpriteDims();
                    return SPRITE_SIZE_PREFIX + dims.width() + "x" + dims.height();
                }, SPRITE_SIZE_PREFIX + MAX_SPRITE_EXPORT_W + "x" +
                        MAX_SPRITE_EXPORT_H).setMini().build();

        mb.addAll(paddingLabel, paddingInfo,
                resetPaddingButton, spriteSizeLabel);

        final StaticLabel layoutLabel = StaticLabel.make(
                labelPosFor(LAYOUT.x, LAYOUT.atY(0.4)), "Sprite sheet layout");
        final Indicator layoutInfo = Indicator.make(ResourceCodes.LAYOUT,
                layoutLabel.followIcon17(), Anchor.LEFT_TOP);
        final MenuElement resetLayoutButton = IconButton.make(
                ResourceCodes.RESET, layoutInfo.following(),
                Anchor.LEFT_TOP, () -> true,
                Sprite.get().getStyle()::resetLayout);

        final double LAYOUT_INC_Y = 0.1;
        double layoutY = 0.5;
        final StaticLabel orientationLabel = StaticLabel.make(
                new Coord2D(LAYOUT.x + BUFFER, LAYOUT.atY(layoutY)),
                "Animation orientation:");
        final Dropdown orientationDropdown = Dropdown.create(
                orientationLabel.followTB(),
                EnumUtils.stream(Orientation.class)
                        .map(Orientation::format).toArray(String[]::new),
                EnumUtils.stream(Orientation.class).map(o ->
                                (Runnable) () -> Sprite.get().getStyle()
                                        .setAnimationOrientation(o))
                        .toArray(Runnable[]::new),
                () -> Sprite.get().getStyle()
                        .getAnimationOrientation().ordinal());

        layoutY += LAYOUT_INC_Y;
        final String DIR_PREFIX = "(Directions are oriented ";
        final DynamicLabel directionDimLabel = DynamicLabel.init(
                miniLabelPosFor(LAYOUT.x, LAYOUT.atY(layoutY)),
                () -> DIR_PREFIX + Sprite.get()
                        .getStyle().getAnimationOrientation()
                        .complementaryAdverb() + ")",
                DIR_PREFIX + Orientation.VERTICAL.complementaryAdverb() + ")")
                .setMini().build();

        layoutY += LAYOUT_INC_Y * 0.75;
        final String ANIMS_PER_DIM_PREFIX = "Multiple animations per ";
        final Checkbox animsPerDimCheckbox = new Checkbox(
                new Coord2D(LAYOUT.x + BUFFER, LAYOUT.atY(layoutY)),
                Anchor.LEFT_TOP,
                Sprite.get().getStyle()::isMultipleAnimsPerDim,
                Sprite.get().getStyle()::setMultipleAnimsPerDim);
        final DynamicLabel animsPerDimLabel = DynamicLabel.init(
                animsPerDimCheckbox.followMiniLabel(),
                () -> ANIMS_PER_DIM_PREFIX + Sprite.get().getStyle()
                        .getAnimationOrientation().animationDim(),
                ANIMS_PER_DIM_PREFIX + Orientation.VERTICAL.animationDim())
                .setMini().build();

        layoutY += LAYOUT_INC_Y * 0.6;
        final String SINGLE_DIM_PREFIX = "All animation frames on a single ";
        final Checkbox singleDimCheckbox = new Checkbox(
                new Coord2D(LAYOUT.x + BUFFER, LAYOUT.atY(layoutY)),
                Anchor.LEFT_TOP,
                Sprite.get().getStyle()::isSingleDim,
                Sprite.get().getStyle()::setSingleDim);
        final DynamicLabel singleDimLabel = DynamicLabel.init(
                singleDimCheckbox.followMiniLabel(),
                () -> SINGLE_DIM_PREFIX + Sprite.get().getStyle()
                        .getAnimationOrientation().animationDim(),
                SINGLE_DIM_PREFIX + Orientation.VERTICAL.animationDim())
                .setMini().build();

        layoutY += LAYOUT_INC_Y * 0.6;
        final String FRAMES_PER_DIM_PREFIX = "Frames per ";

        final DynamicLabel framesPerDimLabel = DynamicLabel.init(
                new Coord2D(LAYOUT.x + BUFFER, LAYOUT.atY(layoutY)),
                () -> FRAMES_PER_DIM_PREFIX + Sprite.get().getStyle()
                        .getAnimationOrientation().animationDim() + ":",
                FRAMES_PER_DIM_PREFIX +
                        Orientation.VERTICAL.animationDim() + ":").build();
        final DynamicTextbox framesPerDimTextbox = DynamicTextbox.init(
                framesPerDimLabel.followTB(), () -> String.valueOf(
                        Sprite.get().getStyle().getFramesPerDim()),
                s -> Sprite.get().getStyle()
                        .setFramesPerDim(Integer.parseInt(s)))
                .setMaxLength(2).setTextValidator(Textbox::validFramesPerDim)
                .build();
        final Indicator framesPerDimInfo = Indicator.make(
                ResourceCodes.FRAMES_PER_DIM,
                framesPerDimTextbox.followIcon17(), Anchor.LEFT_TOP);

        layoutY += LAYOUT_INC_Y;
        final String WRAP_PREFIX = "Wrap animations across ";
        final Checkbox wrapCheckbox = new Checkbox(
                new Coord2D(LAYOUT.x + BUFFER, LAYOUT.atY(layoutY)),
                Anchor.LEFT_TOP,
                Sprite.get().getStyle()::isWrapAnimsAcrossDims,
                Sprite.get().getStyle()::setWrapAnimsAcrossDims);
        final DynamicLabel wrapLabel = DynamicLabel.init(
                        wrapCheckbox.followMiniLabel(),
                        () -> WRAP_PREFIX + Sprite.get().getStyle()
                                .getAnimationOrientation().animationDim() + "s",
                        WRAP_PREFIX + Orientation.VERTICAL.animationDim() + "s")
                .setMini().build();

        final GatewayMenuElement notSingleRowLogic = new GatewayMenuElement(
                new MenuElementGrouping(framesPerDimLabel,
                        framesPerDimTextbox, framesPerDimInfo,
                        wrapCheckbox, wrapLabel),
                () -> !Sprite.get().getStyle().isSingleDim()),
                multAnimsPerDimLogic = new GatewayMenuElement(
                        new MenuElementGrouping(singleDimCheckbox,
                                singleDimLabel, notSingleRowLogic),
                        () -> Sprite.get().getStyle().isMultipleAnimsPerDim());

        mb.addAll(layoutLabel, layoutInfo, resetLayoutButton,
                orientationLabel, orientationDropdown, directionDimLabel,
                animsPerDimLabel, animsPerDimCheckbox, multAnimsPerDimLogic);

        // BOTTOM BAR
        final MenuElement toCustomButton = StaticTextButton.make(
                "< Customize...", BOTTOM.at(0.0, 0.5).displace(4, 0),
                Anchor.LEFT_CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.CUSTOMIZATION, null));
        final MenuElement toExportButton = StaticTextButton.make(
                "Export... >", BOTTOM.at(1.0, 0.5).displace(-4, 0),
                Anchor.RIGHT_CENTRAL,
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
                Anchor.CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.CUSTOMIZATION, null)),
                aboutButton = StaticTextButton.make(
                        "About", ButtonType.STANDARD, Alignment.CENTER,
                        buttonW, textButtonBelow(startButton),
                        Anchor.CENTRAL, () -> true,
                        () -> {} /* TODO */),
                quitButton = StaticTextButton.make(
                        "Quit", ButtonType.STANDARD, Alignment.CENTER,
                        buttonW, textButtonBelow(aboutButton),
                        Anchor.CENTRAL, () -> true,
                        TDSM::quitProgram);

        mb.addAll(startButton, aboutButton, quitButton);

        // Version and credits
        final StaticLabel programLabel = StaticLabel.make(
                canvasAt(0.5, 0.98),
                Anchor.CENTRAL_BOTTOM,
                Graphics.miniText(Colors.darkSystem())
                        .addText(TDSM.getVersion()).addLineBreak()
                        .addText("(c) 2025 Jordan Bunke").build());

        mb.add(programLabel);

        return mb.build();
    }

    public static Menu export() {
        final MenuBuilder mb = new MenuBuilder();

        // TODO

        // TODO - remove - temp
        final GameImage spriteSheet = Sprite.get().renderSpriteSheet();
        GameImageIO.writeImage(Paths.get("").resolve("output").resolve("test.png"), spriteSheet);

        return mb.build();
    }
}
