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
    private JSlider heatup;
    private JSlider heatupTime;
    private JLabel particlesValue;
    private JLabel particleRangeValue;
    private JLabel shakeRangeValue;
    private JLabel heatupValue;
    private JLabel heatupTimeValue;
    private JSlider maxFlameSize;
    private JLabel maxFlameSizeValue;
    private JSlider flameLife;
    private JLabel flameLifeValue;
    private JSlider keyStrokesPerMinute;
    private JLabel keyStrokesPerMinuteValue;

    public PowerModeConfigurableUI(@NotNull PowerMode powerMode) {
        powerModeEnabled.setSelected(powerMode.isEnabled());
        shakeEnabled.setSelected(powerMode.isShakeEnabled());
        shakeEnabled.addChangeListener(e -> powerMode.setShakeEnabled(shakeEnabled.isSelected()));
        initValues(powerMode.getParticleCount(), particles, particlesValue, powerMode, slider -> powerMode.setParticleCount(slider.getValue()));
        initValues(powerMode.getParticleRange(), particleRange, particleRangeValue, powerMode, slider -> powerMode.setParticleRange(slider.getValue()));
        initValues(powerMode.getShakeRange(), shakeRange, shakeRangeValue, powerMode, slider -> powerMode.setShakeRange(slider.getValue()));
        initValues(powerMode.getHeatup(), heatup, heatupValue, powerMode, slider -> powerMode.setHeatup(slider.getValue()));
        initValues(powerMode.getHeatupTime(), heatupTime, heatupTimeValue, powerMode, slider -> powerMode.setHeatupTime(slider.getValue()));
        initValues(powerMode.getFlameLife(), flameLife, flameLifeValue, powerMode, slider -> powerMode.setFlameLife(slider.getValue()));
        initValues(powerMode.getmaxFlameSize(), maxFlameSize, maxFlameSizeValue, powerMode, slider -> powerMode.setmaxFlameSize(slider.getValue()));
        initValues(powerMode.getKeyStrokesPerMinute(), keyStrokesPerMinute, keyStrokesPerMinuteValue, powerMode, slider -> powerMode.setKeyStrokesPerMinute(slider.getValue()));
    }

    private void initValues(int n, JSlider ht, JLabel htv, PowerMode powerMode, ValueSettable valueSettable) {
        ht.setValue(n);
        htv.setText(String.valueOf(n));
        ValueSettable vs = valueSettable;
        ht.addChangeListener(new MyChangeListener(powerMode, ht, htv) {
            @Override
            public void setValue(JSlider slider) {
                vs.setValue(slider);
            }
        });
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

    private abstract class MyChangeListener implements ChangeListener, ValueSettable {
        private final PowerMode powerMode;
        private JSlider slider;
        private JLabel jLabel;

        public MyChangeListener(PowerMode powerMode, JSlider slider, JLabel jLabel) {
            this.powerMode = powerMode;
            this.slider = slider;
            this.jLabel = jLabel;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            setValue(slider);
            jLabel.setText(String.valueOf(slider.getValue()));
        }

    }
}
