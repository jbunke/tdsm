package com.jordanbunke.tdsm.util.hardware;

import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement.Anchor;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.menu.DynamicLabel;
import com.jordanbunke.tdsm.menu.IconButton;
import com.jordanbunke.tdsm.menu.Indicator;
import com.jordanbunke.tdsm.menu.StaticLabel;
import com.jordanbunke.tdsm.menu.pre_export.ColorReplacementButton;
import com.jordanbunke.tdsm.menu.pre_export.ReplacementOptions;
import com.jordanbunke.tdsm.menu.pre_export.ReplacementPreview;
import com.jordanbunke.tdsm.menu.scrollable.HorzScrollBox;
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.Constants;
import com.jordanbunke.tdsm.util.MenuAssembly;
import com.jordanbunke.tdsm.util.ResourceCodes;

import java.awt.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.jordanbunke.tdsm.util.Layout.*;

public final class GBAUtils {
    private static final Integer[] FIVE_BITS_AS_EIGHT_BITS = new Integer[] {
            0, 8, 16, 25, 33, 41, 49, 58, 66, 74,
            82, 90, 99, 107, 115, 123, 132, 140,
            148, 156, 165, 173, 181, 189, 197,
            206, 214, 222, 230, 239, 247, 255
    };

    public static Color quantize(final Color input) {
        if (input.getAlpha() == 0) return input;

        return new Color(quantizeChannel(input.getRed()),
                quantizeChannel(input.getGreen()),
                quantizeChannel(input.getBlue()));
    }

    private static int quantizeChannel(final int channel) {
        return MathPlus.findBest(channel, 0, n -> n,
                (i1, i2) -> Math.abs(i1 - channel) < Math.abs(i2 - channel),
                FIVE_BITS_AS_EIGHT_BITS);
    }

    // Menu

    public static void buildReplacementMenu(
            final MenuBuilder mb, final Style style,
            final Map<Color, Color> replacementMap,
            final Supplier<Color> selectGetter,
            final Consumer<Color> selector,
            final Coord2D warningPos
    ) {
        style.resetPreExport();
        final Map<Color, Integer> cs = Colors.colorOccurrences(style);
        final Color[] sequence = cs.keySet().stream()
                .sorted(Comparator.comparingInt(cs::get))
                .toArray(Color[]::new);

        MenuAssembly.preExportExplanation(mb, """
                Warning: The sprite sheet contains $cols unique non-transparent
                colors, which is more that the $max-color maximum permitted
                for Game Boy Advance sprites."""
                        .replace("$cols", String.valueOf(cs.size()))
                        .replace("$max", String.valueOf(Constants.GBA_SPRITE_COL_LIMIT)),
                0.05, 0.15);

        final double REL_W = 0.6;
        final int LEFT = atX((1.0 - REL_W) / 2.0), INC_Y = atY(1 / 9.0);

        final ReplacementPreview rp = new ReplacementPreview(
                new Coord2D(atX(0.35), atY(0.15)),
                MenuElement.Anchor.CENTRAL_TOP, style);
        mb.add(rp);

        int y = atY(0.43);

        final StaticLabel replacementLabel = StaticLabel.init(
                new Coord2D(LEFT, y), "Replacement").build();
        final Indicator replacementInfo = Indicator.make(
                ResourceCodes.REPLACEMENT, replacementLabel.followIcon17(),
                MenuElement.Anchor.LEFT_TOP);
        final IconButton replacementReset = IconButton.init(
                ResourceCodes.RESET, replacementInfo.following(),
                style::resetPreExport).build();

        mb.addAll(replacementLabel, replacementInfo, replacementReset);

        y += INC_Y;

        final String CC_PREFIX = "Updated color count: ",
                CC_SUFFIX = " (valid for GBA)";
        final DynamicLabel colorCount = DynamicLabel.init(
                new Coord2D(LEFT, y), () -> {
                    final Set<Color> used = new HashSet<>();

                    for (Color c : sequence)
                        used.add(replacementMap.getOrDefault(c, c));

                    final int size = used.size();

                    return CC_PREFIX + size +
                            (size <= Constants.GBA_SPRITE_COL_LIMIT
                                    ? CC_SUFFIX : "");
                }, CC_PREFIX + "XXX" + CC_SUFFIX
        ).setMini().build();
        mb.add(colorCount);

        y += (int) (INC_Y * 0.5);

        Coord2D replPos = new Coord2D(LEFT, y);
        final MenuBuilder crbs = new MenuBuilder();

        for (Color entry : sequence) {
            final ColorReplacementButton crb = new ColorReplacementButton(
                    replPos, entry, Colors.hexCode(entry) +
                    "\n" + cs.get(entry) + " occurrences",
                    c -> replacementMap.getOrDefault(c, null),
                    selectGetter, selector);
            crbs.add(crb);

            replPos = replPos.displaceX(crb.getWidth() + ASSET_BUFFER_X);
        }

        final HorzScrollBox choicesBox = new HorzScrollBox(
                new Coord2D(LEFT, y), new Bounds2D(atX(REL_W),
                COL_SEL_BUTTON_DIM + ASSET_BUFFER_Y),
                Arrays.stream(crbs.build().getMenuElements())
                        .map(Scrollable::new).toArray(Scrollable[]::new),
                replPos.x - ASSET_BUFFER_X, 0);
        mb.add(choicesBox);

        y += INC_Y;

        final String REPL_PREFIX = "Replace ", REPL_SUFFIX = " with:";
        final DynamicLabel replaceWithLabel = DynamicLabel.init(
                new Coord2D(LEFT, y), () -> {
                    if (selectGetter.get() == null)
                        return "";

                    final String hexCode = Colors.hexCode(selectGetter.get());

                    return REPL_PREFIX + hexCode + REPL_SUFFIX;
                }, REPL_PREFIX + "#" + "X".repeat(7) + REPL_SUFFIX
        ).setMini().build();
        mb.add(replaceWithLabel);

        y += (int) (INC_Y * 0.5);

        final ReplacementOptions ro = new ReplacementOptions(
                new Coord2D(LEFT, y), new Bounds2D(atX(REL_W),
                COL_SEL_BUTTON_DIM + ASSET_BUFFER_Y),
                replacementMap, cs.keySet(), selectGetter);
        mb.add(ro);

        final Indicator warning = Indicator.make(
                ResourceCodes.BACK_FROM_REPL, warningPos, Anchor.LEFT_TOP);
        mb.add(warning);
    }
}
