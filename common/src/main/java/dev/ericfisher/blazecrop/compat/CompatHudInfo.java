package dev.ericfisher.blazecrop.compat;

import dev.ericfisher.blazecrop.HoeHelper;
import dev.ericfisher.blazecrop.block.BlazeCropBlock;
import dev.ericfisher.blazecrop.block.TilledNetherrackBlock;
import dev.ericfisher.blazecrop.config.BlazeCropConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Shared lookup results used by both the Jade and TOP HUD providers. */
public final class CompatHudInfo {

  // --- Blaze crop growth ---
  public final boolean isCropNotGrowing;
  public final int cropLightLevel;

  // --- Tilled netherrack moisture ---
  public final boolean isMoist;
  public final int moistureValue;

  // --- Netherrack tillability ---
  public final boolean isHoldingHoe;
  public final boolean canTill;
  public final boolean needsUnbreaking;

  private CompatHudInfo(
      boolean isCropNotGrowing,
      int cropLightLevel,
      boolean isMoist,
      int moistureValue,
      boolean isHoldingHoe,
      boolean canTill,
      boolean needsUnbreaking) {
    this.isCropNotGrowing = isCropNotGrowing;
    this.cropLightLevel = cropLightLevel;
    this.isMoist = isMoist;
    this.moistureValue = moistureValue;
    this.isHoldingHoe = isHoldingHoe;
    this.canTill = canTill;
    this.needsUnbreaking = needsUnbreaking;
  }

  public static CompatHudInfo forCrop(BlockState blockState, LevelReader level, BlockPos pos) {
    BlazeCropBlock crop = (BlazeCropBlock) blockState.getBlock();
    boolean notGrowing =
        !crop.isMaxAge(blockState) && !BlazeCropBlock.hasSufficientLight(level, pos);
    int light = notGrowing ? level.getRawBrightness(pos, 0) : -1;
    return new CompatHudInfo(notGrowing, light, false, -1, false, false, false);
  }

  public static CompatHudInfo forTilledNetherrack(BlockState blockState) {
    int moisture = blockState.getValue(TilledNetherrackBlock.MOISTURE);
    return new CompatHudInfo(
        false, -1, moisture == FarmBlock.MAX_MOISTURE, moisture, false, false, false);
  }

  public static CompatHudInfo forNetherrack(Player player) {
    if (!BlazeCropConfiguration.tilledNetherrack.get()) {
      return new CompatHudInfo(false, -1, false, -1, false, false, false);
    }
    ItemStack hoe = HoeHelper.holdingHoeTool(player);
    boolean holding = !hoe.isEmpty();
    boolean canTill = holding && HoeHelper.canTillNetherrack(hoe, player);
    boolean needsUnbreaking = BlazeCropConfiguration.netherrackNeedsUnbreaking.get();
    return new CompatHudInfo(false, -1, false, -1, holding, canTill, needsUnbreaking);
  }
}
