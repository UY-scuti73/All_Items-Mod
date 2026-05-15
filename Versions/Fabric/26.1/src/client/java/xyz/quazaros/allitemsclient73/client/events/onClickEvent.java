package xyz.quazaros.allitemsclient73.client.events;

import com.mojang.blaze3d.platform.InputConstants;          // was client.util.InputUtil
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;                    // was client.option.KeyBinding
import org.lwjgl.glfw.GLFW;
import xyz.quazaros.allitemsclient73.client.Allitems73Client;
import xyz.quazaros.allitemsclient73.client.inventory.VirtualChestScreen;

public class onClickEvent {

    private static KeyMapping keyBinding; // Store as a static field for the listener

    public static void registerKeyPressed() {
        keyBinding = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.allitemsclient73.openinventory",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_G,
                        KeyMapping.Category.MISC
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.consumeClick()) {
                onInventoryKeyPressed(client, false);
            }
        });
    }

    public static void onInventoryKeyPressed(Minecraft client, boolean filtered) {
        if (client.player == null || client.getConnection() == null) {
            return;
        }

        Allitems73Client.ItemList.updateList();
        client.setScreen(new VirtualChestScreen(filtered));
    }
}