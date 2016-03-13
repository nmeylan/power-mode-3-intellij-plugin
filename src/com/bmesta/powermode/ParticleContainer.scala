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

import java.awt._
import java.awt.event.{ComponentEvent, ComponentListener}
import javax.swing._

import com.bmesta.powermode.element.{ElementOfPower, PowerFire, PowerParticle}
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.{Editor, ScrollingModel}
import org.jetbrains.annotations.NotNull

import scala.util.Random

object ParticleContainer {
  private val logger = Logger.getInstance(this.getClass)
}

/**
  * @author Baptiste Mesta
  */
class ParticleContainer(@NotNull editor: Editor) extends JComponent with ComponentListener {

  import ParticleContainer._


  val myParent = editor.getContentComponent

  myParent.add(this)
  this.setBounds(myParent.getBounds)
  setVisible(true)
  myParent.addComponentListener(this)


  val shakeComponents = Seq(editor.getComponent, editor.getContentComponent)
  private val random = new Random()
  var elementsOfPower = Seq.empty[(ElementOfPower, (Int, Int))]
  var lastShake = System.currentTimeMillis()
  var shakeData = Option.empty[(Int, Int, Int, Int)]

  def updateParticles() {
    if (elementsOfPower.nonEmpty) {
      elementsOfPower = elementsOfPower.seq.filterNot(p => p._1.update)
      repaint()
    }
  }

  def update(@NotNull point: Point) {

    this.setBounds(getMyBounds)

    if (powerMode.isParticlesEnabled) {
      addParticles(point)
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
      elementsOfPower :+=(PowerFire(point.x + 5, point.y - 1, wh, wh, initLife, true, powerMode), getScrollPosition)
      elementsOfPower :+=(PowerFire(point.x + 5, point.y + 15, wh, wh, initLife, false, powerMode), getScrollPosition)
    }
  }

  def addParticles(point: Point): Unit = {
    for (i <- 0 to (powerMode.particleCount * powerMode.valueFactor).toInt) {
      addParticle(point.x, point.y)
    }
  }

  def addParticle(x: Int, y: Int) {
    val dx = (Math.random * 2) * (if (Math.random > 0.5) -1 else 1)
    val dy = (Math.random * -3) - 1
    val size = ((Math.random * powerMode.particleSize) + 1).toInt
    val life = Math.random() * powerMode.getParticleLife * powerMode.valueFactor
    val powerColor = colors((Math.random() * colors.size).toInt)
    val powerParticle = new PowerParticle(x, y, dx.toFloat, dy.toFloat, size, life.toLong, powerColor)
    elementsOfPower :+=(powerParticle, getScrollPosition)
  }

  def colors: Seq[PowerColor] = Seq(
    (getColorPart, getColorPart, getColorPart, 0.9f)
  )

  def getColorPart: Float = {
    ((Math.random()*192)/256).toFloat
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
          editor.getScrollingModel.scrollHorizontally(scrollX - Math.abs(dx / 2))
          editor.getScrollingModel.scrollVertically(scrollY - Math.abs(dy / 4))
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

  protected override def paintComponent(@NotNull g: Graphics) {
    if (shakeData.isDefined && System.currentTimeMillis() - lastShake > 100) {
      doShake(Seq(editor.getComponent))
    }
    super.paintComponent(g)
    renderParticles(g)
  }

  def renderParticles(@NotNull g: Graphics) {

    val scrollingModel: ScrollingModel = editor.getScrollingModel
    val xyNew = (
      scrollingModel.getHorizontalScrollOffset,
      scrollingModel.getVerticalScrollOffset
      )

    elementsOfPower.foreach { pp =>
      val (p, (x, y)) = pp
      val dxx = x - xyNew._1
      val dyy = y - xyNew._2
      p.render(g, dxx, dyy)
    }

  }
}