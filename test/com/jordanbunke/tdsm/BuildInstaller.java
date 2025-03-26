package com.jordanbunke.tdsm;

import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.tdsm.util.Constants;
import com.jordanbunke.tdsm.util.ParserUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BuildInstaller {
    private enum Var {
        VERSION, VERSION_UND, NAME, PROJ_ROOT;

        @Override
        public String toString() {
            return "%" + name() + "%";
        }
    }

    public static void main(String[] args) {
        final Path root = Paths.get("").toAbsolutePath();
        final String name = TDSM.PROGRAM_NAME,
                version = ParserUtils.read(Constants.VERSION_FILE);

        final String script = generateInstallerScript(
                root.toString(), name, version);
        FileIO.writeFile(root.resolve(
                Path.of("config", "installer_script.iss")), script);
    }

    private static String generateInstallerScript(
            final String root, final String name, final String version
    ) {
        final String template = ParserUtils.read(
                Path.of("installer_script_template.iss"));

        final String versionUnd = version.replace(".", "_");

        return template.replace(Var.VERSION.toString(), version)
                .replace(Var.VERSION_UND.toString(), versionUnd)
                .replace(Var.NAME.toString(), name)
                .replace(Var.PROJ_ROOT.toString(), root);
    }
}
