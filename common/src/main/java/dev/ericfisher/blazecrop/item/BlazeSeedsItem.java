package dev.ericfisher.blazecrop.item;

import dev.ericfisher.blazecrop.init.ModBlocks;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class BlazeSeedsItem extends ItemNameBlockItem {

  public BlazeSeedsItem() {
    super(ModBlocks.BLAZE_CROP.get(), new Properties().arch$tab(CreativeModeTabs.NATURAL_BLOCKS));
  }

  @Override
  public void appendHoverText(
      ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, context, tooltip, flag);
    tooltip.add(Component.translatable("blazecrop.tip.seed"));
  }
}
