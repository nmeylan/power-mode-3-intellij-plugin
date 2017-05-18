package de.ax.powermode.power.element

import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.{AlphaComposite, Graphics, Graphics2D}
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

import de.ax.powermode.ImageUtil
import com.intellij.util.PathUtil
import de.ax.powermode.cache.Cache
import de.ax.powermode.power.ElementOfPower
import de.ax.powermode.{PowerMode, Util}

import scala.collection.immutable


case class PowerFlame(_x: Int, _y: Int, _width: Int, _height: Int, initLife: Long, up: Boolean)
  extends ElementOfPower {
  val life = System.currentTimeMillis() + initLife
  var x = _x
  var y = _y
  var width = 0
  var height = 0

  var i = 0
  var currentImage: BufferedImage = null


  override def update(delta: Float): Boolean = {
    if (alive) {
      val flameImages1 = ImageUtil.imagesForPath(powerMode.flameImageFolder)
      currentImage = flameImages1(i % flameImages1.size)
      i += 1
      x = _x - (0.5 * _width * lifeFactor).toInt
      if (up)
        y = _y - (1.1 * _height * lifeFactor).toInt
      else
        y = _y + (0.25 * _height * lifeFactor).toInt
      width = (_width * lifeFactor).toInt
      height = (_height * lifeFactor).toInt
    }
    !alive
  }


  override def render(g: Graphics, dxx: Int, dyy: Int): Unit = {
    if (alive) {

      val g2d: Graphics2D = g.create.asInstanceOf[Graphics2D]
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Util.alpha(0.9f * (1 - lifeFactor))))

      if (up) {
        if (currentImage != null) g2d.drawImage(currentImage, x + dxx, y + dyy, width, height, null)
      } else {
        // flip horizontally
        if (currentImage != null) g2d.drawImage(currentImage, x + dxx, y + dyy + height, width, -height, null)
      }
      g2d.dispose()
    }
  }


}
