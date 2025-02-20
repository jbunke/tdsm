package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement.Anchor;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementGrouping;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.GatewayMenuElement;
import com.jordanbunke.delta_time.text.Text;
import com.jordanbunke.delta_time.text.TextBuilder;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.tdsm.TDSM;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Edge;
import com.jordanbunke.tdsm.data.Orientation;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.data.style.Styles;
import com.jordanbunke.tdsm.flow.ProgramState;
import com.jordanbunke.tdsm.io.Export;
import com.jordanbunke.tdsm.menu.Checkbox;
import com.jordanbunke.tdsm.menu.*;
import com.jordanbunke.tdsm.menu.anim.LoadingAnimation;
import com.jordanbunke.tdsm.menu.anim.Logo;
import com.jordanbunke.tdsm.menu.config.AnimationSequencer;
import com.jordanbunke.tdsm.menu.config.DirectionSequencer;
import com.jordanbunke.tdsm.menu.config.PaddingTextbox;
import com.jordanbunke.tdsm.menu.layer.CustomizationElement;
import com.jordanbunke.tdsm.menu.sampler.Sampler;
import com.jordanbunke.tdsm.menu.text_button.Alignment;
import com.jordanbunke.tdsm.menu.text_button.ButtonType;
import com.jordanbunke.tdsm.menu.text_button.StaticTextButton;
import com.jordanbunke.tdsm.visual_misc.Playback;

import java.awt.*;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;

import static com.jordanbunke.tdsm.util.Constants.*;
import static com.jordanbunke.tdsm.util.Layout.*;
import static com.jordanbunke.tdsm.util.Layout.ScreenBox.*;

public final class MenuAssembly {
    public static Menu stub() {
        return new Menu();
    }

    public static Menu customization() {
        final MenuBuilder mb = new MenuBuilder();

        // PREVIEW
        final StaticLabel animationLabel = StaticLabel.init(labelPosFor(
                PREVIEW.x, PREVIEW.atY(0.75)), "Animation:").build();

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
        final IconButton turnCWButton = IconButton.init(
                ResourceCodes.TURN_CLOCKWISE,
                PREVIEW.at(0.5 - DIVERGENCE, ARROW_HEIGHT),
                () -> Sprite.get().turn(true))
                .setAnchor(Anchor.CENTRAL).build(),
                turnCCWButton = IconButton.init(
                        ResourceCodes.TURN_COUNTERCLOCKWISE,
                        PREVIEW.at(0.5 + DIVERGENCE, ARROW_HEIGHT),
                        () -> Sprite.get().turn(false))
                        .setAnchor(Anchor.CENTRAL).build();

        mb.addAll(animationLabel, animationDropdown,
                turnCWButton, turnCCWButton);

        // SAMPLER
        final Veil veil = new Veil(SAMPLER.pos(), SAMPLER.dims(),
                Sampler.get(), () -> Sampler.get().isActive());

        mb.addAll(veil);

        // TOP BAR
        final StaticLabel styleLabel = StaticLabel.init(
                labelPosFor(TOP.pos()), "Sprite style:").build();

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
                        .indexOf(Sprite.get().getStyle()));
        final Indicator styleInfo = Indicator.make(
                ResourceCodes.CHANGE_STYLE, styleDropdown.followIcon17(),
                Anchor.LEFT_TOP);

        final IconButton randomSpriteButton = IconButton.init(
                ResourceCodes.RANDOM, TOP.at(0.95, 0.5),
                Sprite.get().getStyle()::randomize)
                .setAnchor(Anchor.CENTRAL)
                .setTooltipCode(ResourceCodes.RANDOM_SPRITE).build();

        mb.addAll(styleLabel, styleDropdown, styleInfo, randomSpriteButton);

        // LAYER
        mb.add(CustomizationElement.make());

        // BOTTOM BAR
        final MenuElement toMainButton = StaticTextButton.make(
                "< Main Menu", BOTTOM.at(0.0, 0.5)
                        .displace(BOTTOM_BAR_BUTTON_X, 0),
                Anchor.LEFT_CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.MENU, main()));
        final MenuElement toConfigButton = StaticTextButton.make(
                "Configure... >", BOTTOM.at(1.0, 0.5)
                        .displace(-BOTTOM_BAR_BUTTON_X, 0),
                Anchor.RIGHT_CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.CONFIGURATION, null));

        mb.addAll(toMainButton, toConfigButton);

        return mb.build();
    }

    public static Menu configuration() {
        final MenuBuilder mb = new MenuBuilder();

        // PREVIEW
        final Indicator firstSpriteInfo = Indicator.make(
                ResourceCodes.FIRST_SPRITE, PREVIEW.at(BUFFER / 2, BUFFER / 2),
                Anchor.LEFT_TOP);
        mb.add(firstSpriteInfo);

        // SEQUENCING
        final StaticLabel sequencingLabel = StaticLabel.init(
                labelPosFor(SEQUENCING.pos()), "Sequencing").build();
        final Indicator sequencingInfo = Indicator.make(ResourceCodes.INCLUSION,
                sequencingLabel.followIcon17(), Anchor.LEFT_TOP);

        final double TYPE_Y = 0.17, SEQUENCER_Y = 0.29;
        final StaticLabel dirLabel = StaticLabel.init(
                SEQUENCING.at(0.22, TYPE_Y), "Directions")
                .setAnchor(Anchor.CENTRAL_TOP).setMini().build(),
                animLabel = StaticLabel.init(
                        SEQUENCING.at(0.675, TYPE_Y), "Animations")
                        .setAnchor(Anchor.CENTRAL_TOP).setMini().build();

        final DirectionSequencer dirSequencer = new DirectionSequencer(
                new Coord2D(SEQUENCING.x + BUFFER, SEQUENCING.atY(SEQUENCER_Y)));
        final AnimationSequencer animSequencer = new AnimationSequencer(
                SEQUENCING.at(0.4, SEQUENCER_Y));

        final IconButton resetSequencingButton = IconButton.init(
                ResourceCodes.RESET, sequencingInfo.following(),
                () -> {
                    Sprite.get().getStyle().resetSequencing();
                    dirSequencer.refreshScrollBox();
                    animSequencer.refreshScrollBox();
                }).build();

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
        final StaticLabel paddingLabel = StaticLabel.init(
                labelPosFor(LAYOUT.pos()), "Padding").build();
        final Indicator paddingInfo = Indicator.make(ResourceCodes.PADDING,
                paddingLabel.followIcon17(), Anchor.LEFT_TOP);
        final IconButton resetPaddingButton = IconButton.init(
                ResourceCodes.RESET, paddingInfo.following(),
                Sprite.get().getStyle()::resetPadding).build();

        final double EDGES_Y = 0.1, EDGES_Y_INC = 0.08;
        EnumUtils.stream(Edge.class).forEach(e -> {
            final Coord2D edgeLabelPos = LAYOUT.at(
                            0.0 + (e.ordinal() / 2 == 0 ? 0.0 : 0.5),
                            EDGES_Y + (e.ordinal() % 2 == 0 ? 0.0 : EDGES_Y_INC))
                    .displace(BUFFER, 0);
            final StaticLabel edgeLabel = StaticLabel.init(edgeLabelPos,
                    StringUtils.nameFromID(e.name().toLowerCase()) + ":")
                    .build();
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

        final StaticLabel layoutLabel = StaticLabel.init(
                labelPosFor(LAYOUT.x, LAYOUT.atY(0.4)),
                "Sprite sheet layout").build();
        final Indicator layoutInfo = Indicator.make(ResourceCodes.LAYOUT,
                layoutLabel.followIcon17(), Anchor.LEFT_TOP);
        final IconButton resetLayoutButton = IconButton.init(
                ResourceCodes.RESET, layoutInfo.following(),
                Sprite.get().getStyle()::resetLayout).build();

        final double LAYOUT_INC_Y = 0.1;
        double layoutY = 0.5;
        final StaticLabel orientationLabel = StaticLabel.init(
                new Coord2D(LAYOUT.x + BUFFER, LAYOUT.atY(layoutY)),
                "Animation orientation:").build();
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
                "< Edit...", BOTTOM.at(0.0, 0.5)
                        .displace(BOTTOM_BAR_BUTTON_X, 0),
                Anchor.LEFT_CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.CUSTOMIZATION, null));
        final MenuElement toExportButton = StaticTextButton.make(
                "Export... >", BOTTOM.at(1.0, 0.5)
                        .displace(-BOTTOM_BAR_BUTTON_X, 0),
                Anchor.RIGHT_CENTRAL,
                () -> Sprite.get().getStyle().exportsASprite(),
                () -> ProgramState.set(ProgramState.MENU, export()));

        mb.addAll(toCustomButton, toExportButton);

        return mb.build();
    }

    private static void loadCustomization() {
        ProgramState.to(loading());

        final Thread backgroundThread = new Thread(() -> {
            Sprite.tap();
            ProgramState.set(ProgramState.CUSTOMIZATION, null);
        }, "Loader");
        backgroundThread.start();
    }

    private static Menu loading() {
        final MenuBuilder mb = new MenuBuilder();

        mb.add(new BackgroundElement());

        final LoadingAnimation loadingAnim = LoadingAnimation.make(
                canvasAt(0.5, 0.5), Anchor.CENTRAL);
        mb.add(loadingAnim);

        return mb.build();
    }

    public static Menu main() {
        final MenuBuilder mb = new MenuBuilder();

        openingMenu(mb,
                new Pair<>("Start editing",
                        MenuAssembly::loadCustomization),
                new Pair<>("About", () -> ProgramState.to(about())),
                new Pair<>("Quit", TDSM::quitProgram));

        // Logo
        final Logo logo = Logo.make(canvasAt(0.5, 0.1), Anchor.CENTRAL_TOP);
        mb.add(logo);

        final Blinker blinker = Blinker.make(
                canvasAt(0.5, 0.5), Anchor.CENTRAL);
        mb.add(blinker);

        // Version and credits
        final StaticLabel programLabel = new StaticLabel(
                canvasAt(0.5, 0.98),
                Anchor.CENTRAL_BOTTOM,
                Graphics.miniText(Colors.darkSystem())
                        .addText(TDSM.getVersion()).addLineBreak()
                        .addText("(c) 2025 Jordan Bunke").build().draw());

        mb.add(programLabel);

        return mb.build();
    }

    private static void addBackButton(final MenuBuilder mb, final Menu destination) {
        final IconButton back = IconButton.init(ResourceCodes.BACK,
                new Coord2D(BUFFER / 2, BUFFER / 2),
                () -> ProgramState.to(destination)).build();
        mb.add(back);
    }

    private static Menu about() {
        final MenuBuilder mb = new MenuBuilder();

        addBackButton(mb, main());

        openingMenu(mb,
                new Pair<>("Changelog", () -> ProgramState.to(changelog())),
                new Pair<>("Roadmap", () -> ProgramState.to(roadmap())),
                new Pair<>("Links", () -> ProgramState.to(links())));

        menuTitle(mb, "About");
        menuBlurb(mb, ResourceCodes.ABOUT, Text.Orientation.CENTER);

        return mb.build();
    }

    private static Menu changelog() {
        final MenuBuilder mb = new MenuBuilder();

        mb.add(new BackgroundElement());
        addBackButton(mb, about());

        menuTitle(mb, "Changelog");
        menuBlurb(mb, ResourceCodes.CHANGELOG, Text.Orientation.LEFT);

        return mb.build();
    }

    private static Menu roadmap() {
        final MenuBuilder mb = new MenuBuilder();

        mb.add(new BackgroundElement());
        addBackButton(mb, about());

        menuTitle(mb, "Roadmap");
        menuBlurb(mb, ResourceCodes.ROADMAP, Text.Orientation.LEFT);

        return mb.build();
    }

    private static Menu links() {
        final MenuBuilder mb = new MenuBuilder();

        addBackButton(mb, about());

        openingMenu(mb,
                new Pair<>("My store",
                        () -> visitSite("https://flinkerflitzer.itch.io")),
                new Pair<>("Stipple Effect",
                        () -> visitSite("https://stipple-effect.github.io")),
                new Pair<>("Source code",
                        () -> visitSite("https://github.com/jbunke/tdsm")));

        menuTitle(mb, "Links");
        menuBlurb(mb, ResourceCodes.LINKS, Text.Orientation.CENTER);

        return mb.build();
    }

    private static void visitSite(final String link) {
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (Exception ignored) {}
    }

    private static void menuTitle(
            final MenuBuilder mb, final String title
    ) {
        mb.add(StaticLabel.init(canvasAt(0.5, 0.0), title)
                .setAnchor(Anchor.CENTRAL_TOP).setTextSize(2.0).build());
    }

    private static void menuBlurb(
            final MenuBuilder mb, final String blurbCode,
            final Text.Orientation orientation
    ) {
        final String[] blurb = ParserUtils.readTooltip(blurbCode).split("\n");
        final TextBuilder tb = ProgramFont.MINI.getBuilder(orientation);

        for (int line = 0; line < blurb.length; line++) {
            tb.addText(blurb[line]);

            if (line + 1 < blurb.length)
                tb.addLineBreak();
        }

        final StaticLabel about = new StaticLabel(
                canvasAt(0.5, 0.2), Anchor.CENTRAL_TOP, tb.build().draw());
        mb.add(about);
    }

    @SafeVarargs
    private static void openingMenu(
            final MenuBuilder mb, final Pair<String, Runnable>... buttons
    ) {
        mb.add(new BackgroundElement());

        Coord2D buttonPos = canvasAt(0.5, 37 / 60.0);

        if (buttons.length < 3)
            buttonPos = buttonPos.displace(0,
                    TEXT_BUTTON_INC_Y * (4 - buttons.length));

        final int BUTTON_W = atX(0.3);

        for (Pair<String, Runnable> button : buttons) {
            final MenuElement b = StaticTextButton.make(
                    button.a(), ButtonType.STANDARD, Alignment.CENTER,
                    BUTTON_W, buttonPos, Anchor.CENTRAL_TOP, () -> true,
                    button.b());
            mb.add(b);
            buttonPos = buttonPos.displace(0, TEXT_BUTTON_INC_Y);
        }
    }

    public static Menu export() {
        final MenuBuilder mb = new MenuBuilder();

        final double REL_W = 0.6;
        final int LEFT = atX((1.0 - REL_W) / 2.0), RIGHT = LEFT + atX(REL_W),
                INC_Y = atY(1 / 9.0);

        int y = atY(0.2);

        final StaticLabel folderLabel = StaticLabel.init(
                new Coord2D(LEFT, y), "Folder:").build();
        final MenuElement folderButton = StaticTextButton.make("Choose",
                ButtonType.STANDARD, Alignment.CENTER, folderLabel.followTB(),
                Anchor.LEFT_TOP, () -> true, Export.get()::chooseFolder);

        y += INC_Y;
        final DynamicLabel folderPathLabel = DynamicLabel.init(
                new Coord2D(LEFT, y), () -> {
                    final Path folder = Export.get().getFolder();
                    return folder == null ? "No folder selected!"
                            : folder.toString();
                    }, "X".repeat(50))
                .setMini().build();

        y += INC_Y;
        final StaticLabel fileNameLabel = StaticLabel.init(
                new Coord2D(LEFT, y), "File name:").build();
        final Coord2D fntbPos = fileNameLabel.followTB();
        final DynamicTextbox fileNameTextbox = DynamicTextbox.init(
                fntbPos, Export.get()::getFileName, Export.get()::setFileName)
                .setTextValidator(Export::validFileName)
                .setMaxLength(FILE_NAME_MAX_LENGTH)
                .setWidth((RIGHT - STANDARD_ICON_DIM) - fntbPos.x).build();
        final Indicator fileNameInfo = Indicator.make(
                ResourceCodes.FILE_NAME,
                fileNameTextbox.followIcon17(), Anchor.LEFT_TOP);

        y += INC_Y;
        final StaticLabel overwriteLabel = StaticLabel.init(
                new Coord2D(LEFT, y),
                "Exporting will overwrite at least one file!")
                .setMini().build();
        final GatewayMenuElement overwriteLogic = new GatewayMenuElement(
                overwriteLabel, Export.get()::wouldOverwrite);

        y += (int) (INC_Y * 0.8);
        final Checkbox jsonCheckbox = new Checkbox(
                new Coord2D(LEFT, y), Anchor.LEFT_TOP,
                Export.get()::isExportJSON, Export.get()::setExportJSON);
        final StaticLabel jsonLabel = StaticLabel.init(
                jsonCheckbox.followMiniLabel(), "Export JSON")
                .setMini().build();

        y += (int) (INC_Y * 0.5);
        final Checkbox stipCheckbox = new Checkbox(
                new Coord2D(LEFT, y), Anchor.LEFT_TOP,
                Export.get()::isExportStip, Export.get()::setExportStip);
        final StaticLabel stipLabel = StaticLabel.init(
                stipCheckbox.followMiniLabel(),
                "Export Stipple Effect file (.stip)").setMini().build();
        final Indicator stipInfo = Indicator.make(
                ResourceCodes.EXPORT_STIP, stipLabel.follow(), Anchor.LEFT_TOP);

        final MenuElement backButton = StaticTextButton.make("< Configure...",
                new Coord2D(LEFT, CANVAS_H - BUFFER),
                Anchor.LEFT_BOTTOM, () -> true,
                () -> ProgramState.set(ProgramState.CONFIGURATION, null)),
                exportButton = StaticTextButton.make("Export",
                        new Coord2D(RIGHT, CANVAS_H - BUFFER),
                        Anchor.RIGHT_BOTTOM, Export.get()::canExport,
                        Export.get()::export);

        mb.addAll(folderLabel, folderButton, folderPathLabel,
                fileNameLabel, fileNameTextbox, fileNameInfo, overwriteLogic,
                jsonCheckbox, jsonLabel, stipLabel, stipCheckbox, stipInfo,
                backButton, exportButton);

        return mb.build();
    }
}
