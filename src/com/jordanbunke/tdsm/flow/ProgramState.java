package com.jordanbunke.tdsm.flow;

import com.jordanbunke.delta_time.menu.Menu;

public enum ProgramState {
    SPLASH, CUSTOMIZATION, SAVE, MENU;

    private static ProgramState state;
    private static Menu menu;

    public static ProgramState get() {
        return state;
    }

    public static void set(final ProgramState state, final Menu menu) {
        ProgramState.state = state;

        if (state == MENU)
            ProgramState.menu = menu;
    }
}
