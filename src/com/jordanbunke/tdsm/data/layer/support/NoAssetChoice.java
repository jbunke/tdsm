package com.jordanbunke.tdsm.data.layer.support;

public final class NoAssetChoice {
    public final boolean valid, equalRandomOdds;
    public final double randomProb;

    private NoAssetChoice(
            final boolean valid, final boolean equalRandomOdds,
            final double randomProb
    ) {
        this.valid = valid;
        this.equalRandomOdds = equalRandomOdds;
        this.randomProb = randomProb;
    }

    public static NoAssetChoice invalid() {
        return new NoAssetChoice(false, false, 0.0);
    }

    public static NoAssetChoice equal() {
        return new NoAssetChoice(true, true, 0.0);
    }

    public static NoAssetChoice prob(final double randomProb) {
        return new NoAssetChoice(true, false, randomProb);
    }
}
