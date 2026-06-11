package dev.ericfisher.blazecrop.neoforge.compat;

import com.google.common.base.Function;
import dev.ericfisher.blazecrop.Reference;
import dev.ericfisher.blazecrop.compat.CompatHudInfo;
import dev.ericfisher.blazecrop.init.ModBlocks;
import javax.annotation.Nullable;
import mcjty.theoneprobe.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.InterModComms;

public final class TOPCompatibility implements Function<ITheOneProbe, Void> {

  public static void register() {
    InterModComms.sendTo("theoneprobe", "GetTheOneProbe", TOPCompatibility::new);
  }

  @Nullable @Override
  public Void apply(ITheOneProbe theOneProbe) {
    theOneProbe.registerProvider(
        new IProbeInfoProvider() {
          @Override
          public ResourceLocation getID() {
            return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "probe_provider");
          }

          @Override
          public void addProbeInfo(
              ProbeMode mode,
              IProbeInfo probeInfo,
              Player player,
              Level world,
              BlockState blockState,
              IProbeHitData data) {
            if (blockState.is(ModBlocks.TILLED_NETHERRACK.get())) {
              final CompatHudInfo info = CompatHudInfo.forTilledNetherrack(blockState);
              if (mode == ProbeMode.EXTENDED) {
                if (info.isMoist) {
                  probeInfo.text(CompoundText.create().label("{*blazecrop.wailatop.moist*}"));
                } else {
                  probeInfo.text(CompoundText.create().label("{*blazecrop.wailatop.dry*}"));
                }
              }
              if (mode == ProbeMode.DEBUG) {
                probeInfo.text(CompoundText.create().labelInfo("MOISTURE: ", info.moistureValue));
              }
            } else if (blockState.is(ModBlocks.BLAZE_CROP.get())) {
              final CompatHudInfo info = CompatHudInfo.forCrop(blockState, world, data.getPos());
              if (info.isCropNotGrowing) {
                probeInfo.text(CompoundText.create().error("{*blazecrop.wailatop.nogrowth*}"));
                if (mode == ProbeMode.EXTENDED) {
                  probeInfo.text(
                      CompoundText.create()
                          .label("{*blazecrop.wailatop.light*}: ")
                          .info(String.valueOf(info.cropLightLevel))
                          .error(" (>7)"));
                }
              }
            } else if (blockState.is(Blocks.NETHERRACK)) {
              final CompatHudInfo info = CompatHudInfo.forNetherrack(player);
              if (info.isHoldingHoe) {
                final IProbeInfo hori =
                    probeInfo.horizontal(
                        probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
                if (info.canTill) {
                  hori.icon(
                      ResourceLocation.fromNamespaceAndPath(
                          "theoneprobe", "textures/gui/icons.png"),
                      0,
                      16,
                      13,
                      13,
                      probeInfo
                          .defaultIconStyle()
                          .width(18)
                          .height(14)
                          .textureWidth(32)
                          .textureHeight(32));
                  hori.text(CompoundText.create().ok("{*blazecrop.top.hoe*}"));
                } else {
                  hori.icon(
                      ResourceLocation.fromNamespaceAndPath(
                          "theoneprobe", "textures/gui/icons.png"),
                      16,
                      16,
                      13,
                      13,
                      probeInfo
                          .defaultIconStyle()
                          .width(18)
                          .height(14)
                          .textureWidth(32)
                          .textureHeight(32));
                  hori.text(
                      CompoundText.create()
                          .warning(
                              "{*blazecrop.top.hoe*}"
                                  + (info.needsUnbreaking
                                      ? " ({*enchantment.minecraft.unbreaking*} I+)"
                                      : "")));
                }
              }
            }
          }
        });
    return null;
  }
}
