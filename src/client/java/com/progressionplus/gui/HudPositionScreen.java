package com.progressionplus.gui;

import com.progressionplus.config.HudConfigLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class HudPositionScreen extends Screen {
    private final Screen parent;
    private int hudX, hudY;
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    public HudPositionScreen(Screen parent) {
        super(Text.literal("Налаштування HUD"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int screenW = this.width;
        int screenH = this.height;
        this.hudX = HudConfigLoader.getHudX(screenW);
        this.hudY = HudConfigLoader.getHudY(screenH);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Назад"), btn -> {
            HudConfigLoader.save(this.hudX, this.hudY);
            this.client.setScreen(parent);
        }).dimensions(this.width / 2 - 40, this.height - 30, 80, 20).build());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= hudX && mouseX <= hudX + HudConfigLoader.HUD_WIDTH
                && mouseY >= hudY && mouseY <= hudY + HudConfigLoader.HUD_HEIGHT) {
            dragging = true;
            dragOffsetX = (int) mouseX - hudX;
            dragOffsetY = (int) mouseY - hudY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            int newX = (int) mouseX - dragOffsetX;
            int newY = (int) mouseY - dragOffsetY;
            this.hudX = Math.max(0, Math.min(newX, this.width - HudConfigLoader.HUD_WIDTH));
            this.hudY = Math.max(0, Math.min(newY, this.height - HudConfigLoader.HUD_HEIGHT));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Основний прямокутник HUD
        int fillColor = 0x88000000; // напівпрозорий чорний
        context.fill(hudX, hudY, hudX + HudConfigLoader.HUD_WIDTH, hudY + HudConfigLoader.HUD_HEIGHT, fillColor);

        // Рамка для чіткості
        int borderColor = 0xFFFFFFFF; // білий

        if(hudX > context.getScaledWindowWidth())
            hudX = context.getScaledWindowWidth() - HudConfigLoader.HUD_WIDTH;
        if(hudY > context.getScaledWindowHeight())
            hudY = context.getScaledWindowHeight() - HudConfigLoader.HUD_HEIGHT;

        // Верхня
        context.fill(hudX, hudY, hudX + HudConfigLoader.HUD_WIDTH, hudY + 1, borderColor);
        // Нижня
        context.fill(hudX, hudY + HudConfigLoader.HUD_HEIGHT - 1, hudX + HudConfigLoader.HUD_WIDTH, hudY + HudConfigLoader.HUD_HEIGHT, borderColor);
        // Ліва
        context.fill(hudX, hudY, hudX + 1, hudY + HudConfigLoader.HUD_HEIGHT, borderColor);
        // Права
        context.fill(hudX + HudConfigLoader.HUD_WIDTH - 1, hudY, hudX + HudConfigLoader.HUD_WIDTH, hudY + HudConfigLoader.HUD_HEIGHT, borderColor);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.client.world == null) {
            this.renderPanoramaBackground(context, deltaTicks);
        }

        this.renderDarkening(context);
    }
}
