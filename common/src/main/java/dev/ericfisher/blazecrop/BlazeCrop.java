package dev.ericfisher.blazecrop;

import dev.ericfisher.blazecrop.init.ModBlocks;
import dev.ericfisher.blazecrop.init.ModHooks;
import dev.ericfisher.blazecrop.init.ModItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlazeCrop {

  public static final Logger LOGGER = LogManager.getLogger();

  public static void init() {
    ModBlocks.BLOCKS.register();
    ModItems.ITEMS.register();
    ModHooks.addTillables();
    ModExpectPlatform.initConfig();
  }
}
