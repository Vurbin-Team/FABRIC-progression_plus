package com.progressionplus.datagen;

import com.progressionplus.registry.ModBlocks;
import com.progressionplus.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
                List<ItemConvertible> PINK_GARNET_SMELTABLES = List.of(ModItems.RAW_TITANIUM_ORE, ModBlocks.DEEPSLATE_TITANIUM_ORE_BLOCK);

                offerSmelting(exporter, PINK_GARNET_SMELTABLES, RecipeCategory.MISC, ModItems.TITANIUM_INGOT, 10f, 200, "titanium_ingot");
                offerBlasting(exporter, PINK_GARNET_SMELTABLES, RecipeCategory.MISC, ModItems.TITANIUM_INGOT, 10f, 100, "titanium_ingot");

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TITANIUM_BOOTS)
                        .pattern("R R")
                        .pattern("R R")
                        .input('R', ModItems.TITANIUM_INGOT)
                        .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                        .offerTo(exporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TITANIUM_LEGGINGS)
                        .pattern("RRR")
                        .pattern("R R")
                        .pattern("R R")
                        .input('R', ModItems.TITANIUM_INGOT)
                        .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                        .offerTo(exporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TITANIUM_CHESTPLATE)
                        .pattern("R R")
                        .pattern("RRR")
                        .pattern("RRR")
                        .input('R', ModItems.TITANIUM_INGOT)
                        .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                        .offerTo(exporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TITANIUM_HELMET)
                        .pattern("RRR")
                        .pattern("R R")
                        .input('R', ModItems.TITANIUM_INGOT)
                        .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                        .offerTo(exporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.GRAND_EXP_BOTTLE)
                        .pattern("A#B")
                        .pattern("#E#")
                        .pattern("D#C")
                        .input('A', Items.GLOW_INK_SAC)
                        .input('B', Items.ECHO_SHARD)
                        .input('C', Items.NETHER_WART)
                        .input('D', Items.AMETHYST_SHARD)
                        .input('E', Items.GLASS_BOTTLE)
                        .input('#', Items.EXPERIENCE_BOTTLE)
                        .criterion(hasItem(Items.GLOW_INK_SAC), conditionsFromItem(Items.GLOW_INK_SAC))
                        .criterion(hasItem(Items.EXPERIENCE_BOTTLE), conditionsFromItem(Items.EXPERIENCE_BOTTLE))
                        .criterion(hasItem(Items.ECHO_SHARD), conditionsFromItem(Items.ECHO_SHARD))
                        .criterion(hasItem(Items.NETHER_WART), conditionsFromItem(Items.NETHER_WART))
                        .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                        .criterion(hasItem(Items.EXPERIENCE_BOTTLE), conditionsFromItem(Items.EXPERIENCE_BOTTLE))
                        .offerTo(exporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TITANIUM_SWORD)
                        .pattern("R")
                        .pattern("R")
                        .pattern("S")
                        .input('R', ModItems.TITANIUM_INGOT)
                        .input('S', Items.STICK)
                        .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                        .offerTo(exporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TITANIUM_PICKAXE)
                        .pattern("RRR")
                        .pattern(" S ")
                        .pattern(" S ")
                        .input('R', ModItems.TITANIUM_INGOT)
                        .input('S', Items.STICK)
                        .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                        .offerTo(exporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TITANIUM_SHOVEL)
                        .pattern("R")
                        .pattern("S")
                        .pattern("S")
                        .input('R', ModItems.TITANIUM_INGOT)
                        .input('S', Items.STICK)
                        .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                        .offerTo(exporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TITANIUM_AXE)
                        .pattern("RR")
                        .pattern("RS")
                        .pattern(" S")
                        .input('R', ModItems.TITANIUM_INGOT)
                        .input('S', Items.STICK)
                        .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                        .offerTo(exporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TITANIUM_HOE)
                        .pattern("RR")
                        .pattern(" S")
                        .pattern(" S")
                        .input('R', ModItems.TITANIUM_INGOT)
                        .input('S', Items.STICK)
                        .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                        .offerTo(exporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SKINT_BRICKS)
                        .pattern("RR")
                        .pattern("RR")
                        .input('R', ModBlocks.YELLOW_SKINT_BLOCK)
                        .criterion(hasItem(ModBlocks.YELLOW_SKINT_BLOCK), conditionsFromItem(ModBlocks.YELLOW_SKINT_BLOCK))
                        .offerTo(exporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.YELLOW_SKINT_BLOCK)
                        .pattern("RRR")
                        .pattern("RRR")
                        .pattern("RRR")
                        .input('R', ModItems.YELLOW_SKINT_CRYSTAL_SHARD_2)
                        .criterion(hasItem(ModItems.YELLOW_SKINT_CRYSTAL_SHARD_2), conditionsFromItem(ModItems.YELLOW_SKINT_CRYSTAL_SHARD_2))
                        .offerTo(exporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_BLOCK)
                        .pattern("RRR")
                        .pattern("RRR")
                        .pattern("RRR")
                        .input('R', ModItems.TITANIUM_INGOT)
                        .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                        .offerTo(exporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.EXPERIENCE_STORAGE_PEDESTAL)
                        .pattern("#R#")
                        .pattern("#B#")
                        .pattern("RTR")
                        .input('#', ModItems.TITANIUM_INGOT)
                        .input('R', Blocks.DEEPSLATE_BRICKS)
                        .input('B', Items.GLASS_BOTTLE)
                        .input('T', ModBlocks.TITANIUM_BLOCK)
                        .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                        .criterion(hasItem(Blocks.DEEPSLATE_BRICKS), conditionsFromItem(Blocks.DEEPSLATE_BRICKS))
                        .criterion(hasItem(Items.GLASS_BOTTLE), conditionsFromItem(Items.GLASS_BOTTLE))
                        .criterion(hasItem(ModBlocks.TITANIUM_BLOCK), conditionsFromItem(ModBlocks.TITANIUM_BLOCK))
                        .offerTo(exporter);
    }

    @Override
    public String getName() {
        return "Progression+ Recipes";
    }
}
