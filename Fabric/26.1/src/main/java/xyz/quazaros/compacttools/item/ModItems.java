package xyz.quazaros.compacttools.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import xyz.quazaros.compacttools.CompactTools;
import xyz.quazaros.compacttools.item.CustomItems.CustomToolItem;

public class ModItems {
    public static Item WOODEN_TOOL;
    public static Item STONE_TOOL;
    public static Item COPPER_TOOL;
    public static Item IRON_TOOL;
    public static Item GOLDEN_TOOL;
    public static Item DIAMOND_TOOL;
    public static Item NETHERITE_TOOL;

    private static Item.Settings getSettings(String type, String name) {
        Item.Settings settings = new Item.Settings()
                .maxCount(1)
                .useItemPrefixedTranslationKey()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(CompactTools.MOD_ID, name)));
        if (type.equals("netherite")) settings.fireproof();
        return settings;
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(CompactTools.MOD_ID, name), item);
    }

    public static void registerItems() {
        WOODEN_TOOL = registerItem("wooden_tool", new CustomToolItem(ToolMaterial.WOOD, getSettings("wood", "wooden_tool")));
        STONE_TOOL = registerItem("stone_tool", new CustomToolItem(ToolMaterial.STONE, getSettings("stone", "stone_tool")));
        COPPER_TOOL = registerItem("copper_tool", new CustomToolItem(ToolMaterial.COPPER, getSettings("copper", "copper_tool")));
        IRON_TOOL = registerItem("iron_tool",  new CustomToolItem(ToolMaterial.IRON, getSettings("iron", "iron_tool")));
        GOLDEN_TOOL = registerItem("golden_tool", new CustomToolItem(ToolMaterial.GOLD, getSettings("gold", "golden_tool")));
        DIAMOND_TOOL = registerItem("diamond_tool", new CustomToolItem(ToolMaterial.DIAMOND, getSettings("diamond", "diamond_tool")));
        NETHERITE_TOOL = registerItem("netherite_tool", new CustomToolItem(ToolMaterial.NETHERITE, getSettings("netherite", "netherite_tool")));

        CompactTools.LOGGER.info("Registering Mod Items For " + CompactTools.MOD_ID);

        registerItemGroupEntries();
    }

    public static void registerItemGroupEntries() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(WOODEN_TOOL);
            entries.add(STONE_TOOL);
            entries.add(COPPER_TOOL);
            entries.add(IRON_TOOL);
            entries.add(GOLDEN_TOOL);
            entries.add(DIAMOND_TOOL);
            entries.add(NETHERITE_TOOL);
        });
    }
}
