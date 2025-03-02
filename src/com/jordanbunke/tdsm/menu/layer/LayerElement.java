package com.jordanbunke.tdsm.menu.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.GatewayMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.ThinkingMenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.layer.*;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.menu.*;
import com.jordanbunke.tdsm.menu.sampler.Sampler;
import com.jordanbunke.tdsm.menu.scrollable.HorzScrollBox;
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.ResourceCodes;

import java.util.Arrays;
import java.util.stream.IntStream;

import static com.jordanbunke.tdsm.util.Layout.*;
import static com.jordanbunke.tdsm.util.Layout.ScreenBox.LAYERS;

public final class LayerElement extends MenuElementContainer {
    private static final int HORZ_SCROLL_BOX_W;

    static {
        HORZ_SCROLL_BOX_W = (int) (LAYERS.width * 0.9);
    }

    private final CustomizationLayer layer;
    private final int index;
    private int expandedH;
    private boolean expanded;

    private final StaticLabel nameLabel;
    private final IconOptionsButton collapser, lockGate;
    private final MenuElement randomizeButton, rLogicContainer;
    private final MenuElement[] header;
    private MenuElement[] contents, all;

    private LayerElement(
            final Coord2D position, final CustomizationLayer layer,
            final int index, final int expandedH
    ) {
        super(position, new Bounds2D((int) (LAYERS.width * 0.99),
                COLLAPSED_LAYER_H), Anchor.LEFT_TOP, false);

        this.layer = layer;
        this.index = index;

        this.expandedH = expandedH;
        expanded = false;

        if (layer instanceof ManualRefreshLayer mrl)
            mrl.setElement(this);

        nameLabel = StaticLabel.init(labelPosFor(getPosition()),
                layer.name()).build();
        collapser = IconOptionsButton.init(nameLabel.followIcon17()
                        .displace(BUFFER / 2, 0))
                .setCodes(ResourceCodes.COLLAPSE, ResourceCodes.EXPAND)
                .setIndexFunc(() -> expanded ? 0 : 1)
                .setGlobal(() -> {
                    if (expanded)
                        collapse();
                    else
                        expand();
                }).build();
        lockGate = IconOptionsButton.init(collapser.follow())
                .setCodes(ResourceCodes.LOCKED, ResourceCodes.UNLOCKED)
                .setIndexFunc(() -> layer.isLocked() ? 0 : 1)
                .setGlobal(() -> {
                    if (layer.isLocked())
                        layer.unlock();
                    else
                        layer.lock();
                }).build();
        randomizeButton = IconButton.init(
                ResourceCodes.RANDOM, lockGate.follow(), () -> {
                    layer.randomize(true);
                    Sampler.get().jolt();
                }).build();
        rLogicContainer = new GatewayMenuElement(
                randomizeButton, () -> !layer.isLocked());

        header = new MenuElement[] {
                nameLabel, collapser, lockGate, rLogicContainer
        };
        makeContents();
    }

    public static LayerElement make(
            final Coord2D position, final CustomizationLayer layer, final int index
    ) {
        return new LayerElement(position, layer, index,
                layer.calculateExpandedHeight());
    }

    public void refresh() {
        makeContents();

        final int ehWas = expandedH;
        expandedH = layer.calculateExpandedHeight();

        if (expanded && ehWas != expandedH) {
            setHeight(expandedH);
            CustomizationElement.get().shiftFollowingElements(index, expandedH - ehWas);
        }
    }

    private void makeContents() {
        final MenuBuilder cb = new MenuBuilder(), ab = new MenuBuilder();

        cb.addAll(nameLabel, collapser, lockGate, rLogicContainer);
        ab.addAll(nameLabel, collapser, lockGate, randomizeButton);

        final Coord2D initial = getPosition()
                .displace(LABEL_OFFSET_X, LAYER_CONTENT_DROPOFF);
        addLayerContents(layer, initial, cb, ab);

        contents = cb.build().getMenuElements();
        all = ab.build().getMenuElements();
    }

    private void addMathLayerContents(
            final MathLayer ml, final Coord2D initial,
            final MenuBuilder cb, final MenuBuilder ab
    ) {
        Coord2D pos = initial.displace(0, MATH_LAYER_OFFSET_Y);

        final IconButton decrement = IconButton.init(
                        ResourceCodes.EXPAND, pos, ml::decrement)
                .setTooltipCode(ResourceCodes.DECREMENT).build(),
                increment = IconButton.init(
                                ResourceCodes.COLLAPSE,
                                follow(decrement), ml::increment)
                        .setTooltipCode(ResourceCodes.INCREMENT).build();
        final GatewayMenuElement decLogic =
                new GatewayMenuElement(decrement, () -> !ml.isMin()),
                incLogic = new GatewayMenuElement(increment,
                        () -> !ml.isMax());

        final DynamicLabel formattedValue = DynamicLabel.init(
                        follow(increment).displace(POST_LABEL_BUFFER_X, 0),
                        ml::getFormattedValue, "X".repeat(30))
                .setMini().build();

        cb.addAll(decLogic, incLogic, formattedValue);
        ab.addAll(decrement, increment, formattedValue);
    }

    private void addChoiceLayerContents(
            final ChoiceLayer cl, final Coord2D initial,
            final MenuBuilder cb, final MenuBuilder ab
    ) {
        final int numChoices = cl.getNumChoices();
        final Dropdown choices = Dropdown.create(
                initial, IntStream.range(0, numChoices)
                        .mapToObj(cl::getChoiceAt).toArray(String[]::new),
                IntStream.range(0, numChoices).mapToObj(i ->
                                (Runnable) () -> cl.choose(i))
                        .toArray(Runnable[]::new), cl::getSelection);

        cb.add(choices);
        ab.add(choices);
    }

    private void addColorSelectionLayerContents(
            final ColorSelectionLayer csl, final Coord2D initial,
            final MenuBuilder cb, final MenuBuilder ab
    ) {
        final ColorSelection[] selections = csl.getSelections();

        final MenuBuilder cses = new MenuBuilder();

        for (int i = 0; i < selections.length; i++)
            cses.add(ColorSelectionElement.of(selections[i],
                    initial.displace(i * COL_SEL_LAYER_INC_X, 0),
                    Anchor.LEFT_TOP, csl.isSingle()));

        final HorzScrollBox selectionBox = new HorzScrollBox(
                initial, new Bounds2D(HORZ_SCROLL_BOX_W,
                COL_SEL_SCROLL_BOX_H),
                Arrays.stream(cses.build().getMenuElements())
                        .map(Scrollable::new).toArray(Scrollable[]::new),
                initial.x + (selections.length * COL_SEL_LAYER_INC_X) -
                        COL_SEL_LAST_X_SUB, 0);
        cb.add(selectionBox);
        ab.add(selectionBox);
    }

    private void addAssetChoiceLayerContents(
            final AssetChoiceLayer acl, final Coord2D initial,
            final MenuBuilder cb, final MenuBuilder ab
    ) {
        // MAKE CHOICE BUTTONS
        final int[] indices = acl.getIndices();
        Coord2D pos = initial;

        final MenuBuilder acbs = new MenuBuilder();

        // Dims pre-processing
        final Bounds2D bDims = new Bounds2D(
                acl.dims.width() + INNER_ASSET_BUFFER_X,
                acl.dims.height() + INNER_ASSET_BUFFER_Y);

        for (int index : indices) {
            final AssetChoiceButton acb = index == AssetChoiceLayer.NONE
                    ? AssetChoiceButton.none(pos, bDims, acl)
                    : AssetChoiceButton.ofChoice(pos, bDims, acl, index);
            acbs.add(acb);

            pos = pos.displaceX(bDims.width() + ASSET_BUFFER_X);
        }

        final HorzScrollBox choicesBox = new HorzScrollBox(
                initial, new Bounds2D(HORZ_SCROLL_BOX_W,
                bDims.height() + ASSET_BUFFER_Y),
                Arrays.stream(acbs.build().getMenuElements())
                        .map(Scrollable::new).toArray(Scrollable[]::new),
                pos.x - ASSET_BUFFER_X, 0);
        cb.add(choicesBox);
        ab.add(choicesBox);

        if (acl.maxSelectors() == 0)
            return;

        // MAKE COLOR SELECTION BOXES FOR EACH CHOICE AND LINK VIA THINKING ELEMENT
        final Coord2D SEL_INITIAL = initial.displace(0, choicesBox.getHeight());
        final StaticLabel noColSels = StaticLabel.init(
                new Coord2D(LAYERS.atX(0.5),
                        SEL_INITIAL.y + COL_SEL_BUTTON_DIM / 2),
                "This choice has no color selections.")
                .setAnchor(Anchor.CENTRAL_TOP).setMini().build();
        ab.add(noColSels);

        final MenuElement[] forIndices = new MenuElement[indices.length];

        for (int i = 0; i < forIndices.length; i++) {
            final int index = indices[i];

            if (index == AssetChoiceLayer.NONE)
                forIndices[i] = noColSels;
            else {
                final ColorSelection[] selections = acl
                        .getChoiceAt(index).getColorSelections();

                if (selections.length == 0)
                    forIndices[i] = noColSels;
                else {
                    final MenuBuilder cses = new MenuBuilder();

                    for (int j = 0; j < selections.length; j++)
                        cses.add(ColorSelectionElement.of(selections[j],
                                SEL_INITIAL.displace(j * COL_SEL_LAYER_INC_X, 0),
                                Anchor.LEFT_TOP, false));

                    final HorzScrollBox selectionBox = new HorzScrollBox(
                            SEL_INITIAL, new Bounds2D(HORZ_SCROLL_BOX_W,
                            COL_SEL_SCROLL_BOX_H),
                            Arrays.stream(cses.build().getMenuElements())
                                    .map(Scrollable::new)
                                    .toArray(Scrollable[]::new),
                            SEL_INITIAL.x + (selections.length *
                                    COL_SEL_LAYER_INC_X) -
                                    COL_SEL_LAST_X_SUB, 0);
                    ab.add(selectionBox);
                    forIndices[i] = selectionBox;
                }
            }
        }

        final ThinkingMenuElement logic = new ThinkingMenuElement(
                () -> forIndices[acl.getChoiceIndex() +
                        (acl.noAssetChoice.valid ? 1 : 0)]);
        cb.add(logic);
    }

    private void addLayerContents(
            final CustomizationLayer layer, final Coord2D initial,
            final MenuBuilder cb, final MenuBuilder ab
    ) {
        if (layer instanceof DecisionLayer dl)
            addLayerContents(dl.getDecision(), initial, cb, ab);
        else if (layer instanceof GroupLayer gl) {
            Coord2D pos = initial;
            final CustomizationLayer[] layers = gl.all()
                    .filter(CustomizationLayer::isNonTrivial)
                    .toArray(CustomizationLayer[]::new);

            for (CustomizationLayer child : layers) {
                final StaticLabel subheading = StaticLabel
                        .init(pos, child.name()).setMini().build();
                cb.add(subheading);
                ab.add(subheading);
                pos = pos.displace(0, SUBHEADING_INC_Y);

                addLayerContents(child, pos, cb, ab);

                final int inc = child.calculateExpandedHeight();
                pos = pos.displace(0, inc);
            }
        }
        else if (layer instanceof MathLayer ml)
            addMathLayerContents(ml, initial, cb, ab);
        else if (layer instanceof ChoiceLayer cl)
            addChoiceLayerContents(cl, initial, cb, ab);
        else if (layer instanceof AssetChoiceLayer acl)
            addAssetChoiceLayerContents(acl, initial, cb, ab);
        else if (layer instanceof ColorSelectionLayer csl)
            addColorSelectionLayerContents(csl, initial, cb, ab);
    }

    @Override
    public void update(final double deltaTime) {
        for (MenuElement menuElement : getRelevantElements())
            menuElement.update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        final int x1 = LAYERS.atX(0.04), x2 = LAYERS.atX(0.96),
                y = getY() + getHeight() + BUFFER / 2;
        canvas.drawLine(Colors.lightAccent(), 1f, x1, y, x2, y);

        final MenuElement[] renderOrder =
                MenuElement.sortForRender(getRelevantElements());

        for (MenuElement element : renderOrder)
            element.render(canvas);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        for (MenuElement menuElement : getRelevantElements())
            menuElement.process(eventLogger);
    }

    private void expand() {
        final int deltaY = expandedH - COLLAPSED_LAYER_H;
        setHeight(expandedH);
        CustomizationElement.get().shiftFollowingElements(index, deltaY);

        expanded = true;
    }

    private void collapse() {
        final int deltaY = COLLAPSED_LAYER_H - expandedH;
        setHeight(COLLAPSED_LAYER_H);
        CustomizationElement.get().shiftFollowingElements(index, deltaY);

        expanded = false;
    }

    @Override
    public MenuElement[] getMenuElements() {
        return getAllElements();
    }

    private MenuElement[] getAllElements() {
        return all;
    }

    private MenuElement[] getRelevantElements() {
        return expanded ? contents : header;
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return true;
    }
}
