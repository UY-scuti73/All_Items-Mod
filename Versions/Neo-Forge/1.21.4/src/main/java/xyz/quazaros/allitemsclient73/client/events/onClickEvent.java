package xyz.quazaros.allitemsclient73.client.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;
import xyz.quazaros.allitemsclient73.client.inventory.VirtualChestScreen;
import xyz.quazaros.allitemsclient73.main;

public class onClickEvent {

    public static KeyMapping openInventoryKey;

    // Call this from your main class constructor using modEventBus.addListener
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        openInventoryKey = new KeyMapping(
                "key.allitemsclient73.openinventory",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "key.categories.allitems73"
        );
        event.register(openInventoryKey);
    }

    // Call this via NeoForge.EVENT_BUS.addListener or @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (openInventoryKey.consumeClick()) {
            onInventoryKeyPressed(Minecraft.getInstance(), false);
        }
    }

    public static void onInventoryKeyPressed(Minecraft client, boolean filtered) {
        if (client.player == null || client.getConnection() == null) return;

        main.ItemList.updateList();

        // Open the screen
        client.setScreen(new VirtualChestScreen(filtered));
    }
}