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
import java.awt.event.{ComponentEvent, ComponentListener}
import javax.swing._

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.{Caret, Editor, ScrollingModel}
import de.ax.powermode._
import de.ax.powermode.power.ElementOfPower
import de.ax.powermode.power.element.{PowerFlame, PowerSpark}

object ElementOfPowerContainer {
  private val logger = Logger.getInstance(this.getClass)
}

/**
  * @author Baptiste Mesta
  */
class ElementOfPowerContainer(editor: Editor) extends JComponent with ComponentListener {

  import ElementOfPowerContainer._


  val myParent = editor.getContentComponent

  myParent.add(this)
  this.setBounds(myParent.getBounds)
  setVisible(true)
  myParent.addComponentListener(this)


  val shakeComponents = Seq(editor.getComponent, editor.getContentComponent)
  var elementsOfPower = Seq.empty[(ElementOfPower, (Int, Int))]
  var lastShake = System.currentTimeMillis()
  var shakeData = Option.empty[(Int, Int, Int, Int)]

  var lastUpdate = System.currentTimeMillis()


  def updateElementsOfPower() {
    var delta = System.currentTimeMillis() - lastUpdate
    if (delta > (1000.0 / powerMode.frameRate) * 2)
      delta = 16
    lastUpdate = System.currentTimeMillis()
    val db: Double = 1000.0 / 16
    if (elementsOfPower.nonEmpty) {
      elementsOfPower = elementsOfPower.seq.filterNot(p => p._1.update((delta / db).toFloat))
      repaint()
    }
  }

  def update(point: Point ) {

    this.setBounds(getMyBounds)

    if (powerMode.isSparksEnabled) {
      addSparks(point)
    }
    if (powerMode.isFlamesEnabled) {
      addFlames(point)
    }

    if (powerMode.isShakeEnabled) {
      doShake(shakeComponents)
    }
    repaint()
  }

  def addFlames(point: Point): Unit = {
    val base = 0.3
    val wh = (powerMode.maxFlameSize * base +
      ((math.random * powerMode.maxFlameSize * (1 - base)) * powerMode.valueFactor)
      ).toInt
    val initLife = (powerMode.maxFlameLife * powerMode.valueFactor).toInt
    if (initLife > 100) {
      elementsOfPower :+=(PowerFlame(point.x + 5, point.y - 1, wh, wh, initLife, true, powerMode), getScrollPosition)
      elementsOfPower :+=(PowerFlame(point.x + 5, point.y + 15, wh, wh, initLife, false, powerMode), getScrollPosition)
    }
  }

  def addSparks(point: Point): Unit = {
    for (i <- 0 to (powerMode.sparkCount * powerMode.valueFactor).toInt) {
      addSpark(point.x, point.y)
    }
  }

  def addSpark(x: Int, y: Int) {
    val dx: Double = (Math.random * 2) * (if (Math.random > 0.5) -1 else 1) * powerMode.sparkVelocityFactor
    val dy: Double = ((Math.random * -3) - 1) * powerMode.sparkVelocityFactor
    val size = ((Math.random * powerMode.sparkSize) + 1).toInt
    val life = Math.random() * powerMode.getSparkLife * powerMode.valueFactor
    val powerSpark = PowerSpark(x, y, dx.toFloat, dy.toFloat, size, life.toLong, genNextColor, powerMode.gravityFactor.toFloat)
    elementsOfPower :+=(powerSpark, getScrollPosition)
  }

  def genNextColor: PowerColor = (getColorPart(powerMode.getRedFrom, powerMode.getRedTo),
    getColorPart(powerMode.getGreenFrom, powerMode.getGreenTo),
    getColorPart(powerMode.getBlueFrom, powerMode.getBlueTo),
    powerMode.getColorAlpha / 255f)

  def getColorPart(from: Int, to: Int): Float = {
    (((Math.random() * (to - from)) + from) / 255).toFloat
  }

  def powerMode: PowerMode = {
    PowerMode.getInstance
  }

  def getScrollPosition = (
    editor.getScrollingModel.getHorizontalScrollOffset,
    editor.getScrollingModel.getVerticalScrollOffset
    )

  def doShake(myShakeComponents: Seq[JComponent]): Unit = {
    val editorOk = {
      !(editor match {
        case impl: EditorImpl =>
          impl.getPreferredSize.height < 100 || impl.getPreferredSize.width < 100
        case _ =>
          false
      })
    }
    if (editorOk) {
      shakeData = shakeData match {
        case Some((dx, dy, scrollX, scrollY)) =>
          myShakeComponents.foreach { myShakeComponent =>
            val bounds: Rectangle = myShakeComponent.getBounds
            myShakeComponent.setBounds(bounds.x + dx, bounds.y + dy, bounds.width, bounds.height)
          }
          None
        case None =>
          val dx = generateShakeOffset
          val dy = generateShakeOffset
          val scrollX = editor.getScrollingModel.getHorizontalScrollOffset
          val scrollY = editor.getScrollingModel.getVerticalScrollOffset
          myShakeComponents.foreach { myShakeComponent =>
            val bounds: Rectangle = myShakeComponent.getBounds
            myShakeComponent.setBounds(bounds.x + dx, bounds.y + dy, bounds.width, bounds.height)
          }
          Some((-dx, -dy, scrollX, scrollY))
      }
      lastShake = System.currentTimeMillis()
    }
  }

  def generateShakeOffset: Int = {
    val range = powerMode.shakeRange * powerMode.valueFactor
    (range - (Math.random * 2 * range)).toInt
  }

  def componentResized(e: ComponentEvent) {
    setBounds(getMyBounds)
    logger.debug("Resized")
  }

  def componentMoved(e: ComponentEvent) {
    setBounds(getMyBounds)
    logger.debug("Moved")
  }

  def getMyBounds: Rectangle = {
    val area = editor.getScrollingModel.getVisibleArea
    val rectangle = new Rectangle(area.x, area.y, area.width, area.height)
    rectangle
  }

  def componentShown(e: ComponentEvent) {
  }

  def componentHidden(e: ComponentEvent) {
  }

  protected override def paintComponent(g: Graphics) {
    super.paintComponent(g)
    if (powerMode.isEnabled) {
      if (shakeData.isDefined &&
        System.currentTimeMillis() - lastShake > 100 &&
        shakeData.get._1.abs < 50 && shakeData.get._2.abs < 50) {
        doShake(Seq(editor.getComponent))
      }
      renderElementsOfPower(g)
    }
  }

  def renderElementsOfPower(g: Graphics) {

    val scrollingModel: ScrollingModel = editor.getScrollingModel
    val xyNew = (
      scrollingModel.getHorizontalScrollOffset,
      scrollingModel.getVerticalScrollOffset
      )

    elementsOfPower.foreach { pp =>
      val (elementOfPower, (x, y)) = pp
      val dxx = x - xyNew._1
      val dyy = y - xyNew._2
      elementOfPower.render(g, dxx, dyy)
    }

  }
}