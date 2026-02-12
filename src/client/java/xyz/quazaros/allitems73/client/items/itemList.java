package xyz.quazaros.allitems73.client.items;

import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static xyz.quazaros.allitems73.client.files.files.getItemList;

public class itemList {
    public ArrayList<item> items;
    public Map<String, item> itemMap;

    public itemList() {
        items = new ArrayList<>();
        itemMap = new HashMap<>();

        ArrayList<String> string_list = getItemList(true);

        for (String s : string_list) {
            item tempItem = new item(s);
            if (!tempItem.item_stack.isEmpty()) {items.add(tempItem);}
        }

        for (item i : items) {itemMap.put(i.item_name, i);}
    }

    public int getSize() {
        return items.size();
    }

    public void updateList() {
        Inventory inv = MinecraftClient.getInstance().player.getInventory();

        for (int i = 0; i < inv.size(); i++) {
            String name = inv.getStack(i).getItem().toString();
            get(name).submit();
        }
    }

    public item get(String name) {
        item tempItem = itemMap.get(name);
        if (tempItem == null) {return new item("minecraft:air");}
        return tempItem;
    }

    public String getProgString() {
        int score = 0;
        for (item i : items) {
            if (i.is_found) {
                score++;
            }
        }

        return score + "/" + items.size();
    }

    public ArrayList<item> getFilteredList() {
        ArrayList<item> filteredList = new ArrayList<>();
        for (item i : items) {
            if (!i.is_found && !i.item_stack.isEmpty()) {
                filteredList.add(i);
            }
        }
        for (item i : items) {
            if (i.is_found && !i.item_stack.isEmpty()) {
                filteredList.add(i);
            }
        }
        return filteredList;
    }
}
