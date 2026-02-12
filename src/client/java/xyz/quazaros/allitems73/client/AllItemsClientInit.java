package xyz.quazaros.allitems73.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import xyz.quazaros.allitems73.client.files.files;
import xyz.quazaros.allitems73.client.items.itemList;

import java.util.List;

import static xyz.quazaros.allitems73.client.files.worldKeys.setWorldKey;

public class AllItemsClientInit {
    public static void init() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            setWorldKey();
            Allitems73Client.ItemList = new itemList();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            files.saveList();
        });
    }
}
