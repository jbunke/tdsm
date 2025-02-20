package com.jordanbunke.tdsm.menu.layer;

import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;
import com.jordanbunke.tdsm.menu.scrollable.VertScrollBox;

import java.util.Arrays;

import static com.jordanbunke.tdsm.util.Layout.BUFFER;
import static com.jordanbunke.tdsm.util.Layout.COLLAPSED_LAYER_H;
import static com.jordanbunke.tdsm.util.Layout.ScreenBox.LAYERS;

public final class CustomizationElement extends MenuElementContainer {
    private static CustomizationElement INSTANCE;

    private final LayerElement[] layerElements;
    private VertScrollBox scrollBox;
    private int offset, highest;

    private CustomizationElement() {
        super(LAYERS.at(0.0, 0.015), new Bounds2D((int) (LAYERS.width * 0.99),
                (int) (LAYERS.height * 0.975)), Anchor.LEFT_TOP, false);

        layerElements = makeLayerElements();
        offset = 0;
        highest = getY();
        refreshScrollBox();
    }

    public static CustomizationElement make() {
        INSTANCE = new CustomizationElement();
        return INSTANCE;
    }

    public static CustomizationElement get() {
        return INSTANCE;
    }

    void shiftFollowingElements(final int ref, final int deltaY) {
        for (int i = ref + 1; i < layerElements.length; i++)
            layerElements[i].incrementY(deltaY);

        highest = Arrays.stream(layerElements).map(LayerElement::getY)
                .reduce(Integer.MAX_VALUE, Math::min);

        if (highest != getY()) {
            final int resetDeltaY = getY() - highest;

            for (LayerElement layerElement : layerElements)
                layerElement.incrementY(resetDeltaY);
        }

        refreshScrollBox();
    }

    private LayerElement[] makeLayerElements() {
        final MenuBuilder mb = new MenuBuilder();

        final Coord2D INITIAL = getPosition();
        final CustomizationLayer[] layers = Sprite.get().getStyle().layers
                .get().stream().filter(CustomizationLayer::isNonTrivial)
                .toArray(CustomizationLayer[]::new);

        for (int l = 0; l < layers.length; l++) {
            final CustomizationLayer layer = layers[l];

            mb.add(LayerElement.make(INITIAL.displace(0,
                    COLLAPSED_LAYER_H * l), layer, l));
        }

        return Arrays.stream(mb.build().getMenuElements())
                .filter(m -> m instanceof LayerElement)
                .map(m -> (LayerElement) m).toArray(LayerElement[]::new);
    }

    public void refreshScrollBox() {
        scrollBox = new VertScrollBox(
                getPosition(), getDimensions(),
                Arrays.stream(layerElements).map(Scrollable::new)
                        .toArray(Scrollable[]::new),
                Arrays.stream(layerElements).map(l -> l.getY() + l.getHeight())
                        .reduce(0, Math::max) + BUFFER, offset);

        updateOffset();
    }

    @Override
    public MenuElement[] getMenuElements() {
        return new MenuElement[] { scrollBox };
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return true;
    }

    @Override
    public void update(final double deltaTime) {
        super.update(deltaTime);

        updateOffset();
    }

    private void updateOffset() {
        offset = -scrollBox.getOffset().y;
    }
}
