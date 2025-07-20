package com.progressionplus.registry;

import com.progressionplus.Progressionplus;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final Identifier GROUP_ID = Identifier.of(Progressionplus.MOD_ID, "progressionplus_group");

    public static void register() {
        Registry.register(
                Registries.ITEM_GROUP,
                GROUP_ID,
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemgroup.progression-plus"))
                        .icon(() -> new ItemStack(ModItems.GRAND_EXP_BOTTLE)) // заміни на свій предмет
                        .entries((context, entries) -> {
                            entries.add(ModItems.GRAND_EXP_BOTTLE);
                            // додай інші предмети
                        })
                        .build()
        );

        ModItems.initialize();

        Progressionplus.LOGGER.info("Items registered!");
    }
}
