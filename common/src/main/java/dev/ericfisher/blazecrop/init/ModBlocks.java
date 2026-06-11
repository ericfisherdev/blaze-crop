package dev.ericfisher.blazecrop.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ericfisher.blazecrop.ModExpectPlatform;
import dev.ericfisher.blazecrop.Reference;
import dev.ericfisher.blazecrop.block.BlazeCropBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

public final class ModBlocks {

  public static final DeferredRegister<Block> BLOCKS =
      DeferredRegister.create(Reference.MOD_ID, Registries.BLOCK);

  public static final RegistrySupplier<Block> BLAZE_CROP =
      BLOCKS.register(Reference.Blocks.BLAZE_CROP, BlazeCropBlock::new);

  public static final RegistrySupplier<Block> TILLED_NETHERRACK =
      BLOCKS.register(
          Reference.Blocks.TILLED_NETHERRACK, ModExpectPlatform::getTilledNetherrackBlock);
}
