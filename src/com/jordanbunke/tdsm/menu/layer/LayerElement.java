package com.jordanbunke.tdsm.menu.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
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
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.ResourceCodes;

import static com.jordanbunke.tdsm.util.Layout.*;
import static com.jordanbunke.tdsm.util.Layout.ScreenBox.LAYERS;

public final class LayerElement extends MenuElementContainer {
    private static boolean shifting;

    static {
        shifting = false;
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
            // TODO
        } else if (layer instanceof ColorSelectionLayer csl) {
            final ColorSelection[] selections = csl.getSelections();

            // TODO - wrap in horizontal scroll box
            for (int i = 0; i < selections.length; i++) {
                final ColorSelectionElement cse =
                        ColorSelectionElement.of(selections[i],
                                INITIAL.displace(i * COL_SEL_LAYER_INC_X, 0),
                                Anchor.LEFT_TOP, csl.isSingle());
                mb.add(cse);
            }
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
                    layer.randomize();
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
