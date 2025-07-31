package com.progressionplus.registry.armor;

import com.progressionplus.Progressionplus;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class CustomEquipmentAssetKeys implements EquipmentAssetKeys {
    public static final RegistryKey<? extends Registry<EquipmentAsset>> REGISTRY_KEY =
            RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset"));

    public static final RegistryKey<EquipmentAsset> TITANIUM = register("titanium");

    static RegistryKey<EquipmentAsset> register(String name) {
        return RegistryKey.of(REGISTRY_KEY, Identifier.of(Progressionplus.MOD_ID, name));
    }
}
