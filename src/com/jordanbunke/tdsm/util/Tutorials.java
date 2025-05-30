package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.utility.math.Pair;

import java.util.HashMap;
import java.util.Map;

import static com.jordanbunke.tdsm.util.MenuAssembly.visitSite;
import static com.jordanbunke.tdsm.util.ResourceCodes.*;

public final class Tutorials {
    private static final Map<String, String> codeTitleMap;
    private static final Map<String, Pair<String, Runnable>[]> codeButtonMap;

    static {
        codeButtonMap = new HashMap<>();
        codeTitleMap = new HashMap<>();

        init();
    }

    private static void init() {
        codeTitleMap.put(TUT_MAKE_STYLE, "Make a sprite style");
        codeButtonMap.put(TUT_MAKE_STYLE, array(
                new Pair<>("YouTube",
                        () -> visitSite("https://youtube.com" /* TODO */)),
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
        // TODO
    }

    public static boolean hasButtons(final String code) {
        return codeButtonMap.containsKey(code);
    }

    public static Pair<String, Runnable>[] getButtons(final String code) {
        return codeButtonMap.get(code);
    }
}
