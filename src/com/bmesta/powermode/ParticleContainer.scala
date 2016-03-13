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
  //  this.setBorder(BorderFactory.createLineBorder(JBColor.red))
  setVisible(true)
  myParent.addComponentListener(this)
  val myShakeComponents = Seq(editor.getComponent, editor.getContentComponent)

  def colors = Seq((56 / 255.0f, 255 / 255.0f, 0 / 255.0f, 0.9f), (116 / 255.0f, 80 / 255.0f, 0f, 0.95f), (0 / 255.0f, 0 / 255.0f, 0f, 1f))

  private val random = new Random()
  var particles = Seq.empty[(ElementOfPower, (Int, Int))]
  var lastShake = System.currentTimeMillis()
  var od = Option.empty[(Int, Int, Int, Int)]

  def updateParticles {
    if (particles.nonEmpty) {
      particles = particles.seq.filterNot(p => p._1.update)
      repaint()
    }
  }

  def update(@NotNull point: Point) {

    this.setBounds(getMyBounds)

    if (config.isParticlesEnabled) {
      for (i <- 0 to (config.particleCount * config.valueFactor).toInt) {
        addParticle(point.x, point.y)
      }
    }
    if (config.isFlamesEnabled) {
      val base = 0.3
      val wh = (config.maxFlameSize * base + ((math.random * config.maxFlameSize * (1 - base)) * config.valueFactor)).toInt
      val initLife = (config.maxFlameLife * config.valueFactor).toInt
      if (initLife > 100) {
        particles :+=(PowerFire(point.x + 5, point.y - 1, wh, wh, initLife, true, config), getxy)
        particles :+=(PowerFire(point.x + 5, point.y + 15, wh, wh, initLife, false, config), getxy)
      }
    }
    od = None
    if (config.isShakeEnabled) {
      doShake(myShakeComponents)
    }
    repaint()
  }

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
      od = od match {
        case Some((dx, dy, ox, oy)) =>
          myShakeComponents.foreach { myShakeComponent =>
            val bounds: Rectangle = myShakeComponent.getBounds
            myShakeComponent.setBounds(bounds.x + dx, bounds.y + dy, bounds.width, bounds.height)
          }
          editor.getScrollingModel.scrollHorizontally(ox - Math.abs(dx / 2))
          editor.getScrollingModel.scrollVertically(oy - Math.abs(dy / 4).toInt)
          None
        case None =>
          val dx = genD
          val dy = genD
          val offsetX = editor.getScrollingModel.getHorizontalScrollOffset
          val offsetY = editor.getScrollingModel.getVerticalScrollOffset
          myShakeComponents.foreach { myShakeComponent =>
            val bounds: Rectangle = myShakeComponent.getBounds
            myShakeComponent.setBounds(bounds.x + dx, bounds.y + dy, bounds.width, bounds.height)
          }

          Some((-dx, -dy, offsetX, offsetY))
      }
      lastShake = System.currentTimeMillis()
    }
  }

  def genD: Int = {
    val range = config.shakeRange * config.valueFactor
    (range - (Math.random * 2 * range)).toInt
  }

  def config: PowerMode = {
    PowerMode.getInstance
  }

  def getxy = (editor.getScrollingModel.getHorizontalScrollOffset,
    editor.getScrollingModel.getVerticalScrollOffset)

  def addParticle(x: Int, y: Int) {
    val dx = (Math.random * 2) * (if (Math.random > 0.5) -1 else 1)
    val dy = ((Math.random * -3) - 1) //* (if (Math.random > 0.5) -1 else 1)
    val size = ((Math.random * config.particleSize) + 1).toInt
    val life = Math.random() * config.getParticleLife * config.valueFactor
    val e = new PowerParticle(x, y, dx.toFloat, dy.toFloat, size, life.toInt, colors((Math.random() * (colors.size )).toInt))
    particles :+=(e, getxy)
  }

  def getMyBounds: Rectangle = {
    val bounds1: Rectangle = myParent.getBounds
    val area = editor.getScrollingModel.getVisibleArea
    val rectangle = new Rectangle(area.x, area.y, area.width, area.height)
    rectangle
  }

  def componentResized(e: ComponentEvent) {
    setBounds(getMyBounds)
    logger.debug("Resized")
  }

  def componentMoved(e: ComponentEvent) {
    setBounds(getMyBounds)
    logger.debug("Moved")
  }

  def componentShown(e: ComponentEvent) {
  }

  def componentHidden(e: ComponentEvent) {
  }

  protected override def paintComponent(@NotNull g: Graphics) {
    if (od.isDefined && System.currentTimeMillis() - lastShake > 100) {
      doShake(Seq(editor.getComponent))
    }
    super.paintComponent(g)
    renderParticles(g)
  }

  def renderParticles(@NotNull g: Graphics) {

    val scrollingModel: ScrollingModel = editor.getScrollingModel
    val xyNew = (scrollingModel.getHorizontalScrollOffset,
      scrollingModel.getVerticalScrollOffset)

    particles.foreach { pp =>
      val (p, (x, y)) = pp
      val dxx = x - xyNew._1
      val dyy = y - xyNew._2
      p.render(g, dxx, dyy)
    }

  }
}