package dev.ericfisher.blazecrop.init;

import dev.architectury.hooks.item.tool.HoeItemHooks;
import dev.ericfisher.blazecrop.HoeHelper;
import dev.ericfisher.blazecrop.config.BlazeCropConfiguration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.block.Blocks;

public final class ModHooks {

  private static final Component UNTILLABLE_MESSAGE =
      Component.translatable("blazecrop.alert.hoe")
          .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true));

  public static void addTillables() {
    HoeItemHooks.addTillable(
        Blocks.NETHERRACK,
        ctx -> {
          if (BlazeCropConfiguration.tilledNetherrack.get()) {
            final boolean canTill =
                HoeItem.onlyIfAirAbove(ctx)
                    && HoeHelper.canTillNetherrack(ctx.getItemInHand(), ctx.getPlayer());
            if (!canTill && ctx.getPlayer() != null)
              ctx.getPlayer().displayClientMessage(UNTILLABLE_MESSAGE, true);
            return canTill;
          } else return false;
        },
        ctx -> {},
        ctx -> ModBlocks.TILLED_NETHERRACK.get().defaultBlockState());
  }
}
