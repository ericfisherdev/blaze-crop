package dev.ericfisher.blazecrop;

import dev.ericfisher.blazecrop.config.BlazeCropConfiguration;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

public final class HoeHelper {

  public static boolean canTillNetherrack(ItemStack itemStack, Player player) {
    final Item item = itemStack.getItem();

    if (player != null && player.isCreative()) return true;
    else if (item instanceof HoeItem && player != null)
      return EnchantmentHelper.getItemEnchantmentLevel(
                  player
                      .level()
                      .registryAccess()
                      .lookupOrThrow(Registries.ENCHANTMENT)
                      .getOrThrow(Enchantments.UNBREAKING),
                  itemStack)
              > 0
          || !BlazeCropConfiguration.netherrackNeedsUnbreaking.get();
    else return false;
  }

  public static ItemStack holdingHoeTool(@NotNull Player player) {
    for (InteractionHand enumHand : InteractionHand.values()) {
      final ItemStack itemStack = player.getItemInHand(enumHand);
      if (itemStack.getItem() instanceof HoeItem) return itemStack;
    }

    return ItemStack.EMPTY;
  }
}
