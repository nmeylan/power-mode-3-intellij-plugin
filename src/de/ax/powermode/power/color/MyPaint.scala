package de.ax.powermode.power.color

import java.awt.Color

/**
  * Created by nyxos on 05.07.16.
  */
case class MyPaint(r: Int, g: Int, b: Int, a: Int = 255) {
  def withRed(rr: Int) = copy(r = rr)

  def withGreen(gg: Int) = copy(g = gg)

  def withBlue(bb: Int) = copy(b = bb)

  def withAlpha(aa: Int) = copy(a = aa)

  lazy val color = new Color(r, g, b, a)
}
