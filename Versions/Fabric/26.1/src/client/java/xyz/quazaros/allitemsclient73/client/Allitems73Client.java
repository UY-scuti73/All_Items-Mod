package xyz.quazaros.allitemsclient73.client;

import net.fabricmc.api.ClientModInitializer;
import xyz.quazaros.allitemsclient73.client.items.itemList;

import static xyz.quazaros.allitemsclient73.client.events.onClickEvent.registerKeyPressed;

public class Allitems73Client implements ClientModInitializer {
    public static itemList ItemList;

    @Override
    public void onInitializeClient() {
        AllItemsClientInit.init();
        registerKeyPressed();
    }
}
