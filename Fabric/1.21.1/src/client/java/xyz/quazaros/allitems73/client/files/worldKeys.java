package xyz.quazaros.allitems73.client.files;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.server.integrated.IntegratedServer;

public class worldKeys {
    public static String worldKey;

    private static MinecraftClient client = MinecraftClient.getInstance();
    private static IntegratedServer server = client.getServer();
    private static boolean isSingleplayer = client.isInSingleplayer() && server != null;

    public static void setWorldKey() {
        if (isSingleplayer) {worldKey = getSingleplayerWorldId();}
        else {worldKey = getMultiplayerWorldId();}
    }

    private static String getSingleplayerWorldId() {
        if (!client.isInSingleplayer() || client.getServer() == null) {
            return null;
        }

        String levelName = client.getServer().getSaveProperties().getLevelName();
        return "sp_" + sanitize(levelName);
    }

    private static String getMultiplayerWorldId() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isInSingleplayer()) return null;
        ServerInfo info = client.getCurrentServerEntry();
        if (info == null) {
            return null;
        }

        String addr = info.address;
        return "mp_" + sanitize(addr);
    }

    private static String sanitize(String in) {
        if (in == null) return "unknown";
        return in.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
