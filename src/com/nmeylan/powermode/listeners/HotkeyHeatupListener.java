package com.nmeylan.powermode.listeners;

import com.intellij.ide.DataManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.nmeylan.powermode.Power;
import org.jetbrains.annotations.NotNull;

import javax.swing.KeyStroke;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Optional;

class HotkeyHeatupListener implements AWTEventListener, Power, ApplicationComponent {

  @Override
  public void eventDispatched(AWTEvent e) {
    if (powerMode().isEnabled() && powerMode().isHotkeyHeatup()) {
      if (e instanceof KeyEvent) {
        KeyEvent event = (KeyEvent) e;
        if ((event.getModifiersEx() & (InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) > 0) {
          KeyStroke eventKeyStroke = KeyStroke.getKeyStroke(event.getKeyCode(), event.getModifiersEx());
          if (true) { // TODO set conditions to increaHeatup
            powerMode().increaseHeatup(
              Optional.of(
                DataManager.getInstance().getDataContext(event.getComponent()))
              , eventKeyStroke);
          }
        }
      }
    }
  }

  @Override
  public void initComponent() {
    Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
  }

  @Override
  public void disposeComponent() {

  }

  @NotNull
  @Override
  public String getComponentName() {
    return "HotkeyHeatupListener";
  }
}
