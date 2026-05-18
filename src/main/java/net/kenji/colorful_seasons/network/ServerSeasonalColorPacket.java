package net.kenji.colorful_seasons.network;

import net.kenji.colorful_seasons.api.SeasonColorSettings;
import net.kenji.colorful_seasons.api.SeasonalColorConfigValues;
import net.kenji.colorful_seasons.config.ColorfulSeasonsConfig;
import net.kenji.colorful_seasons.config.ColorfulSeasonsServerConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @param grassSpring All 8 season settings
 */
public record ServerSeasonalColorPacket(boolean affectModdedBlocks, boolean affectSpruceLeaves, boolean updateRealtime,
                                        SeasonColorSettings grassSpring, SeasonColorSettings grassSummer,
                                        SeasonColorSettings grassAutumn, SeasonColorSettings grassWinter,
                                        SeasonColorSettings foliageSpring, SeasonColorSettings foliageSummer,
                                        SeasonColorSettings foliageAutumn, SeasonColorSettings foliageWinter) {

    // Helper to write one SeasonColorSettings
    private static void writeSettings(FriendlyByteBuf buf, SeasonColorSettings s) {
        buf.writeInt(s.r());
        buf.writeInt(s.g());
        buf.writeInt(s.b());
        buf.writeDouble(s.lightness());
    }

    // Helper to read one SeasonColorSettings
    private static SeasonColorSettings readSettings(FriendlyByteBuf buf) {
        return new SeasonColorSettings(
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readDouble()
        );
    }

    public static void encode(ServerSeasonalColorPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.affectModdedBlocks);
        buf.writeBoolean(packet.affectSpruceLeaves);
        buf.writeBoolean(packet.updateRealtime);
        writeSettings(buf, packet.grassSpring);
        writeSettings(buf, packet.grassSummer);
        writeSettings(buf, packet.grassAutumn);
        writeSettings(buf, packet.grassWinter);
        writeSettings(buf, packet.foliageSpring);
        writeSettings(buf, packet.foliageSummer);
        writeSettings(buf, packet.foliageAutumn);
        writeSettings(buf, packet.foliageWinter);
    }

    public static ServerSeasonalColorPacket decode(FriendlyByteBuf buf) {
        return new ServerSeasonalColorPacket(
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                readSettings(buf), // grassSpring
                readSettings(buf), // grassSummer
                readSettings(buf), // grassAutumn
                readSettings(buf), // grassWinter
                readSettings(buf), // foliageSpring
                readSettings(buf), // foliageSummer
                readSettings(buf), // foliageAutumn
                readSettings(buf)  // foliageWinter
        );
    }

    public static void handle(ServerSeasonalColorPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;


            if (!SeasonalColorConfigValues.canAdjustSeasonalColor(player)) return;

            // Apply to the server-side manager
            SeasonalColorConfigValues.affectModdedBlocks = packet.affectModdedBlocks;
            SeasonalColorConfigValues.affectSpruceLeaves = packet.affectSpruceLeaves;
            SeasonalColorConfigValues.updateRealTime = packet.updateRealtime;
            SeasonalColorConfigValues.GRASS_SPRING = packet.grassSpring;
            SeasonalColorConfigValues.GRASS_SUMMER = packet.grassSummer;
            SeasonalColorConfigValues.GRASS_AUTUMN = packet.grassAutumn;
            SeasonalColorConfigValues.GRASS_WINTER = packet.grassWinter;
            SeasonalColorConfigValues.FOLIAGE_SPRING = packet.foliageSpring;
            SeasonalColorConfigValues.FOLIAGE_SUMMER = packet.foliageSummer;
            SeasonalColorConfigValues.FOLIAGE_AUTUMN = packet.foliageAutumn;
            SeasonalColorConfigValues.FOLIAGE_WINTER = packet.foliageWinter;

            // TODO: broadcast updated settings back to all clients
            ModPacketHandler.sendToAll(new ClientSeasonalColorSyncPacket(
                    packet.affectModdedBlocks,
                    packet.affectSpruceLeaves,
                    packet.updateRealtime,
                    SeasonalColorConfigValues.GRASS_SPRING,
                    SeasonalColorConfigValues.GRASS_SUMMER,
                    SeasonalColorConfigValues.GRASS_AUTUMN,
                    SeasonalColorConfigValues.GRASS_WINTER,
                    SeasonalColorConfigValues.FOLIAGE_SPRING,
                    SeasonalColorConfigValues.FOLIAGE_SUMMER,
                    SeasonalColorConfigValues.FOLIAGE_AUTUMN,
                    SeasonalColorConfigValues.FOLIAGE_WINTER
            ));
            if (!SeasonalColorConfigValues.isDedicatedServer(player)) {
                ColorfulSeasonsConfig.save();
            } else {
                ColorfulSeasonsServerConfig.save(player.getServer());
            }

        });
        ctx.get().setPacketHandled(true);
    }
}