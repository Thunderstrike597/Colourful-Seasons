package net.kenji.colorful_seasons.screens;

import com.mojang.datafixers.util.Pair;
import net.kenji.colorful_seasons.api.SeasonalColorManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import sereneseasons.api.season.Season;


public class ColorfulSeasonsScreen extends Screen {
    public ColorfulSeasonsScreen(Component component) {
        super(component);
    }


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
        openSpringButton = createNewButton(Season.SPRING, () -> Minecraft.getInstance().setScreen(SPRING_CONFIG_SCREEN = new SeasonColorConfigScreen(Component.literal("SpringColorScreen"), Season.SPRING, SeasonalColorManager.GRASS_SPRING, SeasonalColorManager.FOLIAGE_SPRING)));
        openSummerButton = createNewButton(Season.SUMMER, () -> Minecraft.getInstance().setScreen(SUMMER_CONFIG_SCREEN = new SeasonColorConfigScreen(Component.literal("SummerColorScreen"), Season.SUMMER, SeasonalColorManager.GRASS_SUMMER, SeasonalColorManager.FOLIAGE_SUMMER)));
        openAutumnButton = createNewButton(Season.AUTUMN, () -> Minecraft.getInstance().setScreen(AUTUMN_CONFIG_SCREEN = new SeasonColorConfigScreen(Component.literal("AutumnColorScreen"), Season.AUTUMN, SeasonalColorManager.GRASS_AUTUMN, SeasonalColorManager.FOLIAGE_AUTUMN)));
        openWinterButton = createNewButton(Season.WINTER, () -> Minecraft.getInstance().setScreen(WINTER_CONFIG_SCREEN = new SeasonColorConfigScreen(Component.literal("WinterColorScreen"), Season.WINTER, SeasonalColorManager.GRASS_WINTER, SeasonalColorManager.FOLIAGE_WINTER)));

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
