package net.kenji.colorful_seasons;

import net.kenji.colorful_seasons.api.SeasonColorSettings;
import net.kenji.colorful_seasons.screens.ColorfulSeasonsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.init.ModTags;
import sereneseasons.season.SeasonColorHandlers;

public class ExaggeratedColorOverride implements SeasonColorHandlers.ColorOverride {

    private final SeasonColorHandlers.ResolverType type;

    // --- Grass ---
    private static final SeasonColorSettings GRASS_SPRING  = new SeasonColorSettings(  0,  0,  0,  0.5);  // no change
    private static final SeasonColorSettings GRASS_SUMMER  = new SeasonColorSettings(  0, 30,  0,  0.5);  // greener
    private static final SeasonColorSettings GRASS_AUTUMN  = new SeasonColorSettings(86,  10,  10,  0.5);  // red
    private static final SeasonColorSettings GRASS_WINTER  = new SeasonColorSettings(  0,  0, 20,  0.45); // slight blue, darker

    // --- Foliage ---
    private static final SeasonColorSettings FOLIAGE_SPRING  = new SeasonColorSettings(  0,  0,  0,  0.5);
    private static final SeasonColorSettings FOLIAGE_SUMMER  = new SeasonColorSettings(  0, 25,  0,  0.5);
    private static final SeasonColorSettings FOLIAGE_AUTUMN  = new SeasonColorSettings( 90,  16,  2,  0.65);  // orange-red
    private static final SeasonColorSettings FOLIAGE_WINTER  = new SeasonColorSettings(  2, 12, 98,  0.65); // blue, slightly dark
    public ExaggeratedColorOverride(SeasonColorHandlers.ResolverType type) {
        this.type = type;
    }

    @Override
    public int apply(int originalColor, int seasonalColor, int currentColor,
                     Holder<Biome> biomeHolder, double x, double z) {

        Level level = Minecraft.getInstance().level;
        if (level == null) return currentColor;
        if (biomeHolder.is(sereneseasons.init.ModTags.Biomes.TROPICAL_BIOMES) || (biomeHolder.is(ModTags.Biomes.BLACKLISTED_BIOMES))) {
            return currentColor;
        }
        ISeasonState seasonState = SeasonHelper.getSeasonState(level);
        Season season = seasonState.getSeason();

        SeasonColorSettings settings = getSettings(season);
        return applySettings(seasonalColor, settings);
    }

    private SeasonColorSettings getSettings(Season season) {
        boolean isGrass = type == SeasonColorHandlers.ResolverType.GRASS;
        return switch (season) {
            case SPRING -> isGrass ? ColorfulSeasonsScreen.GRASS_SPRING : ColorfulSeasonsScreen.FOLIAGE_SPRING;
            case SUMMER -> isGrass ? ColorfulSeasonsScreen.GRASS_SUMMER : ColorfulSeasonsScreen.FOLIAGE_SUMMER;
            case AUTUMN -> isGrass ? ColorfulSeasonsScreen.GRASS_AUTUMN: ColorfulSeasonsScreen.FOLIAGE_AUTUMN;
            case WINTER -> isGrass ? ColorfulSeasonsScreen.GRASS_WINTER : ColorfulSeasonsScreen.FOLIAGE_WINTER;
        };
    }
    public SeasonColorSettings getSettingsOrDefault(SeasonColorSettings settings, SeasonColorSettings defaultSettings){
        if(settings != null){
            return settings;
        }
        return defaultSettings;
    }

    private int applySettings(int seasonalColor, SeasonColorSettings s) {
        int sr = (seasonalColor >> 16) & 0xFF;
        int sg = (seasonalColor >> 8)  & 0xFF;
        int sb =  seasonalColor        & 0xFF;

        // Build target color from settings (0-100 maps to 0-255)
        int targetR = (int)(s.r() / 100.0 * 255);
        int targetG = (int)(s.g() / 100.0 * 255);
        int targetB = (int)(s.b() / 100.0 * 255);

        // Blend strength is the highest channel value
        // So r=100,g=0,b=0 blends ALL channels fully toward (255, 0, 0)
        double blendStrength = Math.max(s.r(), Math.max(s.g(), s.b())) / 100.0;

        int r = clamp((int)(sr + (targetR - sr) * blendStrength));
        int g = clamp((int)(sg + (targetG - sg) * blendStrength));
        int b = clamp((int)(sb + (targetB - sb) * blendStrength));

        // Lightness (unchanged)
        if (s.lightness() < 0.5) {
            double darken = s.lightness() / 0.5;
            r = clamp((int)(r * darken));
            g = clamp((int)(g * darken));
            b = clamp((int)(b * darken));
        } else if (s.lightness() > 0.5) {
            double lighten = (s.lightness() - 0.5) / 0.5;
            r = clamp((int)(r + (255 - r) * lighten));
            g = clamp((int)(g + (255 - g) * lighten));
            b = clamp((int)(b + (255 - b) * lighten));
        }

        return (r << 16) | (g << 8) | b;
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}