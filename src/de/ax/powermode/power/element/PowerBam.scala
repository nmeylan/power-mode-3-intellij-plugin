package de.ax.powermode.power.element

import java.awt.{AlphaComposite, Graphics, Graphics2D}

import de.ax.powermode.Util
import de.ax.powermode.power.ElementOfPower

/**
  * Created by nyxos on 28.12.16.
  */
case class PowerBam(_x: Float, _y: Float, _width: Float, _height: Float, initLife: Long) extends ElementOfPower {

  val life = System.currentTimeMillis() + initLife
  var x: Float = _x
  var y: Float = _y
  var width: Float = 0
  var height: Float = 0


  override def update(delta: Float): Boolean = {
    if (alive) {

      x = _x //* lifeFactor
      y = _y // * lifeFactor
      width = _width // (_width * lifeFactor).toInt
      height = _height // (_height * lifeFactor).toInt
    }
    !alive
  }


  override def render(g: Graphics, dxx: Int, dyy: Int): Unit = {
    if (alive) {
      val currentImage = PowerFlame.images(PowerFlame.images.size - 3)
      val g2d: Graphics2D = g.create.asInstanceOf[Graphics2D]
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Util.alpha(0.9f * (1 - lifeFactor))))
      g2d.drawImage(currentImage, x + dxx toInt, y + dyy toInt, width toInt, height toInt, null)
      g2d.dispose()
    }
  }


}
