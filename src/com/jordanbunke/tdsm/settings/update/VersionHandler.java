package com.jordanbunke.tdsm.settings.update;

import com.jordanbunke.delta_time.utility.Version;
import com.jordanbunke.tdsm.ProgramInfo;
import com.jordanbunke.tdsm.flow.ProgramState;
import com.jordanbunke.tdsm.settings.Settings;
import com.jordanbunke.tdsm.util.MenuAssembly;

import java.util.Arrays;

public final class VersionHandler {
    public static void startup() {
        // Determine startup messages
        final StartupMessage[] messages = determineMessages();

        ProgramState.set(ProgramState.MENU, messages.length == 0
                ? MenuAssembly.mainMenu()
                : MenuAssembly.updateInformation(messages));

        // Update latest opened version to current version
        Settings.set(Settings.SET_ID_VERSION, ProgramInfo.getVersion());
    }

    private static StartupMessage[] determineMessages() {
        final Version lastOpened =
                Settings.get(Settings.SET_ID_VERSION, Version.class);

        return Arrays.stream(StartupMessage.values())
                .filter(sm -> sm.since.isLaterVersion(lastOpened))
                .toArray(StartupMessage[]::new);
    }
}
