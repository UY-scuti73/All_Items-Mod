package xyz.quazaros.allitems73.client.files;

import net.fabricmc.loader.api.FabricLoader;
import xyz.quazaros.allitems73.client.Allitems73Client;
import xyz.quazaros.allitems73.client.items.item;

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
    private static final String MODID = "allitems73";

    private files() {}

    public static ArrayList<String> getItemList(boolean mainList) {
        try {
            return getList(mainList);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static ArrayList<String> getList(boolean mainList) throws IOException {
        String relativePath = mainList ? "AllItems/items.txt" : "AllItems/Data/" + worldKeys.worldKey + ".txt";
        Path listPath = CONFIG_DIR.resolve(relativePath);

        Path parent = listPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        ArrayList<String> itemList = new ArrayList<>();

        if (Files.exists(listPath)) {
            try (Stream<String> lines = Files.lines(listPath)) {
                itemList.addAll(lines.collect(Collectors.toList()));
            }
        } else {
            if (mainList) {
                ArrayList<String> defaultList = getListResource();
                itemList.addAll(defaultList);
                writeList(listPath, itemList);
            } else {
                writeList(listPath, itemList);
            }
        }

        return itemList;
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

        String resourcePath = "/assets/" + MODID + "/items.txt";

        try (InputStream in = files.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                return itemList;
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    itemList.add(line);
                }
            }
        }

        return itemList;
    }

    private static void saveItemList(boolean mainList, List<String> lines) {
        try {
            String relativePath = mainList ? "AllItems/items.txt" : "AllItems/Data/" + worldKeys.worldKey + ".txt";
            Path listPath = CONFIG_DIR.resolve(relativePath);

            Path parent = listPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            writeList(listPath, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveList() {
        List<String> list = new ArrayList<>();
        for (item i : Allitems73Client.ItemList.items) {
            if (i.is_found) {
                list.add(i.item_name);
            }
        }
        saveItemList(false, list);
    }
}