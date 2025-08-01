package com.progressionplus.gui;

import com.progressionplus.config.UpgradeConfig;
import com.progressionplus.config.UpgradeConfigLoader;
import com.progressionplus.upgrade.UpgradeType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import java.util.EnumMap;
import java.util.Map;


public class ConfigScreen extends Screen {
    private final Screen parent;

    private EditBoxWidget baseCostField;
    private EditBoxWidget levelMultiplierField;
    private EditBoxWidget maxUpgradeLevelField;

    private final Map<UpgradeType, EditBoxWidget> bonusFields = new EnumMap<>(UpgradeType.class);
    private final Map<UpgradeType, EditBoxWidget> resistanceFields = new EnumMap<>(UpgradeType.class);

    private static final int FIELD_WIDTH = 60;
    private static final int FIELD_HEIGHT = 20;
    private static final int LABEL_WIDTH = 100;
    private static final int VERTICAL_SPACING = 25;
    private static final int TOP_MARGIN = 30;
    private static final int SIDE_MARGIN = 20;

    private static final Text baseCostText = Text.translatable("config.progression-plus.base_cost");
    private static final Text levelMultiplierText = Text.translatable("config.progression-plus.level_multiplier");
    private static final Text maxUpgradeLevelText = Text.translatable("config.progression-plus.max_upgrade_level");
    private static final Text upgradeText = Text.translatable("config.progression-plus.upgrade");
    private static final Text multiplierText = Text.translatable("config.progression-plus.multiplier");
    private static final Text resistanceText = Text.translatable("config.progression-plus.resistance");
    private static final Text saveButtonText = Text.translatable("config.progression-plus.save_button");
    private static final Text hudPositionButtonText = Text.translatable("config.progression-plus.hud_position_button");
    private static final Text bonusPrefix = Text.translatable("config.progression-plus.bonus_prefix");
    private static final Text resistancePrefix = Text.translatable("config.progression-plus.resistance_prefix");

    public ConfigScreen(Screen parent) {
        super(Text.of("progression+ menu"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        bonusFields.clear();
        resistanceFields.clear();

        int centerX = this.width / 2;
        int currentY = TOP_MARGIN;

        int labelX = centerX - LABEL_WIDTH - SIDE_MARGIN;
        int fieldX = centerX + SIDE_MARGIN;

        // Основні поля
        baseCostField = addLabeledField(baseCostText.getString(), labelX, fieldX, currentY, String.valueOf(UpgradeConfig.getBaseCost()));
        currentY += VERTICAL_SPACING;

        levelMultiplierField = addLabeledField(levelMultiplierText.getString(), labelX, fieldX, currentY, String.valueOf(UpgradeConfig.getLevelMultiplier()));
        currentY += VERTICAL_SPACING + 10;

        maxUpgradeLevelField = addLabeledField(maxUpgradeLevelText.getString(), labelX, fieldX, currentY, String.valueOf(UpgradeConfig.getMaxUpgradeLevel()));
        currentY += VERTICAL_SPACING + 10;

        // Заголовки апгрейдів
        int upgradeLabelX = centerX - LABEL_WIDTH - SIDE_MARGIN;
        int multiplierX = centerX - FIELD_WIDTH / 2;
        int resistanceX = centerX + FIELD_WIDTH + SIDE_MARGIN;

        addTextWidget(upgradeText.getString(), upgradeLabelX, currentY);
        addTextWidget(multiplierText.getString(), multiplierX - textRenderer.getWidth(multiplierText) / 2, currentY);
        addTextWidget(resistanceText.getString(), resistanceX - textRenderer.getWidth(resistanceText) / 2, currentY);
        currentY += VERTICAL_SPACING;

        // Рядки апгрейдів
        for (UpgradeType type : UpgradeType.values()) {
            UpgradeConfig.UpgradeSettings settings = UpgradeConfig.UPGRADE_SETTINGS.get(type);
            if (settings == null) continue;

            currentY = addUpgradeRow(type, settings, upgradeLabelX, multiplierX, resistanceX, currentY);
        }

        currentY += 10;

        // Кнопка збереження
        int buttonWidth = 200;
        int buttonHeight = 20;
        int buttonX = centerX - buttonWidth / 2;

        this.addDrawableChild(ButtonWidget.builder(Text.literal(saveButtonText.getString()), btn -> {
            try {
                int baseCost = Integer.parseInt(baseCostField.getText());
                int levelMultiplier = Integer.parseInt(levelMultiplierField.getText());
                int maxUpgradeLevel = Integer.parseInt(maxUpgradeLevelField.getText());

                Map<UpgradeType, UpgradeConfig.UpgradeSettings> upgrades = new EnumMap<>(UpgradeType.class);
                for (UpgradeType type : UpgradeType.values()) {
                    float bonus = Float.parseFloat(bonusFields.get(type).getText());
                    float resistance = Float.parseFloat(resistanceFields.get(type).getText());
                    upgrades.put(type, new UpgradeConfig.UpgradeSettings(bonus, resistance));
                }

                UpgradeConfigLoader.saveConfig(baseCost, levelMultiplier, maxUpgradeLevel, upgrades);
            } catch (NumberFormatException e) {
                e.printStackTrace(); // Можна замінити на показ повідомлення користувачу
            }

            this.client.setScreen(parent);
        }).dimensions(buttonX, currentY, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal(hudPositionButtonText.getString()), btn -> {
            this.client.setScreen(new HudPositionScreen(this)); // відкриваємо новий екран
        }).dimensions(this.width - 110, 10, 100, 20).build());
    }

    // === Допоміжні методи ===


    private EditBoxWidget addLabeledField(String label, int labelX, int fieldX, int y, String value) {
        addTextWidget(label, labelX, y + 5);
        EditBoxWidget field = new EditBoxWidget(this.textRenderer, fieldX, y, FIELD_WIDTH, FIELD_HEIGHT, Text.literal(label), Text.literal(""));
        field.setText(value);
        this.addDrawableChild(field);
        return field;
    }

    private void addTextWidget(String text, int x, int y) {
        this.addDrawableChild(new TextWidget(x, y, LABEL_WIDTH, FIELD_HEIGHT, Text.literal(text), this.textRenderer));
    }

    private int addUpgradeRow(UpgradeType type, UpgradeConfig.UpgradeSettings settings, int labelX, int bonusX, int resistanceX, int y) {
        addTextWidget(type.name(), labelX, y + 5);

        EditBoxWidget bonusField = new EditBoxWidget(this.textRenderer, bonusX, y, FIELD_WIDTH, FIELD_HEIGHT, Text.literal(type.name() + bonusPrefix.getString()), Text.literal(""));
        bonusField.setText(String.valueOf(settings.bonusPerLevel));
        this.addDrawableChild(bonusField);
        bonusFields.put(type, bonusField);

        EditBoxWidget resistanceField = new EditBoxWidget(this.textRenderer, resistanceX, y, FIELD_WIDTH, FIELD_HEIGHT, Text.literal(type.name() + resistancePrefix.getString()), Text.literal(""));
        resistanceField.setText(String.valueOf(settings.resistancePerLevel));
        this.addDrawableChild(resistanceField);
        resistanceFields.put(type, resistanceField);

        return y + VERTICAL_SPACING;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
