package com.jordanbunke.tdsm.util;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.util.Optional;

public final class MacFileDialog {
    private MacFileDialog() {}

    public static Optional<File> openFolder() {
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        try {
            final FileDialog dialog = new FileDialog(
                    (Frame) null, "Select Export Folder", FileDialog.LOAD);
            dialog.setVisible(true);
            final String dir = dialog.getDirectory();
            final String file = dialog.getFile();
            if (dir == null || file == null)
                return Optional.empty();
            return Optional.of(new File(dir, file));
        } finally {
            System.setProperty("apple.awt.fileDialogForDirectories", "false");
        }
    }

    public static Optional<File> openFile(final String... extensions) {
        final FileDialog dialog = new FileDialog(
                (Frame) null, "Open File", FileDialog.LOAD);
        if (extensions.length > 0)
            dialog.setFilenameFilter((dir, name) -> {
                for (final String ext : extensions)
                    if (name.endsWith("." + ext)) return true;
                return false;
            });
        dialog.setVisible(true);
        final String dir = dialog.getDirectory();
        final String file = dialog.getFile();
        if (dir == null || file == null)
            return Optional.empty();
        return Optional.of(new File(dir, file));
    }
}
