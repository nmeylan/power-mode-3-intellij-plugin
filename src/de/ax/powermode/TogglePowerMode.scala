package de.ax.powermode

import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}

/**
  * Created by nyxos on 31.03.16.
  */
class TogglePowerMode extends AnAction {
  def actionPerformed(e: AnActionEvent): Unit = {
    val powerMode = PowerMode.getInstance
    powerMode.setEnabled(!powerMode.isEnabled)
  }
}