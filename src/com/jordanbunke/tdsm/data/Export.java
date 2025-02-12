package com.jordanbunke.tdsm.data;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.GameImageIO;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

public final class Export {
    private static final Export INSTANCE;

    private Path folder;
    private String fileName;
    private boolean exportJSON;

    static {
        INSTANCE = new Export();
    }

    private Export() {
        folder = null;
        fileName = "";
        exportJSON = false;
    }

    public static Export get() {
        return INSTANCE;
    }

    public boolean canExport() {
        return folder != null && validFileName(fileName);
    }

    public void export() {
        if (!canExport())
            return;

        final GameImage spriteSheet = Sprite.get().renderSpriteSheet();
        final Path spriteSheetPath = folder.resolve(fileName + ".png");

        // TODO - send to an are you sure screen if file path already exists

        GameImageIO.writeImage(spriteSheetPath, spriteSheet);

        if (exportJSON) {
            // TODO
        }
    }

    public boolean isExportJSON() {
        return exportJSON;
    }

    public void setExportJSON(final boolean exportJSON) {
        this.exportJSON = exportJSON;
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
