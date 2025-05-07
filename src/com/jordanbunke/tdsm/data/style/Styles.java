package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.scripting.ast.nodes.function.HeadFuncNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.function.HelperFuncNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.data.style.pkmn.HokkaidoStyle;
import com.jordanbunke.tdsm.data.style.pkmn.KyushuStyle;
import com.jordanbunke.tdsm.util.Constants;
import com.jordanbunke.tdsm.util.ErrorDisplay;
import com.jordanbunke.tdsm_api.TDSMInterpreter;
import com.jordanbunke.tdsm_api.ast.type.StyleTypeNode;
import com.jordanbunke.tdsm_api.util.MetaFuncHelper;

import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class Styles {
    private static final Map<String, Style> styles = new HashMap<>();
    private static final Set<Path> tempDirsForDeletion = new HashSet<>();

    static {
        final Style[] DEFAULTS = new Style[] {
                HokkaidoStyle.get(), KyushuStyle.get(), VigilanteStyle.get()
        };

        for (Style def : DEFAULTS)
            styles.put(def.id, def);

        addShutdownHook();
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Path tempDir : tempDirsForDeletion)
                FileIO.deleteDirRecursive(tempDir);
        }));
    }

    public static Style get(final String id) {
        return styles.getOrDefault(id, null);
    }

    public static Stream<Style> all() {
        return styles.keySet().stream().map(styles::get);
    }

    public static void uploadStyleDialog() {
        FileIO.setDialogToFilesOnly();

        FileIO.openFileFromSystem(
                new String[]{"TDSM style archives"},
                new String[][]{{Constants.STYLE_FILE_EXT, "zip"}}
        ).ifPresent(file -> uploadStyle(file.getPath()));
    }

    public static void uploadStyle(final String tdsmPath) {
        final Path dir = FileIO.extractZipToTempDir(tdsmPath);
        tempDirsForDeletion.add(dir);

        final Path manifestPath = dir.resolve(Constants.STYLE_MANIFEST_FILENAME);

        if (manifestPath.toFile().isFile()) {
            final String content = FileIO.readFile(manifestPath);

            final TDSMInterpreter interpreter = new TDSMInterpreter();
            final HeadFuncNode script = interpreter.build(content);
            final Object out = new TDSMInterpreter().runScript(
                    script, manifestPath, StyleTypeNode.get());
            final Style style = MetaFuncHelper.asClass(
                    Style.class, out, TextPosition.N_A);

            if (style != null) {
                if (style instanceof FromFileStyle ffs) {
                    final FFSSetter[] setters = new FFSSetter[] {
                            Styles::setFFSName,
                            Styles::setFFSTooltip
                            // TODO - extend here
                    };

                    for (FFSSetter setter : setters)
                        setter.set(ffs, script, manifestPath);
                }

                styles.put(style.id, style);
                Sprite.get().setStyle(style);
            } else
                ErrorDisplay.show(ScriptErrorLog.getErrors());
        } else
            ErrorDisplay.show(
                    "Style archive contained does not contain the file \"" +
                            Constants.STYLE_MANIFEST_FILENAME + "\" at root");
    }

    @FunctionalInterface
    interface FFSSetter {
        void set(final FromFileStyle ffs,
                 final HeadFuncNode script, final Path path);
    }

    private static void setFFSName(
            final FromFileStyle ffs, final HeadFuncNode script, final Path path
    ) {
        setFFSProperty(ffs, script, FromFileStyle.NAME, String.class,
                FromFileStyle::setName, FromFileStyle.DEF_NAME,
                TypeNode.getString(), path);
    }

    private static void setFFSTooltip(
            final FromFileStyle ffs, final HeadFuncNode script, final Path path
    ) {
        setFFSProperty(ffs, script, FromFileStyle.INFO_TOOLTIP, String.class,
                FromFileStyle::setInfoTooltip, FromFileStyle.DEF_TOOLTIP,
                TypeNode.getString(), path);
    }

    private static <T> void setFFSProperty(
            final FromFileStyle ffs, final HeadFuncNode script,
            final String funcName, final Class<T> propertyType,
            final BiConsumer<FromFileStyle, T> setter, T def,
            final TypeNode returnType, final Path path
    ) {
        final HelperFuncNode func = script.getHelper(funcName);

        if (MetaFuncHelper.validate(func, returnType)) {
            T value = MetaFuncHelper.evaluate(func,
                    new SymbolTable(func, null, path),
                    propertyType, func.getPosition());

            if (value == null)
                value = def;

            setter.accept(ffs, value);
        } else
            setter.accept(ffs, def);
    }
}
