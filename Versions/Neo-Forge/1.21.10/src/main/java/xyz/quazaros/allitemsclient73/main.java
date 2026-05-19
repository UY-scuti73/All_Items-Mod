package xyz.quazaros.allitemsclient73;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import xyz.quazaros.allitemsclient73.files.WorldKeys;
import xyz.quazaros.allitemsclient73.files.files;
import xyz.quazaros.allitemsclient73.items.itemList;
import xyz.quazaros.allitemsclient73.client.events.onClickEvent;

import static xyz.quazaros.allitemsclient73.files.WorldKeys.setWorldKey;

@Mod(value = "allitemsclient73", dist = Dist.CLIENT)
public class main {
    public static itemList ItemList = new itemList();

    public main(IEventBus modEventBus) {
        modEventBus.addListener(onClickEvent::registerKeyMappings);

        NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLeave);

        NeoForge.EVENT_BUS.addListener(onClickEvent::onClientTick);
    }

    private void onPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getCurrentServer() != null) {
            // Multiplayer
            WorldKeys.setClientWorldKey(mc.getCurrentServer().ip);
        } else if (mc.getSingleplayerServer() != null) {
            // Singleplayer
            WorldKeys.setWorldKey(mc.getSingleplayerServer());
        }
        ItemList.init();
    }

    private void onPlayerLeave(ClientPlayerNetworkEvent.LoggingOut event) {
        files.saveList();
    }
}