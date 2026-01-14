package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.tdsm.flow.ProgramState;

import java.util.Arrays;

public final class ErrorDisplay {
    public static void show(final String... errors) {
        if (errors.length > 0)
            ProgramState.set(ProgramState.MENU,
                    MenuAssembly.encounteredErrors(Arrays.stream(errors)
                            .map(s -> "> " + s).toArray(String[]::new)));
    }

    public static void showScriptErrors() {
        show(ScriptErrorLog.getErrors());
    }
}
