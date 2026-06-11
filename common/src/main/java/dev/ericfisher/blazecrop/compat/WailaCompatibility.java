package dev.ericfisher.blazecrop.compat;

import static net.minecraft.ChatFormatting.*;

import dev.ericfisher.blazecrop.Reference;
import dev.ericfisher.blazecrop.block.BlazeCropBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherrackBlock;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

@WailaPlugin
public class WailaCompatibility implements IWailaPlugin {

  @Override
  public void registerClient(IWailaClientRegistration registration) {
    registration.registerBlockComponent(BlazeCropGrowthProvider.INSTANCE, BlazeCropBlock.class);
    // Scoped to NetherrackBlock so Jade no longer invokes the provider for every block the player
    // looks at. The provider itself still guards on the vanilla Blocks.NETHERRACK instance, since
    // that is the only block the hoe tilling hook registers.
    registration.registerBlockComponent(NetherrackTillingProvider.INSTANCE, NetherrackBlock.class);
  }

  public enum BlazeCropGrowthProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final Component NO_GROWTH =
        Component.translatable("blazecrop.wailatop.nogrowth").withStyle(RED);
    private static final Component LIGHT_LEVEL =
        Component.translatable("blazecrop.wailatop.light").append(": ").withStyle(YELLOW);

    @Override
    public void appendTooltip(
        ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
      final CompatHudInfo info =
          CompatHudInfo.forCrop(
              blockAccessor.getBlockState(), blockAccessor.getLevel(), blockAccessor.getPosition());
      if (info.isCropNotGrowing) {
        tooltip.add(NO_GROWTH);
        if (blockAccessor.getPlayer().isCrouching()) {
          tooltip.add(
              LIGHT_LEVEL
                  .copy()
                  .append(Component.literal(info.cropLightLevel + " (>7)").withStyle(RED)));
        }
      }
    }

    @Override
    public ResourceLocation getUid() {
      return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, Reference.Blocks.BLAZE_CROP);
    }
  }

  public enum NetherrackTillingProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final Component CHECK = Component.literal("✔").withStyle(GREEN);
    private static final Component X = Component.literal("✕").withStyle(RED);
    private static final Component TILL = Component.translatable("blazecrop.waila:till");
    private static final Component UNBREAKING_HINT =
        Component.literal(" (")
            .append(
                Component.translatable("enchantment.minecraft.unbreaking")
                    .append(" I+")
                    .withStyle(RED))
            .append(")");

    private static final ItemStack HOE = new ItemStack(Items.WOODEN_HOE);
    private static final ItemStack ENCHANTED_HOE = HOE.copy();

    static {
      ENCHANTED_HOE.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
    }

    @Override
    public void appendTooltip(
        ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
      if (!blockAccessor.getBlock().equals(Blocks.NETHERRACK)) return;
      final CompatHudInfo info = CompatHudInfo.forNetherrack(blockAccessor.getPlayer());
      if (!info.isHoldingHoe) return;
      final IElementHelper elements = IElementHelper.get();
      tooltip.add(
          elements
              .item(info.needsUnbreaking ? ENCHANTED_HOE : HOE, 0.75F)
              .size(new Vec2(10, 13))
              .translate(new Vec2(-2, -2)));
      tooltip.append(elements.text(info.canTill ? CHECK : X).translate(new Vec2(-3.5f, 6)));
      tooltip.append(elements.spacer(4, 0));
      tooltip.append(
          elements
              .text(TILL.copy().append(unbreakingHint(info.canTill, info.needsUnbreaking)))
              .translate(new Vec2(0, 2)));
    }

    private Component unbreakingHint(boolean canTill, boolean needsUnbreaking) {
      if (needsUnbreaking && !canTill) return UNBREAKING_HINT;
      else return Component.empty();
    }

    @Override
    public ResourceLocation getUid() {
      return ResourceLocation.fromNamespaceAndPath(
          Reference.MOD_ID, Reference.Blocks.TILLED_NETHERRACK);
    }
  }
}
