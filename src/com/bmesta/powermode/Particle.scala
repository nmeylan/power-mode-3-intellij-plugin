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

import org.jetbrains.annotations.NotNull
import java.awt._

/**
  * @author Baptiste Mesta
  */
case class Particle(var x: Int, var y: Int, dx: Int, dy: Int, size: Int, var life: Int, c: Color) {


  def update: Boolean = {
    x += dx
    y += dy
    life -= 1
    life <= 0
  }

  def render(@NotNull g: Graphics, dxx: Int, dyy: Int) {
    if (life > 0) {
      val g2d: Graphics2D = g.create.asInstanceOf[Graphics2D]
      g2d.setColor(c)
      g2d.fillRect(dxx + x - (size / 2), dyy + y - (size / 2), size, size)
      g2d.dispose()
    }
  }
}