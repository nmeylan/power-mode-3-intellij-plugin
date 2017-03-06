package de.ax.powermode

import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}

/**
  * Created by nyxos on 27.12.16.
  */
class NextSongAction extends AnAction with Power{
  override def actionPerformed(anActionEvent: AnActionEvent): Unit = {
    powerMode.maybeElementOfPowerContainerManager.foreach(cm => {
      cm.sound.foreach(_.next())
    })
  }
}
