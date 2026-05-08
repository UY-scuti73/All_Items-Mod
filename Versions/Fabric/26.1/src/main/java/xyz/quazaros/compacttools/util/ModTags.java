package xyz.quazaros.compacttools.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import xyz.quazaros.compacttools.CompactTools;

public class ModTags {

    public static class Blocks {
        public static final TagKey<Block> ALL_MINEABLE = createTag("all_mineable");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(CompactTools.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> TOOLS = createTag("tools");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(CompactTools.MOD_ID, name));
        }
    }
}
