package com.jordanbunke.tdsm.menu.layer;

import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;

import static com.jordanbunke.tdsm.util.Layout.ScreenBox.LAYERS;

// TODO
public final class CustomizationMenuElement extends MenuElementContainer {
    private static CustomizationMenuElement INSTANCE;

    // TODO - scroll box

    private CustomizationMenuElement() {
        super(LAYERS.pos(), LAYERS.dims(), Anchor.LEFT_TOP, false);

        // TODO
    }

    public static CustomizationMenuElement make() {
        INSTANCE = new CustomizationMenuElement();
        return INSTANCE;
    }

    @Override
    public MenuElement[] getMenuElements() {
        // TODO
        return new MenuElement[0];
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return true;
    }
}
