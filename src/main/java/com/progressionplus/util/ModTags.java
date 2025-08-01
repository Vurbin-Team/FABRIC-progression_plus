package com.progressionplus.util;

import com.progressionplus.Progressionplus;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NEEDS_TITANIUM_TOOL = of("needs_titanium_tool");
        public static final TagKey<Block> INCORRECT_FOR_TITANIUM_TOOL = of("incorrect_for_titanium_tool");
        public static final TagKey<Block> SKINT_REPLACEABLE_BLOCKS = of("skint_replaceable_block");

        private static TagKey<Block> of(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(Progressionplus.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> TRANSFORMABLE_ITEMS = of("transformable_items");
        public static final TagKey<Item> TITANIUM_REPAIR = of("titanium_ingot");
        public static final TagKey<Item> TITANIUM_TOOL_MATERIAL = of("titanitum_tool_material");

        private static TagKey<Item> of(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(Progressionplus.MOD_ID, name));
        }
    }
}