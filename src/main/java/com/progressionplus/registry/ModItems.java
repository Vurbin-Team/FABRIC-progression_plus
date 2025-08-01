package com.progressionplus.registry;

import com.progressionplus.Progressionplus;
import com.progressionplus.registry.armor.CustomArmorMaterial;
import com.progressionplus.registry.items.ChargedTitaniumSwordItem;
import com.progressionplus.registry.items.GrandExpBottleItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item GRAND_EXP_BOTTLE = registerItem("grand_exp_bottle",
            new GrandExpBottleItem(new FabricItemSettings()
                    .maxCount(16)
                    .food(new FoodComponent.Builder()
                            .alwaysEdible() // Можно "есть" в любое время
                            .build())
            ));

    public static final Item RAW_TITANIUM_ORE = registerItem("raw_titanium_ore",
            new Item(new FabricItemSettings()));

    public static final Item TITANIUM_INGOT = registerItem("titanium_ingot",
            new Item(new FabricItemSettings()));

    public static final Item DEEPSLATE_HANDLE = registerItem("deepslate_handle",
            new Item(new FabricItemSettings()));

    public static final Item SKINT_CORE = registerItem("skint_core",
            new Item(new FabricItemSettings()));

    public static final Item YELLOW_SKINT_CRYSTAL_SHARD_2 = registerItem("yellow_skint_crystal_shard_2",
            new Item(new FabricItemSettings()));

    // Armor Items
    public static final ArmorItem TITANIUM_HELMET = registerItem("titanium_helmet",
            new ArmorItem(CustomArmorMaterial.TITANIUM, ArmorItem.Type.HELMET, new FabricItemSettings().maxCount(1)));

    public static final ArmorItem TITANIUM_CHESTPLATE = registerItem("titanium_chestplate",
            new ArmorItem(CustomArmorMaterial.TITANIUM, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().maxCount(1)));

    public static final ArmorItem TITANIUM_LEGGINGS = registerItem("titanium_leggings",
            new ArmorItem(CustomArmorMaterial.TITANIUM, ArmorItem.Type.LEGGINGS, new FabricItemSettings().maxCount(1)));

    public static final ArmorItem TITANIUM_BOOTS = registerItem("titanium_boots",
            new ArmorItem(CustomArmorMaterial.TITANIUM, ArmorItem.Type.BOOTS, new FabricItemSettings().maxCount(1)));

    // Tool Items
    public static final Item TITANIUM_SWORD = registerItem("titanium_sword",
            new SwordItem(ModToolMaterials.TITANIUM, 3, -2.4f, new FabricItemSettings()));

    public static final Item CHARGED_TITANIUM_SWORD = registerItem("charged_titanium_sword",
            new ChargedTitaniumSwordItem(ModToolMaterials.TITANIUM, 4, -2.4f, new FabricItemSettings()));

    public static final Item TITANIUM_PICKAXE = registerItem("titanium_pickaxe",
            new PickaxeItem(ModToolMaterials.TITANIUM, 1, -2.8f, new FabricItemSettings()));

    public static final Item TITANIUM_SHOVEL = registerItem("titanium_shovel",
            new ShovelItem(ModToolMaterials.TITANIUM, 1, -3.0f, new FabricItemSettings()));

    public static final Item TITANIUM_AXE = registerItem("titanium_axe",
            new AxeItem(ModToolMaterials.TITANIUM, 5, -3.2f, new FabricItemSettings()));

    public static final Item TITANIUM_HOE = registerItem("titanium_hoe",
            new HoeItem(ModToolMaterials.TITANIUM, 0, -3f, new FabricItemSettings()));

    private static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(Registries.ITEM, new Identifier(Progressionplus.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Progressionplus.LOGGER.info("Registering Mod Items for " + Progressionplus.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(TITANIUM_INGOT);
            entries.add(RAW_TITANIUM_ORE);
        });
    }

    public static void initialize() {
        registerModItems();
    }
}