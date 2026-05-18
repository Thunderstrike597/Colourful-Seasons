package net.kenji.colorful_seasons.screens;

import net.kenji.colorful_seasons.api.ColorfulSeasonsConfigScreen;
import net.kenji.colorful_seasons.api.ConfigManager;
import net.kenji.colorful_seasons.api.SeasonalColorConfigValues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigSettingsScreen extends ColorfulSeasonsConfigScreen {

    public ConfigSettingsScreen(Component title, Screen parentScreen) {
        super(title, parentScreen);
    }


    private Button affectModdedBlocksButton;
    private Button affectSpruceLeavesButton;

    @Override
    protected void init() {
        super.init();

        SeasonalColorConfigValues.pendingAffectModdedBlocks = SeasonalColorConfigValues.affectModdedBlocks;
        SeasonalColorConfigValues.pendingAffectSpruceLeaves = SeasonalColorConfigValues.affectSpruceLeaves;

        int buttonWidth  = (int) (this.width * 0.6);
        int buttonHeight = 20;
        int buttonGap    = 10;
        int buttonX      = (this.width - buttonWidth) / 2;
        int buttonY      = ColorfulSeasonsConfigScreen.HEADER_HEIGHT + 20;

        affectModdedBlocksButton = Button.builder(
                        Component.literal("Affect Modded Blocks: "
                                + (SeasonalColorConfigValues.affectModdedBlocks ? "ON" : "OFF")),
                        button -> updateConfigValues(() ->
                                SeasonalColorConfigValues.pendingAffectModdedBlocks =
                                        !SeasonalColorConfigValues.pendingAffectModdedBlocks))
                .pos(buttonX, buttonY)
                .size(buttonWidth, buttonHeight)
                .build();

        affectSpruceLeavesButton = Button.builder(
                        Component.literal("Affect Spruce Leaves: "
                                + (SeasonalColorConfigValues.affectSpruceLeaves ? "ON" : "OFF")),
                        button -> updateConfigValues(() ->
                                SeasonalColorConfigValues.pendingAffectSpruceLeaves =
                                        !SeasonalColorConfigValues.pendingAffectSpruceLeaves))
                .pos(buttonX, buttonY + buttonHeight + buttonGap)
                .size(buttonWidth, buttonHeight)
                .build();

        this.addRenderableWidget(affectModdedBlocksButton);
        this.addRenderableWidget(affectSpruceLeavesButton);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void markActiveTab() {
        // This IS the settings tab, so disable its button so it can't re-open itself
        settingsTabButton.active = false;
    }

    private void updateConfigValues(Runnable runnable) {
        runnable.run();
        ConfigManager.syncSeasonalColorsToServer();
        this.rebuildWidgets();
        Minecraft.getInstance().levelRenderer.allChanged();
    }
    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().levelRenderer.allChanged();
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Poll the live confirmed values every frame — label only updates
        // once the server has actually applied and synced the change back
        affectModdedBlocksButton.setMessage(Component.literal("Affect Modded Blocks: "
                + (SeasonalColorConfigValues.affectModdedBlocks ? "ON" : "OFF")));
        affectSpruceLeavesButton.setMessage(Component.literal("Affect Spruce Leaves: "
                + (SeasonalColorConfigValues.affectSpruceLeaves ? "ON" : "OFF")));
    }
}