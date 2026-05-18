package net.kenji.colorful_seasons.screens;

import com.mojang.datafixers.util.Pair;
import net.kenji.colorful_seasons.api.ConfigManager;
import net.kenji.colorful_seasons.api.NeoforgeSlider;
import net.kenji.colorful_seasons.api.SeasonalColorConfigValues;
import net.kenji.colorful_seasons.api.SeasonColorSettings;
import net.kenji.colorful_seasons.network.ModPacketHandler;
import net.kenji.colorful_seasons.network.ServerSeasonalColorPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractOptionSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import sereneseasons.api.season.Season;
import sereneseasons.season.SeasonColorHandlers;

import java.util.HashMap;
import java.util.Map;

public class SeasonColorConfigScreen extends Screen {

    public enum ColorValue{
        R,
        G,
        B,
        LIGHTNESS
    }
    public int grassCurrentValueR = 0;
    public int grassCurrentValueG = 0;
    public int grassCurrentValueB = 0;
    public int foliageCurrentValueR = 0;
    public int foliageCurrentValueG = 0;
    public int foliageCurrentValueB = 0;
    public double grassCurrentLightness = 0.5;
    public double foliageCurrentLightness = 0.5;


    public NeoforgeSlider grassColorSliderR;
    public NeoforgeSlider grassColorSliderG;
    public NeoforgeSlider grassColorSliderB;
    public NeoforgeSlider foliageColorSliderR;
    public NeoforgeSlider foliageColorSliderG;
    public NeoforgeSlider foliageColorSliderB;

    public NeoforgeSlider grassLightnessSlider;
    public NeoforgeSlider foliageLightnessSlider;

    public Button updateRealTimeButton;

    public Season season;

    private final static int SCREEN_OFFSET_BUTTON = -50;
    private final static int SCREEN_OFFSET_SLIDER = -15;

    Map<SeasonColorHandlers.ResolverType, Integer> lastSettings = new HashMap<>();

    public static SeasonColorSettings GRASS_SPRING   = SeasonalColorConfigValues.GRASS_SPRING;
    public static SeasonColorSettings GRASS_SUMMER   = SeasonalColorConfigValues.GRASS_SUMMER;
    public static SeasonColorSettings GRASS_AUTUMN   = SeasonalColorConfigValues.GRASS_AUTUMN;
    public static SeasonColorSettings GRASS_WINTER   = SeasonalColorConfigValues.GRASS_WINTER;
    public static SeasonColorSettings FOLIAGE_SPRING = SeasonalColorConfigValues.FOLIAGE_SPRING;
    public static SeasonColorSettings FOLIAGE_SUMMER = SeasonalColorConfigValues.FOLIAGE_SUMMER;
    public static SeasonColorSettings FOLIAGE_AUTUMN = SeasonalColorConfigValues.FOLIAGE_AUTUMN;
    public static SeasonColorSettings FOLIAGE_WINTER = SeasonalColorConfigValues.FOLIAGE_WINTER;


    public SeasonColorConfigScreen(Component component, Season season, SeasonColorSettings grassColorSettings, SeasonColorSettings foliageColorSettings){
        super(component);
        if(grassColorSettings != null) {
            grassCurrentValueR = grassColorSettings.r();
            grassCurrentValueG = grassColorSettings.g();
            grassCurrentValueB = grassColorSettings.b();
            grassCurrentLightness = grassColorSettings.lightness();
        }
        if(foliageColorSettings != null) {
            foliageCurrentValueR = foliageColorSettings.r();
            foliageCurrentValueG = foliageColorSettings.g();
            foliageCurrentValueB = foliageColorSettings.b();
            foliageCurrentLightness = foliageColorSettings.lightness();
        }
        this.season = season;
    }


    @Override
    protected void init() {
        super.init();
        SeasonalColorConfigValues.pendingUpdateRealTime = SeasonalColorConfigValues.updateRealTime;

        grassColorSliderR = createNewSlider(ColorValue.R, SeasonColorHandlers.ResolverType.GRASS);
        grassColorSliderG = createNewSlider(ColorValue.G, SeasonColorHandlers.ResolverType.GRASS);
        grassColorSliderB = createNewSlider(ColorValue.B, SeasonColorHandlers.ResolverType.GRASS);
        grassLightnessSlider = createNewSlider(ColorValue.LIGHTNESS, SeasonColorHandlers.ResolverType.GRASS);

        foliageColorSliderR = createNewSlider(ColorValue.R, SeasonColorHandlers.ResolverType.FOLIAGE);
        foliageColorSliderG = createNewSlider(ColorValue.G, SeasonColorHandlers.ResolverType.FOLIAGE);
        foliageColorSliderB = createNewSlider(ColorValue.B, SeasonColorHandlers.ResolverType.FOLIAGE);
        foliageLightnessSlider = createNewSlider(ColorValue.LIGHTNESS, SeasonColorHandlers.ResolverType.FOLIAGE);

        updateRealTimeButton = createNewButton(() -> {
            SeasonalColorConfigValues.pendingUpdateRealTime = !SeasonalColorConfigValues.pendingUpdateRealTime;
            ConfigManager.syncSeasonalColorsToServer();
            this.rebuildWidgets();
        });
        GRASS_SPRING   = SeasonalColorConfigValues.GRASS_SPRING;
        GRASS_SUMMER   = SeasonalColorConfigValues.GRASS_SUMMER;
        GRASS_AUTUMN   = SeasonalColorConfigValues.GRASS_AUTUMN;
        GRASS_WINTER   = SeasonalColorConfigValues.GRASS_WINTER;
        FOLIAGE_SPRING = SeasonalColorConfigValues.FOLIAGE_SPRING;
        FOLIAGE_SUMMER = SeasonalColorConfigValues.FOLIAGE_SUMMER;
        FOLIAGE_AUTUMN = SeasonalColorConfigValues.FOLIAGE_AUTUMN;
        FOLIAGE_WINTER = SeasonalColorConfigValues.FOLIAGE_WINTER;


        addRenderableWidget(grassColorSliderR);  // was addWidget
        addRenderableWidget(grassColorSliderG);
        addRenderableWidget(grassColorSliderB);
        addRenderableWidget(grassLightnessSlider);
        addRenderableWidget(foliageColorSliderR);  // was addWidget
        addRenderableWidget(foliageColorSliderG);
        addRenderableWidget(foliageColorSliderB);

        addRenderableWidget(foliageLightnessSlider);

        addRenderableWidget(updateRealTimeButton);
    }

    @Override
    public void tick() {
        super.tick();
        if(season == Season.SPRING){
            GRASS_SPRING = getColorSettings(this, SeasonColorHandlers.ResolverType.GRASS);
            FOLIAGE_SPRING = getColorSettings(this, SeasonColorHandlers.ResolverType.FOLIAGE);
        }
        if(season == Season.SUMMER){
            GRASS_SUMMER = getColorSettings(this, SeasonColorHandlers.ResolverType.GRASS);
            FOLIAGE_SUMMER = getColorSettings(this, SeasonColorHandlers.ResolverType.FOLIAGE);
        }
        if(season == Season.AUTUMN){
            GRASS_AUTUMN = getColorSettings(this, SeasonColorHandlers.ResolverType.GRASS);
            FOLIAGE_AUTUMN = getColorSettings(this, SeasonColorHandlers.ResolverType.FOLIAGE);
        }
        if(season == Season.WINTER){
            GRASS_WINTER = getColorSettings(this, SeasonColorHandlers.ResolverType.GRASS);
            FOLIAGE_WINTER = getColorSettings(this, SeasonColorHandlers.ResolverType.FOLIAGE);
        }
        SeasonColorSettings grassSettings = getColorSettings(this, SeasonColorHandlers.ResolverType.GRASS);
        SeasonColorSettings foliageSettings = getColorSettings(this, SeasonColorHandlers.ResolverType.FOLIAGE);

        int lastGrassRgb = lastSettings.getOrDefault(SeasonColorHandlers.ResolverType.GRASS, -1);
        int lastFoliageRgb = lastSettings.getOrDefault(SeasonColorHandlers.ResolverType.FOLIAGE, -1);

        int grassHash = grassSettings.r() + grassSettings.g() + grassSettings.b()
                + (int)(grassSettings.lightness() * 1000);
        int foliageHash = foliageSettings.r() + foliageSettings.g() + foliageSettings.b()
                + (int)(foliageSettings.lightness() * 1000);

        if ((lastGrassRgb != -1 && lastFoliageRgb != -1)) {
            if (grassHash != lastGrassRgb || foliageHash != lastFoliageRgb) {
                ConfigManager.syncSeasonalColorsToServer();
            }
        }
        lastSettings.put(SeasonColorHandlers.ResolverType.GRASS, grassHash);
        lastSettings.put(SeasonColorHandlers.ResolverType.FOLIAGE, foliageHash);
    }



    public SeasonColorSettings getColorSettings(SeasonColorConfigScreen screen, SeasonColorHandlers.ResolverType type) {
        return switch (type) {
            case GRASS -> new SeasonColorSettings(
                    (int) screen.grassColorSliderR.getValue(),
                    (int) screen.grassColorSliderG.getValue(),
                    (int) screen.grassColorSliderB.getValue(),
                    screen.grassLightnessSlider.getValue());  // ← read slider
            case FOLIAGE -> new SeasonColorSettings(
                    (int) screen.foliageColorSliderR.getValue(),
                    (int) screen.foliageColorSliderG.getValue(),
                    (int) screen.foliageColorSliderB.getValue(),
                    screen.foliageLightnessSlider.getValue());  // ← read slider
        };
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }


    Button createNewButton(Runnable runnable) {
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight(); // was getScreenHeight()

        return Button.builder(Component.literal("Update RealTime: " + (SeasonalColorConfigValues.updateRealTime ? "ON" : "OFF")), (button) -> runnable.run())
                .pos(screenWidth / 2 + SCREEN_OFFSET_BUTTON, 20)
                .size(100, 20)  // add this
                .build();
    }

    NeoforgeSlider createNewSlider(ColorValue value, SeasonColorHandlers.ResolverType type){
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight(); // was getScreenHeight()

        Pair<Integer, Integer> widthHeightPair = new Pair<>(screenWidth / 2 + 10, screenHeight / 2 + 2);
        int sliderWidth = 150;  // fixed width
        int sliderHeight = 20;
        return new NeoforgeSlider(
                getSliderPosition(value, type).getFirst(),
                getSliderPosition(value, type).getSecond(),
                sliderWidth,
                sliderHeight,
                type == SeasonColorHandlers.ResolverType.GRASS ? Component.literal("Grass: ") : Component.literal("Foliage: "),
                Component.literal(" " + value.name()),
                0,
                value == ColorValue.LIGHTNESS ? 1.0 : 100,
                getCurrentValue(value, type),
                value == ColorValue.LIGHTNESS ? 0.1 : 1,
                1,
                true
        );
    }
    public double getCurrentValue(ColorValue value, SeasonColorHandlers.ResolverType type){
        return switch (type) {
            case GRASS -> switch (value) {
                case R -> grassCurrentValueR;
                case G -> grassCurrentValueG;
                case B -> grassCurrentValueB;
                case LIGHTNESS -> grassCurrentLightness;
            };
            case FOLIAGE -> switch (value) {
                case R -> foliageCurrentValueR;
                case G -> foliageCurrentValueG;
                case B -> foliageCurrentValueB;
                case LIGHTNESS -> foliageCurrentLightness;
            };
        };
    }

    public Pair<Integer, Integer> getSliderPosition(ColorValue value, SeasonColorHandlers.ResolverType type){
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight(); // was getScreenHeight()
        int finalHeight = screenHeight / 2;
        int finalWidth = screenWidth / 2 + SCREEN_OFFSET_SLIDER;

        switch (value){
            case R -> finalHeight -= 50;
            case B -> finalHeight += 50;
            case LIGHTNESS -> finalHeight += 100;
        }
        switch (type){
            case GRASS -> finalWidth -= 145;
            case FOLIAGE -> finalWidth += 25;
        }

        return new Pair<Integer, Integer>(finalWidth, finalHeight - 20);
    }

    @Override
    public void onClose() {
        super.onClose();

        ConfigManager.syncSeasonalColorsToServer();
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Poll the live confirmed values every frame — label only updates
        // once the server has actually applied and synced the change back
        updateRealTimeButton.setMessage(Component.literal("Update RealTime: "
                + (SeasonalColorConfigValues.updateRealTime ? "ON" : "OFF")));

    }
}
