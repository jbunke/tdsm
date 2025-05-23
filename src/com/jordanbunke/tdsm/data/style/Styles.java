package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.nodes.function.HeadFuncNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.function.HelperFuncNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.util.Constants;
import com.jordanbunke.tdsm.util.ErrorDisplay;
import com.jordanbunke.tdsm.util.Layout;
import com.jordanbunke.tdsm_api.TDSMInterpreter;
import com.jordanbunke.tdsm_api.ast.type.StyleTypeNode;
import com.jordanbunke.tdsm_api.util.MetaFuncHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public final class Styles {
    private static final Map<String, Style> styles = new HashMap<>();
    private static final Set<Path> tempDirsForDeletion = new HashSet<>();

    static {
        addShutdownHook();

        fromResources("temp");
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
        return styles.keySet().stream().map(styles::get)
                .sorted(Comparator.comparing(Style::name));
    }

    public static void fromResources(final String code) {
        final Path zipPath = Constants.STYLES_FOLDER.resolve(code + ".zip");
        uploadStyle(ResourceLoader.loadResource(zipPath), false);
    }

    public static void uploadStyleDialog() {
        FileIO.setDialogToFilesOnly();

        FileIO.openFileFromSystem(
                new String[]{"TDSM style archives"},
                new String[][]{{Constants.STYLE_FILE_EXT, "zip"}}
        ).ifPresent(Styles::uploadFromFile);
    }

    private static void uploadFromFile(final File archive) {
        try {
            uploadStyle(new FileInputStream(archive), true);
        } catch (FileNotFoundException e) {
            ErrorDisplay.show(
                    "Failed to provide a valid archive file (." +
                            Constants.STYLE_FILE_EXT + " or .zip)");
        }
    }

    private static void uploadStyle(
            final InputStream archiveIn, final boolean set
    ) {
        final Path dir = FileIO.extractZipToTempDir(archiveIn);
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
                            Styles::setFFSTooltip,
                            Styles::setFFSPreviewScale,
                            Styles::setFFSSettings
                            // TODO - extend here
                    };

                    for (FFSSetter setter : setters)
                        setter.set(ffs, script, manifestPath);
                }

                styles.put(style.id, style);
                if (set) Sprite.get().setStyle(style);
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

    private static void setFFSPreviewScale(
            final FromFileStyle ffs, final HeadFuncNode script, final Path path
    ) {
        setFFSProperty(ffs, script, FromFileStyle.PREVIEW_SCALE,
                Integer.class, FromFileStyle::setPreviewScale,
                Layout.SPRITE_PREVIEW_SCALE_UP, TypeNode.getInt(), path);
    }

    private static void setFFSSettings(
            final FromFileStyle ffs, final HeadFuncNode script, final Path path
    ) {
        setFFSProperty(ffs, script, FromFileStyle.SETTINGS,
                ScriptArray.class, FromFileStyle::setSettings,
                FromFileStyle.DEF_SETTINGS,
                TypeNode.arrayOf(TypeNode.getString()), path);
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
