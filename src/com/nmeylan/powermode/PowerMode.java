package com.nmeylan.powermode;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.nmeylan.powermode.color.ColorEdges;
import com.nmeylan.powermode.listeners.MyCaretListener;
import com.nmeylan.powermode.listeners.MyTypedActionHandler;
import com.nmeylan.powermode.management.ElementOfPowerContainerManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import javax.swing.KeyStroke;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@State(name = "PowerModeII", storages = {@Storage(file = "$APP_CONFIG$/power.mode.ii.xml")})
public class PowerMode implements PersistentStateComponent<PowerMode>, ApplicationComponent {
  private static List<Integer> HOT_INPUTS = Arrays.asList(InputEvent.CTRL_DOWN_MASK, InputEvent.ALT_DOWN_MASK, InputEvent.SHIFT_DOWN_MASK);
  private boolean hotkeyHeatup = true;
  private long bamLife = 1000;
  private double gravityFactor = 21.21;
  private double sparkVelocityFactor = 4.36;
  private int sparkSize = 3;
  private int frameRate = 30;
  private int maxFlameSize = 100;
  private int maxFlameLife = 2000;
  private int heatupTime = 10000;
  private Map<KeyStroke, Long> lastKeys = new HashMap<>();

  private int keyStrokesPerMinute = 300;
  private double heatupFactor = 1.0;
  private int sparkLife = 3000;
  private int sparkCount = 10;
  private int shakeRange = 4;

  private boolean enabled = true;
  private boolean caretActionEnabled = true;
  private boolean shakeEnabled = true;
  private boolean bamEnabled = true;
  private boolean flamesEnabled = true;
  private boolean sparksEnabled = true;
  private boolean isCustomFlameImages = false;
  private boolean isCustomBamImages = false;

  private int redFrom = 200;
  private int redTo = 255;
  private int greenFrom = 0;
  private int greenTo = 255;
  private int blueFrom = 0;
  private int blueTo = 103;
  private int colorAlpha = 164;
  private double heatupThreshold = 0.0;
  private double hotkeyWeight = keyStrokesPerMinute * 0.05;

  private Optional<ElementOfPowerContainerManager> maybeElementOfPowerContainerManager = Optional.empty();

  private Optional<File> customFlameImageFolder = Optional.empty();
  private Optional<File> customBamImageFolder = Optional.empty();

  public static Logger logger() {
    return Logger.getLogger(PowerMode.class);
  }

  @Nullable
  public static PowerMode getInstance() {
    try {
      return ApplicationManager.getApplication().getComponent(PowerMode.class);
    } catch (Throwable e) {
      logger().debug("error getting component: " + e.getMessage(), e);
    }
    return null;
  }

  public ColorEdges obtainColorEdges(){
    ColorEdges edges = new ColorEdges();
    edges.setAlpha(getColorAlpha());
    edges.setRedFrom(getRedFrom());
    edges.setRedTo(getRedTo());
    edges.setGreenFrom(getGreenFrom());
    edges.setGreenTo(getGreenTo());
    edges.setBlueFrom(getBlueFrom());
    edges.setBlueTo(getBlueTo());
    return edges;
  }

  public Optional<File> flameImageFolder() {
    return isCustomFlameImages ? customFlameImageFolder : Optional.of(new File("fire/animated/256"));
  }

  public Optional<File> bamImageFolder() {
    return isCustomBamImages ? customBamImageFolder : Optional.of(new File("bam"));
  }

  public void increaseHeatup(Optional<DataContext> dataContext, KeyStroke keyStroke) {
    if (keyStroke != null) {
      long ct = System.currentTimeMillis();
      lastKeys.put(keyStroke, ct);
    }
  }

  public void reduceHeatup() {
    long ct = System.currentTimeMillis();
    lastKeys = filterLastKeys(ct);
  }

  private Map<KeyStroke, Long> filterLastKeys(Long ct) {
    return lastKeys.entrySet().stream().filter(e -> e.getValue() >= ct - heatupTime).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public double rawValueFactor() {
    double base = heatupFactor + ((1 - heatupFactor) * timeFactor(true));
    return (base - heatupThreshold) / (1 - heatupThreshold);
  }

  public double valueFactor() {
    double base = heatupFactor + ((1 - heatupFactor) * timeFactor(false));
    double elems = (base - heatupThreshold) / (1 - heatupThreshold);
    double max = Math.max(elems, 0.0);
    assert (max <= 1);
    assert (max >= 0);
    return max;
  }

  private double timeFactor(boolean isRaw) {
    if (heatupTime < 1000) {
      return 1;
    } else if (!lastKeys.keySet().isEmpty()) {
      double d = heatupTime / (60000.0 / keyStrokesPerMinute);
      double keysWorth = lastKeys.keySet().stream().filter(keystroke -> HOT_INPUTS.contains(keystroke.getModifiers())).count() * hotkeyWeight;
      if (isRaw) {
        return keysWorth / d;
      }
      return Math.min(keysWorth, d) / d;
    }
    return 0.0;
  }


  @Override
  public void initComponent() {
    PowerMode.logger().debug("initComponent...");
    EditorFactory editorFactory = EditorFactory.getInstance();
    maybeElementOfPowerContainerManager = Optional.of(new ElementOfPowerContainerManager());
    maybeElementOfPowerContainerManager.ifPresent(e -> editorFactory.addEditorFactoryListener(e, () -> {
    }));
    EditorActionManager editorActionManager = EditorActionManager.getInstance();
    EditorFactory
      .getInstance()
      .getEventMulticaster()
      .addCaretListener(new MyCaretListener());
    editorActionManager.getTypedAction().setupRawHandler(
      new MyTypedActionHandler(
        editorActionManager.getTypedAction().getRawHandler()));

    PowerMode.logger().debug("initComponent done");
  }

  @Override
  public void disposeComponent() {
    maybeElementOfPowerContainerManager.ifPresent(e -> e.dispose());
  }

  @Override
  public String getComponentName() {
    return "PowerModeIII";
  }

  @Override
  public PowerMode getState() {
    return this;
  }

  @Override
  public void loadState(PowerMode state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public boolean isHotkeyHeatup() {
    return hotkeyHeatup;
  }

  public void setHotkeyHeatup(boolean hotkeyHeatup) {
    this.hotkeyHeatup = hotkeyHeatup;
  }

  public long getBamLife() {
    return bamLife;
  }

  public void setBamLife(long bamLife) {
    this.bamLife = bamLife;
  }

  public double getGravityFactor() {
    return gravityFactor;
  }

  public void setGravityFactor(double gravityFactor) {
    this.gravityFactor = gravityFactor;
  }

  public double getSparkVelocityFactor() {
    return sparkVelocityFactor;
  }

  public void setSparkVelocityFactor(double sparkVelocityFactor) {
    this.sparkVelocityFactor = sparkVelocityFactor;
  }

  public int getSparkSize() {
    return sparkSize;
  }

  public void setSparkSize(int sparkSize) {
    this.sparkSize = sparkSize;
  }

  public int getFrameRate() {
    return frameRate;
  }

  public void setFrameRate(int frameRate) {
    this.frameRate = frameRate;
  }

  public int getMaxFlameSize() {
    return maxFlameSize;
  }

  public void setMaxFlameSize(int maxFlameSize) {
    this.maxFlameSize = maxFlameSize;
  }

  public int getMaxFlameLife() {
    return maxFlameLife;
  }

  public void setMaxFlameLife(int maxFlameLife) {
    this.maxFlameLife = maxFlameLife;
  }

  public int getHeatupTime() {
    return heatupTime;
  }

  public void setHeatupTime(int heatupTime) {
    this.heatupTime = Math.max(0, heatupTime);
  }

  public Map<KeyStroke, Long> getLastKeys() {
    return lastKeys;
  }

  public void setLastKeys(Map<KeyStroke, Long> lastKeys) {
    this.lastKeys = lastKeys;
  }

  public int getKeyStrokesPerMinute() {
    return keyStrokesPerMinute;
  }

  public void setKeyStrokesPerMinute(int keyStrokesPerMinute) {
    this.keyStrokesPerMinute = keyStrokesPerMinute;
  }

  public int getHeatup() {
    return (int) (heatupFactor * 100);
  }

  public void setHeatup(int heatup) {
    setHeatupFactor(heatup / 100);
  }

  public void setHeatupFactor(double heatupFactor) {
    this.heatupFactor = heatupFactor;
  }

  public int getSparkLife() {
    return sparkLife;
  }

  public void setSparkLife(int sparkLife) {
    this.sparkLife = sparkLife;
  }

  public int getSparkCount() {
    return sparkCount;
  }

  public void setSparkCount(int sparkCount) {
    this.sparkCount = sparkCount;
  }

  public int getShakeRange() {
    return shakeRange;
  }

  public void setShakeRange(int shakeRange) {
    this.shakeRange = shakeRange;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isShakeEnabled() {
    return shakeEnabled;
  }

  public void setShakeEnabled(boolean shakeEnabled) {
    this.shakeEnabled = shakeEnabled;
  }

  public boolean isBamEnabled() {
    return bamEnabled;
  }

  public void setBamEnabled(boolean bamEnabled) {
    this.bamEnabled = bamEnabled;
  }

  public boolean isFlamesEnabled() {
    return flamesEnabled;
  }

  public void setFlamesEnabled(boolean flamesEnabled) {
    this.flamesEnabled = flamesEnabled;
  }

  public boolean isSparksEnabled() {
    return sparksEnabled;
  }

  public void setSparksEnabled(boolean sparksEnabled) {
    this.sparksEnabled = sparksEnabled;
  }

  public boolean isCustomFlameImages() {
    return isCustomFlameImages;
  }

  public void setCustomFlameImages(boolean customFlameImages) {
    isCustomFlameImages = customFlameImages;
  }

  public boolean isCustomBamImages() {
    return isCustomBamImages;
  }

  public void setCustomBamImages(boolean customBamImages) {
    isCustomBamImages = customBamImages;
  }

  public int getRedFrom() {
    return redFrom;
  }

  public void setRedFrom(int redFrom) {
    if (redFrom <= redTo) {
      this.redFrom = redFrom;
    }
  }

  public int getRedTo() {
    return redTo;
  }

  public void setRedTo(int redTo) {
    if (redTo >= redFrom) {
      this.redTo = redTo;
    }
  }

  public int getGreenFrom() {
    return greenFrom;
  }

  public void setGreenFrom(int greenFrom) {
    if (greenFrom <= greenTo) {
      this.greenFrom = greenFrom;
    }
  }

  public int getGreenTo() {
    return greenTo;
  }

  public void setGreenTo(int greenTo) {
    if (greenTo >= greenFrom) {
      this.greenTo = greenTo;
    }
  }

  public int getBlueFrom() {
    return blueFrom;
  }

  public void setBlueFrom(int blueFrom) {
    if (blueFrom <= blueTo) {
      this.blueFrom = blueFrom;
    }
  }

  public int getBlueTo() {
    return blueTo;
  }

  public void setBlueTo(int blueTo) {
    if (blueTo >= blueFrom) {
      this.blueTo = blueTo;
    }
  }

  public int getColorAlpha() {
    return colorAlpha;
  }

  public void setColorAlpha(int colorAlpha) {
    this.colorAlpha = colorAlpha;
  }

  public int getHeatupThreshold() {
    return (int)(heatupThreshold * 100);
  }

  public void setHeatupThreshold(int heatupThreshold) {
    this.heatupThreshold = heatupThreshold / 100;
  }

  public Optional<ElementOfPowerContainerManager> getMaybeElementOfPowerContainerManager() {
    return maybeElementOfPowerContainerManager;
  }

  public void setMaybeElementOfPowerContainerManager(Optional<ElementOfPowerContainerManager> maybeElementOfPowerContainerManager) {
    this.maybeElementOfPowerContainerManager = maybeElementOfPowerContainerManager;
  }

  public String getCustomFlameImageFolder() {
    return customFlameImageFolder.map(f -> f.getAbsolutePath()).orElse("");
  }

  public void setCustomFlameImageFolder(String customFlameImageFolder) {
    this.customFlameImageFolder = Optional.of(new File(customFlameImageFolder));
  }

  public String getCustomBamImageFolder() {
    return customBamImageFolder.map(f -> f.getAbsolutePath()).orElse("");
  }

  public void setCustomBamImageFolder(String customBamImageFolder) {
    this.customBamImageFolder = Optional.of(new File(customBamImageFolder));
  }

  public boolean isCaretActionEnabled() {
    return caretActionEnabled;
  }

  public void setCaretActionEnabled(boolean caretActionEnabled) {
    this.caretActionEnabled = caretActionEnabled;
  }
}
