package com.jordanbunke.tdsm.menu.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.PlaceholderMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.ThinkingMenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.layer.AssetChoiceLayer;
import com.jordanbunke.tdsm.data.layer.ColorSelectionLayer;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;
import com.jordanbunke.tdsm.data.layer.DecisionLayer;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.menu.IconButton;
import com.jordanbunke.tdsm.menu.IconOptionsButton;
import com.jordanbunke.tdsm.menu.StaticLabel;
import com.jordanbunke.tdsm.menu.sampler.Sampler;
import com.jordanbunke.tdsm.menu.scrollable.HorzScrollBox;
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.ResourceCodes;

import java.util.Arrays;

import static com.jordanbunke.tdsm.util.Layout.*;
import static com.jordanbunke.tdsm.util.Layout.ScreenBox.LAYERS;

public final class LayerElement extends MenuElementContainer {
    private static final int HORZ_SCROLL_BOX_W;

    private static boolean shifting;

    static {
        shifting = false;

        HORZ_SCROLL_BOX_W = (int) (LAYERS.width * 0.9);
    }

    private final CustomizationLayer layer;
    private final int index;
    private int expandedH;
    private boolean expanded;

    private StaticLabel nameLabel;
    private IconOptionsButton collapser;
    private MenuElement randomizeButton;
    private MenuElement[] contents;

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

        makeElements();
        layer.setElement(this);
    }

    public static LayerElement make(
            final Coord2D position, final CustomizationLayer layer, final int index
    ) {
        return new LayerElement(position, layer, index,
                layer.calculateExpandedHeight());
    }

    public void refresh() {
        if (layer instanceof DecisionLayer)
            makeElements();
        else
            makeContents();

        final int ehWas = expandedH;
        expandedH = layer.calculateExpandedHeight();

        if (expanded && ehWas != expandedH) {
            setHeight(expandedH);
            CustomizationElement.get().shiftFollowingElements(index, expandedH - ehWas);
        }
    }

    private void makeContents() {
        final MenuBuilder mb = new MenuBuilder();

        mb.addAll(nameLabel, collapser, randomizeButton);

        addLayerContents(layer, mb);

        contents = mb.build().getMenuElements();
    }

    private void addLayerContents(
            final CustomizationLayer layer, final MenuBuilder mb
    ) {
        final Coord2D INITIAL = getPosition()
                .displace(LABEL_OFFSET_X, LAYER_CONTENT_DROPOFF);

        if (layer instanceof DecisionLayer dl) {
            addLayerContents(dl.getDecision(), mb);
        } else if (layer instanceof AssetChoiceLayer acl) {
            // MAKE CHOICE BUTTONS
            final int[] indices = acl.getIndices();
            Coord2D pos = INITIAL;

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

                pos = pos.displace(bDims.width() + ASSET_BUFFER_X, 0);
            }

            final HorzScrollBox choicesBox = new HorzScrollBox(
                    INITIAL, new Bounds2D(HORZ_SCROLL_BOX_W,
                    bDims.height() + ASSET_BUFFER_Y),
                    Arrays.stream(acbs.build().getMenuElements())
                            .map(Scrollable::new).toArray(Scrollable[]::new),
                    pos.x - ASSET_BUFFER_X, 0);
            mb.add(choicesBox);

            // MAKE COLOR SELECTION BOXES FOR EACH CHOICE AND LINK VIA THINKING ELEMENT
            final Coord2D SEL_INITIAL = INITIAL.displace(0, choicesBox.getHeight());
            final MenuElement PLACEHOLDER = new PlaceholderMenuElement();

            final MenuElement[] forIndices = new MenuElement[indices.length];

            for (int i = 0; i < forIndices.length; i++) {
                final int index = indices[i];

                if (index == AssetChoiceLayer.NONE)
                    forIndices[i] = PLACEHOLDER;
                else {
                    final ColorSelection[] selections = acl
                            .getChoiceAt(index).getColorSelections();

                    if (selections.length == 0)
                        forIndices[i] = PLACEHOLDER;
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
                        forIndices[i] = selectionBox;
                    }
                }
            }

            final ThinkingMenuElement logic = new ThinkingMenuElement(
                    () -> forIndices[acl.getChoiceIndex() +
                            (acl.noAssetChoice.valid ? 1 : 0)]);
            mb.add(logic);
        } else if (layer instanceof ColorSelectionLayer csl) {
            final ColorSelection[] selections = csl.getSelections();

            final MenuBuilder cses = new MenuBuilder();

            for (int i = 0; i < selections.length; i++)
                cses.add(ColorSelectionElement.of(selections[i],
                        INITIAL.displace(i * COL_SEL_LAYER_INC_X, 0),
                        Anchor.LEFT_TOP, csl.isSingle()));

            final HorzScrollBox selectionBox = new HorzScrollBox(
                    INITIAL, new Bounds2D(HORZ_SCROLL_BOX_W,
                    COL_SEL_SCROLL_BOX_H),
                    Arrays.stream(cses.build().getMenuElements())
                            .map(Scrollable::new).toArray(Scrollable[]::new),
                    INITIAL.x + (selections.length * COL_SEL_LAYER_INC_X) -
                            COL_SEL_LAST_X_SUB, 0);
            mb.add(selectionBox);
        }
    }

    private void makeElements() {
        nameLabel = StaticLabel.make(labelPosFor(getPosition()), layer.name());
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
        randomizeButton = IconButton.make(ResourceCodes.RANDOM, collapser.follow(),
                Anchor.LEFT_TOP, () -> true, () -> {
                    layer.randomize(true);
                    Sampler.get().jolt();
                });

        makeContents();
    }

    @Override
    public void render(final GameImage canvas) {
        super.render(canvas);

        final int x1 = LAYERS.atX(0.04), x2 = LAYERS.atX(0.96),
                y = getY() + getHeight() + BUFFER / 2;
        canvas.drawLine(Colors.lightAccent(), 1f, x1, y, x2, y);
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
        return expanded || shifting ? contents
                : new MenuElement[] { nameLabel, collapser, randomizeButton };
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return true;
    }

    public static void setShifting(final boolean shifting) {
        LayerElement.shifting = shifting;
    }
}
