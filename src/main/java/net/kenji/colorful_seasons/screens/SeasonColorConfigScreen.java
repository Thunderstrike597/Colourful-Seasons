package net.kenji.colorful_seasons.screens;

import com.mojang.datafixers.util.Pair;
import net.kenji.colorful_seasons.api.SeasonColorSettings;
import net.kenji.colorful_seasons.config.ColorfulSeasonsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import sereneseasons.api.season.Season;
import sereneseasons.season.SeasonColorHandlers;

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


    public ForgeSlider grassColorSliderR;
    public ForgeSlider grassColorSliderG;
    public ForgeSlider grassColorSliderB;
    public ForgeSlider foliageColorSliderR;
    public ForgeSlider foliageColorSliderG;
    public ForgeSlider foliageColorSliderB;

    public ForgeSlider grassLightnessSlider;
    public ForgeSlider foliageLightnessSlider;
    public Season season;

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
        grassColorSliderR = createNewSlider(ColorValue.R, SeasonColorHandlers.ResolverType.GRASS);
        grassColorSliderG = createNewSlider(ColorValue.G, SeasonColorHandlers.ResolverType.GRASS);
        grassColorSliderB = createNewSlider(ColorValue.B, SeasonColorHandlers.ResolverType.GRASS);
        grassLightnessSlider = createNewSlider(ColorValue.LIGHTNESS, SeasonColorHandlers.ResolverType.GRASS);

        foliageColorSliderR = createNewSlider(ColorValue.R, SeasonColorHandlers.ResolverType.FOLIAGE);
        foliageColorSliderG = createNewSlider(ColorValue.G, SeasonColorHandlers.ResolverType.FOLIAGE);
        foliageColorSliderB = createNewSlider(ColorValue.B, SeasonColorHandlers.ResolverType.FOLIAGE);
        foliageLightnessSlider = createNewSlider(ColorValue.LIGHTNESS, SeasonColorHandlers.ResolverType.FOLIAGE);


        addRenderableWidget(grassColorSliderR);  // was addWidget
        addRenderableWidget(grassColorSliderG);
        addRenderableWidget(grassColorSliderB);
        addRenderableWidget(grassLightnessSlider);
        addRenderableWidget(foliageColorSliderR);  // was addWidget
        addRenderableWidget(foliageColorSliderG);
        addRenderableWidget(foliageColorSliderB);

        addRenderableWidget(foliageLightnessSlider);
    }

    @Override
    public void tick() {
        super.tick();
        if(season == Season.SPRING){
            ColorfulSeasonsScreen.GRASS_SPRING = getColorSettings(this, SeasonColorHandlers.ResolverType.GRASS);
            ColorfulSeasonsScreen.FOLIAGE_SPRING = getColorSettings(this, SeasonColorHandlers.ResolverType.FOLIAGE);

        }
        if(season == Season.SUMMER){
            ColorfulSeasonsScreen.GRASS_SUMMER = getColorSettings(this, SeasonColorHandlers.ResolverType.GRASS);
            ColorfulSeasonsScreen.FOLIAGE_SUMMER = getColorSettings(this, SeasonColorHandlers.ResolverType.FOLIAGE);

        }
        if(season == Season.AUTUMN){
            ColorfulSeasonsScreen.GRASS_AUTUMN = getColorSettings(this, SeasonColorHandlers.ResolverType.GRASS);
            ColorfulSeasonsScreen.FOLIAGE_AUTUMN = getColorSettings(this, SeasonColorHandlers.ResolverType.FOLIAGE);
        }
        if(season == Season.WINTER){
            ColorfulSeasonsScreen.GRASS_WINTER = getColorSettings(this, SeasonColorHandlers.ResolverType.GRASS);
            ColorfulSeasonsScreen.FOLIAGE_WINTER = getColorSettings(this, SeasonColorHandlers.ResolverType.FOLIAGE);

        }
    }
    public SeasonColorSettings getColorSettings(SeasonColorConfigScreen screen, SeasonColorHandlers.ResolverType type){
        return switch (type)
        {
            case GRASS -> new SeasonColorSettings(
                    (int)screen.grassColorSliderR.getValue()
                    ,(int)screen.grassColorSliderG.getValue()
                    , (int)screen.grassColorSliderB.getValue()
                    , screen.grassCurrentLightness);
            case FOLIAGE -> new SeasonColorSettings(
                    (int)screen.foliageColorSliderR.getValue()
                    ,(int)screen.foliageColorSliderG.getValue()
                    , (int)screen.foliageColorSliderB.getValue()
                    , screen.grassCurrentLightness);
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
                value == ColorValue.LIGHTNESS ? 1 : 100,
                getCurrentValue(value, type),
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
        int finalWidth = screenWidth / 2;

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
        ColorfulSeasonsConfig.save();
        Minecraft.getInstance().levelRenderer.allChanged();
    }

}
