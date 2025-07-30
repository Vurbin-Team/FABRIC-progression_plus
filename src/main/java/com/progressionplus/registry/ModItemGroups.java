package com.progressionplus.registry;

import com.progressionplus.Progressionplus;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final Identifier GROUP_ID = Identifier.of(Progressionplus.MOD_ID, "progressionplus_group");
    public static final RegistryKey<ItemGroup> PROGRESSION_PLUS_GROUP = registerGroupKey();

    private static RegistryKey<ItemGroup> registerGroupKey() {
        return RegistryKey.of(RegistryKeys.ITEM_GROUP, GROUP_ID);
    }

    public static void register() {
        Registry.register(
                Registries.ITEM_GROUP,
                GROUP_ID,
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemgroup.progression-plus"))
                        .icon(() -> new ItemStack(ModItems.GRAND_EXP_BOTTLE))
                        .entries((context, entries) -> {
                            entries.add(ModItems.GRAND_EXP_BOTTLE);
                            entries.add(ModItems.RAW_TITANIUM_ORE);
                            entries.add(ModItems.TITANIUM_INGOT);
                            entries.add(ModItems.TITANIUM_HELMET);
                            entries.add(ModItems.TITANIUM_CHESTPLATE);
                            entries.add(ModItems.TITANIUM_LEGGINGS);
                            entries.add(ModItems.TITANIUM_BOOTS);
                            entries.add(ModItems.TITANIUM_SWORD);
                            entries.add(ModItems.TITANIUM_PICKAXE);
                            entries.add(ModItems.TITANIUM_AXE);
                            entries.add(ModItems.TITANIUM_SHOVEL);
                            entries.add(ModItems.TITANIUM_HOE);
                            entries.add(ModItems.DEEPSLATE_HANDLE);
                            entries.add(ModItems.YELLOW_SKINT_CRYSTAL_SHARD_2);
                        })
                        .build()
        );

        ModItems.initialize();

        Progressionplus.LOGGER.info("Items registered!");
    }
}
