package net.kenji.colorful_seasons.keybinds;

import net.kenji.colorful_seasons.screens.ColorfulSeasonsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "colorful_seasons", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeybinds {

    public static final KeyMapping OPEN_CONFIG_SCREEN = new KeyMapping(
            "key.colorful_seasons.open_config",  // translation key
            GLFW.GLFW_KEY_BACKSLASH,             // default key
            "key.categories.colorful_seasons"    // category translation key
    );

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_CONFIG_SCREEN);
    }
}