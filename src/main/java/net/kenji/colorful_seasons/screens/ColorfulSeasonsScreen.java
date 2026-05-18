package net.kenji.colorful_seasons.screens;

import net.kenji.colorful_seasons.api.ColorfulSeasonsConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.kenji.colorful_seasons.api.SeasonalColorConfigValues;
import sereneseasons.api.season.Season;

public class ColorfulSeasonsScreen extends ColorfulSeasonsConfigScreen {

    public ColorfulSeasonsScreen(Component title, Screen parentScreen) {
        super(title, parentScreen);
    }

    public static SeasonColorConfigScreen SPRING_CONFIG_SCREEN;
    public static SeasonColorConfigScreen SUMMER_CONFIG_SCREEN;
    public static SeasonColorConfigScreen AUTUMN_CONFIG_SCREEN;
    public static SeasonColorConfigScreen WINTER_CONFIG_SCREEN;

    private static final int BUTTON_WIDTH  = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_GAP    = 10;
    private static final int SEASON_GAP    = 20;

    @Override
    protected void init() {
        super.init();

        // Centred horizontally
        int buttonX = this.width / 2 - BUTTON_WIDTH / 2;

        int groupHeight = (BUTTON_HEIGHT * 4) + (BUTTON_GAP * 3) + SEASON_GAP;
        int contentAreaHeight = this.height - HEADER_HEIGHT;
        int groupStartY = HEADER_HEIGHT + (contentAreaHeight - groupHeight) / 2;

        this.addRenderableWidget(buildSeasonButton(Season.SPRING, buttonX, groupStartY,
                () -> Minecraft.getInstance().setScreen(
                        SPRING_CONFIG_SCREEN = new SeasonColorConfigScreen(
                                Component.literal("SpringColorScreen"),
                                Season.SPRING,
                                SeasonalColorConfigValues.GRASS_SPRING,
                                SeasonalColorConfigValues.FOLIAGE_SPRING))));

        this.addRenderableWidget(buildSeasonButton(Season.SUMMER, buttonX,
                groupStartY + (BUTTON_HEIGHT + BUTTON_GAP),
                () -> Minecraft.getInstance().setScreen(
                        SUMMER_CONFIG_SCREEN = new SeasonColorConfigScreen(
                                Component.literal("SummerColorScreen"),
                                Season.SUMMER,
                                SeasonalColorConfigValues.GRASS_SUMMER,
                                SeasonalColorConfigValues.FOLIAGE_SUMMER))));

        this.addRenderableWidget(buildSeasonButton(Season.AUTUMN, buttonX,
                groupStartY + (BUTTON_HEIGHT + BUTTON_GAP) * 2 + SEASON_GAP,
                () -> Minecraft.getInstance().setScreen(
                        AUTUMN_CONFIG_SCREEN = new SeasonColorConfigScreen(
                                Component.literal("AutumnColorScreen"),
                                Season.AUTUMN,
                                SeasonalColorConfigValues.GRASS_AUTUMN,
                                SeasonalColorConfigValues.FOLIAGE_AUTUMN))));

        this.addRenderableWidget(buildSeasonButton(Season.WINTER, buttonX,
                groupStartY + (BUTTON_HEIGHT + BUTTON_GAP) * 3 + SEASON_GAP,
                () -> Minecraft.getInstance().setScreen(
                        WINTER_CONFIG_SCREEN = new SeasonColorConfigScreen(
                                Component.literal("WinterColorScreen"),
                                Season.WINTER,
                                SeasonalColorConfigValues.GRASS_WINTER,
                                SeasonalColorConfigValues.FOLIAGE_WINTER))));
    }

    @Override
    protected void markActiveTab() {
        seasonalColorsTabButton.active = false;
    }

    // render() removed entirely — base class handles background/title/tabs,
    // and the "Seasons:" label is gone per the new layout preference.

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().levelRenderer.allChanged();
    }

    private Button buildSeasonButton(Season season, int x, int y, Runnable onClick) {
        return Button.builder(Component.literal(season.name()), btn -> onClick.run())
                .pos(x, y)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
    }
}