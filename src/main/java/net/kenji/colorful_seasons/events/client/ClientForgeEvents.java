package net.kenji.colorful_seasons.events.client;

import net.kenji.colorful_seasons.ColorfulSeasons;
import net.kenji.colorful_seasons.keybinds.ModKeybinds;
import net.kenji.colorful_seasons.screens.ColorfulSeasonsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ColorfulSeasons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)

public class ClientForgeEvents {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (ModKeybinds.OPEN_CONFIG_SCREEN.consumeClick()) {
            Minecraft.getInstance().setScreen(
                    new ColorfulSeasonsScreen(Component.literal("Colorful Seasons"), Minecraft.getInstance().screen)
            );
        }
    }
}
