package com.progressionplus.registry;

import com.progressionplus.Progressionplus;
import com.progressionplus.registry.armor.CustomArmorMaterial;
import com.progressionplus.registry.items.GrandExpBottleItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.*;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
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
    public static final Item RAW_TITANIUM_ORE = register("raw_titanium_ore",
            Item::new, new Settings());
    public static final Item TITANIUM_INGOT = register("titanium_ingot",
            Item::new, new Settings());
    public static final Item DEEPSLATE_HANDLE = register("deepslate_handle",
            Item::new, new Settings());

    public static final Item YELLOW_SKINT_CRYSTAL_SHARD_2 = register("yellow_skint_crystal_shard_2",
            Item::new, new Settings());


    public static final Item TITANIUM_HELMET = register("titanium_helmet",
            Item::new, new Settings().maxCount(1).armor(CustomArmorMaterial.TITANIUM, EquipmentType.HELMET));
    public static final Item TITANIUM_CHESTPLATE = register("titanium_chestplate",
            Item::new, new Settings().maxCount(1).armor(CustomArmorMaterial.TITANIUM, EquipmentType.CHESTPLATE));
    public static final Item TITANIUM_LEGGINGS = register("titanium_leggings",
            Item::new, new Settings().maxCount(1).armor(CustomArmorMaterial.TITANIUM, EquipmentType.LEGGINGS));
    public static final Item TITANIUM_BOOTS = register("titanium_boots",
            Item::new, new Settings().maxCount(1).armor(CustomArmorMaterial.TITANIUM, EquipmentType.BOOTS));


    public static final Item TITANIUM_SWORD = registerItem("titanium_sword",
            setting -> new Item(setting.sword(ModToolMaterials.TITANIUM, 3, -2.4f)));
    public static final Item TITANIUM_PICKAXE = registerItem("titanium_pickaxe",
            setting -> new Item(setting.pickaxe(ModToolMaterials.TITANIUM, 1, -2.8f)));
    public static final Item TITANIUM_SHOVEL = registerItem("titanium_shovel",
            setting -> new ShovelItem(ModToolMaterials.TITANIUM, 1, -3.0f, setting));
    public static final Item TITANIUM_AXE = registerItem("titanium_axe",
            setting -> new AxeItem(ModToolMaterials.TITANIUM, 5, -3.2f, setting));
    public static final Item TITANIUM_HOE = registerItem("titanium_hoe",
            setting -> new HoeItem(ModToolMaterials.TITANIUM, 0, -3f, setting));



    public static Item register(String path, Function<Settings, Item> factory, Item.Settings settings) {
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Progressionplus.MOD_ID, path));
        return Items.register(registryKey, factory, settings);
    }

    private static Item registerItem(String name, Function<Item.Settings, Item> function) {
        return Registry.register(Registries.ITEM, Identifier.of(Progressionplus.MOD_ID, name),
                function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Progressionplus.MOD_ID, name)))));
    }
    public static void registerModItems() {
        Progressionplus.LOGGER.info("Registering Mod Items for " + Progressionplus.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(TITANIUM_INGOT);
            entries.add(RAW_TITANIUM_ORE);
        });
    }

    public static void initialize() {
    }
}
