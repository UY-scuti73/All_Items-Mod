package xyz.quazaros.allitems73.client.files;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.server.IntegratedServer;

public class worldKeys {
    public static String worldKey;

    public static void setWorldKey() {
        Minecraft client = Minecraft.getInstance();

        // We check current status dynamically rather than using static initializers
        if (client.isLocalServer()) {
            worldKey = getSingleplayerWorldId(client);
        } else {
            worldKey = getMultiplayerWorldId(client);
        }
    }

    private static String getSingleplayerWorldId(Minecraft client) {
        IntegratedServer server = client.getSingleplayerServer();
        if (server == null) return "sp_unknown";

        String levelName = server.getWorldData().getLevelName();
        return "sp_" + sanitize(levelName);
    }

    private static String getMultiplayerWorldId(Minecraft client) {
        ServerData info = client.getCurrentServer();
        if (info == null) {
            return "mp_unknown";
        }

        String addr = info.ip;
        return "mp_" + sanitize(addr);
    }

    private static String sanitize(String in) {
        if (in == null) return "unknown";
        return in.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}