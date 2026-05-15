package net.kenji.colorful_seasons.network;

import net.kenji.colorful_seasons.api.SeasonColorSettings;
import net.kenji.colorful_seasons.api.SeasonalColorManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientSeasonalColorSyncPacket {

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


    public ClientSeasonalColorSyncPacket(boolean updateRealtime, SeasonColorSettings grassSpring, SeasonColorSettings grassSummer, SeasonColorSettings grassAutumn, SeasonColorSettings grassWinter, SeasonColorSettings foliageSpring, SeasonColorSettings foliageSummer, SeasonColorSettings foliageAutumn, SeasonColorSettings foliageWinter) {
        this.updateRealtime = updateRealtime;
        this.grassSpring = grassSpring;
        this.grassSummer = grassSummer;
        this.grassAutumn = grassAutumn;
        this.grassWinter = grassWinter;
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


    public static void encode(ClientSeasonalColorSyncPacket packet, FriendlyByteBuf buf) {
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
    public static void handle(ClientSeasonalColorSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getDirection().getReceptionSide().isClient()) {
                executeOnClient(packet);
            }
        });
        ctx.get().setPacketHandled(true);
    }
    @OnlyIn(Dist.CLIENT)
    private static void executeOnClient(ClientSeasonalColorSyncPacket packet){
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        SeasonalColorManager.updateRealTime = packet.updateRealtime;
        SeasonalColorManager.GRASS_SPRING   = packet.grassSpring;
        SeasonalColorManager.GRASS_SUMMER   = packet.grassSummer;
        SeasonalColorManager.GRASS_AUTUMN   = packet.grassAutumn;
        SeasonalColorManager.GRASS_WINTER   = packet.grassWinter;
        SeasonalColorManager.FOLIAGE_SPRING = packet.foliageSpring;
        SeasonalColorManager.FOLIAGE_SUMMER = packet.foliageSummer;
        SeasonalColorManager.FOLIAGE_AUTUMN = packet.foliageAutumn;
        SeasonalColorManager.FOLIAGE_WINTER = packet.foliageWinter;
        if(SeasonalColorManager.updateRealTime)
            Minecraft.getInstance().levelRenderer.allChanged();
    }

}