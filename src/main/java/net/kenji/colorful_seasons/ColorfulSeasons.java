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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import sereneseasons.core.SereneSeasons;
import sereneseasons.season.SeasonColorHandlers;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ColorfulSeasons.MODID)
public class ColorfulSeasons {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "colorful_seasons";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();


    public ColorfulSeasons() {
        IEventBus modEventBus = ModLoadingContext.get().getActiveContainer().getEventBus();

        // Register the commonSetup method for modloading


        // Register ourselves for server and other game events we are interested in
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.addListener(ColorfulSeasonsServerConfig::onServerStarting);
        NeoForge.EVENT_BUS.addListener(ColorfulSeasonsServerConfig::onServerStopping);

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

    }


    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientSetup {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ModList.get().getModContainerById(MODID).ifPresent(container ->
                    container.registerExtensionPoint(
                            IConfigScreenFactory.class,
                            (mc, screen) -> new ConfigSettingsScreen(Component.literal("Colorful Seasons"), screen)
                    )
            );

            ColorfulSeasonsConfig.load();
        }
    }
}
