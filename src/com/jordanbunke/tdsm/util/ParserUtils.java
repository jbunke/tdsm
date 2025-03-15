package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.ResourceLoader;

import java.nio.file.Path;

public final class ParserUtils {
    public static String readTooltip(final String code) {
        return read(Constants.TOOLTIPS_FOLDER.resolve(code + ".txt"));
    }

    public static String read(final Path resource) {
        return FileIO.readResource(ResourceLoader.loadResource(resource), "");
    }

    public static String[] readAssetCSV(
            final String styleID, final String layerID
    ) {
        final Path csvPath = Constants.ASSET_ROOT_FOLDER.resolve(
                Path.of(styleID, layerID, layerID + ".csv"));
        return read(csvPath).split(",");
    }
}
