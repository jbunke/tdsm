package com.jordanbunke.tdsm.util;

import static com.jordanbunke.tdsm.util.ResourceCodes.FEEDBACK_ITCH;
import static com.jordanbunke.tdsm.util.ResourceCodes.FEEDBACK_STEAM;

public enum Marketplace {
    ITCH("itch.io", FEEDBACK_ITCH, "https://flinkerflitzer.itch.io/tdsm", "/rate"),
    STEAM("Steam", FEEDBACK_STEAM, "https://store.steampowered.com/app/3672340", ""),
    NONE;

    public final String name, feedbackResourceCode, storeLink;
    private final String feedbackAppend;

    Marketplace(
            final String name, final String feedbackResourceCode,
            final String storeLink, final String feedbackAppend
    ) {
        this.name = name;
        this.feedbackResourceCode = feedbackResourceCode;
        this.storeLink = storeLink;
        this.feedbackAppend = feedbackAppend;
    }

    Marketplace() {
        this("", "", "", "");
    }

    public static Marketplace parse(final String marketplace) {
        for (Marketplace m : Marketplace.values())
            if (m.name.equals(marketplace))
                return m;

        return NONE;
    }

    public boolean isValid() {
        return this != NONE;
    }

    public String feedbackLink() {
        return storeLink + feedbackAppend;
    }

    @Override
    public String toString() {
        return name.toLowerCase();
    }
}
