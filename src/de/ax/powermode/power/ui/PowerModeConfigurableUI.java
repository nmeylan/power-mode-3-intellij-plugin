package de.ax.powermode.power.ui;

import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import de.ax.powermode.PowerMode;
import de.ax.powermode.power.color.ColorViewController;
import de.ax.powermode.power.color.MultiGradientPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
    private JLabel velocityFactorValue;
    private JLabel gravityFactorValue;
    private JSlider velocityFactor;
    private JSlider gravityFactor;
    private JLabel frameRateValue;
    private JSlider frameRate;
    private JSlider sparkColorRedTo;
    private JSlider sparkColorRedFrom;
    private JSlider sparkColorGreenFrom;
    private JSlider sparkColorGreenTo;
    private JSlider sparkColorBlueFrom;
    private JSlider sparkColorBlueTo;
    private JLabel sparkColorRedFromValue;
    private JLabel sparkColorRedToValue;
    private JLabel sparkColorGreenFromValue;
    private JLabel sparkColorGreenToValue;
    private JLabel sparkColorBlueFromValue;
    private JLabel sparkColorBlueToValue;
    private JLabel sparkColorAlphaValue;
    private JSlider sparkColorAlpha;
    private JPanel colorView;
    private JCheckBox visualizeEveryCaretMovementCheckBox;
    private JCheckBox PLAYMUSICCheckBox;
    private JTextField soundsFolder;
    private JCheckBox BAMCheckBox;
    private JLabel bamLifeValue;
    private JSlider bamLife;
    private JLabel heatupThresholdValue;
    private JSlider heatupThreshold;
    private JCheckBox PowerIndicatorCheckBox;


    public PowerModeConfigurableUI(PowerMode powerMode) {
        ((MultiGradientPanel) colorView).setColorEdges(PowerMode.obtainColorEdges(powerMode));
        new ColorViewController((MultiGradientPanel) colorView, powerMode);
        powerModeEnabled.setSelected(powerMode.isEnabled());
        shakeEnabled.setSelected(powerMode.isShakeEnabled());
        shakeEnabled.addChangeListener(e -> powerMode.setShakeEnabled(shakeEnabled.isSelected()));
        FLAMESCheckBox.setSelected(powerMode.isFlamesEnabled());
        FLAMESCheckBox.addChangeListener(e -> powerMode.setFlamesEnabled(FLAMESCheckBox.isSelected()));
        PARTICLESCheckBox.setSelected(powerMode.isSparksEnabled());
        PARTICLESCheckBox.addChangeListener(e -> powerMode.setSparksEnabled(PARTICLESCheckBox.isSelected()));
        BAMCheckBox.setSelected(powerMode.isBamEnabled());
        BAMCheckBox.addChangeListener(e -> powerMode.setIsBamEnabled(BAMCheckBox.isSelected()));
        visualizeEveryCaretMovementCheckBox.setSelected(powerMode.getIsCaretAction());
        visualizeEveryCaretMovementCheckBox.addChangeListener(e -> powerMode.setIsCaretAction(visualizeEveryCaretMovementCheckBox.isSelected()));
        PLAYMUSICCheckBox.setSelected(powerMode.isSoundsPlaying());
        PLAYMUSICCheckBox.addChangeListener(e -> powerMode.setIsSoundsPlaying(PLAYMUSICCheckBox.isSelected()));

        PowerIndicatorCheckBox.setSelected(powerMode.isPowerIndicatorEnabled());
        PowerIndicatorCheckBox.addChangeListener(e -> powerMode.setIsPowerIndicatorEnabled(PowerIndicatorCheckBox.isSelected()));

        soundsFolder.setText(powerMode.getSoundsFolder());

        soundsFolder.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                powerMode.setSoundsFolder(soundsFolder.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                powerMode.setSoundsFolder(soundsFolder.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                powerMode.setSoundsFolder(soundsFolder.getText());
            }
        });
        initValues(powerMode.getSparkCount(), sparkCount, sparkCountValue, slider -> powerMode.setSparkCount(slider.getValue()));
        initValues(powerMode.getSparkSize(), sparkSize, sparkSizeValue, slider -> powerMode.setSparkSize(slider.getValue()));
        initValues(powerMode.getSparkLife(), sparkLife, sparkLifeValue, slider -> powerMode.setSparkLife(slider.getValue()));
        initValues(Double.valueOf((powerMode.getSparkVelocityFactor() * 100.0)).intValue(), velocityFactor, velocityFactorValue, slider -> powerMode.setSparkVelocityFactor(slider.getValue() / 100.0));
        initValues(Double.valueOf(powerMode.getGravityFactor() * 100.0).intValue(), gravityFactor, gravityFactorValue, slider -> powerMode.setGravityFactor(slider.getValue() / 100.0));
        initValues(powerMode.getShakeRange(), shakeRange, shakeRangeValue, slider -> powerMode.setShakeRange(slider.getValue()));
        initValues(powerMode.getHeatup(), heatup, heatupValue, slider -> powerMode.setHeatup(slider.getValue()));
        initValues(powerMode.getHeatupTime(), heatupTime, heatupTimeValue, slider -> powerMode.setHeatupTime(slider.getValue()));
        initValues(powerMode.getHeatupThreshold(), heatupThreshold, heatupThresholdValue, slider -> powerMode.setHeatupThreshold(slider.getValue()));
        initValues(powerMode.getFlameLife(), flameLife, flameLifeValue, slider -> powerMode.setFlameLife(slider.getValue()));
        initValues((int) powerMode.getBamLife(), bamLife, bamLifeValue, slider -> powerMode.setBamLife(slider.getValue()));
        initValues(powerMode.getmaxFlameSize(), maxFlameSize, maxFlameSizeValue, slider -> powerMode.setmaxFlameSize(slider.getValue()));
        initValues(powerMode.getKeyStrokesPerMinute(), keyStrokesPerMinute, keyStrokesPerMinuteValue, slider -> powerMode.setKeyStrokesPerMinute(slider.getValue()));
        initValues(powerMode.getFrameRate(), frameRate, frameRateValue, slider -> powerMode.setFrameRate(slider.getValue()));

        initValuesColor(powerMode.getRedFrom(), sparkColorRedFrom, sparkColorRedFromValue, powerMode, slider -> powerMode.setRedFrom(slider.getValue()));
        initValuesColor(powerMode.getRedTo(), sparkColorRedTo, sparkColorRedToValue, powerMode, slider -> powerMode.setRedTo(slider.getValue()));
        bindSlieders(sparkColorRedFrom, sparkColorRedTo);

        initValuesColor(powerMode.getGreenFrom(), sparkColorGreenFrom, sparkColorGreenFromValue, powerMode, slider -> powerMode.setGreenFrom(slider.getValue()));
        initValuesColor(powerMode.getGreenTo(), sparkColorGreenTo, sparkColorGreenToValue, powerMode, slider -> powerMode.setGreenTo(slider.getValue()));
        bindSlieders(sparkColorGreenFrom, sparkColorGreenTo);

        initValuesColor(powerMode.getBlueFrom(), sparkColorBlueFrom, sparkColorBlueFromValue, powerMode, slider -> powerMode.setBlueFrom(slider.getValue()));
        initValuesColor(powerMode.getBlueTo(), sparkColorBlueTo, sparkColorBlueToValue, powerMode, slider -> powerMode.setBlueTo(slider.getValue()));
        bindSlieders(sparkColorBlueFrom, sparkColorBlueTo);

        initValuesColor(powerMode.getColorAlpha(), sparkColorAlpha, sparkColorAlphaValue, powerMode, slider -> powerMode.setColorAlpha(slider.getValue()));

    }

    private void bindSlieders(JSlider from, JSlider to) {
        from.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (from.getValue() > to.getValue()) {
                    to.setValue(from.getValue());
                }
            }
        });
        to.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (to.getValue() < from.getValue()) {
                    from.setValue(to.getValue());
                }
            }
        });
    }

    private void initValuesColor(int initValue, JSlider slider, JLabel sliderValueLabel, PowerMode powerMode, ValueColorSettable valueSettable) {
        initValues(initValue, slider, sliderValueLabel, slider1 -> {
            valueSettable.setValue(slider1);
            ((MultiGradientPanel) colorView).setColorEdges(PowerMode.obtainColorEdges(powerMode));
        });

    }

    private void initValues(int initValue, JSlider slider, JLabel sliderValueLabel, ValueSettable valueSettable) {
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
    public void reset(PowerMode powerMode) {
        powerModeEnabled.setSelected(powerMode.isEnabled());
    }

    @Override
    public boolean isModified(PowerMode powerMode) {
        return powerModeEnabled.isSelected() != powerMode.isEnabled();
    }

    @Override
    public void apply(PowerMode powerMode) throws ConfigurationException {
        powerMode.setEnabled(powerModeEnabled.isSelected());
    }


    @Override
    public JComponent getComponent() {
        return mainPanel;
    }

    private void createUIComponents() {
        colorView = new MultiGradientPanel(200, null);


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

    private interface ValueColorSettable {
        void setValue(JSlider slider);
    }
}
