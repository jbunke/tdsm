package com.jordanbunke.tdsm.menu.pre_export;

import com.jordanbunke.color_proc.ColorAlgo;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.menu.scrollable.HorzScrollBox;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.jordanbunke.tdsm.util.Layout.ASSET_BUFFER_X;
import static com.jordanbunke.tdsm.util.Layout.COL_SEL_BUTTON_DIM;

public final class ReplacementOptions extends MenuElementContainer {
    private final Map<Color, Color> replacementMap;
    private final Supplier<Color> selectGetter;
    private final Set<Color> colorPool;

    private Color selectedColor;
    private MenuElement[] contents;

    public ReplacementOptions(
            final Coord2D position, final Bounds2D dimensions,
            final Map<Color, Color> replacementMap,
            final Set<Color> colorPool, final Supplier<Color> selectGetter
    ) {
        super(position, dimensions, Anchor.LEFT_TOP, true);

        this.replacementMap = replacementMap;
        this.colorPool = colorPool;
        this.selectGetter = selectGetter;

        selectedColor = selectGetter.get();

        build();
    }

    private Color[] replacementOptions() {
        final Set<Color> options = colorPool.stream()
                .map(c -> replacementMap.getOrDefault(c, c))
                .collect(Collectors.toSet());
        options.remove(selectedColor);

        return options.stream().sorted(
                Comparator.comparingDouble(c -> ColorAlgo.diffRGB(c, selectedColor))
                ).toArray(Color[]::new);
    }

    private void build() {
        if (selectedColor == null) {
            contents = new MenuElement[] {};
            return;
        }

        final Color[] options = replacementOptions();

        final MenuBuilder robs = new MenuBuilder();
        Coord2D pos = getPosition();
        final int INC_X = COL_SEL_BUTTON_DIM + ASSET_BUFFER_X;

        final ReplacementOptionButton none = ReplacementOptionButton
                .none(pos, selectedColor, replacementMap);
        robs.add(none);

        pos = pos.displaceX(INC_X);

        for (Color option : options) {
            final ReplacementOptionButton rob = ReplacementOptionButton
                    .make(pos, selectedColor, option, replacementMap);
            robs.add(rob);

            pos = pos.displaceX(INC_X);
        }

        contents = new MenuElement[] {
                new HorzScrollBox(getPosition(), getDimensions(),
                        Arrays.stream(robs.build().getMenuElements())
                                .map(Scrollable::new)
                                .toArray(Scrollable[]::new),
                        pos.x - ASSET_BUFFER_X, 0)
        };
    }

    @Override
    public void update(final double deltaTime) {
        final Color retrieved = selectGetter.get();

        if ((selectedColor == null && retrieved != null) ||
                (selectedColor != null && !selectedColor.equals(retrieved))) {
            selectedColor = retrieved;
            build();
        } else {
            super.update(deltaTime);
        }
    }

    @Override
    public MenuElement[] getMenuElements() {
        return contents;
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return true;
    }
}
