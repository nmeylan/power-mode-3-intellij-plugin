package com.bmesta.powermode.element

import java.awt.geom.AffineTransform
import java.awt.image.{RescaleOp, BufferedImage}
import java.awt.{AlphaComposite, Graphics2D, Graphics}
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO

import com.bmesta.powermode.PowerMode
import com.intellij.util.PathUtil

import collection.JavaConversions._

object PowerFire {
  val resolution = 256

  lazy val images = {
    new File(PathUtil.getJarPathForClass(classOf[PowerFire]), s"fire/animated/$resolution").listFiles().filter(_.isFile).map { f =>
      val img = {
        try {
          ImageIO.read(f)
        } catch {
          case e =>
            e.printStackTrace()
            System.exit(1)
            throw e
        }
      }

      val bi = new BufferedImage(img.getWidth, img.getHeight, BufferedImage.TYPE_INT_ARGB)
      val gi = bi.getGraphics
      gi.drawImage(img, 0, 0, null)

      val g = bi.createGraphics()
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f))

      val at = AffineTransform.getScaleInstance(resolution, resolution)
      g.drawRenderedImage(bi, at)
      bi
    }
  }
}


/**
  * Created by nyxos on 10.03.16.
  */
case class PowerFire(_x: Int, _y: Int, _width: Int, _height: Int, initLife: Int, up: Boolean, config: PowerMode) extends ElementOfPower {

  var x = _x
  var y = _y
  val life = System.currentTimeMillis() + initLife
  var width = 0
  var height = 0

  var i = 0
  var currentImage: BufferedImage = null

  override def update: Boolean = {
    if (alive) {
      currentImage = PowerFire.images(i % 25)
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

  def alive: Boolean = {
    life > System.currentTimeMillis() //&& width >= 0 && width <= _width
  }


  override def render(g: Graphics, dxx: Int, dyy: Int): Unit = {
    if (alive) {

      val g2d: Graphics2D = g.create.asInstanceOf[Graphics2D]
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f * (1 - lifeFactor)))

      if (up) {
        if (currentImage != null) g2d.drawImage(currentImage, x + dxx, y + dyy, width, height, null)
      } else {
        // flip horizontally
        if (currentImage != null) g2d.drawImage(currentImage, x + dxx, y + dyy + height, width, -height, null)
      }
      g2d.dispose()
    }
  }

  def lifeFactor: Float = {
    1 - ((life - System.currentTimeMillis()) / initLife.toFloat) toFloat
  }
}
