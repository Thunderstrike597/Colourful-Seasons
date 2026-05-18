package net.kenji.colorful_seasons;

import net.kenji.colorful_seasons.api.SeasonalColorConfigValues;
import net.kenji.colorful_seasons.api.SeasonColorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.init.ModTags;
import sereneseasons.season.SeasonColorHandlers;

public record SeasonalColorOverride(
        SeasonColorHandlers.ResolverType type) implements SeasonColorHandlers.ColorOverride {


    @Override
    public int apply(int originalColor, int seasonalColor, int currentColor,
                     Holder<Biome> biomeHolder, double x, double z) {

        Level level = Minecraft.getInstance().level;
        if (level == null) return currentColor;
        if (biomeHolder.is(ModTags.Biomes.TROPICAL_BIOMES) || (biomeHolder.is(ModTags.Biomes.BLACKLISTED_BIOMES))) {
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
            case SPRING -> isGrass ? SeasonalColorConfigValues.GRASS_SPRING : SeasonalColorConfigValues.FOLIAGE_SPRING;
            case SUMMER -> isGrass ? SeasonalColorConfigValues.GRASS_SUMMER : SeasonalColorConfigValues.FOLIAGE_SUMMER;
            case AUTUMN -> isGrass ? SeasonalColorConfigValues.GRASS_AUTUMN : SeasonalColorConfigValues.FOLIAGE_AUTUMN;
            case WINTER -> isGrass ? SeasonalColorConfigValues.GRASS_WINTER : SeasonalColorConfigValues.FOLIAGE_WINTER;
        };
    }


    public static int applySettings(int seasonalColor, SeasonColorSettings s) {
        int sr = (seasonalColor >> 16) & 0xFF;
        int sg = (seasonalColor >> 8) & 0xFF;
        int sb = seasonalColor & 0xFF;

        // Build target color from settings (0-100 maps to 0-255)
        int targetR = (int) (s.r() / 100.0 * 255);
        int targetG = (int) (s.g() / 100.0 * 255);
        int targetB = (int) (s.b() / 100.0 * 255);

        // Blend strength is the highest channel value
        // So r=100,g=0,b=0 blends ALL channels fully toward (255, 0, 0)
        double blendStrength = Math.max(s.r(), Math.max(s.g(), s.b())) / 100.0;

        int r = clamp((int) (sr + (targetR - sr) * blendStrength));
        int g = clamp((int) (sg + (targetG - sg) * blendStrength));
        int b = clamp((int) (sb + (targetB - sb) * blendStrength));

        // Lightness (unchanged)
        if (s.lightness() < 0.5) {
            double darken = s.lightness() / 0.5;
            r = clamp((int) (r * darken));
            g = clamp((int) (g * darken));
            b = clamp((int) (b * darken));
        } else if (s.lightness() > 0.5) {
            double lighten = (s.lightness() - 0.5) / 0.5;
            r = clamp((int) (r + (255 - r) * lighten));
            g = clamp((int) (g + (255 - g) * lighten));
            b = clamp((int) (b + (255 - b) * lighten));
        }

        return (r << 16) | (g << 8) | b;
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}