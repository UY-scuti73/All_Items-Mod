package xyz.quazaros.allitemsclient73.client.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.impl.client.keymapping.KeyMappingRegistryImpl;
import net.fabricmc.fabric.mixin.client.keymapping.KeyMappingAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import xyz.quazaros.allitemsclient73.client.Allitems73Client;
import xyz.quazaros.allitemsclient73.client.inventory.VirtualChestScreen;

public class onClickEvent {

    private static KeyMapping keyBinding;

    public static void registerKeyPressed() {
        keyBinding = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.allitemsclient73.openinventory",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_G,
                        KeyMapping.Category.INVENTORY
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