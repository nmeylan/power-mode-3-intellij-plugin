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
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import java.util.ArrayList
import java.util.Iterator
import javax.swing._
import com.bmesta.powermode.element.{ElementOfPower, PowerFire, PowerParticle}
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.ui.JBColor
import org.jetbrains.annotations.NotNull

import scala.collection.mutable.ListBuffer

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

  var particles = Seq.empty[ElementOfPower]


  protected override def paintComponent(@NotNull g: Graphics) {
    if (od.isDefined && System.currentTimeMillis() - lastShake > 100) {
      doShake(Seq(editor.getComponent))
    }
    super.paintComponent(g)
    renderParticles(g)
  }

  var lastShake = System.currentTimeMillis()

  val myShakeComponents = Seq(editor.getComponent, editor.getContentComponent)

  def doShake(myShakeComponents: Seq[JComponent]): Unit = {
    val editorNotOk = {
      editor match {
        case impl: EditorImpl =>
          impl.getPreferredSize.height < 100 || impl.getPreferredSize.width < 100
        case _ =>
          false
      }
    }
    if (config.isShakeEnabled && !editorNotOk) {
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

  def updateParticles {
    if (particles.nonEmpty) {
      particles = particles.seq.filterNot(_.update)
      repaint()
    }
  }


  val colors = Seq(JBColor.black, JBColor.white, JBColor.darkGray, JBColor.red, JBColor.CYAN, JBColor.pink, JBColor.YELLOW)

  def addParticle(x: Int, y: Int) {
    val dx = (Math.random * 4).toInt * (if (Math.random > 0.5) -1 else 1)
    val dy = (Math.random * -3 - 1).toInt * (if (Math.random > 0.5) -1 else 1)
    val size = ((Math.random * 5) + 1).toInt
    val life = Math.random() * config.particleRange * config.valueFactor toInt
    val e = new PowerParticle(x, y, dx, dy, size, life, colors((Math.random() * (colors.size - 1)).toInt))
    particles :+= e
  }

  def renderParticles(@NotNull g: Graphics) {
    //        particles.foreach(_.render(g,editor.getScrollingModel.getHorizontalScrollOffset,editor.getScrollingModel.getVerticalScrollOffset))
    particles.foreach(_.render(g, 0, 0))
  }


  var od = Option.empty[(Int, Int, Int, Int)]

  def update(@NotNull point: Point) {
    this.setBounds(getMyBounds)
    for (i <- 0 to (config.particleCount * config.valueFactor).toInt) {
      addParticle(point.x, point.y)
    }
    val wh = 100
    val initLife = 2000
    particles :+= PowerFire(point.x+5, point.y-10,wh,wh,initLife,true)
    particles :+= PowerFire(point.x+5, point.y+22,wh,wh,initLife,false)
    od = None
    doShake(myShakeComponents)
    //    myShakeComponents.map(_.repaint())
    repaint()
  }


  def getMyBounds: Rectangle = {
    val bounds1: Rectangle = myParent.getBounds
    val area = editor.getScrollingModel.getVisibleArea
    val rectangle = new Rectangle(area.x, area.y, area.width, area.height)
    rectangle
  }

  def componentResized(e: ComponentEvent) {
    setBounds(getMyBounds)
    logger.info("Resized")
  }

  def componentMoved(e: ComponentEvent) {
    setBounds(getMyBounds)
    logger.info("Moved")
  }

  def componentShown(e: ComponentEvent) {
  }

  def componentHidden(e: ComponentEvent) {
  }
}