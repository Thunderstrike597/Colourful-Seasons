package net.kenji.colorful_seasons.api;

import net.kenji.colorful_seasons.screens.ColorfulSeasonsScreen;
import net.kenji.colorful_seasons.screens.ConfigSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class ColorfulSeasonsConfigScreen extends Screen {

    // The screen that opened this one, so we can return to it on close
    protected final Screen parentScreen;

    // Tab button references so the active tab can be visually distinguished
    protected Button settingsTabButton;
    protected Button seasonalColorsTabButton;

    protected static final int HEADER_HEIGHT = 40; // px from top where content starts
    protected static final int TAB_WIDTH     = 150;
    protected static final int TAB_HEIGHT    = 20;

    protected ColorfulSeasonsConfigScreen(Component title, Screen parentScreen) {
        super(title);
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();

        // --- Tab buttons ---
        // Left tab: Settings
        settingsTabButton = Button.builder(
                        Component.literal("Settings"),
                        btn -> Minecraft.getInstance().setScreen(
                                new ConfigSettingsScreen(
                                        Component.literal("Colorful Seasons Config"), parentScreen)))
                .pos(this.width / 2 - TAB_WIDTH, HEADER_HEIGHT - TAB_HEIGHT)
                .size(TAB_WIDTH, TAB_HEIGHT)
                .build();

        // Right tab: Seasonal Colors
        seasonalColorsTabButton = Button.builder(
                        Component.literal("Seasonal Colors"),
                        btn -> Minecraft.getInstance().setScreen(
                                new ColorfulSeasonsScreen(
                                        Component.literal("Colorful Seasons Config"), parentScreen)))
                .pos(this.width / 2, HEADER_HEIGHT - TAB_HEIGHT)
                .size(TAB_WIDTH, TAB_HEIGHT)
                .build();

        // Mark the active tab as inactive (grayed out / not clickable)
        markActiveTab();

        this.addRenderableWidget(settingsTabButton);
        this.addRenderableWidget(seasonalColorsTabButton);
    }

    /**
     * Subclasses override this to disable their own tab button,
     * visually indicating it is the currently active tab.
     */
    protected abstract void markActiveTab();

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Draw the vanilla dirt/panorama background
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // Draw the title: "COLORFUL SEASONS CONFIG" centred at the top
        guiGraphics.drawCenteredString(
                this.font,
                this.title,
                this.width / 2,
                8,          // y — sits above the divider line
                0xFFFFFF);

        // Draw a horizontal divider line just above the tabs
        int lineY = HEADER_HEIGHT - TAB_HEIGHT - 2;
        guiGraphics.fill(this.width / 2 - TAB_WIDTH, lineY,
                this.width / 2 + TAB_WIDTH, lineY + 1,
                0xFFFFFFFF);

        // Let widgets (buttons etc.) render themselves
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        // Return to whichever screen opened this config
        Minecraft.getInstance().setScreen(parentScreen);
    }
}