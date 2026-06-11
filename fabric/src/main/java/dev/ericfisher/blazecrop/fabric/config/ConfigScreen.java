package dev.ericfisher.blazecrop.fabric.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ConfigScreen implements ModMenuApi {

  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return dev.ericfisher.blazecrop.config.ConfigScreen::getConfigScreen;
  }
}
