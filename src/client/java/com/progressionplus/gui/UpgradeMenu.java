package com.progressionplus.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.progressionplus.Progressionplus;
import com.progressionplus.config.UpgradeConfig;
import com.progressionplus.data.PlayerComponents;
import com.progressionplus.network.ClientModMessages;
import com.progressionplus.sounds.ModSounds;
import com.progressionplus.upgrade.UpgradeType;
import com.progressionplus.upgrades.PlayerUpgrade;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class UpgradeMenu extends Screen {
    @FunctionalInterface
    private interface ScaledRenderer {
        void render(DrawContext context, int adjustedX, int adjustedY);
    }


    private static final Identifier BACKGROUND_TEXTURE = Identifier.of(Progressionplus.MOD_ID, "textures/gui/background.png");
    private static final Identifier CARD_BACKGROUND_TEXTURE = Identifier.of(Progressionplus.MOD_ID, "textures/gui/background_player.png");
    private static final Identifier BUTTON_TEXTURE = new Identifier(Progressionplus.MOD_ID, "textures/gui/button_level_up.png");
    private static final Identifier BUTTON_TEXTURE_DISABLED = new Identifier(Progressionplus.MOD_ID, "textures/gui/button_level_up_disabled.png");
    private static final Identifier BUTTON_TEXTURE_FOCUSED = new Identifier(Progressionplus.MOD_ID, "textures/gui/button_level_up_focused.png");

    private static final int TEXT_COLOR = 0xfff7cc;
    private static final int OVERLAY_COLOR = 0xC0000000;

    /**
     * The only place that defines the menu geometry. Coordinates use the
     * 1920 x 1080 reference canvas, relative to the main panel's top-left.
     */
    private static final class Layout {
        private static final int REFERENCE_WIDTH = 1920;
        private static final int REFERENCE_HEIGHT = 1080;
        private static final float TEXTURE_SCALE = 2.5f;
        private static final float MAIN_PANEL_WIDTH = 480;
        private static final float MAIN_PANEL_HEIGHT = 270;
        private static final float CARD_WIDTH = 135;
        private static final float CARD_HEIGHT = 270;
        private static final float MAIN_PANEL_HORIZONTAL_SHIFT = 0.10f;
        private static final float CARD_LEFT_MARGIN = 0.02f;

        private static final float TITLE_SCALE = 3.0f;
        private static final float LIST_SCALE = 2.5f;
        private static final float EXPERIENCE_VALUE_SCALE = 5.0f;
        private static final float EXPERIENCE_LABEL_SCALE = 2.0f;
        private static final float CARD_LEVEL_SCALE = 2.3f;
        private static final int ROW_SPACING = 13;
        private static final int ROW_START_Y = 73;
        private static final int LIST_TEXT_X = 240;
        private static final int INDICATOR_VALUE_X = 180;
        private static final int DEFENSE_VALUE_X = 389;
        private static final int BUTTON_SIZE = 9;
        private static final int BUTTON_X = 400;
        private static final int LEVEL_NUMBER_DIGITS = 2;
        private static final String LEVEL_PREFIX_GAP = "   ";
        private static final int PERCENTAGE_MULTIPLIER = 100;
        private static final String PERCENTAGE_TRANSITION = "% > ";

        private static final Point UPGRADES = new Point(45, 0);
        private static final Point DEFENSES = new Point(45, 290);
        private static final Point EXPERIENCE = new Point(310, 260);
        private static final Point INDICATORS = new Point(90, 290);
        private static final Point[] TITLES = {
                new Point(53, 31), new Point(238, 31),
                new Point(53, 132), new Point(238, 132)
        };
        private static final int EXPERIENCE_LABEL_OFFSET_Y = ROW_SPACING + 8;
        private static final float CARD_LEVEL_BOTTOM_MARGIN = 1f / 14f;
        private static final float ENTITY_SIZE_RATIO = 0.3f;
    }

    private record Point(int x, int y) {}

    private int mainBgWidth;
    private int mainBgHeight;
    private int mainBgX;
    private int mainBgY;

    private int cardBgWidth;
    private int cardBgHeight;
    private int cardBgX;
    private int cardBgY;
    private float baseScale;

    private int levels;
    private int requiredTotalExp;

    private PlayerUpgrade playerUpgrades;

    private List<DynamicTextureButtonWidget> upgradeButtons = new ArrayList<>();



    public UpgradeMenu()  {
        super(Text.translatable("gui.progression-plus.upgrade_menu"));
    }

    @Override
    protected void init() {
        // aspect ratio.
        float scaleX = this.width / (float) Layout.REFERENCE_WIDTH;
        float scaleY = this.height / (float) Layout.REFERENCE_HEIGHT;
        this.baseScale = Math.min(scaleX, scaleY);

        // mainBgWidth/Height считаем строго из baseScale и реальных
        // пропорций текстуры (480x270 = 16:9), а не из this.width напрямую.
        // Так mainBg всегда масштабируется синхронно с baseScale.
        this.mainBgWidth = scaled(Layout.MAIN_PANEL_WIDTH * Layout.TEXTURE_SCALE);
        this.mainBgHeight = scaled(Layout.MAIN_PANEL_HEIGHT * Layout.TEXTURE_SCALE);
        this.mainBgX = centered(mainBgWidth) + scaledScreen(Layout.MAIN_PANEL_HORIZONTAL_SHIFT);
        this.mainBgY = this.height / 2 - mainBgHeight / 2;

        // cardBgWidth/Height аналогично из реальных пропорций текстуры (135x270 = 1:2)
        this.cardBgWidth = scaled(Layout.CARD_WIDTH * Layout.TEXTURE_SCALE);
        this.cardBgHeight = scaled(Layout.CARD_HEIGHT * Layout.TEXTURE_SCALE);
        this.cardBgX = scaledScreen(Layout.CARD_LEFT_MARGIN);
        this.cardBgY = this.height / 2 - cardBgHeight / 2;

        this.playerUpgrades = PlayerComponents.PLAYER_UPGRADES.get(this.client.player).getPlayerUpgrade();
        this.levels = playerUpgrades.getTotalLevels();
        this.requiredTotalExp = playerUpgrades.getRequiredExp(levels);

        this.upgradeButtons.clear();

        initUpgradeButtons();
    }

    private void initUpgradeButtons() {

        // Используем фиксированные координаты относительно основного фона
        int baseX = backgroundX(Layout.BUTTON_X * Layout.TEXTURE_SCALE);
        float textScale = textScale(Layout.LIST_SCALE);
        int textAnchorY = backgroundY(Layout.UPGRADES);
        int baseY = renderedY(textAnchorY, textScale, Layout.ROW_START_Y);

        // Размер кнопки с учетом масштаба
        int scaledButtonSize = scaled(Layout.BUTTON_SIZE * Layout.TEXTURE_SCALE);
        int scaledPadding = scaled(Layout.ROW_SPACING * Layout.TEXTURE_SCALE);

        int currentY = baseY;

        for (UpgradeType type : UpgradeType.values()) {
            DynamicTextureButtonWidget button = createUpgradeButton(type, baseX, currentY, scaledButtonSize);
            this.upgradeButtons.add(button);
            currentY += scaledPadding;
        }
    }

    private DynamicTextureButtonWidget createUpgradeButton(UpgradeType type, int x, int y, int buttonSize) {
        DynamicTextureButtonWidget button = new DynamicTextureButtonWidget(x, y, buttonSize, buttonSize,0,0,0,
                BUTTON_TEXTURE, btn -> {
            ClientModMessages.sendSyncPacketToServer(type, client.player);

            // Звук при нажатии кнопки
            client.getSoundManager().play(PositionedSoundInstance.master(ModSounds.BUTTON_UPGRADE, 1.0F));

            // Обновляем состояние всех кнопок
            updateButtonStates();
        });

        this.addDrawableChild(button);
        return button;
    }

    private void updateButtonStates() {
        float currentExp = client.player.totalExperience;

        for (DynamicTextureButtonWidget button : upgradeButtons) {
            if (currentExp >= requiredTotalExp) {
                button.setTexture(BUTTON_TEXTURE);
                button.active = true;
            } else {
                button.setTexture(BUTTON_TEXTURE_DISABLED);
                button.active = false;
            }

            if((button.isFocused() || button.isHovered()) && currentExp >= requiredTotalExp){
                button.setTexture(BUTTON_TEXTURE_FOCUSED);
            } else if (currentExp >= requiredTotalExp) {
                button.setTexture(BUTTON_TEXTURE);
            } else {
                button.setTexture(BUTTON_TEXTURE_DISABLED);
            }
        }
    }



    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.levels = playerUpgrades.getTotalLevels();
        this.requiredTotalExp = playerUpgrades.getRequiredExp(levels);
        context.fill(0, 0, this.width, this.height, OVERLAY_COLOR);

        renderCustomBackground(context);
        renderCustomPlayerCard(context, mouseX, mouseY);

        // Обновляем состояние кнопок перед рендерингом
        updateButtonStates();

        // Add this line to render buttons and other child elements
        super.render(context, mouseX, mouseY, delta);
    }

    void renderCustomBackground(DrawContext context) {

        renderTitles(context, mainBgX, mainBgY);
        renderUpgrades(context);
        renderRequiredExpCounter(context);
        renderDefenses(context);
        renderBonusIndicators(context);

        context.drawTexture( BACKGROUND_TEXTURE,
                mainBgX, mainBgY, 0, 0,
                mainBgWidth, mainBgHeight,
                mainBgWidth, mainBgHeight);
    }


    //  ---------------------  render titles ---------------------
    private record TitleInfo(String translationKey, Point position, String suffix) {}
    private void renderTitle(DrawContext context, int adjustedX, int adjustedY, TitleInfo info) {
        MutableText text = Text.translatable(info.translationKey());
        if (info.suffix() != null) {
            text = text.append(info.suffix());
        }

        context.drawTextWithShadow(
                this.textRenderer,
                text,
                adjustedX + info.position().x(),
                adjustedY + info.position().y(),
                TEXT_COLOR
        );
    }
    private void renderTitles(DrawContext context, int x, int y) {
        renderWithScale(context, x, y, Layout.TITLE_SCALE, (ctx, adjustedX, adjustedY) -> {
            TitleInfo[] titles = {
                    new TitleInfo("upgrade.progression-plus.level", Layout.TITLES[0], " " + levels),
                    new TitleInfo("upgrade.progression-plus.attributes", Layout.TITLES[1], null),
                    new TitleInfo("upgrade.progression-plus.indicators", Layout.TITLES[2], null),
                    new TitleInfo("upgrade.progression-plus.defense", Layout.TITLES[3], null)
            };

            for (TitleInfo title : titles) {
                renderTitle(ctx, adjustedX, adjustedY, title);
            }
        });
    }



    //  ---------------------  render upgrades ---------------------
    private record UpgradeInfo(String translationKey, int xOffset, int yOffset, int suffix) {}
    private void renderUpgrade(DrawContext context, int adjustedX, int adjustedY, UpgradeInfo info) {
        MutableText translatedText = Text.translatable(info.translationKey()).styled(style ->
                style.withColor(TEXT_COLOR));

        if (info.suffix() != -1) {
            String prefix = String.format("%0" + Layout.LEVEL_NUMBER_DIGITS + "d%s",
                    info.suffix(), Layout.LEVEL_PREFIX_GAP);

            MutableText coloredPrefix = Text.literal(prefix).styled(style ->
                    style.withColor(Formatting.GREEN)  // Цвет префикса
            );

            translatedText = coloredPrefix.append(translatedText);  // Склеиваем цветной префикс и основной текст
        }

        context.drawTextWithShadow(
                this.textRenderer,
                translatedText,
                (int)adjustedX + info.xOffset(),
                (int)adjustedY + info.yOffset(),
                TEXT_COLOR
        );
    }
    private void renderUpgrades(DrawContext context) {
        int x = backgroundX(Layout.UPGRADES);
        int y = backgroundY(Layout.UPGRADES);

        renderWithScale(context, x, y, Layout.LIST_SCALE, (ctx, adjustedX, adjustedY) -> {
            UpgradeInfo[] upgrades = {
                    new UpgradeInfo("upgrade.progression-plus.strength",    Layout.LIST_TEXT_X, Layout.ROW_START_Y,
                            playerUpgrades.getLevel(UpgradeType.STRENGTH)),
                    new UpgradeInfo("upgrade.progression-plus.endurance",   Layout.LIST_TEXT_X, Layout.ROW_START_Y + Layout.ROW_SPACING,
                            playerUpgrades.getLevel(UpgradeType.ENDURANCE)),
                    new UpgradeInfo("upgrade.progression-plus.agility",     Layout.LIST_TEXT_X, Layout.ROW_START_Y + 2 * Layout.ROW_SPACING,
                            playerUpgrades.getLevel(UpgradeType.AGILITY)),
                    new UpgradeInfo("upgrade.progression-plus.luck",        Layout.LIST_TEXT_X, Layout.ROW_START_Y + 3 * Layout.ROW_SPACING,
                            playerUpgrades.getLevel(UpgradeType.LUCK)),
                    new UpgradeInfo("upgrade.progression-plus.mining_speed", Layout.LIST_TEXT_X, Layout.ROW_START_Y + 4 * Layout.ROW_SPACING,
                            playerUpgrades.getLevel(UpgradeType.MINING_SPEED)),
            };

            for (UpgradeInfo upgrade : upgrades) {
                renderUpgrade(ctx, adjustedX, adjustedY, upgrade);
            }
        });
    }


    //  ---------------------  render defense stats  ---------------------
    private record DefenseInfo(String translationKey, int xOffset, int yOffset, float resistancePerLevel, int upgradeLevel) {}

    private void renderDefense(DrawContext context, int adjustedX, int adjustedY, DefenseInfo info) {
        int resistencePerLevel = (int) (info.resistancePerLevel() * Layout.PERCENTAGE_MULTIPLIER);
        int pracentage = resistencePerLevel * info.upgradeLevel();

        MutableText translatedText = Text.translatable(info.translationKey())
                .styled(style -> style.withColor(TEXT_COLOR));

        MutableText coloredPrefix = Text.literal(pracentage + "")
                .styled(style -> style.withColor(Formatting.YELLOW));

        context.drawTextWithShadow(
                this.textRenderer,
                translatedText,
                (int) adjustedX + info.xOffset(),
                (int) adjustedY + info.yOffset(),
                TEXT_COLOR
        );

        coloredPrefix.append(Text.literal(Layout.PERCENTAGE_TRANSITION + (pracentage + resistencePerLevel) + "%")
                .styled(style -> style.withColor(TEXT_COLOR)));

        int textWidth = this.textRenderer.getWidth(coloredPrefix);

        context.drawTextWithShadow(
                this.textRenderer,
                coloredPrefix,
                (int) adjustedX + Layout.DEFENSE_VALUE_X - textWidth / 2,
                (int) adjustedY + info.yOffset(),
                TEXT_COLOR
        );
    }
    private void renderDefenses(DrawContext context) {
        int x = backgroundX(Layout.DEFENSES);
        int y = backgroundY(Layout.DEFENSES);

        renderWithScale(context, x, y, Layout.LIST_SCALE, (ctx, adjustedX, adjustedY) -> {
            DefenseInfo[] defenses = {
                    new DefenseInfo("defense.progression-plus.strength_damage_bonus",   Layout.LIST_TEXT_X, Layout.ROW_START_Y,
                            playerUpgrades.getUpgradeResistanceBonus(UpgradeType.STRENGTH),
                            playerUpgrades.getLevel(UpgradeType.STRENGTH)),
                    new DefenseInfo("defense.progression-plus.endurance_health_bonus",  Layout.LIST_TEXT_X, Layout.ROW_START_Y + Layout.ROW_SPACING,
                            playerUpgrades.getUpgradeResistanceBonus(UpgradeType.ENDURANCE),
                            playerUpgrades.getLevel(UpgradeType.ENDURANCE)),
                    new DefenseInfo("defense.progression-plus.agility_deffense_bonus",  Layout.LIST_TEXT_X, Layout.ROW_START_Y + 2 * Layout.ROW_SPACING,
                            playerUpgrades.getUpgradeResistanceBonus(UpgradeType.AGILITY),
                            playerUpgrades.getLevel(UpgradeType.AGILITY)),
                    new DefenseInfo("defense.progression-plus.luck_bonus",              Layout.LIST_TEXT_X, Layout.ROW_START_Y + 3 * Layout.ROW_SPACING,
                            playerUpgrades.getUpgradeResistanceBonus(UpgradeType.LUCK),
                            playerUpgrades.getLevel(UpgradeType.LUCK)),
                    new DefenseInfo("defense.progression-plus.mining_speed_bonus",      Layout.LIST_TEXT_X, Layout.ROW_START_Y + 4 * Layout.ROW_SPACING,
                            playerUpgrades.getUpgradeResistanceBonus(UpgradeType.MINING_SPEED),
                            playerUpgrades.getLevel(UpgradeType.MINING_SPEED)),
            };

            for (DefenseInfo defense : defenses) {
                renderDefense(ctx, adjustedX, adjustedY, defense);
            }
        });
    }


    //  ---------------------  render custom player card ---------------------
    private void renderCustomPlayerCard(DrawContext context, int mouseX, int mouseY) {
        // Draw card background
        context.drawTexture(CARD_BACKGROUND_TEXTURE,
                cardBgX, cardBgY, 0, 0,
                cardBgWidth, cardBgHeight,
                cardBgWidth, cardBgHeight);

        // Отрисовка текста уровня с масштабированием
        String levelText = levels + " Lvl";

        // Используем renderWithScale для масштабирования текста
        // Координаты рассчитываются как смещение от центра карточки
        int centerX = cardBgX + cardBgWidth / 2;
        int textY = cardBgY + cardBgHeight - Math.round(cardBgHeight * Layout.CARD_LEVEL_BOTTOM_MARGIN);

        renderWithScale(context, centerX, textY, Layout.CARD_LEVEL_SCALE, (ctx, adjustedX, adjustedY) -> {
            // Центрируем текст в масштабированном контексте
            int textWidth = this.textRenderer.getWidth(levelText);
            int centeredX = adjustedX - textWidth / 2;

            ctx.drawTextWithShadow(this.textRenderer, Text.of(levelText),
                    centeredX, adjustedY, TEXT_COLOR);
        });

        // Draw player entity
        int playerScale = Math.round(cardBgHeight * Layout.ENTITY_SIZE_RATIO);
        myDrawEntity(context,
                cardBgX + cardBgWidth / 2,
                cardBgY + (int)(cardBgHeight / 1.28f),
                playerScale,
                mouseX,
                mouseY,
                client.player);
    }


    //  ---------------------  render custom required exp counter  ---------------------
    private void renderRequiredExpCounter(DrawContext context) {
        int x = backgroundX(Layout.EXPERIENCE);
        int y = backgroundY(Layout.EXPERIENCE);

        float myCurentExp = this.client.player.totalExperience;

        // Первая часть с масштабом 5f
        renderWithScale(context, x , y, Layout.EXPERIENCE_VALUE_SCALE, (ctx, adjustedX, adjustedY) -> {
            String levelText = (int)myCurentExp + " / " + requiredTotalExp;
            int textWidth = this.textRenderer.getWidth(levelText);
            ctx.drawTextWithShadow(this.textRenderer,
                    Text.of(levelText),
                    adjustedX - textWidth / 2, adjustedY, TEXT_COLOR);
        });


        // Вторая часть с масштабом 2f
        renderWithScale(context, x, y, Layout.EXPERIENCE_LABEL_SCALE, (ctx, adjustedX, adjustedY) -> {
            MutableText underLevelText = Text.translatable("upgrade.progression-plus.required_exp");
            int underTextWidth = this.textRenderer.getWidth(underLevelText);
            ctx.drawTextWithShadow(this.textRenderer, underLevelText,
                    adjustedX - underTextWidth / 2, adjustedY + Layout.EXPERIENCE_LABEL_OFFSET_Y, TEXT_COLOR);
        });
    }


    // ---------------------  render bonus indicators  ---------------------

    private void renderBonusIndicators(DrawContext context) {
        int x = backgroundX(Layout.INDICATORS);
        int y = backgroundY(Layout.INDICATORS);

        renderWithScale(context, x, y, Layout.LIST_SCALE, (ctx, adjustedX, adjustedY) -> {
            // Пример индикаторов
            MutableText[] indicatorsText = {
                    Text.translatable("indicator.progression-plus.hp_bonus"),
                    Text.translatable("indicator.progression-plus.damage_bonus"),
                    Text.translatable("indicator.progression-plus.speed_bonus"),
                    Text.translatable("indicator.progression-plus.mining_speed_bonus"),
            };

            String[] indicatorsValues = {
                    String.valueOf((int)(client.player.getMaxHealth())),
                    String.format("%.2f", ((playerUpgrades.getUpgradeBonus(UpgradeType.STRENGTH)))),
                    (int)(playerUpgrades.getUpgradeBonus(UpgradeType.AGILITY) * 100) + "%",
                    (int)(playerUpgrades.getUpgradeBonus(UpgradeType.MINING_SPEED) * 100) + "%",
            };

            int tempPadding = Layout.ROW_SPACING;
            for (var text : indicatorsText){
                ctx.drawTextWithShadow(this.textRenderer,
                        Text.of(text.getString()),
                        adjustedX, adjustedY + Layout.ROW_START_Y + tempPadding - Layout.ROW_SPACING, TEXT_COLOR);
                tempPadding += Layout.ROW_SPACING;
            }

            tempPadding = Layout.ROW_SPACING;
            for (var indicatorValue : indicatorsValues){
                int textWidth = this.textRenderer.getWidth(indicatorValue);
                ctx.drawTextWithShadow(this.textRenderer,
                        Text.of(indicatorValue),
                        adjustedX + Layout.INDICATOR_VALUE_X - textWidth,
                        adjustedY + Layout.ROW_START_Y + tempPadding - Layout.ROW_SPACING, TEXT_COLOR);
                tempPadding += Layout.ROW_SPACING;
            }
        });
    }


    // Утилитарная функция для выполнения кода в масштабированном контексте
    private void renderWithScale(DrawContext context, int x, int y, float scaleMultiplier, ScaledRenderer renderer) {
        float scaleFactor = textScale(scaleMultiplier);
        int adjustedX = (int)(x / scaleFactor);
        int adjustedY = (int)(y / scaleFactor);

        context.getMatrices().push();
        context.getMatrices().scale(scaleFactor, scaleFactor, 1.0f);

        renderer.render(context, adjustedX, adjustedY);

        context.getMatrices().pop();
    }

    private float textScale(float multiplier) {
        return baseScale * multiplier;
    }

    private int renderedY(int anchorY, float scale, int textOffsetY) {
        return Math.round(((int) (anchorY / scale) + textOffsetY) * scale);
    }

    private int centered(int elementWidth) {
        return this.width / 2 - elementWidth / 2;
    }

    private int scaled(float referencePixels) {
        return Math.round(referencePixels * baseScale);
    }

    private int scaledScreen(float screenFraction) {
        return Math.round(this.width * screenFraction);
    }

    private int backgroundX(Point point) {
        return backgroundX(point.x());
    }

    private int backgroundY(Point point) {
        return backgroundY(point.y());
    }

    private int backgroundX(float offset) {
        return mainBgX + Math.round(offset * baseScale);
    }

    private int backgroundY(float offset) {
        return mainBgY + Math.round(offset * baseScale);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public static void myDrawEntity(DrawContext context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float)Math.atan((x - mouseX) / 40.0F);
        float g = (float)Math.atan((y - size - size / 1.8f - mouseY) / 40.0F);
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateX(g * 20.0F * 0.017453292F);
        quaternionf.mul(quaternionf2);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = 180.0F + f * 20.0F;
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        drawEntity(context, x, y, size, quaternionf, quaternionf2, entity);
        entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
    }

    public static void drawEntity(DrawContext context, int x, int y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 100.0);
        context.getMatrices().multiplyPositionMatrix((new Matrix4f()).scaling((float)size, (float)size, (float)(-size)));
        context.getMatrices().multiply(quaternionf);
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        if (quaternionf2 != null) {
            quaternionf2.conjugate();
            entityRenderDispatcher.setRotation(quaternionf2);
        }

        entityRenderDispatcher.setRenderShadows(false);
        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, context.getMatrices(), context.getVertexConsumers(), 15728880);
        });
        context.draw();
        entityRenderDispatcher.setRenderShadows(true);
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }
}