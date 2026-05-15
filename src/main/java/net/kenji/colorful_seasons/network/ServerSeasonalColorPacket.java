package net.kenji.colorful_seasons.network;

import net.kenji.colorful_seasons.api.SeasonColorSettings;
import net.kenji.colorful_seasons.api.SeasonalColorManager;
import net.kenji.colorful_seasons.config.ColorfulSeasonsConfig;
import net.kenji.colorful_seasons.config.ColorfulSeasonsServerConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ServerSeasonalColorPacket {

    public final boolean updateRealtime;
    // All 8 season settings
    public final SeasonColorSettings grassSpring;
    public final SeasonColorSettings grassSummer;
    public final SeasonColorSettings grassAutumn;
    public final SeasonColorSettings grassWinter;
    public final SeasonColorSettings foliageSpring;
    public final SeasonColorSettings foliageSummer;
    public final SeasonColorSettings foliageAutumn;
    public final SeasonColorSettings foliageWinter;

    public ServerSeasonalColorPacket(
            boolean updateRealtime,
            SeasonColorSettings grassSpring, SeasonColorSettings grassSummer,
            SeasonColorSettings grassAutumn, SeasonColorSettings grassWinter,
            SeasonColorSettings foliageSpring, SeasonColorSettings foliageSummer,
            SeasonColorSettings foliageAutumn, SeasonColorSettings foliageWinter) {
        this.updateRealtime = updateRealtime;
        this.grassSpring   = grassSpring;
        this.grassSummer   = grassSummer;
        this.grassAutumn   = grassAutumn;
        this.grassWinter   = grassWinter;
        this.foliageSpring = foliageSpring;
        this.foliageSummer = foliageSummer;
        this.foliageAutumn = foliageAutumn;
        this.foliageWinter = foliageWinter;
    }

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


            if (!SeasonalColorManager.canAdjustSeasonalColor(player)) return;

            // Apply to the server-side manager
            SeasonalColorManager.updateRealTime = packet.updateRealtime;
            SeasonalColorManager.GRASS_SPRING   = packet.grassSpring;
            SeasonalColorManager.GRASS_SUMMER   = packet.grassSummer;
            SeasonalColorManager.GRASS_AUTUMN   = packet.grassAutumn;
            SeasonalColorManager.GRASS_WINTER   = packet.grassWinter;
            SeasonalColorManager.FOLIAGE_SPRING = packet.foliageSpring;
            SeasonalColorManager.FOLIAGE_SUMMER = packet.foliageSummer;
            SeasonalColorManager.FOLIAGE_AUTUMN = packet.foliageAutumn;
            SeasonalColorManager.FOLIAGE_WINTER = packet.foliageWinter;

            // TODO: broadcast updated settings back to all clients
            ModPacketHandler.sendToAll(new ClientSeasonalColorSyncPacket(
                    packet.updateRealtime,
                    SeasonalColorManager.GRASS_SPRING,
                    SeasonalColorManager.GRASS_SUMMER,
                    SeasonalColorManager.GRASS_AUTUMN,
                    SeasonalColorManager.GRASS_WINTER,
                    SeasonalColorManager.FOLIAGE_SPRING,
                    SeasonalColorManager.FOLIAGE_SUMMER,
                    SeasonalColorManager.FOLIAGE_AUTUMN,
                    SeasonalColorManager.FOLIAGE_WINTER
            ));
            if(!SeasonalColorManager.isDedicatedServer(player)){
                ColorfulSeasonsConfig.save();
            }
            else{
                ColorfulSeasonsServerConfig.save(player.getServer());
            }

        });
        ctx.get().setPacketHandled(true);
    }
}