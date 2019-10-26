package com.nmeylan.powermode;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class ToggleCaretPowerMode extends AnAction implements Power {

  @Override
  public void actionPerformed(AnActionEvent e){
    powerMode().setCaretActionEnabled(!powerMode().isCaretActionEnabled());
  }
}
