package com.progressionplus.gui;

import com.progressionplus.Progressionplus;
import com.progressionplus.config.HudConfigLoader;
import com.progressionplus.data.PlayerComponents;
import com.progressionplus.upgrades.PlayerUpgrade;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import static com.progressionplus.config.HudConfigLoader.getHudX;
import static com.progressionplus.config.HudConfigLoader.getHudY;

public class CustomHudRenderer implements HudRenderCallback {
    private static final Identifier HUD_TEXTURE = Identifier.of(Progressionplus.MOD_ID, "textures/gui/exp_counter_background.png");
    private static final Identifier HUD_TEXTURE_FLIPPED = Identifier.of(Progressionplus.MOD_ID, "textures/gui/exp_counter_background_flipped.png");
    private PlayerUpgrade playerUpgrades;
    private static final int HUD_TEXTURE_WIDTH = HudConfigLoader.HUD_WIDTH;
    private static final int HUD_TEXTURE_HEIGHT = HudConfigLoader.HUD_HEIGHT;


    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.options.hudHidden) return;
      
        playerUpgrades = PlayerComponents.PLAYER_UPGRADES.get(client.player).getPlayerUpgrade();

        int screenWidth = drawContext.getScaledWindowWidth();
        int screenHeight = drawContext.getScaledWindowHeight();
//
//        int HUD_X = screenWidth - HUD_TEXTURE_WIDTH;
//        int HUD_Y = (int) (screenHeight / 1.5f); // Положение текстуры по Y

        // Получение значений опыта и уровня
        String currentExp = client.player.totalExperience + "";
        String currentLevel = playerUpgrades.getTotalLevels() + " lvl";

        // Используем метод для определения стороны
        boolean isOnRightSide = HudConfigLoader.isOnRight(screenWidth);

        // Рендерим фон, флип по X если нужно
        if (isOnRightSide) {
            drawContext.drawTexture(
                    HUD_TEXTURE,
                    getHudX(screenWidth), getHudY(screenHeight),
                    0, 0,
                    HUD_TEXTURE_WIDTH, HUD_TEXTURE_HEIGHT,
                    HUD_TEXTURE_WIDTH, HUD_TEXTURE_HEIGHT
            );
        } else {
            drawContext.drawTexture(
                    HUD_TEXTURE_FLIPPED,
                    getHudX(screenWidth), getHudY(screenHeight),
                    HUD_TEXTURE_WIDTH, 0,
                    HUD_TEXTURE_WIDTH, HUD_TEXTURE_HEIGHT,
                    HUD_TEXTURE_WIDTH, HUD_TEXTURE_HEIGHT
            );
        }

        int centerX = getHudX(screenWidth) + HUD_TEXTURE_WIDTH / 2;
        int lineHeight = HUD_TEXTURE_HEIGHT / 3;
        int firstLineY = getHudY(screenHeight) + lineHeight / 2;
        int thirdLineY = getHudY(screenHeight) + lineHeight * 2;

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
