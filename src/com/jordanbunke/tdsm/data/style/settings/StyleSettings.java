package com.jordanbunke.tdsm.data.style.settings;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.sprite.SpriteAssembler;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.util.ResourceCodes;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public final class StyleSettings {
    private static final Map<String, Function<Style, StyleSetting>> VALID_SETTINGS;

    private final Map<String, StyleSetting> contents;

    public StyleSettings() {
        contents = new HashMap<>();
    }

    static {
        VALID_SETTINGS = new HashMap<>();

        VALID_SETTINGS.put(ResourceCodes.QUANTIZE_GBA,
                QuantizeGBASetting::new);
        VALID_SETTINGS.put(ResourceCodes.WARN_ROM_15_COLS,
                Warn15ColorsSetting::new);
    }

    public static boolean validCode(final String code) {
        return VALID_SETTINGS.containsKey(code);
    }

    public void add(final Style style, final String code) {
        if (validCode(code) && !contains(code))
            contents.put(code, VALID_SETTINGS.get(code).apply(style));
    }

    public boolean contains(final String code) {
        return contents.containsKey(code);
    }

    public StyleSetting get(final String code) {
        return contains(code) ? contents.get(code) : null;
    }

    public boolean has() {
        return !contents.isEmpty();
    }

    public void resetPreExport() {
        stream().forEach(StyleSetting::resetPreExport);
    }

    public boolean hasPreExportStep() {
        return stream().map(StyleSetting::hasPreExportStep)
                .reduce(false, Boolean::logicalOr);
    }

    public void considerations(
            final SpriteAssembler<String, String> assembler
    ) {
        stream().forEach(s -> s.considerations(assembler));
    }

    public GameImage preExportTransform(final GameImage input) {
        return preExportAuthority()
                .map(s -> s.preExportTransform(input)).orElse(input);
    }

    public void buildPreExportMenu(
            final MenuBuilder mb, final Coord2D warningPos
    ) {
        preExportAuthority().ifPresent(
                s -> s.buildPreExportMenu(mb, warningPos));
    }

    private Optional<StyleSetting> preExportAuthority() {
        return stream().filter(StyleSetting::hasPreExportStep).findFirst();
    }

    public StyleSetting[] array() {
        return stream().sorted(Comparator.comparing(s -> s.infoCode))
                .toArray(StyleSetting[]::new);
    }

    private Stream<StyleSetting> stream() {
        return contents.keySet().stream().map(contents::get);
    }
}
