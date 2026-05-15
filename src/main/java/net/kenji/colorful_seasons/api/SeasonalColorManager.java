package net.kenji.colorful_seasons.api;

import net.kenji.colorful_seasons.network.ClientSeasonalColorSyncPacket;
import net.kenji.colorful_seasons.network.ModPacketHandler;
import net.kenji.colorful_seasons.network.ServerSeasonalColorPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class SeasonalColorManager {
    public static boolean updateRealTime = false;


    public static SeasonColorSettings GRASS_SPRING   = new SeasonColorSettings(  10, 68, 30, 0.5);
    public static SeasonColorSettings GRASS_SUMMER   = new SeasonColorSettings(  25, 85,  28, 0.5);
    public static SeasonColorSettings GRASS_AUTUMN   = new SeasonColorSettings( 60, 22,  9, 0.5);
    public static SeasonColorSettings GRASS_WINTER   = new SeasonColorSettings(  8, 40, 45, 0.5);
    public static SeasonColorSettings FOLIAGE_SPRING = new SeasonColorSettings(  0, 95, 38, 0.5);
    public static SeasonColorSettings FOLIAGE_SUMMER = new SeasonColorSettings(  0, 25,  0, 0.5);
    public static SeasonColorSettings FOLIAGE_AUTUMN = new SeasonColorSettings( 85,  8, 18, 0.5);
    public static SeasonColorSettings FOLIAGE_WINTER = new SeasonColorSettings( 0, 60, 92, 0.6);


    public static boolean isDedicatedServer(Player player){
        MinecraftServer server = player.getServer();
        if (server == null) return false;

       return server.isDedicatedServer();
    }


    public static boolean canAdjustSeasonalColor(Player player) {
        // Check if on dedicated server vs integrated (singleplayer)

        if (isDedicatedServer(player)) {
            // On dedicated server: require level 4 OP
            return player.hasPermissions(4);
        } else {
            // Integrated/singleplayer: always allow
            return true;
        }
    }
    public static void syncSeasonalColorsToClient(ServerPlayer serverPlayer){

        ModPacketHandler.sendToPlayer(new ClientSeasonalColorSyncPacket(
                SeasonalColorManager.updateRealTime,
                GRASS_SPRING,
                GRASS_SUMMER,
                GRASS_AUTUMN,
                GRASS_WINTER,
                FOLIAGE_SPRING,
                FOLIAGE_SUMMER,
                FOLIAGE_AUTUMN,
                FOLIAGE_WINTER
        ), serverPlayer);
    }

}
