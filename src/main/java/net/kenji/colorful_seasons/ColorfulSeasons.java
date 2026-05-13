package net.kenji.colorful_seasons;

import com.mojang.logging.LogUtils;
import net.kenji.colorful_seasons.config.ColorfulSeasonsConfig;
import net.kenji.colorful_seasons.screens.ColorfulSeasonsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.checkerframework.checker.units.qual.C;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import sereneseasons.season.SeasonColorHandlers;
import sereneseasons.util.Color;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ColorfulSeasons.MODID)
public class ColorfulSeasons {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "colorful_seasons";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();


    public ColorfulSeasons() {
        IEventBus modEventBus = MinecraftForge.EVENT_BUS;

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        SeasonColorHandlers.registerResolverOverride(
                SeasonColorHandlers.ResolverType.GRASS,
                new ExaggeratedColorOverride(SeasonColorHandlers.ResolverType.GRASS)
        );
        SeasonColorHandlers.registerResolverOverride(
                SeasonColorHandlers.ResolverType.FOLIAGE,
                new ExaggeratedColorOverride(SeasonColorHandlers.ResolverType.FOLIAGE)
        );
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
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
                    () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ColorfulSeasonsScreen(Component.literal("Colorful Seasons")))
            );

            ColorfulSeasonsConfig.load();
        }
    }
    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(InputEvent.Key event) {
            if(event.getKey() == GLFW.GLFW_KEY_BACKSLASH){
                Minecraft.getInstance().setScreen(new ColorfulSeasonsScreen(Component.literal("Settings Screen")));
            }
        }
    }
}
