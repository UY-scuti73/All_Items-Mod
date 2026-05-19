package xyz.quazaros.allitemsclient73.files;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;

public class WorldKeys {
    private static String worldKey;

    // Call this on the Server (Main Mod Class)
    public static void setWorldKey(MinecraftServer server) {
        if (server == null) {
            worldKey = null;
            return;
        }

        String folderName;

        if (server instanceof DedicatedServer) {
            folderName = ((DedicatedServer) server).getProperties().levelName;
            worldKey = "mp_" + sanitize(folderName);
        } else {
            folderName = server.getWorldData().getLevelName();
            worldKey = "sp_" + sanitize(folderName);
        }
    }

    // Call this on the Client (Client Mod Class)
    public static void setClientWorldKey(String address) {
        if (worldKey != null && worldKey.startsWith("sp_")) {
            return;
        }

        worldKey = "remote_" + sanitize(address);
    }

    public static String getWorldKey() {
        return worldKey;
    }

    private static String sanitize(String in) {
        if (in == null || in.isBlank()) return "unknown";
        return in.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}