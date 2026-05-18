package net.kenji.colorful_seasons.api;

import net.kenji.colorful_seasons.network.ClientSeasonalColorSyncPacket;
import net.kenji.colorful_seasons.network.ModPacketHandler;
import net.kenji.colorful_seasons.network.ServerSeasonalColorPacket;
import net.kenji.colorful_seasons.screens.SeasonColorConfigScreen;
import net.minecraft.server.level.ServerPlayer;

public class ConfigManager {

    public static void syncSeasonalColorsToClient(ServerPlayer serverPlayer){

        ModPacketHandler.sendToPlayer(new ClientSeasonalColorSyncPacket(
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
        ), serverPlayer);
    }
    public static void syncSeasonalColorsToServer(){

        ModPacketHandler.sendToServer(new ServerSeasonalColorPacket(
                SeasonalColorConfigValues.pendingAffectModdedBlocks,
                SeasonalColorConfigValues.pendingAffectSpruceLeaves,
                SeasonalColorConfigValues.pendingUpdateRealTime,
                SeasonColorConfigScreen.GRASS_SPRING,
                SeasonColorConfigScreen.GRASS_SUMMER,
                SeasonColorConfigScreen.GRASS_AUTUMN,
                SeasonColorConfigScreen.GRASS_WINTER,
                SeasonColorConfigScreen.FOLIAGE_SPRING,
                SeasonColorConfigScreen.FOLIAGE_SUMMER,
                SeasonColorConfigScreen.FOLIAGE_AUTUMN,
                SeasonColorConfigScreen.FOLIAGE_WINTER
        ));
    }
}
