package xyz.quazaros.allitems73.client.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import xyz.quazaros.allitems73.client.Allitems73Client;
import xyz.quazaros.allitems73.client.files.files;
import xyz.quazaros.allitems73.client.inventory.VirtualChestScreen;

public class onClickEvent {
    public static void registerKeyPressed() {
        KeyBinding keyBinding;

        keyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.allitems73.openinventory",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_G,
                        KeyBinding.Category.MISC
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                onInventoryKeyPressed(client, false);
            }
        });
    }

    public static void onInventoryKeyPressed(MinecraftClient client, boolean filtered) {
        if (client.player == null || client.getNetworkHandler() == null) {return;}
        Allitems73Client.ItemList.updateList();
        client.setScreen(new VirtualChestScreen(filtered));
    }
}
