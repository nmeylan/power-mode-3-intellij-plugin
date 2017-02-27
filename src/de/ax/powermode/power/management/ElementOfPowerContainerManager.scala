/*
 * Copyright 2015 Baptiste Mesta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ax.powermode.power.management

import java.awt._
import javax.swing._

import com.intellij.openapi.actionSystem.{DataConstants, DataContext, PlatformDataKeys}
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.{EditorFactoryAdapter, EditorFactoryEvent}
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import de.ax.powermode.power.sound.PowerSound
import de.ax.powermode.{Power, PowerMode, Util}

import scala.collection.mutable
import scala.util.Try

/**
  * @author Baptiste Mesta
  */
class ElementOfPowerContainerManager extends EditorFactoryAdapter with Power {


  val elementsOfPowerContainers = mutable.Map.empty[Editor, ElementOfPowerContainer]
  lazy val sound = new PowerSound(powerMode.soundsFolder, powerMode.valueFactor)

  def showIndicator(dataContext: DataContext) {
    val maybeProject: Seq[Project] = Seq(dataContext.getData(DataConstants.PROJECT), dataContext.getData(PlatformDataKeys.PROJECT_CONTEXT))
      .toStream.flatMap(o =>Option(o).map(_.asInstanceOf[Project]))
    maybeProject.headOption.foreach(p => {
      val textEditor: Editor = FileEditorManager.getInstance(p).getSelectedTextEditor
      SwingUtilities.invokeLater(new Runnable {
        override def run() = {
          elementsOfPowerContainers.get(textEditor).foreach(_.addPowerIndicator())
        }
      })
    })
  }

  val elementsOfPowerUpdateThread = new Thread(new Runnable() {
    def run {
      while (true) {
        try {
          if (powerMode != null) {
            powerMode.reduceHeatup
            updateSound
            updateContainers
            try {
              Thread.sleep(1000 / powerMode.frameRate)
            }
            catch {
              case ignored: InterruptedException => {
              }
            }
          }
        } catch {
          case e => PowerMode.logger.error(e.getMessage, e)
        }
      }
    }

    def updateContainers: Unit = {
      elementsOfPowerContainers.values.foreach(_.updateElementsOfPower())
    }

    def updateSound: Unit = {
      try {
        if (powerMode.isEnabled &&
          powerMode.soundsFolder.exists(f => f.exists() && f.isDirectory)
          && powerMode.isSoundsPlaying) {
          sound.play()
        } else {
          sound.stop()
        }
        sound.setVolume(powerMode.valueFactor)
      } catch {
        case e: Exception =>
          e.printStackTrace()
      }
    }
  })
  elementsOfPowerUpdateThread.start()

  override def editorCreated(event: EditorFactoryEvent) {
    val editor: Editor = event.getEditor
    val isActualEditor = Try {
      editor.getColorsScheme.getClass.getName.contains("EditorImpl") && Util.editorOk(editor, 100)
    }.getOrElse(false)
    if (isActualEditor) {
      elementsOfPowerContainers.put(editor, new ElementOfPowerContainer(editor))
    }
  }

  override def editorReleased(event: EditorFactoryEvent) {
    elementsOfPowerContainers.remove(event.getEditor)
  }

  def initializeAnimation(editor: Editor, pos: Point) {
    if (powerMode.isEnabled) {
      SwingUtilities.invokeLater(new Runnable() {
        def run {
          initializeInUI(editor, pos)
        }
      })
    }
  }

  private def initializeInUI(editor: Editor, pos: Point) {
    elementsOfPowerContainers.get(editor).foreach(_.initializeAnimation(pos))
  }


  def dispose {
    elementsOfPowerUpdateThread.interrupt()
    elementsOfPowerContainers.clear
  }
}