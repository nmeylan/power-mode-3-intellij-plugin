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
package de.ax.powermode

import java.awt._
import javax.swing._

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.{EditorFactoryAdapter, EditorFactoryEvent}
import org.jetbrains.annotations.NotNull

import scala.collection.mutable

/**
  * @author Baptiste Mesta
  */
class ParticleContainerManager extends EditorFactoryAdapter {
  val particleContainers = mutable.Map.empty[Editor, ParticleContainer]

  val particleContainerUpdateThread = new Thread(new Runnable() {
    def run {
      while (true) {
        PowerMode.getInstance.reduced
        particleContainers.values.foreach(_.updateParticles)
        try {
          Thread.sleep(1000 / 60)
        }
        catch {
          case ignored: InterruptedException => {
          }
        }
      }
    }
  })
  particleContainerUpdateThread.start()

  override def editorCreated(@NotNull event: EditorFactoryEvent) {
    val editor: Editor = event.getEditor
    particleContainers.put(editor, new ParticleContainer(editor))
  }

  override def editorReleased(@NotNull event: EditorFactoryEvent) {
    particleContainers.remove(event.getEditor)
  }

  def update(@NotNull editor: Editor) {

    if (PowerMode.getInstance.isEnabled) {
      PowerMode.getInstance.updated
      SwingUtilities.invokeLater(new Runnable() {
        def run {
          updateInUI(editor)
        }
      })
    }
  }

  private def updateInUI(@NotNull editor: Editor) {
    val caretPosition = getCaretPosition(editor)
    particleContainers.get(editor).foreach(_.update(caretPosition))
  }

  def getCaretPosition(editor: Editor): Point = {
    val p: Point = editor.visualPositionToXY(editor.getCaretModel.getVisualPosition)
    val location = editor.getScrollingModel.getVisibleArea.getLocation
    p.translate(-location.x, -location.y)
    p
  }

  def dispose {
    particleContainerUpdateThread.interrupt()
    particleContainers.clear
  }
}