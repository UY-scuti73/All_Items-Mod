package xyz.quazaros.allitems73.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;
import xyz.quazaros.allitems73.client.items.itemList;

import static xyz.quazaros.allitems73.client.events.onClickEvent.registerKeyPressed;

public class Allitems73Client implements ClientModInitializer {
    public static itemList ItemList;

    @Override
    public void onInitializeClient() {
        AllItemsClientInit.init();
        registerKeyPressed();
    }
}
