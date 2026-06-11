package dev.ericfisher.blazecrop.neoforge;

import static dev.ericfisher.blazecrop.Reference.MOD_ID;

import dev.ericfisher.blazecrop.neoforge.compat.TOPCompatibility;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

@Mod(MOD_ID)
public class BlazeCrop {

  public BlazeCrop(IEventBus modBus, ModContainer modContainer) {
    ModExpectPlatformImpl.setModContainer(modContainer);
    dev.ericfisher.blazecrop.BlazeCrop.init();
    modBus.addListener(BlazeCrop::onEnqueueIMC);
  }

  public static void onEnqueueIMC(InterModEnqueueEvent event) {
    if (ModList.get().isLoaded("theoneprobe")) TOPCompatibility.register();
  }
}
