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
package com.bmesta.powermode

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.editor.actionSystem.TypedAction
import com.intellij.openapi.editor.actionSystem.TypedActionHandler
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

import scala.collection.mutable
import scala.util.Try

/**
  * @author Baptiste Mesta
  */
object PowerMode {
  @Nullable def getInstance: PowerMode = {
    ApplicationManager.getApplication.getComponent(classOf[PowerMode])
  }
}

@State(name = "PowerMode", storages = Array(new Storage(file = "$APP_CONFIG$/power.mode.xml")))
class PowerMode extends ApplicationComponent with PersistentStateComponent[PowerMode] {
  var heatupTime = 6000

  var lastKeys: List[Long] = List.empty[Long]

  def timeFactor: Double = {
    val tf = Try {
      if (heatupTime < 1000) {
        1
      } else {
        math.min(heatupTime, lastKeys.max - lastKeys.min).toDouble / heatupTime
      }
    }.getOrElse(0.0)
    tf
  }


  def updated {
    val ct = System.currentTimeMillis()
    lastKeys = ct :: lastKeys.filter(_ >= ct - heatupTime)
    try {
      println(s"valueFactor= $heatupFactor + ((1 - $heatupFactor) * $timeFactor)")
      println(s"timeFactor=  math.min($heatupTime, ${lastKeys.max} - ${lastKeys.min} (${lastKeys.size}=${lastKeys.max - lastKeys.min})).toDouble / $heatupTime")
    }catch {
      case e=> e.printStackTrace()
    }
  }

  def reduced: Unit = {
    val ct = System.currentTimeMillis()
    lastKeys = lastKeys.filter(_ >= ct - heatupTime)
  }

  var heatupFactor = 0.3

  var particleRange = 50

  var particleCount = 10

  var shakeRange = 10


  //  def cooldownTimeFactor = math.max(0, heatupTime - (System.currentTimeMillis() - lastAction)) / heatupTime


  def valueFactor = heatupFactor + ((1 - heatupFactor) * timeFactor)


  private var particleContainerManager = Option.empty[ParticleContainerManager]
  private var enabled: Boolean = true
  private var shakeEnabled: Boolean = true

  def initComponent {
    val editorFactory = EditorFactory.getInstance
    particleContainerManager = Some(new ParticleContainerManager)
    particleContainerManager.foreach(editorFactory.addEditorFactoryListener(_, new Disposable() {
      def dispose {
      }
    }))
    val rawHandler = EditorActionManager.getInstance.getTypedAction.getRawHandler
    EditorActionManager.getInstance.getTypedAction.setupRawHandler(new TypedActionHandler() {
      def execute(@NotNull editor: Editor, c: Char, @NotNull dataContext: DataContext) {
        updateEditor(editor)
        rawHandler.execute(editor, c, dataContext)
      }
    })
  }

  private def updateEditor(@NotNull editor: Editor) {
    particleContainerManager.foreach(_.update(editor))
  }

  def disposeComponent {
    particleContainerManager.foreach(_.dispose)
    particleContainerManager = null
  }

  @NotNull def getComponentName: String = {
    return "PowerMode"
  }

  @Nullable def getState: PowerMode = {
    return this
  }

  def loadState(@NotNull state: PowerMode) {
    XmlSerializerUtil.copyBean(state, this)
  }

  def isEnabled: Boolean = {
    enabled
  }

  def setEnabled(enabled: Boolean) {
    this.enabled = enabled
  }

  def setShakeEnabled(shakeEnabled: Boolean) {
    this.shakeEnabled = shakeEnabled
  }

  def isShakeEnabled: Boolean = {
    shakeEnabled
  }

  def setParticleCount(particleCount: Int) {
    this.particleCount = particleCount
  }

  def getParticleCount = particleCount

  def setParticleRange(particleRange: Int) {
    this.particleRange = particleRange
  }

  def getParticleRange = particleRange

  def setShakeRange(shakeRange: Int) {
    this.shakeRange = shakeRange
  }

  def getShakeRange = shakeRange

  def setHeatup(heatup: Int) {
    this.heatupFactor = heatup / 100.0
  }

  def getHeatup = (heatupFactor * 100).toInt

  def setHeatupTime(heatupTime: Int) {
    this.heatupTime = math.max(0, heatupTime)
  }

  def getHeatupTime = heatupTime
}