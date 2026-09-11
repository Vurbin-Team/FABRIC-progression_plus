package com.progressionplus.registry.armor;

import com.google.common.collect.Maps;
import com.progressionplus.util.ModTags;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.sound.SoundEvents;

import java.util.Map;

public class CustomArmorMaterial implements ArmorMaterials {
    public static final ArmorMaterial TITANIUM =
            new ArmorMaterial(60,
                    createDefenseMap(3, 6, 8, 3, 11),
                    15, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
                    2.0F, 0.2F,
                    ModTags.Items.TITANIUM_REPAIR,
                    CustomEquipmentAssetKeys.TITANIUM);

    private static Map<EquipmentType, Integer> createDefenseMap(int bootsDefense, int leggingsDefense, int chestplateDefense, int helmetDefense, int bodyDefense) {
        return Maps.newEnumMap(Map.of(EquipmentType.BOOTS, bootsDefense, EquipmentType.LEGGINGS, leggingsDefense, EquipmentType.CHESTPLATE, chestplateDefense, EquipmentType.HELMET, helmetDefense, EquipmentType.BODY, bodyDefense));
    }
}
