package com.jordanbunke.tdsm.menu.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;
import com.jordanbunke.tdsm.menu.IconOptionsButton;
import com.jordanbunke.tdsm.menu.StaticLabel;
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
    private final int index, expandedH;
    private boolean expanded;

    private final StaticLabel nameLabel;
    private final IconOptionsButton collapser;
    private final MenuElement[] contents;

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

        nameLabel = StaticLabel.make(labelPosFor(position), layer.name());
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

        contents = makeContents();
    }

    public static LayerElement make(
            final Coord2D position, final CustomizationLayer layer, final int index
    ) {
        // TODO

        return new LayerElement(position, layer, index, 50 /* TODO */);
    }

    private MenuElement[] makeContents() {
        final MenuBuilder mb = new MenuBuilder();

        mb.addAll(nameLabel, collapser);

        // TODO

        return mb.build().getMenuElements();
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
                : new MenuElement[] { nameLabel, collapser };
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return true;
    }

    public static void setShifting(final boolean shifting) {
        LayerElement.shifting = shifting;
    }
}
