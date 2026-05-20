package xyz.quazaros.allitemsclient73.items;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;

import java.util.*;

import static xyz.quazaros.allitemsclient73.files.files.getItemList;
import static xyz.quazaros.allitemsclient73.files.files.getProgress;

public class itemList {
    public ArrayList<item> items;
    public Map<String, item> itemMap;

    public itemList() {
        items = new ArrayList<>();
        itemMap = new HashMap<>();
    }

    public void init() {
        ArrayList<String> string_list = getItemList();
        ArrayList<String> submit_list = getProgress();

        for (String s : string_list) {
            item tempItem = new item(s);
            if (!tempItem.item_stack.isEmpty()) {items.add(tempItem);}
        }

        for (item i : items) {itemMap.put(i.item_name, i);}

        for (String s : submit_list) {
            item tempItem = get(s);
            if (tempItem != null) {
                tempItem.submit();
            }
        }
    }

    public int getSize() {
        return items.size();
    }

    public void updateList() {
        Inventory inv = Minecraft.getInstance().player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            item tempItem = get(inv.getItem(i).getItem().toString());
            if (tempItem != null) {
                tempItem.submit();
            }
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
