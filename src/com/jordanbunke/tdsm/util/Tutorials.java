package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.utility.math.Pair;

import java.util.HashMap;
import java.util.Map;

import static com.jordanbunke.tdsm.util.MenuAssembly.visitSite;
import static com.jordanbunke.tdsm.util.ResourceCodes.*;

public final class Tutorials {
    private static final Map<String, String> codeTitleMap;
    private static final Map<String, Pair<String, Runnable>[]> codeButtonMap;

    private static String active;

    static {
        codeButtonMap = new HashMap<>();
        codeTitleMap = new HashMap<>();

        init();

        setActive(codes()[0]);
    }

    private static void init() {
        codeTitleMap.put(TUT_MAKE_STYLE, "Make a sprite style");
        codeButtonMap.put(TUT_MAKE_STYLE, array(
                // TODO - YouTube tutorial
                new Pair<>("Scripting API",
                        () -> visitSite("https://github.com/jbunke/tdsm-api")),
                new Pair<>("DeltaScript",
                        () -> visitSite("https://github.com/jbunke/deltascript"))));

        // TODO

        codeTitleMap.put(TUT_ANIMS, "Animations");

        codeTitleMap.put(TUT_DIRS, "Directions");

        codeTitleMap.put(TUT_CONFIG, "Configuration");

        codeTitleMap.put(TUT_LAYERS, "Layers");

        codeTitleMap.put(TUT_LOCK, "Locking layers");

        codeTitleMap.put(TUT_EXPORT, "Export");

        codeTitleMap.put(TUT_RANDOM, "Randomization");

        codeTitleMap.put(TUT_COL_SEL, "Color selections");
    }

    @SafeVarargs
    private static Pair<String, Runnable>[] array(
            final Pair<String, Runnable>... elements
    ) {
        return elements;
    }

    public static String[] codes() {
        return codeTitleMap.keySet().stream().sorted().toArray(String[]::new);
    }

    public static String getTitle(final String code) {
        return codeTitleMap.get(code);
    }

    public static void setActive(final String code) {
        active = code;
    }

    public static String getActive() {
        return active;
    }

    public static Pair<String, Runnable>[] getButtons(final String code) {
        return codeButtonMap.getOrDefault(code, null);
    }
}
