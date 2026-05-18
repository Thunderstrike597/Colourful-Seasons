package net.kenji.colorful_seasons.network;

import net.kenji.colorful_seasons.api.SeasonColorSettings;
import net.kenji.colorful_seasons.api.SeasonalColorConfigValues;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * @param grassSpring All 8 season settings
 */
public record ClientSeasonalColorSyncPacket(boolean affectModdedBlocks, boolean affectSpruceLeaves, boolean updateRealtime,
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


    public static void encode(ClientSeasonalColorSyncPacket packet, FriendlyByteBuf buf) {
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


    public static ClientSeasonalColorSyncPacket decode(FriendlyByteBuf buf) {
        return new ClientSeasonalColorSyncPacket(
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

    // Handle: Process the packet on the receiving side
    public static void handle(ClientSeasonalColorSyncPacket packet, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.isClientSide()) {
                executeOnClient(packet);
            }
        });
        ctx.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void executeOnClient(ClientSeasonalColorSyncPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
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
        SeasonalColorConfigValues.pendingAffectModdedBlocks = SeasonalColorConfigValues.affectModdedBlocks;
        SeasonalColorConfigValues.pendingAffectSpruceLeaves = SeasonalColorConfigValues.affectSpruceLeaves;
        SeasonalColorConfigValues.pendingUpdateRealTime = SeasonalColorConfigValues.updateRealTime;
        if (SeasonalColorConfigValues.updateRealTime)
            Minecraft.getInstance().levelRenderer.allChanged();
    }

}