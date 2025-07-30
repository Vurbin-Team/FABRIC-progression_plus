package com.progressionplus.gui;

import com.progressionplus.Progressionplus;
import com.progressionplus.config.HudConfigLoader;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class CustomHudRenderer implements HudRenderCallback {
    private static final Identifier HUD_TEXTURE = Identifier.of(Progressionplus.MOD_ID, "textures/gui/exp_counter_background.png");
    private static final Identifier HUD_TEXTURE_FLIPPED = Identifier.of(Progressionplus.MOD_ID, "textures/gui/exp_counter_background_flipped.png");
    private static final int HUD_TEXTURE_WIDTH = HudConfigLoader.HUD_WIDTH;
    private static final int HUD_TEXTURE_HEIGHT = HudConfigLoader.HUD_HEIGHT;

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        int screenW = drawContext.getScaledWindowWidth();
        int screenH = drawContext.getScaledWindowHeight();
        int hudX = HudConfigLoader.getHudX(screenW);
        int hudY = HudConfigLoader.getHudY(screenH);

        // Используем метод для определения стороны
        boolean isOnRightSide = HudConfigLoader.isOnRight(screenW);

        // Рендерим фон, флип по X если нужно
        if (isOnRightSide) {
            drawContext.drawTexture(
                    RenderLayer::getGuiTextured,
                    HUD_TEXTURE,
                    hudX, hudY,
                    0, 0,
                    HUD_TEXTURE_WIDTH, HUD_TEXTURE_HEIGHT,
                    HUD_TEXTURE_WIDTH, HUD_TEXTURE_HEIGHT
            );
        } else {
            drawContext.drawTexture(
                    RenderLayer::getGuiTextured,
                    HUD_TEXTURE_FLIPPED,
                    hudX, hudY,
                    HUD_TEXTURE_WIDTH, 0,
                    HUD_TEXTURE_WIDTH, HUD_TEXTURE_HEIGHT,
                    HUD_TEXTURE_WIDTH, HUD_TEXTURE_HEIGHT
            );
        }

        // Текст опыта и уровня
        String currentExp = String.valueOf(client.player.totalExperience);
        String currentLevel = client.player.experienceLevel + " lvl";

        int centerX = hudX + HUD_TEXTURE_WIDTH / 2;
        int lineHeight = HUD_TEXTURE_HEIGHT / 3;
        int firstLineY = hudY + lineHeight / 2;
        int thirdLineY = hudY + lineHeight * 2;

        int expWidth = client.textRenderer.getWidth(currentExp);
        int levelWidth = client.textRenderer.getWidth(currentLevel);

        if(isOnRightSide){
            drawContext.drawText(client.textRenderer, currentExp, centerX - expWidth / 2 + 10, firstLineY, 0xFFFFFF, true);
            drawContext.drawText(client.textRenderer, currentLevel, centerX - levelWidth / 2 + 10, thirdLineY, 0xFFFFFF, true);
        }
        else {
            drawContext.drawText(client.textRenderer, currentExp, centerX - expWidth / 2 - 10, firstLineY, 0xFFFFFF, true);
            drawContext.drawText(client.textRenderer, currentLevel, centerX - levelWidth / 2 - 10, thirdLineY, 0xFFFFFF, true);
        }
    }
}
