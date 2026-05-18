package net.kenji.colorful_seasons.network;

import net.kenji.colorful_seasons.ColorfulSeasons;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.*;
import net.minecraftforge.network.SimpleChannel;


import net.kenji.colorful_seasons.ColorfulSeasons;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public class ModPacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(ColorfulSeasons.MODID, "main"))
            .networkProtocolVersion(1)
            .clientAcceptedVersions((status, version) -> true)
            .serverAcceptedVersions((status, version) -> true)
            .simpleChannel();

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {

        INSTANCE.messageBuilder(ServerSeasonalColorPacket.class, id())
                .encoder(ServerSeasonalColorPacket::encode)
                .decoder(ServerSeasonalColorPacket::decode)
                .consumerMainThread(ServerSeasonalColorPacket::handle)
                .add();

        INSTANCE.messageBuilder(ClientSeasonalColorSyncPacket.class, id())
                .encoder(ClientSeasonalColorSyncPacket::encode)
                .decoder(ClientSeasonalColorSyncPacket::decode)
                .consumerMainThread(ClientSeasonalColorSyncPacket::handle)
                .add();
    }

    public static void sendToServer(Object packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }

    public static void sendToPlayer(Object packet, ServerPlayer player) {
        INSTANCE.send(packet, PacketDistributor.PLAYER.with(player));
    }

    public static void sendToAll(Object packet) {
        INSTANCE.send(packet, PacketDistributor.ALL.noArg());
    }
}