package com.nmeylan.powermode.listeners;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.nmeylan.powermode.Power;

public class TogglePowerMode extends AnAction implements Power {

  @Override
  public void actionPerformed(AnActionEvent e) {
    powerMode().setEnabled(!powerMode().isEnabled());
  }
}
