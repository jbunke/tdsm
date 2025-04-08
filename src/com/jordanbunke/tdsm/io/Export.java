package com.jordanbunke.tdsm.io;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.stip_parser.ParserSerializer;
import com.jordanbunke.stip_parser.rep.IRLayer;
import com.jordanbunke.stip_parser.rep.IRState;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.flow.ProgramState;
import com.jordanbunke.tdsm.util.EnumUtils;
import com.jordanbunke.tdsm.util.MenuAssembly;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class Export {
    private static final Export INSTANCE;

    private Path folder;
    private String fileName;
    private boolean exportJSON, exportStip;

    private enum FileType {
        PNG, JSON, STIP;

        String extension() {
            return "." + name().toLowerCase();
        }

        boolean exporting() {
            return switch (this) {
                case JSON -> get().exportJSON;
                case STIP -> get().exportStip;
                default -> true;
            };
        }
    }

    static {
        INSTANCE = new Export();
    }

    private Export() {
        folder = null;
        fileName = "";
        exportJSON = false;
        exportStip = false;
    }

    public static Export get() {
        return INSTANCE;
    }

    public boolean canExport() {
        return folder != null && validFileName(fileName);
    }

    public boolean wouldOverwrite() {
        if (!canExport())
            return false;

        return EnumUtils.stream(FileType.class)
                .filter(FileType::exporting).map(this::getPath)
                .map(p -> p.toFile().isFile())
                .reduce(false, Boolean::logicalOr);
    }

    private Path getPath(final FileType type) {
        if (!canExport())
            return Path.of(fileName + type.extension());

        return folder.resolve(fileName + type.extension());
    }

    public void export() {
        if (!canExport())
            return;

        final GameImage spriteSheet = Sprite.get().renderSpriteSheet();
        final Path spriteSheetPath = getPath(FileType.PNG);

        GameImageIO.writeImage(spriteSheetPath, spriteSheet);

        if (exportJSON) exportJSON();
        if (exportStip) exportStip();

        ProgramState.set(ProgramState.MENU, MenuAssembly.main());
    }

    private void exportJSON() {
        final Path jsonPath = getPath(FileType.JSON);
        final String json = Sprite.get().getStyle().buildJSON();

        FileIO.writeFile(jsonPath, json);
    }

    private void exportStip() {
        final Path stipPath = getPath(FileType.STIP);
        final List<Pair<String, GameImage>> stipRep =
                Sprite.get().renderStipExport();

        ParserSerializer.save(
                buildStippleEffectRepresentation(stipRep), stipPath);
    }

    private IRState buildStippleEffectRepresentation(
            final List<Pair<String, GameImage>> stipRep
    ) {
        final Bounds2D dims = Sprite.get().getStyle().getSpriteSheetDims();

        final IRLayer[] layers = stipRep.stream()
                .map(p -> IRLayer.of(p.b()).setName(p.a()).build())
                .toArray(IRLayer[]::new);

        return IRState.of(dims.width(), dims.height(), 1, layers).build();
    }

    public boolean isExportJSON() {
        return exportJSON;
    }

    public void setExportJSON(final boolean exportJSON) {
        this.exportJSON = exportJSON;
    }

    public boolean isExportStip() {
        return exportStip;
    }

    public void setExportStip(final boolean exportStip) {
        this.exportStip = exportStip;
    }

    public Path getFolder() {
        return folder;
    }

    public void chooseFolder() {
        FileIO.setDialogToFoldersOnly();
        final Optional<File> opened = FileIO.openFileFromSystem();

        if (opened.isEmpty())
            return;

        folder = opened.get().toPath();
    }

    // scripting inclusion
    @SuppressWarnings("unused")
    public void setFolder(final Path folder) {
        this.folder = folder;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public static boolean validFileName(final String fileName) {
        final Set<Character> illegalCharSet = Set.of(
                '/', '\\', ':', '*', '?', '"', '<', '>', '|', '{', '}');

        return !fileName.isEmpty() && illegalCharSet.stream()
                .map(c -> fileName.indexOf(c) == -1)
                .reduce((a, b) -> a && b).orElse(false);
    }
}
