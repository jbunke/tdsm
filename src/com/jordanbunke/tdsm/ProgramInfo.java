package com.jordanbunke.tdsm;

import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.utility.Version;
import com.jordanbunke.json.JSONBuilder;
import com.jordanbunke.json.JSONPair;
import com.jordanbunke.json.JSONReader;
import com.jordanbunke.tdsm.util.Constants;
import com.jordanbunke.tdsm.util.RuntimeSettings;

import java.nio.file.Path;

public final class ProgramInfo {
    public static String PROGRAM_NAME = "Top Down Sprite Maker";
    private static Version VERSION = new Version(1, 0, 0);
    private static boolean IS_DEVBUILD = false, IS_DEMO = false;

    static void readProgramFile() {
        final String programFile = FileIO.readResource(ResourceLoader
                .loadResource(Constants.PROGRAM_FILE), "prg");

        final JSONPair[] pairs = JSONReader.readObject(programFile);

        for (JSONPair pair : pairs) {
            switch (pair.key()) {
                case Constants.NAME_CODE ->
                        PROGRAM_NAME = String.valueOf(pair.value());
                case Constants.VERSION_CODE ->
                        VERSION = Version.parse(String.valueOf(pair.value()));
                case Constants.IS_DEVBUILD_CODE ->
                        IS_DEVBUILD = Boolean.parseBoolean(String.valueOf(pair.value()));
                case Constants.IS_DEMO_CODE ->
                        IS_DEMO = Boolean.parseBoolean(String.valueOf(pair.value()));
            }
        }

        if (RuntimeSettings.isOverwrite()) {
            final Path RES_ROOT = Path.of("res");

            if (IS_DEVBUILD) {
                VERSION.incrementBuild();

                final Path toSave = RES_ROOT.resolve(Constants.PROGRAM_FILE);

                final JSONBuilder updated = new JSONBuilder();

                updated.add(new JSONPair(Constants.NAME_CODE, PROGRAM_NAME));
                updated.add(new JSONPair(Constants.VERSION_CODE, String.valueOf(VERSION)));
                updated.add(new JSONPair(Constants.IS_DEVBUILD_CODE, IS_DEVBUILD));
                updated.add(new JSONPair(Constants.IS_DEMO_CODE, IS_DEMO));

                FileIO.writeFile(toSave, updated.write());
            }

            final Path versionFile = RES_ROOT.resolve(Constants.VERSION_FILE),
                    releaseFile = RES_ROOT.resolve(Constants.RELEASE_FILE);
            FileIO.writeFile(versionFile, VERSION.toString());
            FileIO.writeFile(releaseFile, IS_DEMO
                    ? Constants.IS_DEMO_CODE : Constants.IS_RELEASE_CODE);
        }
    }

    public static String formatVersion() {
        return "v" + VERSION + (IS_DEVBUILD ? " (devbuild)" : "") +
                (IS_DEMO ? " (demo)" : "");
    }

    public static Version getVersion() {
        return VERSION;
    }

    public static boolean isFullRelease() {
        return !IS_DEMO;
    }
}
