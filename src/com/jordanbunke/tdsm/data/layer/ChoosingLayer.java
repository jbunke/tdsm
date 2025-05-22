package com.jordanbunke.tdsm.data.layer;

/**
 * This class is designed for the scripting API implementation,
 * which benefits from treating {@link ChoiceLayer} and
 * {@link AssetChoiceLayer} collectively under certain circumstances
 * */
public interface ChoosingLayer {
    // set
    void choose(final int selection);
    void chooseFromScript(final int selection);
    @SuppressWarnings("unused")
    boolean choose(final String choice);

    // get
    boolean hasChoice();
    @SuppressWarnings("unused")
    String getChoiceID();
    @SuppressWarnings("unused")
    String getChoiceIDAt(final int index);
    int getChoiceIndex();

    // aux
    int getNumChoices();
    @SuppressWarnings("unused")
    String getID();
}
