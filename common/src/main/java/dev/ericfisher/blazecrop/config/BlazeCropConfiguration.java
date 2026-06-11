package dev.ericfisher.blazecrop.config;

import dev.ericfisher.blazecrop.BlazeCrop;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.config.IConfigSpec.ILoadedConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

public final class BlazeCropConfiguration {

  public static final ModConfigSpec COMMON_CONFIG;
  public static ConfigValue<Double, ModConfigSpec.DoubleValue> tilledSoilMultiplier;
  public static ConfigValue<Double, ModConfigSpec.DoubleValue> tilledNetherMultiplier;
  public static ConfigValue<Boolean, ModConfigSpec.BooleanValue> tilledNetherrack;
  public static ConfigValue<Integer, ModConfigSpec.IntValue> blazeChance;
  public static ConfigValue<Boolean, ModConfigSpec.BooleanValue> netherrackNeedsUnbreaking;

  static {
    final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
    buildConfig(builder);
    COMMON_CONFIG = builder.build();
  }

  public static void buildConfig(ModConfigSpec.Builder builder) {
    tilledSoilMultiplier =
        ConfigValue.defineDouble(
            builder,
            "tilledSoilMultiplier",
            0.5D,
            0.0D,
            25.0D,
            "Crop growth multiplier on farmland, this value multiplies the default vanilla growth rate.",
            "Example: 10.5 -> Ten and a half times the speed of vanilla crop growth.");
    tilledNetherMultiplier =
        ConfigValue.defineDouble(
            builder,
            "tilledNetherMultiplier",
            1.0D,
            0.0D,
            25.0D,
            "Crop growth multiplier on tilled netherrack, this value multiplies the default vanilla growth rate.",
            "Example: 10.5 -> Ten and a half times the speed of vanilla crop growth.");
    tilledNetherrack =
        ConfigValue.defineBoolean(
            builder,
            "tilledNetherrack",
            true,
            "Enable tilling Netherrack.",
            "Disabling this does not remove the Tilled Netherrack block, only the creation of it.");
    blazeChance =
        ConfigValue.defineInt(
            builder,
            "blazeChance",
            50,
            0,
            Integer.MAX_VALUE,
            "Chance of spawning a Blaze when a fully grown Blaze Crop grown on Tilled Netherrack is harvested.",
            "Example: 10 -> 1 in 10 chance. 0 to disable.");
    netherrackNeedsUnbreaking =
        ConfigValue.defineBoolean(
            builder,
            "netherrackNeedsUnbreaking",
            true,
            "Require a hoe to be enchanted with Unbreaking (I) to till Netherrack.");
  }

  public static void onLoad(String configFile, ILoadedConfig configData) {
    BlazeCrop.LOGGER.info("Reloading {} from disk", configFile);
    COMMON_CONFIG.acceptConfig(configData);
  }

  /**
   * Wrapper around the Forge config spec to make it easier to use when building the {@link
   * ConfigScreen}.
   */
  public static class ConfigValue<T, V extends ModConfigSpec.ConfigValue<T>> {

    private final V configValue;
    private final String path;
    private final T min;
    private final T max;
    private final Component comment;

    private ConfigValue(
        V configValue, String path, @Nullable T min, @Nullable T max, String... comment) {
      this.configValue = configValue;
      this.path = path;
      this.min = min;
      this.max = max;
      this.comment = Component.literal(String.join("\n", comment));
    }

    public static ConfigValue<Double, ModConfigSpec.DoubleValue> defineDouble(
        ModConfigSpec.Builder builder,
        String path,
        double defaultValue,
        double min,
        double max,
        String... comment) {
      return new ConfigValue<>(
          builder.comment(comment).defineInRange(path, defaultValue, min, max),
          path,
          min,
          max,
          comment);
    }

    public static ConfigValue<Integer, ModConfigSpec.IntValue> defineInt(
        ModConfigSpec.Builder builder,
        String path,
        int defaultValue,
        int min,
        int max,
        String... comment) {
      return new ConfigValue<>(
          builder.comment(comment).defineInRange(path, defaultValue, min, max),
          path,
          min,
          max,
          comment);
    }

    public static ConfigValue<Boolean, ModConfigSpec.BooleanValue> defineBoolean(
        ModConfigSpec.Builder builder, String path, boolean defaultValue, String... comment) {
      return new ConfigValue<>(
          builder.comment(comment).define(path, defaultValue), path, null, null, comment);
    }

    public T get() {
      return configValue.get();
    }

    public V getConfigValue() {
      return configValue;
    }

    public T getMin() {
      return min;
    }

    public T getMax() {
      return max;
    }

    public Component getComment() {
      return comment;
    }

    public String getPath() {
      return path;
    }
  }
}
