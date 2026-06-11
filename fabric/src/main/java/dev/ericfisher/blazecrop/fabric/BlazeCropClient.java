package dev.ericfisher.blazecrop.fabric;

import dev.ericfisher.blazecrop.init.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

/**
 * Client-only initialization. {@link BlockRenderLayerMap} and {@link RenderType} are {@code
 * EnvType.CLIENT} classes that do not exist on a dedicated server, so they must only be touched
 * from the {@code client} entrypoint — referencing them from the common {@code main} entrypoint
 * crashes dedicated servers at class load.
 */
public class BlazeCropClient implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BLAZE_CROP.get(), RenderType.cutout());
  }
}
