package com.jordanbunke.tdsm.menu.layer;

import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementGrouping;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.menu.IconButton;
import com.jordanbunke.tdsm.menu.StaticLabel;
import com.jordanbunke.tdsm.menu.sampler.Sampler;
import com.jordanbunke.tdsm.util.ResourceCodes;

import static com.jordanbunke.tdsm.util.Layout.*;

public final class ColorSelectionElement extends MenuElementGrouping {
    private ColorSelectionElement(
            final MenuElement... elements
    ) {
        super(elements);
    }

    public static ColorSelectionElement of(
            final ColorSelection selection, final Coord2D position
    ) {
        return of(selection, miniLabelPosFor(position.x, position.y),
                Anchor.LEFT_TOP, false);
    }

    public static ColorSelectionElement of(
            final ColorSelection selection, Coord2D position,
            final Anchor anchor, final boolean rootOfLayer
    ) {
        final MenuBuilder mb = new MenuBuilder();

        if (!rootOfLayer) {
            final StaticLabel nameLabel = StaticLabel
                    .init(position, selection.name).setMini()
                    .setAnchor(anchor).build();
            position = position.displace(0, COL_SEL_DROPOFF);

            mb.add(nameLabel);
        }

        final ColorSelectionButton button =
                new ColorSelectionButton(position, anchor, selection);
        final Coord2D middleOfButton = button.getRenderPosition()
                .displace(button.getWidth() / 2, button.getHeight() / 2);
        final IconButton randomizer = IconButton.init(
                ResourceCodes.RANDOM,
                middleOfButton.displace(COL_SEL_BUTTON_DIM, 0),
                () -> {
                    selection.randomize(true);
                    Sampler.get().jolt();
                }).setAnchor(Anchor.CENTRAL).build();

        mb.addAll(button, randomizer);

        return new ColorSelectionElement(mb.build().getMenuElements());
    }
}
