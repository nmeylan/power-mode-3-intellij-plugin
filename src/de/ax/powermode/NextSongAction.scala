package de.ax.powermode

import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}

/**
  * Created by nyxos on 27.12.16.
  */
class NextSongAction extends AnAction {
  override def actionPerformed(anActionEvent: AnActionEvent): Unit = {
    PowerMode.getInstance.maybeElementOfPowerContainerManager.foreach(cm => {
      cm.sound.next()
    })
  }
}
