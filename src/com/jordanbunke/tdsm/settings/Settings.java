package com.jordanbunke.tdsm.settings;

import com.jordanbunke.delta_time.error.GameError;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.utility.Version;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.stip_parser.ParserSerializer;
import com.jordanbunke.stip_parser.SerialBlock;
import com.jordanbunke.tdsm.ProgramInfo;
import com.jordanbunke.tdsm.util.OSUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Settings {
    private static final Path SETTINGS_FILE;

    private static final Map<String, Setting<?>> settingsMap;

    public static final String SET_ID_VERSION = "last-opened-version";

    static {
        SETTINGS_FILE = determineSettingsFile();

        settingsMap = new HashMap<>();
        initialize();

        Runtime.getRuntime().addShutdownHook(new Thread(Settings::write));
    }

    private static Path determineSettingsFile() {
        final Path internal = Path.of("data", ".settings"),
                medial = Path.of(ProgramInfo.PROGRAM_NAME).resolve(internal);

        if (OSUtils.isWindows()) {
            final String appData = System.getenv("APPDATA");
            return Path.of(appData).resolve(medial);
        } else
            return internal;
    }

    private static void initialize() {
        addSetting(new Setting<>(Version.class, SET_ID_VERSION,
                Version::parse, new Version(1, 0, 0)));
    }

    private static <T> void addSetting(final Setting<T> setting) {
        settingsMap.put(setting.id, setting);
    }

    public static void read() {
        final String file = FileIO.readFile(SETTINGS_FILE);

        if (file == null)
            return;

        final SerialBlock[] blocks = ParserSerializer
                .deserializeBlocksAtDepthLevel(file);

        for (SerialBlock block : blocks) {
            final String id = block.tag();

            if (settingsMap.containsKey(id)) {
                final String valueString = block.value();
                final Setting<?> setting = settingsMap.get(id);

                setting.read(valueString);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void write() {
        final Path settingsFolder = SETTINGS_FILE.getParent();

        if (!settingsFolder.toFile().exists())
            FileIO.safeMakeDirectory(settingsFolder);
        else if (!settingsFolder.toFile().isDirectory()) {
            try {
                Files.delete(settingsFolder);
                FileIO.safeMakeDirectory(settingsFolder);
            } catch (IOException ioe) {
                GameError.send("Couldn't delete file at " + settingsFolder +
                        " needed to clear space for the settings folder. " +
                        "Could not write " + ProgramInfo.PROGRAM_NAME +
                        " settings.");
                return;
            }
        }

        final StringBuilder sb = new StringBuilder();

        ParserSerializer.serializeSimpleAttributes(sb, -1,
                settingsMap.keySet().stream().sorted()
                        .map(id -> new Pair<>(id, settingsMap.get(id).value))
                        .toArray(Pair[]::new));

        FileIO.writeFile(SETTINGS_FILE, sb.toString());
    }

    public static void set(final String id, final Object value) {
        if (!settingsMap.containsKey(id))
            return;

        settingsMap.get(id).set(value);
    }

    public static <T> T get(final String id, final Class<T> type) {
        if (!settingsMap.containsKey(id))
            return null;

        final Setting<?> setting = settingsMap.get(id);

        if (type == setting.type)
            return type.cast(setting.get());

        return null;
    }

    private static class Setting<T> {
        private final Class<T> type;
        private final String id;
        private final Function<String, T> parser;
        private final Predicate<T> validator;
        private final T defaultValue;
        private T value;

        Setting(
                final Class<T> type, final String id,
                final Function<String, T> parser,
                final Predicate<T> validator, final T defaultValue
        ) {
            this.type = type;
            this.id = id;
            this.parser = parser;
            this.validator = validator;
            this.defaultValue = defaultValue;

            value = defaultValue;
        }

        Setting(
                final Class<T> type, final String id,
                final Function<String, T> parser, final T defaultValue
        ) {
            this(type, id, parser, Objects::nonNull, defaultValue);
        }

        private void read(final String valueString) {
            set(parser.apply(valueString));
        }

        private void set(final Object value) {
            if (type.isInstance(value)) {
                final T cast = type.cast(value);

                if (validator.test(cast))
                    this.value = cast;
            }
        }

        private T get() {
            return value;
        }
    }
}
