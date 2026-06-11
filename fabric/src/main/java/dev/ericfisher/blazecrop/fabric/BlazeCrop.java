package dev.ericfisher.blazecrop.fabric;

import dev.ericfisher.blazecrop.init.ModBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public class BlazeCrop implements ModInitializer {

  @Override
  public void onInitialize() {
    dev.ericfisher.blazecrop.BlazeCrop.init();
    BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BLAZE_CROP.get(), RenderType.cutout());
  }
}
