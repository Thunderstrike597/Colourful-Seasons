package net.kenji.colorful_seasons.network;

import net.kenji.colorful_seasons.ColorfulSeasons;
import net.kenji.colorful_seasons.api.SeasonColorSettings;
import net.kenji.colorful_seasons.api.SeasonalColorConfigValues;
import net.kenji.colorful_seasons.config.ColorfulSeasonsConfig;
import net.kenji.colorful_seasons.config.ColorfulSeasonsServerConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jline.utils.Log;

/**
 * @param grassSpring All 8 season settings
 */
public record ServerSeasonalColorPacket(boolean affectModdedBlocks, boolean affectSpruceLeaves, boolean updateRealtime,
                                        SeasonColorSettings grassSpring, SeasonColorSettings grassSummer,
                                        SeasonColorSettings grassAutumn, SeasonColorSettings grassWinter,
                                        SeasonColorSettings foliageSpring, SeasonColorSettings foliageSummer,
                                        SeasonColorSettings foliageAutumn, SeasonColorSettings foliageWinter) implements CustomPacketPayload {

    public static CustomPacketPayload.Type<ServerSeasonalColorPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ColorfulSeasons.MODID, "sync_server_seasonal_color"));

    public static StreamCodec<FriendlyByteBuf, ServerSeasonalColorPacket> STREAM_CODEC =
            StreamCodec.of(ServerSeasonalColorPacket::encode, ServerSeasonalColorPacket::decode);

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

    public static void encode(FriendlyByteBuf buf, ServerSeasonalColorPacket packet) {
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


    public static void handle(ServerSeasonalColorPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if(!(player instanceof ServerPlayer serverPlayer)) return;



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
            Log.info("Logging Server Sync!");
            if (!SeasonalColorConfigValues.isDedicatedServer(serverPlayer)) {
                ColorfulSeasonsConfig.save();
            } else {
                ColorfulSeasonsServerConfig.save(serverPlayer.getServer());
            }

        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}