package net.kenji.colorful_seasons.events;

import net.kenji.colorful_seasons.ColorfulSeasons;
import net.kenji.colorful_seasons.api.ConfigManager;
import net.kenji.colorful_seasons.api.SeasonalColorConfigValues;
import net.kenji.colorful_seasons.config.ColorfulSeasonsConfig;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ColorfulSeasons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        if(event.getEntity() instanceof ServerPlayer serverPlayer){
            if(!SeasonalColorConfigValues.isDedicatedServer(serverPlayer)){
                ColorfulSeasonsConfig.load();
            }
            else{
                if(!serverPlayer.level().isClientSide())
                    ConfigManager.syncSeasonalColorsToClient(serverPlayer);
            }
        }
    }
}
