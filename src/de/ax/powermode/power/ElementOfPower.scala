package de.ax.powermode.power

import java.awt.Graphics

/**
  * Created by nyxos on 10.03.16.
  */
trait ElementOfPower {

  def update(delta:Float): Boolean

  def render( g: Graphics, dxx: Int, dyy: Int)

  def life: Long

  def initLife: Long

  def lifeFactor: Float = {
    1 - ((life - System.currentTimeMillis()) / initLife.toFloat) toFloat
  }

  def alive: Boolean = {
    life > System.currentTimeMillis() //&& width >= 0 && width <= _width
  }
}
