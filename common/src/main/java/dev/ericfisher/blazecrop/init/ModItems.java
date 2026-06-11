package dev.ericfisher.blazecrop.init;

import static dev.ericfisher.blazecrop.Reference.MOD_ID;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ericfisher.blazecrop.Reference;
import dev.ericfisher.blazecrop.item.BlazeSeedsItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public final class ModItems {

  public static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(MOD_ID, Registries.ITEM);

  public static final RegistrySupplier<Item> BLAZE_SEEDS =
      ITEMS.register(Reference.Items.SEEDS, BlazeSeedsItem::new);

  public static final RegistrySupplier<BlockItem> TILLED_NETHERRACK =
      ITEMS.register(
          Reference.Blocks.TILLED_NETHERRACK,
          () ->
              new BlockItem(
                  ModBlocks.TILLED_NETHERRACK.get(),
                  new Item.Properties().arch$tab(CreativeModeTabs.NATURAL_BLOCKS)));
}
