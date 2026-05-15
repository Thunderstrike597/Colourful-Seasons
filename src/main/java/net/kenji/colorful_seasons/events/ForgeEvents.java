package net.kenji.colorful_seasons.events;

import net.kenji.colorful_seasons.ColorfulSeasons;
import net.kenji.colorful_seasons.api.SeasonalColorManager;
import net.kenji.colorful_seasons.config.ColorfulSeasonsConfig;
import net.kenji.colorful_seasons.keybinds.ModKeybinds;
import net.kenji.colorful_seasons.network.ModPacketHandler;
import net.kenji.colorful_seasons.screens.ColorfulSeasonsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ColorfulSeasons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        if(event.getEntity() instanceof ServerPlayer serverPlayer){
            if(!SeasonalColorManager.isDedicatedServer(serverPlayer)){
                ColorfulSeasonsConfig.load();
            }
            else{
                if(!serverPlayer.level().isClientSide())
                    SeasonalColorManager.syncSeasonalColorsToClient(serverPlayer);
            }
        }
    }
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (ModKeybinds.OPEN_CONFIG_SCREEN.consumeClick()) {
            Minecraft.getInstance().setScreen(
                    new ColorfulSeasonsScreen(Component.literal("Colorful Seasons"))
            );
        }
    }


}
