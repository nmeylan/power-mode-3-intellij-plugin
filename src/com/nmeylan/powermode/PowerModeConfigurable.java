package com.nmeylan.powermode;


import com.intellij.openapi.options.ConfigurableBase;
import com.intellij.openapi.options.ConfigurableUi;
import com.nmeylan.powermode.power.ui.PowerModeConfigurableUI;
import org.jetbrains.annotations.NotNull;

public class PowerModeConfigurable extends ConfigurableBase {

  private PowerMode settings;

  public PowerModeConfigurable() {
    super("Power Mode 3", "Power Mode 3", "Power Mode 3");
    settings = PowerMode.getInstance();
  }

  @NotNull
  @Override
  protected PowerMode getSettings() {
    if (settings == null) {
      throw new IllegalStateException("power mode is null");
    }
    return settings;
  }

  @Override
  protected ConfigurableUi createUi() {
    return new PowerModeConfigurableUI(settings);
  }
}
