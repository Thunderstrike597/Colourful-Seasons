package net.kenji.colorful_seasons.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kenji.colorful_seasons.api.SeasonColorSettings;
import net.kenji.colorful_seasons.screens.ColorfulSeasonsScreen;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.file.Path;

public class ColorfulSeasonsConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Path: .minecraft/config/colorful_seasons.json
    private static Path getConfigPath() {
        return Minecraft.getInstance().gameDirectory.toPath()
                .resolve("config")
                .resolve("colorful_seasons.json");
    }

    // Plain data class Gson can serialize
    private static class ConfigData {
        int[] grassSpring   = {0, 0, 0};       double grassSpringL   = 0.5;
        int[] grassSummer   = {0, 30, 0};      double grassSummerL   = 0.5;
        int[] grassAutumn   = {86, 10, 10};    double grassAutumnL   = 0.5;
        int[] grassWinter   = {0, 0, 20};      double grassWinterL   = 0.45;

        int[] foliageSpring = {0, 0, 0};       double foliageSpringL   = 0.5;
        int[] foliageSummer = {0, 25, 0};      double foliageSummerL   = 0.5;
        int[] foliageAutumn = {90, 16, 2};     double foliageAutumnL   = 0.65;
        int[] foliageWinter = {2, 12, 98};     double foliageWinterL   = 0.65;
    }

    public static void save() {
        ConfigData data = new ConfigData();

        data.grassSpring  = toArr(ColorfulSeasonsScreen.GRASS_SPRING);
        data.grassSpringL = ColorfulSeasonsScreen.GRASS_SPRING.lightness();
        data.grassSummer  = toArr(ColorfulSeasonsScreen.GRASS_SUMMER);
        data.grassSummerL = ColorfulSeasonsScreen.GRASS_SUMMER.lightness();
        data.grassAutumn  = toArr(ColorfulSeasonsScreen.GRASS_AUTUMN);
        data.grassAutumnL = ColorfulSeasonsScreen.GRASS_AUTUMN.lightness();
        data.grassWinter  = toArr(ColorfulSeasonsScreen.GRASS_WINTER);
        data.grassWinterL = ColorfulSeasonsScreen.GRASS_WINTER.lightness();

        data.foliageSpring  = toArr(ColorfulSeasonsScreen.FOLIAGE_SPRING);
        data.foliageSpringL = ColorfulSeasonsScreen.FOLIAGE_SPRING.lightness();
        data.foliageSummer  = toArr(ColorfulSeasonsScreen.FOLIAGE_SUMMER);
        data.foliageSummerL = ColorfulSeasonsScreen.FOLIAGE_SUMMER.lightness();
        data.foliageAutumn  = toArr(ColorfulSeasonsScreen.FOLIAGE_AUTUMN);
        data.foliageAutumnL = ColorfulSeasonsScreen.FOLIAGE_AUTUMN.lightness();
        data.foliageWinter  = toArr(ColorfulSeasonsScreen.FOLIAGE_WINTER);
        data.foliageWinterL = ColorfulSeasonsScreen.FOLIAGE_WINTER.lightness();

        try (Writer writer = new FileWriter(getConfigPath().toFile())) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        File file = getConfigPath().toFile();
        if (!file.exists()) return; // first launch, use defaults

        try (Reader reader = new FileReader(file)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);

            ColorfulSeasonsScreen.GRASS_SPRING  = fromArr(data.grassSpring,  data.grassSpringL);
            ColorfulSeasonsScreen.GRASS_SUMMER  = fromArr(data.grassSummer,  data.grassSummerL);
            ColorfulSeasonsScreen.GRASS_AUTUMN  = fromArr(data.grassAutumn,  data.grassAutumnL);
            ColorfulSeasonsScreen.GRASS_WINTER  = fromArr(data.grassWinter,  data.grassWinterL);

            ColorfulSeasonsScreen.FOLIAGE_SPRING  = fromArr(data.foliageSpring,  data.foliageSpringL);
            ColorfulSeasonsScreen.FOLIAGE_SUMMER  = fromArr(data.foliageSummer,  data.foliageSummerL);
            ColorfulSeasonsScreen.FOLIAGE_AUTUMN  = fromArr(data.foliageAutumn,  data.foliageAutumnL);
            ColorfulSeasonsScreen.FOLIAGE_WINTER  = fromArr(data.foliageWinter,  data.foliageWinterL);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[] toArr(SeasonColorSettings s) {
        return new int[]{s.r(), s.g(), s.b()};
    }

    private static SeasonColorSettings fromArr(int[] arr, double lightness) {
        return new SeasonColorSettings(arr[0], arr[1], arr[2], lightness);
    }
}