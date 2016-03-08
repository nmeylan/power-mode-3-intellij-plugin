package com.bmesta.powermode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author Baptiste Mesta
 */
public class PowerModeConfigurableUI implements ConfigurableUi<PowerMode> {


    private JPanel mainPanel;
    private JCheckBox powerModeEnabled;
    private JCheckBox shakeEnabled;
    private JSlider particles;
    private JSlider particleRange;
    private JSlider shakeRange;

    public PowerModeConfigurableUI(@NotNull PowerMode powerMode) {
        powerModeEnabled.setSelected(powerMode.isEnabled());
        shakeEnabled.setSelected(powerMode.isShakeEnabled());
        shakeEnabled.addChangeListener(e -> powerMode.setShakeEnabled(shakeEnabled.isSelected()));
        particles.setValue(powerMode.getParticleCount());
        particles.addChangeListener(e -> powerMode.setParticleCount(particles.getValue()));
        particleRange.setValue(powerMode.getParticleRange());
        particleRange.addChangeListener(e -> powerMode.setParticleRange(particleRange.getValue()));
        shakeRange.setValue(powerMode.getShakeRange());
        shakeRange.addChangeListener(e -> powerMode.setShakeRange(shakeRange.getValue()));
    }

    @Override
    public void reset(@NotNull PowerMode powerMode) {
        powerModeEnabled.setSelected(powerMode.isEnabled());
    }

    @Override
    public boolean isModified(@NotNull PowerMode powerMode) {
        return powerModeEnabled.isSelected() != powerMode.isEnabled();
    }

    @Override
    public void apply(@NotNull PowerMode powerMode) throws ConfigurationException {
        powerMode.setEnabled(powerModeEnabled.isSelected());
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return mainPanel;
    }
}
