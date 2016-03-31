package de.ax.powermode;

import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Baptiste Mesta
 */
public class PowerModeConfigurableUI implements ConfigurableUi<PowerMode> {


    private JPanel mainPanel;
    private JCheckBox powerModeEnabled;
    private JCheckBox shakeEnabled;
    private JSlider sparkCount;
    private JSlider sparkLife;
    private JSlider shakeRange;
    private JSlider heatup;
    private JSlider heatupTime;
    private JLabel sparkCountValue;
    private JLabel sparkLifeValue;
    private JLabel shakeRangeValue;
    private JLabel heatupValue;
    private JLabel heatupTimeValue;
    private JSlider maxFlameSize;
    private JLabel maxFlameSizeValue;
    private JSlider flameLife;
    private JLabel flameLifeValue;
    private JSlider keyStrokesPerMinute;
    private JLabel keyStrokesPerMinuteValue;
    private JCheckBox FLAMESCheckBox;
    private JCheckBox PARTICLESCheckBox;
    private JSlider sparkSize;
    private JLabel sparkSizeValue;

    public PowerModeConfigurableUI(@NotNull PowerMode powerMode) {
        powerModeEnabled.setSelected(powerMode.isEnabled());
        shakeEnabled.setSelected(powerMode.isShakeEnabled());
        shakeEnabled.addChangeListener(e -> powerMode.setShakeEnabled(shakeEnabled.isSelected()));
        FLAMESCheckBox.setSelected(powerMode.isFlamesEnabled());
        FLAMESCheckBox.addChangeListener(e -> powerMode.setFlamesEnabled(FLAMESCheckBox.isSelected()));
        PARTICLESCheckBox.setSelected(powerMode.isSparksEnabled());
        PARTICLESCheckBox.addChangeListener(e -> powerMode.setSparksEnabled(PARTICLESCheckBox.isSelected()));
        initValues(powerMode.getSparkCount(), sparkCount, sparkCountValue, slider -> powerMode.setSparkCount(slider.getValue()));
        initValues(powerMode.getSparkSize(), sparkSize, sparkSizeValue, slider -> powerMode.setSparkSize(slider.getValue()));
        initValues(powerMode.getSparkLife(), sparkLife, sparkLifeValue, slider -> powerMode.setSparkLife(slider.getValue()));
        initValues(powerMode.getShakeRange(), shakeRange, shakeRangeValue, slider -> powerMode.setShakeRange(slider.getValue()));
        initValues(powerMode.getHeatup(), heatup, heatupValue, slider -> powerMode.setHeatup(slider.getValue()));
        initValues(powerMode.getHeatupTime(), heatupTime, heatupTimeValue, slider -> powerMode.setHeatupTime(slider.getValue()));
        initValues(powerMode.getFlameLife(), flameLife, flameLifeValue, slider -> powerMode.setFlameLife(slider.getValue()));
        initValues(powerMode.getmaxFlameSize(), maxFlameSize, maxFlameSizeValue, slider -> powerMode.setmaxFlameSize(slider.getValue()));
        initValues(powerMode.getKeyStrokesPerMinute(), keyStrokesPerMinute, keyStrokesPerMinuteValue, slider -> powerMode.setKeyStrokesPerMinute(slider.getValue()));


    }

    private void initValues(int initValue, @NotNull JSlider slider, @NotNull JLabel sliderValueLabel, ValueSettable valueSettable) {
        slider.setValue(initValue);
        sliderValueLabel.setText(String.valueOf(initValue));
        slider.addChangeListener(new MyChangeListener(slider, sliderValueLabel) {
            @Override
            public void setValue(JSlider slider) {
                valueSettable.setValue(slider);
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
        private JSlider slider;
        private JLabel jLabel;

        public MyChangeListener(JSlider slider, JLabel jLabel) {
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
