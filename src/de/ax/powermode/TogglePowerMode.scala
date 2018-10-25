package de.ax.powermode

import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}

/**
  * Created by nyxos on 31.03.16.
  */
class TogglePowerMode extends AnAction with Power {
  def actionPerformed(e: AnActionEvent): Unit = {
    powerMode.setEnabled(!powerMode.isEnabled)
  }
}
