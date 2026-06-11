package dev.ericfisher.blazecrop.fabric;

import static dev.ericfisher.blazecrop.Reference.MOD_ID;

import dev.architectury.event.events.common.InteractionEvent;
import dev.ericfisher.blazecrop.ModExpectPlatform;
import dev.ericfisher.blazecrop.Reference;
import dev.ericfisher.blazecrop.block.BlazeCropBlock;
import dev.ericfisher.blazecrop.block.TilledNetherrackBlock;
import dev.ericfisher.blazecrop.config.BlazeCropConfiguration;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.config.ModConfig;

public class ModExpectPlatformImpl extends ModExpectPlatform {

  public static Block getTilledNetherrackBlock() {
    return new TilledNetherrackBlock();
  }

  public static void initConfig() {
    NeoForgeConfigRegistry.INSTANCE.register(
        Reference.MOD_ID,
        ModConfig.Type.COMMON,
        BlazeCropConfiguration.COMMON_CONFIG,
        Reference.CONFIG_FILE);
    NeoForgeModConfigEvents.loading(MOD_ID)
        .register(
            listener ->
                BlazeCropConfiguration.onLoad(listener.getFileName(), listener.getLoadedConfig()));
    NeoForgeModConfigEvents.reloading(MOD_ID)
        .register(
            listener ->
                BlazeCropConfiguration.onLoad(listener.getFileName(), listener.getLoadedConfig()));
  }

  /* Neoforge-specific hooks */

  public static boolean canSustainPlant(
      BlockState blockState,
      LevelReader level,
      BlockPos pos,
      Direction facing,
      BlazeCropBlock crop) {
    return crop.mayPlaceOn(blockState, level, pos);
  }

  public static boolean onCropsGrowPre(
      ServerLevel level, BlockPos pos, BlockState state, boolean doGrow) {
    return doGrow;
  }

  public static void onCropsGrowPost(ServerLevel level, BlockPos pos, BlockState state) {
    // NO OP
  }

  public static boolean onFarmlandTrample(
      Level level, BlockPos pos, BlockState state, float fallDistance, Entity entity) {
    // Vanilla logic
    return level.random.nextFloat() < fallDistance - -1.5F
        && entity instanceof LivingEntity
        && (entity instanceof Player || level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))
        && entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight() > -1.512F
        && InteractionEvent.FARMLAND_TRAMPLE
                .invoker()
                .trample(level, pos, state, fallDistance, entity)
                .value()
            == null; // Fire architectury event
  }
}
