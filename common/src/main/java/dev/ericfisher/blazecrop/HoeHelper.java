package dev.ericfisher.blazecrop;

import dev.ericfisher.blazecrop.config.BlazeCropConfiguration;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HoeHelper {

  /**
   * Whether the given stack may till netherrack. Called per-frame by the Jade/TOP HUD providers, so
   * the cheap checks (creative, config) short-circuit before the enchantment-registry lookup.
   */
  public static boolean canTillNetherrack(@NotNull ItemStack itemStack, @Nullable Player player) {
    if (player == null) return false;
    if (player.isCreative()) return true;
    if (!(itemStack.getItem() instanceof HoeItem)) return false;
    if (!BlazeCropConfiguration.netherrackNeedsUnbreaking.get()) return true;
    return hasUnbreaking(itemStack, player);
  }

  /**
   * Uses the non-throwing registry lookups: enchantments are data-driven in 1.21, so a datapack
   * could remove Unbreaking, and this runs per-frame from HUD providers where throwing would be
   * unacceptable. A missing enchantment simply counts as "not enchanted".
   */
  private static boolean hasUnbreaking(ItemStack itemStack, Player player) {
    return player
        .level()
        .registryAccess()
        .lookup(Registries.ENCHANTMENT)
        .flatMap(enchantments -> enchantments.get(Enchantments.UNBREAKING))
        .map(unbreaking -> EnchantmentHelper.getItemEnchantmentLevel(unbreaking, itemStack) > 0)
        .orElse(false);
  }

  public static ItemStack holdingHoeTool(@NotNull Player player) {
    for (InteractionHand enumHand : InteractionHand.values()) {
      final ItemStack itemStack = player.getItemInHand(enumHand);
      if (itemStack.getItem() instanceof HoeItem) return itemStack;
    }

    return ItemStack.EMPTY;
  }
}
