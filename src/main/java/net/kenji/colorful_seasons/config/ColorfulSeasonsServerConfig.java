package net.kenji.colorful_seasons.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kenji.colorful_seasons.api.SeasonColorSettings;
import net.kenji.colorful_seasons.api.SeasonalColorConfigValues;
import net.kenji.colorful_seasons.network.ClientSeasonalColorSyncPacket;
import net.kenji.colorful_seasons.network.ModPacketHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;

import java.io.*;
import java.nio.file.Path;

public class ColorfulSeasonsServerConfig {


    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path getConfigPath(MinecraftServer server) {
        return server.getWorldPath(LevelResource.ROOT)
                .resolve("serverconfig")
                .resolve("colorful_seasons_server.json");
    }

    // Plain data class Gson can serialize
    private static class ConfigData {
        boolean affectModdedBlocks = false;
        boolean affectSpruceLeaves = false;
        boolean updateRealTime = false;

        int[] grassSpring   = {0, 0, 0};       double grassSpringL   = 0.5;
        int[] grassSummer   = {0, 30, 0};      double grassSummerL   = 0.5;
        int[] grassAutumn   = {86, 10, 10};    double grassAutumnL   = 0.5;
        int[] grassWinter   = {0, 0, 20};      double grassWinterL   = 0.45;

        int[] foliageSpring = {0, 0, 0};       double foliageSpringL   = 0.5;
        int[] foliageSummer = {0, 25, 0};      double foliageSummerL   = 0.5;
        int[] foliageAutumn = {90, 16, 2};     double foliageAutumnL   = 0.65;
        int[] foliageWinter = {2, 12, 98};     double foliageWinterL   = 0.65;
    }

    public static void save(MinecraftServer server) {
        ConfigData data = new ConfigData();
        data.affectModdedBlocks = SeasonalColorConfigValues.affectModdedBlocks;
        data.affectSpruceLeaves = SeasonalColorConfigValues.affectSpruceLeaves;
        data.updateRealTime = SeasonalColorConfigValues.updateRealTime;

        data.grassSpring  = toArr(SeasonalColorConfigValues.GRASS_SPRING);
        data.grassSpringL = SeasonalColorConfigValues.GRASS_SPRING.lightness();
        data.grassSummer  = toArr(SeasonalColorConfigValues.GRASS_SUMMER);
        data.grassSummerL = SeasonalColorConfigValues.GRASS_SUMMER.lightness();
        data.grassAutumn  = toArr(SeasonalColorConfigValues.GRASS_AUTUMN);
        data.grassAutumnL = SeasonalColorConfigValues.GRASS_AUTUMN.lightness();
        data.grassWinter  = toArr(SeasonalColorConfigValues.GRASS_WINTER);
        data.grassWinterL = SeasonalColorConfigValues.GRASS_WINTER.lightness();

        data.foliageSpring  = toArr(SeasonalColorConfigValues.FOLIAGE_SPRING);
        data.foliageSpringL = SeasonalColorConfigValues.FOLIAGE_SPRING.lightness();
        data.foliageSummer  = toArr(SeasonalColorConfigValues.FOLIAGE_SUMMER);
        data.foliageSummerL = SeasonalColorConfigValues.FOLIAGE_SUMMER.lightness();
        data.foliageAutumn  = toArr(SeasonalColorConfigValues.FOLIAGE_AUTUMN);
        data.foliageAutumnL = SeasonalColorConfigValues.FOLIAGE_AUTUMN.lightness();
        data.foliageWinter  = toArr(SeasonalColorConfigValues.FOLIAGE_WINTER);
        data.foliageWinterL = SeasonalColorConfigValues.FOLIAGE_WINTER.lightness();

        try {
            File file = getConfigPath(server).toFile();
            file.getParentFile().mkdirs(); // ensure config dir exists
            try (Writer writer = new FileWriter(file)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load(MinecraftServer server) {
        File file = getConfigPath(server).toFile();
        if (!file.exists()) return; // first launch, use defaults

        try (Reader reader = new FileReader(file)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            SeasonalColorConfigValues.affectModdedBlocks = data.affectModdedBlocks;
            SeasonalColorConfigValues.affectSpruceLeaves = data.affectSpruceLeaves;
            SeasonalColorConfigValues.updateRealTime = data.updateRealTime;

            SeasonalColorConfigValues.GRASS_SPRING  = fromArr(data.grassSpring,  data.grassSpringL);
            SeasonalColorConfigValues.GRASS_SUMMER  = fromArr(data.grassSummer,  data.grassSummerL);
            SeasonalColorConfigValues.GRASS_AUTUMN  = fromArr(data.grassAutumn,  data.grassAutumnL);
            SeasonalColorConfigValues.GRASS_WINTER  = fromArr(data.grassWinter,  data.grassWinterL);

            SeasonalColorConfigValues.FOLIAGE_SPRING  = fromArr(data.foliageSpring,  data.foliageSpringL);
            SeasonalColorConfigValues.FOLIAGE_SUMMER  = fromArr(data.foliageSummer,  data.foliageSummerL);
            SeasonalColorConfigValues.FOLIAGE_AUTUMN  = fromArr(data.foliageAutumn,  data.foliageAutumnL);
            SeasonalColorConfigValues.FOLIAGE_WINTER  = fromArr(data.foliageWinter,  data.foliageWinterL);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void onServerStarting(ServerStartingEvent event) {
        System.out.println("[ColorfulSeasons] Server starting, config path: " + getConfigPath(event.getServer()).toAbsolutePath());
        load(event.getServer());
        // Broadcast loaded values to any already-connected clients
        ModPacketHandler.sendToAll(new ClientSeasonalColorSyncPacket(
                SeasonalColorConfigValues.affectModdedBlocks,
                SeasonalColorConfigValues.affectSpruceLeaves,
                SeasonalColorConfigValues.updateRealTime,
                SeasonalColorConfigValues.GRASS_SPRING,
                SeasonalColorConfigValues.GRASS_SUMMER,
                SeasonalColorConfigValues.GRASS_AUTUMN,
                SeasonalColorConfigValues.GRASS_WINTER,
                SeasonalColorConfigValues.FOLIAGE_SPRING,
                SeasonalColorConfigValues.FOLIAGE_SUMMER,
                SeasonalColorConfigValues.FOLIAGE_AUTUMN,
                SeasonalColorConfigValues.FOLIAGE_WINTER
        ));
    }

    public static void onServerStopping(ServerStoppingEvent event) {
        save(event.getServer());
    }
    private static int[] toArr(SeasonColorSettings s) {
        return new int[]{s.r(), s.g(), s.b()};
    }

    private static SeasonColorSettings fromArr(int[] arr, double lightness) {
        return new SeasonColorSettings(arr[0], arr[1], arr[2], lightness);
    }
}