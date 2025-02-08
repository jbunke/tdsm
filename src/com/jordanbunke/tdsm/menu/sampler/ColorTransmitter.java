package com.jordanbunke.tdsm.menu.sampler;

import java.awt.*;

public interface ColorTransmitter {
    void receive(final Color color);

    default void send() {
        Sampler.get().setColor(getColor(), this);
    }

    Color getColor();
}
