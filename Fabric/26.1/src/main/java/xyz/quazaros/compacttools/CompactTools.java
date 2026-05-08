package xyz.quazaros.compacttools;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.quazaros.compacttools.item.ModItems;

public class CompactTools implements ModInitializer {
    public static final String MOD_ID = "compacttools73";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.registerItems();
        LOGGER.info("CompactTools Initialized");
    }
}
