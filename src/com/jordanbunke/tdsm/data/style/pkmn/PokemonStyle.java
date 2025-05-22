package com.jordanbunke.tdsm.data.style.pkmn;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.sprite.SpriteAssembler;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.Directions.Dir;
import com.jordanbunke.tdsm.data.Directions.NumDirs;
import com.jordanbunke.tdsm.data.Replacement;
import com.jordanbunke.tdsm.data.layer.Layers;
import com.jordanbunke.tdsm.data.layer.builders.ACLBuilder;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.data.layer.support.NoAssetChoice;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.data.style.settings.StyleSetting;
import com.jordanbunke.tdsm.data.style.settings.StyleSettings;
import com.jordanbunke.tdsm.util.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import static com.jordanbunke.color_proc.ColorProc.*;
import static com.jordanbunke.color_proc.ColorProc.fromHSV;
import static com.jordanbunke.tdsm.util.Colors.black;

public abstract class PokemonStyle extends Style {
    static final boolean HORIZONTAL_ANIMS = true;

    static final String COMBINED_OUTFIT = "Combined outfit";

    static final String ANIM_ID_IDLE = "idle", ANIM_ID_WALK = "walk",
            ANIM_ID_RUN = "run", ANIM_ID_FISH = "fish",
            ANIM_ID_BIKE_IDLE = "bike_idle", ANIM_ID_CYCLE = "cycle",
            ANIM_ID_SURF = "surf", ANIM_ID_SWIM = "swim",
            ANIM_ID_CAPSULE = "use_capsule";

    static final Set<Color>
            SKIN, SKIN_OUTLINES, HAIR, IRIS, EYE_WHITE,
            CLOTH_1, CLOTH_2, CLOTH_3, CLOTH_4;
    static final Color
            BASE_SKIN, BASE_HAIR, BASE_IRIS, BASE_EYE_WHITE,
            BASE_CLOTH_1, BASE_CLOTH_2, BASE_CLOTH_3, BASE_CLOTH_4;

    static final Color[]
            SKIN_SWATCHES, HAIR_SWATCHES,
            IRIS_SWATCHES, CLOTHES_SWATCHES;

    // SETTINGS
    private final StyleSettings settings;

    static {
        BASE_SKIN = new Color(0xb8f8b8);
        SKIN = Set.of(BASE_SKIN,
                new Color(0x98e898),
                new Color(0x70d870));
        SKIN_OUTLINES = Set.of(
                new Color(0x557840),
                new Color(0x364030));

        BASE_HAIR = new Color(0xb0b0f8);
        HAIR = Set.of(BASE_HAIR,
                new Color(0x8080f0),
                new Color(0x4848c8),
                new Color(0x303070));

        BASE_IRIS = new Color(0xff0000);
        IRIS = Set.of(BASE_IRIS,
                new Color(0x884848));

        BASE_EYE_WHITE = new Color(0x00ffff);
        EYE_WHITE = Set.of(BASE_EYE_WHITE,
                new Color(0xa8d8d8));

        BASE_CLOTH_1 = new Color(0xf08080);
        BASE_CLOTH_2 = new Color(0xf0b880);
        BASE_CLOTH_3 = new Color(0xf0f080);
        BASE_CLOTH_4 = new Color(0xb8f080);

        CLOTH_1 = Set.of(BASE_CLOTH_1,
                new Color(0xf8b0b0),
                new Color(0xc84848),
                new Color(0x703030));
        CLOTH_2 = Set.of(BASE_CLOTH_2,
                new Color(0xf8d4b0),
                new Color(0xc88848),
                new Color(0x705030));
        CLOTH_3 = Set.of(BASE_CLOTH_3,
                new Color(0xf8f8b0),
                new Color(0xc8c848),
                new Color(0x707030));
        CLOTH_4 = Set.of(BASE_CLOTH_4,
                new Color(0xd4f8b0),
                new Color(0x88c848),
                new Color(0x507030));

        SKIN_SWATCHES = new Color[] {
                new Color(0xf8d0b8),
                new Color(0xa88050),
                new Color(0xc89060),
                new Color(0xf8e0b8),
                new Color(0x986860),
                new Color(0x986840),
                new Color(0x58402e),
        };
        HAIR_SWATCHES = new Color[] {
                new Color(0x404040),
                new Color(0x342820),
                new Color(0x4b382c),
                new Color(0x684828),
                new Color(0x82662d),
                new Color(0xb2864b),
                new Color(0xc4b880),
                new Color(0xdedbb8),
                new Color(0x888480),
                new Color(0xc4beb8)
        };
        IRIS_SWATCHES = new Color[] {
                black(),
                new Color(0x402016),
                new Color(0x7c6424),
                new Color(0x506c32),
                new Color(0x70207c),
                new Color(0x70a0c0)
        };
        CLOTHES_SWATCHES = new Color[] {
                new Color(0xcbcbce),
                new Color(0x383838),
                new Color(0xe06040),
                new Color(0x4060e0),
                new Color(0xa040e0),
                new Color(0x309ea4),
                new Color(0xc0709c),
                new Color(0x70c070),
                new Color(0xf8b020),
                new Color(0x784040),
                new Color(0x609038),
                new Color(0x989090)
        };
    }
    protected PokemonStyle(
            final String id, final Bounds2D dims,
            final Animation[] animations
    ) {
        super(id, dims, setUpDirections(), animations, new Layers());

        // Initialize settings
        settings = new StyleSettings();
        settings.add(this, ResourceCodes.QUANTIZE_GBA);
        settings.add(this, ResourceCodes.WARN_ROM_15_COLS);
    }

    private static Directions setUpDirections() {
        return new Directions(NumDirs.FOUR, HORIZONTAL_ANIMS,
                Dir.DOWN, Dir.LEFT, Dir.RIGHT, Dir.UP);
    }

    static ColorSelection clothesSwatch(
            final int index
    ) {
        final String name = switch (index) {
            case 0 -> "Main";
            case 1 -> "Accent";
            case 2 -> "3rd";
            default -> (index + 1) + "th";
        };

        return new ColorSelection(name, true, CLOTHES_SWATCHES);
    }

    static Replacement clothesReplace(final Color input) {
        final Color rgbInput = rgbOnly(input), base;
        final int index;

        if (CLOTH_1.contains(rgbInput)) {
            index = 0;
            base = BASE_CLOTH_1;
        } else if (CLOTH_2.contains(rgbInput)) {
            index = 1;
            base = BASE_CLOTH_2;
        } else if (CLOTH_3.contains(rgbInput)) {
            index = 2;
            base = BASE_CLOTH_3;
        } else if (CLOTH_4.contains(rgbInput)) {
            index = 3;
            base = BASE_CLOTH_4;
        } else {
            index = -1;
            base = black();
        }

        return new Replacement(index, c -> {
            final double is = rgbToSat(input), iv = rgbToValue(input),
                    ch = rgbToHue(c), cs = rgbToSat(c), cv = rgbToValue(c),
                    bs = rgbToSat(base), bv = rgbToValue(base),
                    sRatio = (cs * is) / bs, vRatio = (cv * iv) / bv,
                    s = MathPlus.bounded(0.0, sRatio, 1.0),
                    v = MathPlus.bounded(0.0, vRatio, 1.0);

            return fromHSV(ch, s, v, input.getAlpha());
        });
    }

    static ACLBuilder buildClothes(
            final PokemonStyle style, final String layerID,
            final ColorSelection[] selections
    ) {
        final String[] csv = ParserUtils.readAssetCSV(style.id, layerID);

        final Function<Integer, ColorSelection[]> shortener = l -> {
            final ColorSelection[] shortened = new ColorSelection[l];

            System.arraycopy(selections, 0, shortened, 0, l);

            return shortened;
        };

        final AssetChoiceTemplate[] templates = Arrays.stream(csv)
                .map(s -> s.split(":")).map(s -> {
                    final String code = s[0];
                    final int numSels = Integer.parseInt(s[1]);
                    final ColorSelection[] sels = shortener.apply(numSels);

                    return new AssetChoiceTemplate(code,
                            PokemonStyle::clothesReplace, sels);
                }).toArray(AssetChoiceTemplate[]::new);

        return ACLBuilder.of(layerID, style, templates)
                .setNoAssetChoice(NoAssetChoice.prob(0.0));
    }

    static Replacement replace(final Color input) {
        return replaceWithNSelections(input, 0);
    }

    static Replacement replaceWithNSelections(final Color input, final int n) {
        final Color rgbInput = rgbOnly(input);

        int index = -1;
        Color b = black();

        final List<Set<Color>> REPLS = List.of(
                CLOTH_1, CLOTH_2, CLOTH_3, CLOTH_4);
        final Color[] BASES = new Color[] {
                BASE_CLOTH_1, BASE_CLOTH_2, BASE_CLOTH_3, BASE_CLOTH_4
        };

        final boolean isSkin = SKIN.contains(rgbInput),
                isOutline = SKIN_OUTLINES.contains(rgbInput),
                isHair = HAIR.contains(rgbInput),
                isIris = IRIS.contains(rgbInput),
                isEW = EYE_WHITE.contains(rgbInput);

        for (int i = 0; i < n; i++)
            if (REPLS.get(i).contains(rgbInput)) {
                index = i;
                b = BASES[i];
                break;
            }

        if (isSkin || isOutline) {
            index = n;
            b = BASE_SKIN;
        } else if (isHair) {
            index = n + 1;
            b = BASE_HAIR;
        } else if (isIris) {
            index = n + 2;
            b = BASE_IRIS;
        } else if (isEW) {
            index = n + 3;
            b = BASE_EYE_WHITE;
        }

        final Color base = b;

        return new Replacement(index, c -> {
            final double ih = rgbToHue(input),
                    is = rgbToSat(input), iv = rgbToValue(input),
                    ch = rgbToHue(c), cs = rgbToSat(c),
                    cv = rgbToValue(c),
                    bs = rgbToSat(base), bv = rgbToValue(base),
                    sRatio = (cs * is) / bs, vRatio = (cv * iv) / bv,
                    s = MathPlus.bounded(0.0, sRatio, 1.0),
                    v = MathPlus.bounded(0.0, vRatio, 1.0);

            if (isOutline) {
                // Skin outline
                final double hueDiff = rgbToHue(base) - ih,
                        hue = normalizeHue(ch - hueDiff);

                return fromHSV(hue, s, v);
            }

            return fromHSV(ch, s, v, input.getAlpha());
        });
    }

    @Override
    public String name() {
        return StringUtils.nameFromID(id);
    }

    @Override
    public boolean shipping() {
        return true;
    }

    @Override
    public boolean hasSettings() {
        return settings.has();
    }

    @Override
    public StyleSetting[] getSettings() {
        return settings.array();
    }

    @Override
    public boolean hasPreExportStep() {
        return settings.hasPreExportStep();
    }

    @Override
    public void buildPreExportMenu(final MenuBuilder mb, final Coord2D warningPos) {
        settings.buildPreExportMenu(mb, warningPos);
    }

    @Override
    public GameImage preExportTransform(final GameImage input) {
        return settings.preExportTransform(input);
    }

    @Override
    public void resetPreExport() {
        settings.resetPreExport();
    }

    @Override
    protected void considerations(
            final SpriteAssembler<String, String> assembler
    ) {
        if (settings != null)
            settings.considerations(assembler);
    }
}
