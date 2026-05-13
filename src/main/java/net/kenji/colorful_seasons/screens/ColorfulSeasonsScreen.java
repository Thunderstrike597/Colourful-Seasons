package net.kenji.colorful_seasons.screens;

import com.mojang.datafixers.util.Pair;
import net.kenji.colorful_seasons.api.SeasonColorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import sereneseasons.api.season.Season;
import sereneseasons.season.SeasonHandler;


public class ColorfulSeasonsScreen extends Screen {
    public ColorfulSeasonsScreen(Component component) {
        super(component);
    }
    public static SeasonColorSettings GRASS_SPRING  = new SeasonColorSettings(  0,  0,  0,  0.5);  // no change
    public static SeasonColorSettings GRASS_SUMMER  = new SeasonColorSettings(  0, 30,  0,  0.5);  // greener
    public static SeasonColorSettings GRASS_AUTUMN  = new SeasonColorSettings(86,  10,  10,  0.5);  // red
    public static SeasonColorSettings GRASS_WINTER  = new SeasonColorSettings(  0,  0, 20,  0.45); // slight blue, darker
    public static SeasonColorSettings FOLIAGE_SPRING  = new SeasonColorSettings(  0,  0,  0,  0.5);
    public static SeasonColorSettings FOLIAGE_SUMMER  = new SeasonColorSettings(  0, 25,  0,  0.5);
    public static SeasonColorSettings FOLIAGE_AUTUMN  = new SeasonColorSettings( 90,  16,  2,  0.65);  // orange-red
    public static SeasonColorSettings FOLIAGE_WINTER  = new SeasonColorSettings(  2, 12, 98,  0.65); // blue, slightly dark


    public static SeasonColorConfigScreen SPRING_CONFIG_SCREEN;
    public static SeasonColorConfigScreen SUMMER_CONFIG_SCREEN;
    public static SeasonColorConfigScreen AUTUMN_CONFIG_SCREEN;
    public static SeasonColorConfigScreen WINTER_CONFIG_SCREEN;

    public Button openSpringButton;
    public Button openSummerButton;
    public Button openAutumnButton;
    public Button openWinterButton;

    @Override
    protected void init() {
        super.init();
        openSpringButton = createNewButton(Season.SPRING, () -> Minecraft.getInstance().setScreen(SPRING_CONFIG_SCREEN = new SeasonColorConfigScreen(Component.literal("SpringColorScreen"), Season.SPRING, GRASS_SPRING, FOLIAGE_SPRING)));
        openSummerButton = createNewButton(Season.SUMMER, () -> Minecraft.getInstance().setScreen(SUMMER_CONFIG_SCREEN = new SeasonColorConfigScreen(Component.literal("SummerColorScreen"), Season.SUMMER, GRASS_SUMMER, FOLIAGE_SUMMER)));
        openAutumnButton = createNewButton(Season.AUTUMN, () -> Minecraft.getInstance().setScreen(AUTUMN_CONFIG_SCREEN = new SeasonColorConfigScreen(Component.literal("AutumnColorScreen"), Season.AUTUMN, GRASS_AUTUMN, FOLIAGE_AUTUMN)));
        openWinterButton = createNewButton(Season.WINTER, () -> Minecraft.getInstance().setScreen(WINTER_CONFIG_SCREEN = new SeasonColorConfigScreen(Component.literal("WinterColorScreen"), Season.WINTER, GRASS_WINTER, FOLIAGE_WINTER)));

        addRenderableWidget(openSpringButton);
        addRenderableWidget(openSummerButton);
        addRenderableWidget(openAutumnButton);
        addRenderableWidget(openWinterButton);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }






    Button createNewButton(Season season, Runnable runnable) {
        return Button.builder(Component.literal(season.name()), (button) -> runnable.run())
                .pos(getButtonPosition(season).getFirst(), getButtonPosition(season).getSecond())
                .size(100, 20)  // add this
                .build();
    }



    public Pair<Integer, Integer> getButtonPosition(Season season) {
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight(); // was getScreenHeight()
        int finalHeight = screenHeight / 2;

        switch (season) {
            case SPRING -> finalHeight -= 75;
            case SUMMER -> finalHeight -= 25;
            case AUTUMN -> finalHeight += 25;
            case WINTER -> finalHeight += 75;
        }

        return new Pair<>(screenWidth / 2, finalHeight);
    }
    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().levelRenderer.allChanged();
    }

}
