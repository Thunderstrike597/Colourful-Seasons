package net.kenji.colorful_seasons;

import com.mojang.logging.LogUtils;
import net.kenji.colorful_seasons.config.ColorfulSeasonsConfig;
import net.kenji.colorful_seasons.config.ColorfulSeasonsServerConfig;
import net.kenji.colorful_seasons.keybinds.ModKeybinds;
import net.kenji.colorful_seasons.network.ModPacketHandler;
import net.kenji.colorful_seasons.screens.ColorfulSeasonsScreen;
import net.kenji.colorful_seasons.screens.ConfigSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import sereneseasons.core.SereneSeasons;
import sereneseasons.season.SeasonColorHandlers;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ColorfulSeasons.MODID)
public class ColorfulSeasons {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "colorful_seasons";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();


    public ColorfulSeasons() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.addListener(ColorfulSeasonsServerConfig::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(ColorfulSeasonsServerConfig::onServerStopping);

        if(FMLLoader.getDist() == Dist.CLIENT) {
            SeasonColorHandlers.registerResolverOverride(
                    SeasonColorHandlers.ResolverType.GRASS,
                    new SeasonalColorOverride(SeasonColorHandlers.ResolverType.GRASS)
            );
            SeasonColorHandlers.registerResolverOverride(
                    SeasonColorHandlers.ResolverType.FOLIAGE,
                    new SeasonalColorOverride(SeasonColorHandlers.ResolverType.FOLIAGE)
            );
        }
        BlockColorHandlers.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModPacketHandler::register);
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public class ClientSetup {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ModLoadingContext.get().registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ConfigSettingsScreen(Component.literal("Colorful Seasons"), screen))
            );

            ColorfulSeasonsConfig.load();
        }
    }
}
