package com.progressionplus.registry;

import com.progressionplus.Progressionplus;
import com.progressionplus.registry.items.GrandExpBottleItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

import static net.minecraft.item.Items.GLASS_BOTTLE;

public class ModItems {
    public static final Item GRAND_EXP_BOTTLE = register("grand_exp_bottle",
            GrandExpBottleItem::new, new Settings().maxCount(16)
                    .component(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT)
                    .component(DataComponentTypes.CONSUMABLE, ConsumableComponents.DRINK)
                    .useRemainder(GLASS_BOTTLE));


    public static Item register(String path, Function<Settings, Item> factory, Item.Settings settings) {
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Progressionplus.MOD_ID, path));
        return Items.register(registryKey, factory, settings);
    }

    public static void initialize() {
    }
}
