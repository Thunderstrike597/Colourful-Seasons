package net.kenji.colorful_seasons.events.client;

import glitchcore.event.TickEvent;
import net.kenji.colorful_seasons.ColorfulSeasons;
import net.kenji.colorful_seasons.keybinds.ModKeybinds;
import net.kenji.colorful_seasons.screens.ColorfulSeasonsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = ColorfulSeasons.MODID, value = Dist.CLIENT)

public class ClientForgeEvents {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        if (ModKeybinds.OPEN_CONFIG_SCREEN.consumeClick()) {
            Minecraft.getInstance().setScreen(
                    new ColorfulSeasonsScreen(Component.literal("Colorful Seasons"), Minecraft.getInstance().screen)
            );
        }
    }
}
