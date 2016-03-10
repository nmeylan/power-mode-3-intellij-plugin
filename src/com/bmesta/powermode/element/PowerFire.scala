package com.bmesta.powermode.element

import java.awt.geom.AffineTransform
import java.awt.image.{RescaleOp, BufferedImage}
import java.awt.{Graphics2D, Graphics}
import java.io.File
import javax.imageio.ImageIO

import com.intellij.util.PathUtil

import collection.JavaConversions._

object PowerFire {


  val image = {
    val img = {
      try {
        ImageIO.read(new File(PathUtil.getJarPathForClass(classOf[PowerFire]), "fire1.png"))
      } catch {
        case e =>
          e.printStackTrace()
          System.exit(1)
          throw e
      }
    }

    val bi = new BufferedImage(img.getWidth, img.getHeight, BufferedImage.TYPE_INT_ARGB)
    bi.getGraphics.drawImage(img, 0, 0, null)
    val g = bi.createGraphics()
    val at = AffineTransform.getScaleInstance(20, 20)
    new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB)
    g.drawRenderedImage(bi, at)
    bi
  }
}


/**
  * Created by nyxos on 10.03.16.
  */
case class PowerFire(x: Int, y: Int, _width: Int, _height: Int, initLife: Int) extends ElementOfPower {

  var life = initLife
  var width = _width
  var height = _height

  override def update: Boolean = {
    if (life > 0) {
      width -= 1
      height -= 1
      life -= 1
    }
    life <= 0
  }

  override def render(g: Graphics, dxx: Int, dyy: Int): Unit = {
    if (life > 0) {
      val g2d: Graphics2D = g.create.asInstanceOf[Graphics2D]
      g2d.drawImage(PowerFire.image, x - 5, y - 5, width, height, null)
      g2d.dispose()
    }
  }
}
