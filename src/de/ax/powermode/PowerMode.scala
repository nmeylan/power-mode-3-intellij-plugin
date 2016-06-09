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

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.{ApplicationComponent, PersistentStateComponent, State, Storage}
import com.intellij.openapi.editor.actionSystem.{EditorActionManager, TypedActionHandler}
import com.intellij.openapi.editor.{Editor, EditorFactory}
import com.intellij.util.xmlb.XmlSerializerUtil
import org.apache.log4j._
import org.jetbrains.annotations.{NotNull, Nullable}

import scala.util.Try

/**
  * @author Baptiste Mesta
  */
object PowerMode {

  val logger = Logger.getLogger(classOf[PowerMode])

  @Nullable def getInstance: PowerMode = {
    ApplicationManager.getApplication.getComponent(classOf[PowerMode])
  }
}

@State(name = "PowerModeII", storages = Array(new Storage(file = "$APP_CONFIG$/power.mode.ii.xml")))
class PowerMode extends ApplicationComponent with PersistentStateComponent[PowerMode] {


  var gravityFactor: Double = 1

  var sparkVelocityFactor: Double = 1

  var sparkSize = 3

  var sparksEnabled = true

  var frameRate: Int = 30

  def setFrameRate(f: Int) {
    frameRate = f
  }

  def getFrameRate() = frameRate

  import PowerMode.logger

  var maxFlameSize = 100

  var maxFlameLife = 2000

  var heatupTime = 6000

  var lastKeys: List[Long] = List.empty[Long]

  var keyStrokesPerMinute = 300
  var heatupFactor = 0.3
  var sparkLife = 2000
  var sparkCount = 10
  var shakeRange = 10
  var flamesEnabled: Boolean = true
  private var sparkContainerManager = Option.empty[ElementOfPowerContainerManager]
  private var enabled: Boolean = true
  private var shakeEnabled: Boolean = true

  def updated {
    val ct = System.currentTimeMillis()
    lastKeys = ct :: lastKeys.filter(_ >= ct - heatupTime)
    logger.debug(s"valueFactor: $valueFactor")
    logger.debug(s"timeFactor: $timeFactor")
  }

  def valueFactor = heatupFactor + ((1 - heatupFactor) * timeFactor)

  def timeFactor: Double = {
    val tf = Try {
      if (heatupTime < 1000) {
        1
      } else {
        val d = heatupTime.toDouble / (60000.0 / keyStrokesPerMinute)
        math.min(lastKeys.size, d) / d
      }
    }.getOrElse(0.0)
    tf
  }

  def reduced: Unit = {
    val ct = System.currentTimeMillis()
    lastKeys = lastKeys.filter(_ >= ct - heatupTime)
  }

  def initComponent {
    val editorFactory = EditorFactory.getInstance
    sparkContainerManager = Some(new ElementOfPowerContainerManager)
    sparkContainerManager.foreach(editorFactory.addEditorFactoryListener(_, new Disposable() {
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
    sparkContainerManager.foreach(_.update(editor))
  }

  def disposeComponent {
    sparkContainerManager.foreach(_.dispose)
    sparkContainerManager = null
  }

  @NotNull def getComponentName: String = {
    return "PowerModeII"
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

  def isShakeEnabled: Boolean = {
    shakeEnabled
  }

  def setShakeEnabled(shakeEnabled: Boolean) {
    this.shakeEnabled = shakeEnabled
  }

  def getSparkCount = sparkCount

  def setSparkCount(sparkCount: Int) {
    this.sparkCount = sparkCount
  }

  def getSparkLife = sparkLife

  def setSparkLife(sparkRange: Int) {
    this.sparkLife = sparkRange
  }

  def getShakeRange = shakeRange

  def setShakeRange(shakeRange: Int) {
    this.shakeRange = shakeRange
  }

  def getHeatup = (heatupFactor * 100).toInt

  def setHeatup(heatup: Int) {
    this.heatupFactor = heatup / 100.0
  }

  def getHeatupTime = heatupTime

  def setHeatupTime(heatupTime: Int) {
    this.heatupTime = math.max(0, heatupTime)
  }

  def getFlameLife: Int = {
    return maxFlameLife
  }

  def setFlameLife(flameLife: Int): Unit = {
    maxFlameLife = flameLife
  }

  def getmaxFlameSize: Int = {
    return maxFlameSize
  }

  def setmaxFlameSize(maxFlameSize: Int): Unit = {
    this.maxFlameSize = maxFlameSize
  }

  def getKeyStrokesPerMinute: Int = {
    return keyStrokesPerMinute
  }

  def setKeyStrokesPerMinute(keyStrokesPerMinute: Int) {
    this.keyStrokesPerMinute = keyStrokesPerMinute
  }

  def isFlamesEnabled: Boolean = {
    return flamesEnabled
  }

  def setFlamesEnabled(flamesEnabled: Boolean) {
    this.flamesEnabled = flamesEnabled
  }

  def isSparksEnabled: Boolean = {
    return sparksEnabled
  }

  def setSparksEnabled(sparksEnabled: Boolean) {
    this.sparksEnabled = sparksEnabled
  }

  def getSparkSize: Int = {
    return sparkSize
  }

  def setSparkSize(sparkSize: Int) {
    this.sparkSize = sparkSize
  }

  def getGravityFactor(): Double = gravityFactor

  def setGravityFactor(f: Double) {
    gravityFactor = f
  }

  def getSparkVelocityFactor(): Double = sparkVelocityFactor

  def setSparkVelocityFactor(f: Double) {
    sparkVelocityFactor = f
  }


}