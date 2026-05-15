package net.kenji.colorful_seasons.network;

import net.kenji.colorful_seasons.ColorfulSeasons;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ColorfulSeasons.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;
    private static int id() { return packetId++; }

    public static void register() {
        INSTANCE.messageBuilder(ServerSeasonalColorPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ServerSeasonalColorPacket::decode)
                .encoder(ServerSeasonalColorPacket::encode)
                .consumerMainThread(ServerSeasonalColorPacket::handle)
                .add();
        INSTANCE.messageBuilder(ClientSeasonalColorSyncPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientSeasonalColorSyncPacket::decode)
                .encoder(ClientSeasonalColorSyncPacket::encode)
                .consumerMainThread(ClientSeasonalColorSyncPacket::handle)
                .add();
    }

    public static void sendToServer(Object packet) { INSTANCE.sendToServer(packet); }
    public static void sendToPlayer(Object packet, ServerPlayer player) { INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet); }
    public static void sendToAll(Object packet) { INSTANCE.send(PacketDistributor.ALL.noArg(), packet); }
}