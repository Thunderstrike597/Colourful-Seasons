package net.kenji.colorful_seasons.screens;

import com.mojang.datafixers.util.Pair;
import net.kenji.colorful_seasons.api.SeasonalColorManager;
import net.kenji.colorful_seasons.api.SeasonColorSettings;
import net.kenji.colorful_seasons.network.ModPacketHandler;
import net.kenji.colorful_seasons.network.ServerSeasonalColorPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;
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

    public boolean updateRealTime = false;

    public int grassCurrentValueR = 0;
    public int grassCurrentValueG = 0;
    public int grassCurrentValueB = 0;
    public int foliageCurrentValueR = 0;
    public int foliageCurrentValueG = 0;
    public int foliageCurrentValueB = 0;
    public double grassCurrentLightness = 0.5;
    public double foliageCurrentLightness = 0.5;


    public ForgeSlider grassColorSliderR;
    public ForgeSlider grassColorSliderG;
    public ForgeSlider grassColorSliderB;
    public ForgeSlider foliageColorSliderR;
    public ForgeSlider foliageColorSliderG;
    public ForgeSlider foliageColorSliderB;

    public ForgeSlider grassLightnessSlider;
    public ForgeSlider foliageLightnessSlider;

    public Button updateRealTimeButton;

    public Season season;

    private final static int SCREEN_OFFSET_BUTTON = 40;
    private final static int SCREEN_OFFSET_SLIDER = 10;

    Map<SeasonColorHandlers.ResolverType, Integer> lastSettings = new HashMap<>();

    public static SeasonColorSettings GRASS_SPRING   = SeasonalColorManager.GRASS_SPRING;
    public static SeasonColorSettings GRASS_SUMMER   = SeasonalColorManager.GRASS_SUMMER;
    public static SeasonColorSettings GRASS_AUTUMN   = SeasonalColorManager.GRASS_AUTUMN;
    public static SeasonColorSettings GRASS_WINTER   = SeasonalColorManager.GRASS_WINTER;
    public static SeasonColorSettings FOLIAGE_SPRING = SeasonalColorManager.FOLIAGE_SPRING;
    public static SeasonColorSettings FOLIAGE_SUMMER = SeasonalColorManager.FOLIAGE_SUMMER;
    public static SeasonColorSettings FOLIAGE_AUTUMN = SeasonalColorManager.FOLIAGE_AUTUMN;
    public static SeasonColorSettings FOLIAGE_WINTER = SeasonalColorManager.FOLIAGE_WINTER;


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
        this.updateRealTime = SeasonalColorManager.updateRealTime;
        this.season = season;
    }


    @Override
    protected void init() {
        super.init();
        grassColorSliderR = createNewSlider(ColorValue.R, SeasonColorHandlers.ResolverType.GRASS);
        grassColorSliderG = createNewSlider(ColorValue.G, SeasonColorHandlers.ResolverType.GRASS);
        grassColorSliderB = createNewSlider(ColorValue.B, SeasonColorHandlers.ResolverType.GRASS);
        grassLightnessSlider = createNewSlider(ColorValue.LIGHTNESS, SeasonColorHandlers.ResolverType.GRASS);

        foliageColorSliderR = createNewSlider(ColorValue.R, SeasonColorHandlers.ResolverType.FOLIAGE);
        foliageColorSliderG = createNewSlider(ColorValue.G, SeasonColorHandlers.ResolverType.FOLIAGE);
        foliageColorSliderB = createNewSlider(ColorValue.B, SeasonColorHandlers.ResolverType.FOLIAGE);
        foliageLightnessSlider = createNewSlider(ColorValue.LIGHTNESS, SeasonColorHandlers.ResolverType.FOLIAGE);

        updateRealTimeButton = createNewButton(() -> {
            if(SeasonalColorManager.updateRealTime) {
                updateRealTime = false;
                SeasonalColorManager.updateRealTime = false;
            }
            else {
                updateRealTime = true;
                SeasonalColorManager.updateRealTime = true;
            }
            this.rebuildWidgets();
        });
        GRASS_SPRING   = SeasonalColorManager.GRASS_SPRING;
        GRASS_SUMMER   = SeasonalColorManager.GRASS_SUMMER;
        GRASS_AUTUMN   = SeasonalColorManager.GRASS_AUTUMN;
        GRASS_WINTER   = SeasonalColorManager.GRASS_WINTER;
        FOLIAGE_SPRING = SeasonalColorManager.FOLIAGE_SPRING;
        FOLIAGE_SUMMER = SeasonalColorManager.FOLIAGE_SUMMER;
        FOLIAGE_AUTUMN = SeasonalColorManager.FOLIAGE_AUTUMN;
        FOLIAGE_WINTER = SeasonalColorManager.FOLIAGE_WINTER;


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
                syncSeasonalColorsToServer();
            }
        }
        lastSettings.put(SeasonColorHandlers.ResolverType.GRASS, grassHash);
        lastSettings.put(SeasonColorHandlers.ResolverType.FOLIAGE, foliageHash);
    }



    Button createNewButton(Runnable runnable) {
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight(); // was getScreenHeight()

        return Button.builder(Component.literal("Update RealTime: " + (updateRealTime ? "ON" : "OFF")), (button) -> runnable.run())
                .pos(screenWidth / 2 - SCREEN_OFFSET_BUTTON, 20)
                .size(100, 20)  // add this
                .build();
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

    ForgeSlider createNewSlider(ColorValue value, SeasonColorHandlers.ResolverType type){
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight(); // was getScreenHeight()

        Pair<Integer, Integer> widthHeightPair = new Pair<>(screenWidth / 2 + 10, screenHeight / 2 + 2);
        int sliderWidth = 150;  // fixed width
        int sliderHeight = 20;
        return new ForgeSlider(
                getSliderPosition(value, type).getFirst(),
                getSliderPosition(value, type).getSecond(),
                sliderWidth,
                sliderHeight,
                type == SeasonColorHandlers.ResolverType.GRASS ? Component.literal("Grass") : Component.literal("Foliage"),
                Component.literal(value.name()),
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
        int finalWidth = screenWidth / 2 - SCREEN_OFFSET_SLIDER;

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

        syncSeasonalColorsToServer();
    }

    public static void syncSeasonalColorsToServer(){

        ModPacketHandler.sendToServer(new ServerSeasonalColorPacket(
                SeasonalColorManager.updateRealTime,
                GRASS_SPRING,
                GRASS_SUMMER,
                GRASS_AUTUMN,
                GRASS_WINTER,
                FOLIAGE_SPRING,
                FOLIAGE_SUMMER,
                FOLIAGE_AUTUMN,
                FOLIAGE_WINTER
        ));
    }

}
