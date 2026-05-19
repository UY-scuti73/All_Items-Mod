package xyz.quazaros.allitemsclient73.client.files;

import net.fabricmc.loader.api.FabricLoader;
import xyz.quazaros.allitemsclient73.client.Allitems73Client;
import xyz.quazaros.allitemsclient73.client.items.item;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class files {

    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    private static final String MODID = "allitemsclient73";

    private files() {}

    private static Path getWorldFolder() {
        return CONFIG_DIR.resolve("AllItemsClient/Worlds").resolve(worldKeys.worldKey);
    }

    // Returns the full item list for building itemList, from the world's items.txt
    // Seeds items.txt from default_list.txt (or resources) if it doesn't exist yet
    public static ArrayList<String> getItemList() {
        try {
            Path defaultListPath = CONFIG_DIR.resolve("AllItemsClient/default_list.txt");

            // Ensure default_list.txt exists, seed from resources if not
            if (!Files.exists(defaultListPath)) {
                Files.createDirectories(defaultListPath.getParent());
                writeList(defaultListPath, getListResource());
            }

            Path worldItemsPath = getWorldFolder().resolve("items.txt");

            // Ensure world items.txt exists, seed from default_list.txt if not
            if (!Files.exists(worldItemsPath)) {
                Files.createDirectories(worldItemsPath.getParent());
                try (Stream<String> lines = Files.lines(defaultListPath)) {
                    writeList(worldItemsPath, lines.collect(Collectors.toList()));
                }
            }

            // Read and return world items.txt (never written to again after this)
            try (Stream<String> lines = Files.lines(worldItemsPath)) {
                return new ArrayList<>(lines.collect(Collectors.toList()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Returns the found items for the current world from progress.txt
    public static ArrayList<String> getProgress() {
        ArrayList<String> progress = new ArrayList<>();
        Path progressPath = getWorldFolder().resolve("progress.txt");

        if (!Files.exists(progressPath)) return progress;

        try (Stream<String> lines = Files.lines(progressPath)) {
            progress.addAll(lines.collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return progress;
    }

    // Saves only found items to the world's progress.txt
    public static void saveList() {
        List<String> list = new ArrayList<>();
        for (item i : Allitems73Client.ItemList.items) {
            if (i.is_found) {
                list.add(i.item_name);
            }
        }

        try {
            Path progressPath = getWorldFolder().resolve("progress.txt");
            Files.createDirectories(progressPath.getParent());
            writeList(progressPath, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeList(Path path, List<String> lines) throws IOException {
        Files.write(
                path,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    private static ArrayList<String> getListResource() throws IOException {
        ArrayList<String> itemList = new ArrayList<>();

        try (InputStream in = files.class.getResourceAsStream("/assets/" + MODID + "/items.txt")) {
            if (in == null) return itemList;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    itemList.add(line);
                }
            }
        }

        return itemList;
    }
}