package com.progressionplus.registry;

import com.progressionplus.Progressionplus;
import com.progressionplus.registry.armor.CustomArmorMaterial;
import com.progressionplus.registry.items.GrandExpBottleItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.*;
import net.minecraft.item.Item.Settings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Function;

import static net.minecraft.item.Items.GLASS_BOTTLE;

public class ModItems {
    public static final Item GRAND_EXP_BOTTLE = register("grand_exp_bottle",
            GrandExpBottleItem::new, new Settings().maxCount(16)
                    .component(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT)
                    .recipeRemainder(GLASS_BOTTLE));
    public static final Item RAW_TITANIUM_ORE = register("raw_titanium_ore",
            Item::new, new Settings());
    public static final Item TITANIUM_INGOT = register("titanium_ingot",
            Item::new, new Settings());


    public static final Item YELLOW_SKINT_CRYSTAL_SHARD_2 = register("yellow_skint_crystal_shard_2",
            Item::new, new Settings());

    public static final ArmorItem TITANIUM_HELMET = register("titanium_helmet",
            settings -> new ArmorItem(CustomArmorMaterial.TITANIUM, ArmorItem.Type.HELMET, settings),
            new Settings().maxCount(1));
    public static final ArmorItem TITANIUM_CHESTPLATE = register("titanium_chestplate",
            settings -> new ArmorItem(CustomArmorMaterial.TITANIUM, ArmorItem.Type.CHESTPLATE, settings),
            new Settings().maxCount(1));
    public static final ArmorItem TITANIUM_LEGGINGS = register("titanium_leggings",
            settings -> new ArmorItem(CustomArmorMaterial.TITANIUM, ArmorItem.Type.LEGGINGS, settings),
            new Settings().maxCount(1));
    public static final ArmorItem TITANIUM_BOOTS = register("titanium_boots",
            settings -> new ArmorItem(CustomArmorMaterial.TITANIUM, ArmorItem.Type.BOOTS, settings),
            new Settings().maxCount(1));

    public static final Item TITANIUM_SWORD = register("titanium_sword",
            settings -> new SwordItem(ModToolMaterials.TITANIUM, settings), new Settings());
    public static final Item TITANIUM_PICKAXE = register("titanium_pickaxe",
            settings -> new PickaxeItem(ModToolMaterials.TITANIUM, settings), new Settings());
    public static final Item TITANIUM_SHOVEL = register("titanium_shovel",
            settings -> new ShovelItem(ModToolMaterials.TITANIUM, settings), new Settings());
    public static final Item TITANIUM_AXE = register("titanium_axe",
            settings -> new AxeItem(ModToolMaterials.TITANIUM, settings), new Settings());
    public static final Item TITANIUM_HOE = register("titanium_hoe",
            settings -> new HoeItem(ModToolMaterials.TITANIUM, settings), new Settings());

    private static <T extends Item> T register(String path, Function<Settings, T> factory, T.Settings settings) {
        final Identifier id = Identifier.of(Progressionplus.MOD_ID, path);
        final T item = factory.apply(settings);
        return Registry.register(Registries.ITEM, id, item);
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
