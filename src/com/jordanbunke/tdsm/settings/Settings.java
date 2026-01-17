package com.jordanbunke.tdsm.settings;

import com.jordanbunke.delta_time.error.GameError;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.utility.Version;
import com.jordanbunke.json.JSONBuilder;
import com.jordanbunke.json.JSONPair;
import com.jordanbunke.json.JSONReader;
import com.jordanbunke.tdsm.ProgramInfo;
import com.jordanbunke.tdsm.util.Constants;
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
        final Path internal = Constants.INTERNAL_SETTINGS_FILEPATH;
        final String name = ProgramInfo.PROGRAM_NAME,
                unixFriendlyName = name.toLowerCase().replace(" ", "-");

        if (OSUtils.isWindows()) {
            final String appData = System.getenv("APPDATA");
            return Path.of(appData, name).resolve(internal);
        }

        if (OSUtils.isMacOS()) {
            return Path.of(System.getProperty("user.home"),
                    "Library", "Application Support", unixFriendlyName)
                    .resolve(internal);
        }

        // Assume Linux/Unix
        final String xdgConfig = System.getenv("XDG_CONFIG_HOME");
        if (xdgConfig != null && !xdgConfig.isBlank())
            return Path.of(xdgConfig, unixFriendlyName).resolve(internal);
        else
            return Path.of(System.getProperty("user.home"),
                    ".config", unixFriendlyName).resolve(internal);
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

        final JSONPair[] pairs = JSONReader.readObject(file);

        if (pairs == null)
            return;

        for (JSONPair pair : pairs) {
            final String id = pair.key();

            if (settingsMap.containsKey(id)) {
                final String valueString = String.valueOf(pair.value());
                final Setting<?> setting = settingsMap.get(id);

                setting.read(valueString);
            }
        }
    }

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

        final JSONBuilder jb = new JSONBuilder();

        settingsMap.keySet().stream().sorted().map(id -> {
            final Object value = settingsMap.get(id).value;

            if (validJSONDataType(value))
                return new JSONPair(id, value);

            return new JSONPair(id, String.valueOf(value));
        }).forEach(jb::add);

        FileIO.writeFile(SETTINGS_FILE, jb.write());
    }

    private static boolean validJSONDataType(final Object value) {
        return value == null || value instanceof Double ||
                value instanceof Integer || value instanceof Boolean;
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

        @Override
        public String toString() {
            return type.getSimpleName() + " " + id + " = " + value;
        }
    }
}
