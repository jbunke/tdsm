package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement.Anchor;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementGrouping;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.GatewayMenuElement;
import com.jordanbunke.delta_time.text.Text;
import com.jordanbunke.delta_time.text.TextBuilder;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.tdsm.ProgramInfo;
import com.jordanbunke.tdsm.TDSM;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Edge;
import com.jordanbunke.tdsm.data.Orientation;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.data.style.FromFileStyle;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.data.style.Styles;
import com.jordanbunke.tdsm.data.style.settings.StyleSetting;
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
import com.jordanbunke.tdsm.menu.scrollable.VertScrollBox;
import com.jordanbunke.tdsm.menu.text_button.Alignment;
import com.jordanbunke.tdsm.menu.text_button.ButtonType;
import com.jordanbunke.tdsm.menu.text_button.StaticTextButton;
import com.jordanbunke.tdsm.settings.update.StartupMessage;
import com.jordanbunke.tdsm.visual_misc.Playback;

import java.awt.*;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.jordanbunke.tdsm.util.Constants.*;
import static com.jordanbunke.tdsm.util.Layout.*;
import static com.jordanbunke.tdsm.util.Layout.ScreenBox.*;

public final class MenuAssembly {
    public static Menu stub() {
        return new Menu();
    }

    public static Menu customization() {
        final MenuBuilder mb = new MenuBuilder();
        final Style style = Sprite.get().getStyle();

        // PREVIEW
        if (style.settings.has()) {
            IconButton settings = IconButton.init(
                    ResourceCodes.SETTINGS, PREVIEW.at(BUFFER / 2, BUFFER / 2),
                    () -> ProgramState.set(ProgramState.MENU, styleSettings())
            ).setTooltipCode(ResourceCodes.STYLE_SETTINGS).build();
            mb.add(settings);
        }

        final StaticLabel animationLabel = StaticLabel.init(labelPosFor(
                PREVIEW.x, PREVIEW.atY(0.75)), "Animation:").build();

        final Animation[] anims = style.animations;
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

        final Style[] styles = Styles.all().filter(s -> {
            if (RuntimeSettings.isShowWIP())
                return true;

            return s.shipping();
        }).toArray(Style[]::new);
        final int co = STYLE_NAME_CUTOFF;
        final Dropdown styleDropdown = Dropdown.create(
                styleLabel.followTB(),
                Arrays.stream(styles).map(Style::name)
                        .map(s -> s.length() > co
                                ? s.substring(0, co) + "..." : s)
                        .toArray(String[]::new),
                Arrays.stream(styles)
                        .map(s -> (Runnable) () -> Sprite.get().setStyle(s))
                        .toArray(Runnable[]::new),
                () -> Arrays.stream(styles).toList()
                        .indexOf(style));
        final Indicator.Builder sib = Indicator.init(
                iconAfterTextButton(styleDropdown)).setAnchor(Anchor.LEFT_TOP);

        if (style instanceof FromFileStyle ffs)
            sib.setTooltip(ffs.infoToolTip());
        else
            sib.setTooltipCode(style.id); // TODO - temp

        final Indicator styleInfo = sib.build();

        final IconButton randomSpriteButton = IconButton.init(
                ResourceCodes.RANDOM, TOP.at(0.95, 0.5),
                style::randomize).setAnchor(Anchor.CENTRAL)
                .setTooltipCode(ResourceCodes.RANDOM_SPRITE).build(),
                loadFromJSONButton = IconButton.init(
                        ResourceCodes.LOAD_FROM_JSON,
                        randomSpriteButton.getRenderPosition(),
                        JSONHelper::loadFromJSON)
                        .setAnchor(Anchor.RIGHT_TOP).build(),
                uploadStyleButton = IconButton.init(ResourceCodes.ADD,
                                loadFromJSONButton.getRenderPosition(),
                                Styles::uploadStyleDialog)
                        .setAnchor(Anchor.RIGHT_TOP)
                        .setTooltipCode(ResourceCodes.UPLOAD_STYLE).build();

        mb.addAll(styleLabel, styleDropdown, styleInfo,
                randomSpriteButton, loadFromJSONButton, uploadStyleButton);

        // LAYER
        mb.add(CustomizationElement.make());

        // BOTTOM BAR
        final MenuElement toMainButton = StaticTextButton.make(
                "< Main Menu", BOTTOM.at(0.0, 0.5)
                        .displace(BOTTOM_BAR_BUTTON_X, 0),
                Anchor.LEFT_CENTRAL, () -> true,
                () -> ProgramState.set(ProgramState.MENU, mainMenu()));
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
        final Style style = Sprite.get().getStyle();

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
                    style.resetSequencing();
                    dirSequencer.refreshScrollBox();
                    animSequencer.refreshScrollBox();
                }).build();

        final String NO_FRAMES = "Configuration produces no frames!";
        final DynamicLabel frameCountLabel = DynamicLabel.init(
                        SEQUENCING.at(0.5, 0.98),
                        () -> style.exportsASprite()
                                ? (style.exportFrameCount() +
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
                style::resetPadding).build();

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
                    final Bounds2D dims = style.getExportSpriteDims();
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
                style::resetLayout).build();

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
                                (Runnable) () -> style.setAnimationOrientation(o))
                        .toArray(Runnable[]::new),
                () -> style.getAnimationOrientation().ordinal());

        layoutY += LAYOUT_INC_Y;
        final String DIR_PREFIX = "(Directions are oriented ";
        final DynamicLabel directionDimLabel = DynamicLabel.init(
                        miniLabelPosFor(LAYOUT.x, LAYOUT.atY(layoutY)),
                        () -> DIR_PREFIX + style.getAnimationOrientation()
                                .complementaryAdverb() + ")",
                        DIR_PREFIX + Orientation.VERTICAL.complementaryAdverb() + ")")
                .setMini().build();

        layoutY += LAYOUT_INC_Y * 0.75;
        final String ANIMS_PER_DIM_PREFIX = "Multiple animations per ";
        final Checkbox animsPerDimCheckbox = new Checkbox(
                new Coord2D(LAYOUT.x + BUFFER, LAYOUT.atY(layoutY)),
                Anchor.LEFT_TOP, style::isMultipleAnimsPerDim,
                style::setMultipleAnimsPerDim);
        final DynamicLabel animsPerDimLabel = DynamicLabel.init(
                        animsPerDimCheckbox.followMiniLabel(),
                        () -> ANIMS_PER_DIM_PREFIX +
                                style.getAnimationOrientation().animationDim(),
                        ANIMS_PER_DIM_PREFIX + Orientation.VERTICAL.animationDim())
                .setMini().build();

        layoutY += LAYOUT_INC_Y * 0.6;
        final String SINGLE_DIM_PREFIX = "All animation frames on a single ";
        final Checkbox singleDimCheckbox = new Checkbox(
                new Coord2D(LAYOUT.x + BUFFER, LAYOUT.atY(layoutY)),
                Anchor.LEFT_TOP, style::isSingleDim, style::setSingleDim);
        final DynamicLabel singleDimLabel = DynamicLabel.init(
                        singleDimCheckbox.followMiniLabel(),
                        () -> SINGLE_DIM_PREFIX + style
                                .getAnimationOrientation().animationDim(),
                        SINGLE_DIM_PREFIX + Orientation.VERTICAL.animationDim())
                .setMini().build();

        layoutY += LAYOUT_INC_Y * 0.6;
        final String FRAMES_PER_DIM_PREFIX = "Frames per ";

        final DynamicLabel framesPerDimLabel = DynamicLabel.init(
                new Coord2D(LAYOUT.x + BUFFER, LAYOUT.atY(layoutY)),
                () -> FRAMES_PER_DIM_PREFIX + style
                        .getAnimationOrientation().animationDim() + ":",
                FRAMES_PER_DIM_PREFIX +
                        Orientation.VERTICAL.animationDim() + ":").build();
        final DynamicTextbox framesPerDimTextbox = DynamicTextbox.init(
                        framesPerDimLabel.followTB(), () -> String.valueOf(
                                style.getFramesPerDim()),
                        s -> style.setFramesPerDim(Integer.parseInt(s)))
                .setMaxLength(2).setTextValidator(Textbox::validFramesPerDim)
                .build();
        final Indicator framesPerDimInfo = Indicator.make(
                ResourceCodes.FRAMES_PER_DIM,
                framesPerDimTextbox.followIcon17(), Anchor.LEFT_TOP);

        layoutY += LAYOUT_INC_Y;
        final String WRAP_PREFIX = "Wrap animations across ";
        final Checkbox wrapCheckbox = new Checkbox(
                new Coord2D(LAYOUT.x + BUFFER, LAYOUT.atY(layoutY)),
                Anchor.LEFT_TOP, style::isWrapAnimsAcrossDims,
                style::setWrapAnimsAcrossDims);
        final DynamicLabel wrapLabel = DynamicLabel.init(
                        wrapCheckbox.followMiniLabel(),
                        () -> WRAP_PREFIX + style
                                .getAnimationOrientation().animationDim() + "s",
                        WRAP_PREFIX + Orientation.VERTICAL.animationDim() + "s")
                .setMini().build();

        final GatewayMenuElement notSingleRowLogic = new GatewayMenuElement(
                new MenuElementGrouping(framesPerDimLabel,
                        framesPerDimTextbox, framesPerDimInfo,
                        wrapCheckbox, wrapLabel),
                () -> !style.isSingleDim()),
                multAnimsPerDimLogic = new GatewayMenuElement(
                        new MenuElementGrouping(singleDimCheckbox,
                                singleDimLabel, notSingleRowLogic),
                        style::isMultipleAnimsPerDim);

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
                Anchor.RIGHT_CENTRAL, style::exportsASprite,
                () -> {
                    if (style.settings.hasPreExportStep())
                        ProgramState.set(ProgramState.MENU, preExport());
                    else {
                        style.settings.resetPreExport();
                        ProgramState.set(ProgramState.MENU, export());
                    }
                });

        mb.addAll(toCustomButton, toExportButton);

        return mb.build();
    }

    private static Menu preExport() {
        final MenuBuilder mb = new MenuBuilder();
        final Style style = Sprite.get().getStyle();

        final double REL_W = 0.6;
        final int LEFT = atX((1.0 - REL_W) / 2.0), RIGHT = LEFT + atX(REL_W);

        final MenuElement backButton = StaticTextButton.make("< Configure...",
                new Coord2D(LEFT, CANVAS_H - BUFFER),
                Anchor.LEFT_BOTTOM, () -> true,
                () -> ProgramState.set(ProgramState.CONFIGURATION, null)),
                exportButton = StaticTextButton.make("Advance... >",
                        new Coord2D(RIGHT, CANVAS_H - BUFFER),
                        Anchor.RIGHT_BOTTOM, () -> true,
                        () -> ProgramState.set(ProgramState.MENU, export()));

        mb.addAll(backButton, exportButton);

        style.settings.buildPreExportMenu(mb, iconAfterTextButton(backButton));

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

    public static Menu mainMenu() {
        final MenuBuilder mb = new MenuBuilder();

        mb.add(new BackgroundElement());

        addMenuButtons(mb,
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
                        .addText(ProgramInfo.formatVersion()).addLineBreak()
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
        return openingMenu("About", ResourceCodes.ABOUT,
                Text.Orientation.CENTER, mainMenu(),
                new Pair<>("Changelog", () -> ProgramState.to(changelog())),
                new Pair<>("Roadmap", () -> ProgramState.to(roadmap())),
                new Pair<>("License", () -> ProgramState.to(license())),
                new Pair<>("Links", () -> ProgramState.to(links())));
    }

    private static Menu changelog() {
        return openingMenu("Changelog", ResourceCodes.CHANGELOG,
                Text.Orientation.LEFT, about());
    }

    private static Menu roadmap() {
        return openingMenu("Roadmap", ResourceCodes.ROADMAP,
                Text.Orientation.LEFT, about());
    }

    private static Menu license() {
        return openingMenu("License", ResourceCodes.LICENSE,
                Text.Orientation.LEFT, about(),
                new Pair<>("Summarize",
                        () -> ProgramState.to(licenseSummarized())));
    }

    private static Menu licenseSummarized() {
        return openingMenu("Summary of License", ResourceCodes.SUMMARY,
                Text.Orientation.LEFT, license());
    }

    private static Menu links() {
        return openingMenu("Links", ResourceCodes.LINKS,
                Text.Orientation.CENTER, about(),
                new Pair<>("My store",
                        () -> visitSite("https://flinkerflitzer.itch.io")),
                new Pair<>("Stipple Effect",
                        () -> visitSite("https://stipple-effect.github.io")),
                new Pair<>("Source code",
                        () -> visitSite("https://github.com/jbunke/tdsm")));
    }

    private static void visitSite(final String link) {
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (Exception ignored) {}
    }

    public static void menuTitle(
            final MenuBuilder mb, final String title
    ) {
        mb.add(StaticLabel.init(canvasAt(0.5, 0.0), title)
                .setAnchor(Anchor.CENTRAL_TOP).setTextSize(2.0).build());
    }

    public static void preExportExplanation(
            final MenuBuilder mb, final String explanation,
            final double percY, final double percH
    ) {
        menuBlurb(mb, Text.Orientation.CENTER, percY,
                (int) (CANVAS_H * percH), explanation);
    }

    private static void menuBlurb(
            final MenuBuilder mb, final String blurbCode,
            final Text.Orientation orientation, final int height
    ) {
        menuBlurb(mb, orientation, 0.2,
                height, ParserUtils.readResourceText(blurbCode));
    }

    private static void menuBlurb(
            final MenuBuilder mb, final Text.Orientation orientation,
            final double percY, final int height, final String content
    ) {
        final String[] blurb = content.split("\n");
        final TextBuilder tb = ProgramFont.MINI.getBuilder(orientation);

        for (int line = 0; line < blurb.length; line++) {
            tb.addText(blurb[line]);

            if (line + 1 < blurb.length)
                tb.addLineBreak();
        }

        final StaticLabel about = new StaticLabel(
                canvasAt(0.5, percY), Anchor.CENTRAL_TOP, tb.build().draw());
        final VertScrollBox box = new VertScrollBox(
                new Coord2D(SCREEN_BOX_EDGE, atY(percY)),
                new Bounds2D(CANVAS_W - (SCREEN_BOX_EDGE * 2), height),
                new Scrollable[] { new Scrollable(about) },
                about.getY() + about.getHeight(), 0);
        mb.add(box);
    }

    @SafeVarargs
    private static Menu openingMenu(
            final String title, final String blurbCode,
            final Text.Orientation orientation, final Menu back,
            final Pair<String, Runnable>... buttons
    ) {
        final MenuBuilder mb = new MenuBuilder();

        final int height = atY(switch (buttons.length) {
            case 3, 4 -> 0.4;
            default -> 0.4 + (0.1 * (3 - buttons.length));
        });

        mb.add(new BackgroundElement());

        if (back != null)
            addBackButton(mb, back);

        addMenuButtons(mb, buttons);
        menuBlurb(mb, blurbCode, orientation, height);

        menuTitle(mb, title);

        return mb.build();
    }

    @SafeVarargs
    private static void addMenuButtons(
            final MenuBuilder mb, final Pair<String, Runnable>... buttons
    ) {
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

    public static Menu updateInformation(final StartupMessage[] messages) {
        final MenuBuilder mb = new MenuBuilder();

        menuTitle(mb, "Important update information");

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < messages.length; i++) {
            final StartupMessage message = messages[i];

            sb.append("[ ").append(i + 1)
                    .append(" of ").append(messages.length)
                    .append(" ]\nSince v").append(message.since.toString())
                    .append(":").append("\n".repeat(2))
                    .append(ParserUtils.readResourceText(message.id()));

            if (i + 1 < messages.length)
                sb.append("\n".repeat(3));
        }

        menuBlurb(mb, Text.Orientation.LEFT, 0.2, atY(0.65), sb.toString());

        final MenuElement close = StaticTextButton.make("Got it",
                ButtonType.STANDARD, Alignment.CENTER, atX(0.3),
                new Coord2D(atX(0.5), CANVAS_H - BUFFER),
                Anchor.CENTRAL_BOTTOM, () -> true,
                () -> ProgramState.set(ProgramState.MENU, mainMenu()));
        mb.add(close);

        return mb.build();
    }

    public static Menu encounteredErrors(final String[] errors) {
        final MenuBuilder mb = new MenuBuilder();

        menuTitle(mb, "Operation encountered errors");

        final List<String> lines = new LinkedList<>();

        for (int i = 0; i < errors.length; i++) {
            final String error = errors[i];

            if (error.length() > SMALL_FONT_LINE_CHAR_LIMIT) {
                final Pair<String, String> splitError = splitLine(error);

                if (splitError == null)
                    lines.add(error);
                else {
                    lines.add(splitError.a());
                    lines.add(" ".repeat(10) + splitError.b());
                }
            }

            if (i + 1 < errors.length)
                lines.add("\n");
        }

        final String concat = lines.size() == 1 ? lines.get(0)
                : lines.stream()
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");

        menuBlurb(mb, Text.Orientation.LEFT, 0.2, atY(0.65), concat);

        final MenuElement close = StaticTextButton.make("Close",
                ButtonType.STANDARD, Alignment.CENTER, atX(0.3),
                new Coord2D(atX(0.5), CANVAS_H - BUFFER),
                Anchor.CENTRAL_BOTTOM, () -> true,
                () -> ProgramState.set(ProgramState.CUSTOMIZATION, null));
        mb.add(close);

        return mb.build();
    }

    private static Pair<String, String> splitLine(final String line) {
        for (int i = SMALL_FONT_LINE_CHAR_LIMIT; i >= 0; i--) {
            if (line.charAt(i) == ' ')
                return new Pair<>(
                        line.substring(0, i), line.substring(i + 1));
        }

        return null;
    }

    public static Menu styleSettings() {
        final MenuBuilder mb = new MenuBuilder();
        final Style style = Sprite.get().getStyle();

        menuTitle(mb, "Style Settings");

        final double REL_W = 0.6;
        final int LEFT = atX((1.0 - REL_W) / 2.0),
                RIGHT = LEFT + atX(REL_W), INC_Y = atY(1 / 9.0);

        int y = atY(0.2);

        final StyleSetting[] settings = style.settings.array();

        if (settings.length > 0) {
            final StaticLabel optionsHeader = StaticLabel.init(
                    new Coord2D(LEFT, y), "Options").build();
            mb.add(optionsHeader);

            y += INC_Y;

            for (StyleSetting setting : settings) {
                final Checkbox checkbox = new Checkbox(new Coord2D(LEFT, y),
                        Anchor.LEFT_TOP, setting::get, setting::set);
                final StaticLabel label = StaticLabel.init(
                                checkbox.followMiniLabel(),
                                setting.description).setMini().build();
                mb.addAll(checkbox, label);

                if (setting.infoCode != null) {
                    final Indicator info = Indicator.make(setting.infoCode,
                            label.follow(), Anchor.LEFT_TOP);
                    mb.add(info);
                }

                y += (int) (INC_Y * 0.5);
            }
        }

        // TODO - consider for removal
        style.buildSettingsMenu(mb, y);

        final MenuElement close = StaticTextButton.make("Close",
                new Coord2D(RIGHT, CANVAS_H - BUFFER),
                Anchor.RIGHT_BOTTOM, () -> true,
                () -> {
            style.update();
            ProgramState.set(ProgramState.CUSTOMIZATION, null);
        });
        mb.add(close);

        return mb.build();
    }

    public static Menu export() {
        final MenuBuilder mb = new MenuBuilder();

        menuTitle(mb, "Export");

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
