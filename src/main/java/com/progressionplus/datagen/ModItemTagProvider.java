package com.progressionplus.datagen;

import com.progressionplus.registry.ModItems;
import com.progressionplus.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ItemTags.SWORDS)
                .add(ModItems.TITANIUM_SWORD);
        getOrCreateTagBuilder(ItemTags.SWORDS)
                .add(ModItems.CHARGED_TITANIUM_SWORD);
        getOrCreateTagBuilder(ItemTags.PICKAXES)
                .add(ModItems.TITANIUM_PICKAXE);
        getOrCreateTagBuilder(ItemTags.SHOVELS)
                .add(ModItems.TITANIUM_SHOVEL);
        getOrCreateTagBuilder(ItemTags.AXES)
                .add(ModItems.TITANIUM_AXE);
        getOrCreateTagBuilder(ItemTags.HOES)
                .add(ModItems.TITANIUM_HOE);

        getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.TITANIUM_BOOTS)
                .add(ModItems.TITANIUM_HELMET)
                .add(ModItems.TITANIUM_CHESTPLATE)
                .add(ModItems.TITANIUM_LEGGINGS);

        getOrCreateTagBuilder(ItemTags.TRIM_MATERIALS)
                .add(ModItems.TITANIUM_INGOT);

        getOrCreateTagBuilder(ModTags.Items.TITANIUM_TOOL_MATERIAL)
                .add(ModItems.TITANIUM_INGOT);

        getOrCreateTagBuilder(ModTags.Items.TRANSFORMABLE_ITEMS)
                .add(ModItems.TITANIUM_INGOT);


    }
}