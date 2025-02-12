package com.jordanbunke.tdsm.io;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.flow.ProgramState;
import com.jordanbunke.tdsm.util.EnumUtils;

import java.io.File;
import java.nio.file.Path;
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

        ProgramState.set(ProgramState.CUSTOMIZATION, null);
    }

    private void exportJSON() {
        final Path jsonPath = getPath(FileType.JSON);
        final String json = Sprite.get().getStyle().buildJSON();

        FileIO.writeFile(jsonPath, json);
    }

    private void exportStip() {
        // TODO
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
