package xyz.quazaros.allitems73.client.items;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup.Type;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

public class item {
    public String item_name;
    public String item_display_name;
    public Item item_type;
    public ItemStack item_stack;

    public boolean is_found;

    public item(String name) {
        item_name = name;
        item_display_name = camel_case(item_name);
        item_type = getType();
        item_stack = new ItemStack(item_type, 1);

        item_stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);

        is_found = false;
    }

    public void submit() {
        is_found = true;
        item_stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
    }

    private Item getType() {
        for (Item i : Registries.ITEM) {
            if (i.toString().equalsIgnoreCase(item_name)) {
                return i;
            }
        }
        return Items.AIR;
    }

    private String camel_case(String str) {
        if (str.contains(":")) {
            str = str.substring(str.indexOf(':') + 1);
        }

        for (int i = 1; i < str.length(); i++) {
            if (str.charAt(i) == '_') {
                str = str.substring(0, i) + " " + str.substring(i + 1);
            }
            if (str.charAt(i - 1) == ' ') {
                str = str.substring(0, i) + str.substring(i, i + 1).toUpperCase() + str.substring(i + 1);
            }
        }
        str = str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
        return str;
    }
}
