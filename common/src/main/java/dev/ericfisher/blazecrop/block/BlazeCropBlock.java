package dev.ericfisher.blazecrop.block;

import dev.ericfisher.blazecrop.ModExpectPlatform;
import dev.ericfisher.blazecrop.config.BlazeCropConfiguration;
import dev.ericfisher.blazecrop.init.ModBlocks;
import dev.ericfisher.blazecrop.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlazeCropBlock extends CropBlock {

  private static final Properties PROPERTIES =
      Properties.of()
          .mapColor(MapColor.PLANT)
          .noCollission()
          .noOcclusion()
          .randomTicks()
          .instabreak()
          .sound(SoundType.CROP)
          // Past the halfway point of growth (age > MAX_AGE / 2, i.e. age >= 4) the crop glows at
          // torch level as a "ready to harvest" tell; younger crops emit no light.
          .lightLevel(state -> state.getValue(AGE) > MAX_AGE / 2 ? 14 : 0);

  /**
   * Half-extent of the cube a mature crop can ignite within (a flammable block within 5 blocks).
   */
  public static final int FIRE_SPREAD_RANGE = 5;

  public BlazeCropBlock() {
    super(PROPERTIES);
  }

  @Override
  public void playerDestroy(
      Level level,
      Player player,
      BlockPos pos,
      BlockState state,
      @Nullable BlockEntity blockEntity,
      ItemStack tool) {
    super.playerDestroy(level, player, pos, state, blockEntity, tool);
    if (this.isMaxAge(state)) {
      maybeSpawnBlaze(level, pos, player);
    }
  }

  @Override
  protected InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
    return harvest(state, level, pos, player)
        ? InteractionResult.sidedSuccess(level.isClientSide)
        : super.useWithoutItem(state, level, pos, player, hit);
  }

  @Override
  protected ItemInteractionResult useItemOn(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    return harvest(state, level, pos, player)
        ? ItemInteractionResult.sidedSuccess(level.isClientSide)
        : super.useItemOn(stack, state, level, pos, player, hand, hit);
  }

  /**
   * Harvests a fully grown crop in place: drops the produce and replants by resetting it to age 0,
   * leaving a single seed behind. Returns {@code false} (so vanilla interaction continues) when the
   * feature is disabled or the crop is not yet mature.
   */
  private boolean harvest(BlockState state, Level level, BlockPos pos, Player player) {
    if (!BlazeCropConfiguration.rightClickHarvest.get() || !this.isMaxAge(state)) {
      return false;
    }
    if (level instanceof ServerLevel serverLevel) {
      dropHarvestProduce(serverLevel, pos, state);
      maybeSpawnBlaze(serverLevel, pos, player);
      serverLevel.setBlock(pos, this.getStateForAge(0), Block.UPDATE_CLIENTS);
      serverLevel.playSound(
          null, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }
    return true;
  }

  /** Drops this block's loot but withholds one seed, which becomes the replanted crop. */
  private void dropHarvestProduce(ServerLevel level, BlockPos pos, BlockState state) {
    final ItemLike replantSeed = this.getBaseSeedId();
    boolean seedWithheld = false;
    for (ItemStack drop : Block.getDrops(state, level, pos, null)) {
      if (!seedWithheld && drop.is(replantSeed.asItem())) {
        drop.shrink(1);
        seedWithheld = true;
      }
      if (!drop.isEmpty()) {
        Block.popResource(level, pos, drop);
      }
    }
  }

  private static void maybeSpawnBlaze(Level level, BlockPos pos, Player player) {
    if (BlazeCropConfiguration.blazeChance.get() <= 0 || !isOnNetherrack(level, pos)) {
      return;
    }
    if (level.random.nextInt(BlazeCropConfiguration.blazeChance.get()) != 0) {
      return;
    }
    final Blaze blaze = EntityType.BLAZE.create(level);
    if (blaze != null) {
      blaze.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
      blaze.lookAt(player, 360.0F, 360.0F);
      blaze.setTarget(player);
      level.addFreshEntity(blaze);
    }
  }

  private static boolean isOnNetherrack(LevelReader worldIn, BlockPos pos) {
    return worldIn.getBlockState(pos.below()).is(ModBlocks.TILLED_NETHERRACK.get());
  }

  private static boolean isOnNetherrack(BlockState soilState) {
    return soilState.is(ModBlocks.TILLED_NETHERRACK.get());
  }

  @Override
  public boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
    // Blaze crops only grow on tilled netherrack.
    return state.is(ModBlocks.TILLED_NETHERRACK.get());
  }

  @Override
  public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (!level.isLoaded(pos)) return; // Neoforge
    if (tryRainDestroy(level, pos, random)) {
      return; // crop was washed out; nothing left to grow or ignite
    }
    if (this.isMaxAge(state)) {
      trySpreadFire(level, pos, random);
      return;
    }
    final BlockState soilState = level.getBlockState(pos.below());
    if (hasSufficientLight(soilState, level, pos)) {
      final int age = this.getAge(state);
      final float growthChance = getGrowthSpeed(this, soilState, level, pos);
      final boolean doGrow =
          growthChance > 0 && random.nextInt((int) (25.0F / growthChance) + 1) == 0;
      if (ModExpectPlatform.onCropsGrowPre(level, pos, state, doGrow)) { // Neoforge
        level.setBlock(pos, this.getStateForAge(age + 1), 2);
        ModExpectPlatform.onCropsGrowPost(level, pos, state); // Neoforge
      }
    }
  }

  /**
   * While it is raining on a sky-exposed crop, the crop may be washed out and destroyed with no
   * drops. Gated by the {@code rainDestroyChance} config (1-in-N, 0 disables). {@link
   * Level#isRainingAt} already excludes the Nether, dry biomes, and covered crops. Returns true if
   * the crop was destroyed.
   *
   * <p>This is intentional game design, not an oversight: tilled netherrack and blaze crops can be
   * built in the Overworld (tilling only needs netherrack, which players can place anywhere), so a
   * naive uncovered farm under open sky is a real scenario. Rain destruction is the thematic
   * downside of exposing a nether plant to the sky and is independent of the lava-moisture
   * mechanic, which governs growth speed rather than rain immunity.
   */
  private static boolean tryRainDestroy(ServerLevel level, BlockPos pos, RandomSource random) {
    final int chance = BlazeCropConfiguration.rainDestroyChance.get();
    if (chance <= 0 || !level.isRainingAt(pos)) {
      return false;
    }
    if (random.nextInt(chance) != 0) {
      return false;
    }
    return level.destroyBlock(
        pos, false); // false -> no drops, but break particles/sound still play
  }

  /**
   * At max age, a Blaze Crop may ignite a single flammable block within {@link #FIRE_SPREAD_RANGE}
   * blocks. Gated by the vanilla {@code doFireTick} gamerule and the {@code fireSpreadChance}
   * config (1-in-N, 0 disables). Reuses vanilla flammability via {@link
   * BaseFireBlock#canBePlacedAt} and never ignites a cell on or adjacent to the farm (Blaze Crops /
   * Tilled Netherrack).
   */
  private static void trySpreadFire(ServerLevel level, BlockPos cropPos, RandomSource random) {
    final int chance = BlazeCropConfiguration.fireSpreadChance.get();
    if (chance <= 0 || !level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
      return;
    }
    if (random.nextInt(chance) != 0) {
      return;
    }
    final int span = FIRE_SPREAD_RANGE * 2 + 1;
    final BlockPos target =
        cropPos.offset(
            random.nextInt(span) - FIRE_SPREAD_RANGE,
            random.nextInt(span) - FIRE_SPREAD_RANGE,
            random.nextInt(span) - FIRE_SPREAD_RANGE);
    if (!level.isLoaded(target)
        || !BaseFireBlock.canBePlacedAt(level, target, Direction.UP)
        || touchesFarm(level, target)) {
      return;
    }
    level.setBlock(target, BaseFireBlock.getState(level, target), Block.UPDATE_ALL);
  }

  /**
   * True if {@code pos} or any of its 26 surrounding cells is a Blaze Crop or Tilled Netherrack.
   */
  private static boolean touchesFarm(LevelReader level, BlockPos pos) {
    for (BlockPos cursor : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
      final Block block = level.getBlockState(cursor).getBlock();
      if (block instanceof BlazeCropBlock || block instanceof TilledNetherrackBlock) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasSufficientLight(
      BlockState soilState, LevelReader worldIn, BlockPos pos) {
    return isOnNetherrack(soilState) || worldIn.getRawBrightness(pos, 0) <= 7;
  }

  public static boolean hasSufficientLight(LevelReader worldIn, BlockPos pos) {
    return hasSufficientLight(worldIn.getBlockState(pos.below()), worldIn, pos);
  }

  // Reimplementing the whole thing because it's static, and we don't want to penalize tilled
  // netherrack.
  protected static float getGrowthSpeed(
      Block block, BlockState centerSoilState, Level level, BlockPos pos) {
    float f = 1.0F;
    BlockPos soilOrigin = pos.below();
    BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

    for (int i = -1; i <= 1; ++i) {
      for (int j = -1; j <= 1; ++j) {
        float g = 0.0F;
        BlockState soilState =
            (i == 0 && j == 0)
                ? centerSoilState
                : level.getBlockState(mutable.setWithOffset(soilOrigin, i, 0, j));
        if (ModExpectPlatform.canSustainPlant(
            soilState, level, soilOrigin, Direction.UP, (BlazeCropBlock) block)) {
          g = 1.0F;
          if (soilState.getValue(FarmBlock.MOISTURE) > 0) {
            g = 3.0F;
          }
        }

        if (i != 0 || j != 0) {
          g /= 4.0F;
        }

        f += g;
      }
    }

    BlockPos west = pos.west();
    BlockPos east = pos.east();
    BlockPos north = pos.north();
    BlockPos south = pos.south();
    boolean bl = level.getBlockState(west).is(block) || level.getBlockState(east).is(block);
    boolean bl2 = level.getBlockState(north).is(block) || level.getBlockState(south).is(block);
    if (bl && bl2) {
      f /= 2.0F;
    } else {
      boolean bl3 =
          level.getBlockState(west.north()).is(block)
              || level.getBlockState(east.north()).is(block)
              || level.getBlockState(east.south()).is(block)
              || level.getBlockState(west.south()).is(block);
      if (bl3) {
        f /= 2.0F;
      }
    }

    f *= BlazeCropConfiguration.tilledNetherMultiplier.get();

    return f;
  }

  @Override
  protected int getBonemealAgeIncrease(Level level) {
    return 0;
  }

  @Override
  public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    final BlockPos below = pos.below();
    final BlockState soilState = level.getBlockState(below);
    //noinspection ConstantValue
    return hasSufficientLight(soilState, level, pos)
        && ModExpectPlatform.canSustainPlant(soilState, level, below, Direction.UP, this);
  }

  @Override
  @NotNull protected ItemLike getBaseSeedId() {
    return ModItems.BLAZE_SEEDS.get();
  }

  @Override
  public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
    return false;
  }
}
