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
package de.ax.powermode.power.element

import java.awt._

import de.ax.powermode.power.ElementOfPower
import de.ax.powermode.{PowerColor, Util}

/**
  * @author Baptiste Mesta
  */
case class PowerSpark(var x: Float, var y: Float, dx: Float, var dy: Float, size: Float, val initLife: Long, color: PowerColor, gravityFactor: Float) extends ElementOfPower {
  val life = System.currentTimeMillis() + initLife

  def update(delta: Float): Boolean = {
    dy += (0.07f * gravityFactor) * delta
    x += dx * delta
    y += dy * delta
    !alive
  }

  def render( g: Graphics, dxx: Int, dyy: Int) {
    if (alive) {
      val g2d: Graphics2D = g.create.asInstanceOf[Graphics2D]
      g2d.setColor(new Color(color._1, color._2, color._3, Util.alpha(color._4)))
      g2d.fillOval((dxx + x - (size / 2)).toInt, (dyy + y - (size / 2)).toInt, size.toInt, size.toInt)
      g2d.dispose()
    }
  }

}