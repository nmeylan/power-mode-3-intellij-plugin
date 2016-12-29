package de.ax.powermode.power.element

import java.awt.{AlphaComposite, Graphics, Graphics2D}

import de.ax.powermode.Util
import de.ax.powermode.power.ElementOfPower

/**
  * Created by nyxos on 28.12.16.
  */
case class PowerBam(_x: Float, _y: Float, _width: Float, _height: Float, initLife: Long) extends ElementOfPower {

  val life = System.currentTimeMillis() + initLife
  var x: Double = _x
  var y: Double = _y
  var width: Double = 0
  var height: Double = 0


  override def update(delta: Float): Boolean = {
    if (alive) {

      x = _x+(0.5*_width) -(0.5* _width * lifeFactor)
      y = _y +(0.5*_height)-(0.5* _height * lifeFactor)
//      width = _width * (1.0 - lifeFactor) // (_width * lifeFactor).toInt
//      height = _height * (1.0 - lifeFactor) // (_height * lifeFactor).toInt
      width = _width * lifeFactor
      height = _height * lifeFactor
    }
    !alive
  }


  override def render(g: Graphics, dxx: Int, dyy: Int): Unit = {
    if (alive) {

      val g2d: Graphics2D = g.create.asInstanceOf[Graphics2D]
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Util.alpha(0.9f * (1 - lifeFactor))))
      g2d.drawImage(Util.powerBamImage, x + dxx toInt, y + dyy toInt, width toInt, height toInt, null)
      g2d.dispose()
    }
  }


}
