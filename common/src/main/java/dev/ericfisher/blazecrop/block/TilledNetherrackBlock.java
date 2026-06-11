package dev.ericfisher.blazecrop.block;

import static net.minecraft.world.level.block.Blocks.NETHERRACK;

import dev.ericfisher.blazecrop.ModExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class TilledNetherrackBlock extends FarmBlock {

  private static final Properties PROPERTIES =
      Properties.of()
          .mapColor(MapColor.NETHER)
          .randomTicks()
          .destroyTime(0.6F)
          .requiresCorrectToolForDrops()
          .strength(3.0F, 9.0F)
          .sound(SoundType.NETHERRACK)
          // Moistened (lava-kept) soil glows at redstone-torch level; dry soil is dark.
          .lightLevel(state -> state.getValue(MOISTURE) > 0 ? 7 : 0);

  public TilledNetherrackBlock() {
    super(PROPERTIES);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
        ? NETHERRACK.defaultBlockState()
        : super.getStateForPlacement(context);
  }

  @Override
  public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource pRand) {
    if (!state.canSurvive(level, pos)) turnToNetherrack(state, level, pos);
  }

  @Override
  public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    final int moisture = state.getValue(MOISTURE);
    // Tilled netherrack is kept moist by lava, not water or rain.
    if (!isNearLava(level, pos)) {
      if (moisture > 0) {
        level.setBlock(pos, state.setValue(MOISTURE, moisture - 1), 2);
      } else if (!hasCropAbove(level, pos)) {
        turnToNetherrack(state, level, pos);
      }
    } else if (moisture < MAX_MOISTURE) {
      level.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE), 2);
    }
  }

  @Override
  public void fallOn(
      Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
    //noinspection ConstantValue
    if (!level.isClientSide
        && ModExpectPlatform.onFarmlandTrample(level, pos, state, fallDistance, entity))
      turnToNetherrack(level.getBlockState(pos), level, pos);

    entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
  }

  public static void turnToNetherrack(BlockState state, Level level, BlockPos pos) {
    level.setBlockAndUpdate(pos, pushEntitiesUp(state, NETHERRACK.defaultBlockState(), level, pos));
  }

  private static boolean hasCropAbove(BlockGetter level, BlockPos pos) {
    return level.getBlockState(pos.above()).is(BlockTags.MAINTAINS_FARMLAND);
  }

  /** Mirrors {@code FarmBlock.isNearWater} but looks for lava within a 4-block radius. */
  private static boolean isNearLava(LevelReader level, BlockPos pos) {
    for (BlockPos cursor : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
      if (level.getFluidState(cursor).is(FluidTags.LAVA)) {
        return true;
      }
    }
    return false;
  }
}
