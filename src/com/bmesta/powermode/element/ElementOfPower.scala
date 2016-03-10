package com.bmesta.powermode.element

import java.awt.Graphics

import org.jetbrains.annotations.NotNull

/**
  * Created by nyxos on 10.03.16.
  */
trait ElementOfPower {

  def update: Boolean

  def render(@NotNull g: Graphics, dxx: Int, dyy: Int)
}
