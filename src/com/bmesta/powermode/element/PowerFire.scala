package com.bmesta.powermode.element

import java.awt.geom.AffineTransform
import java.awt.image.{RescaleOp, BufferedImage}
import java.awt.{AlphaComposite, Graphics2D, Graphics}
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO

import com.intellij.util.PathUtil

import collection.JavaConversions._

object PowerFire {


  lazy val images = {
    new File(PathUtil.getJarPathForClass(classOf[PowerFire]), "fire/animated").listFiles().filter(_.isFile).map { f =>
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
      val at = AffineTransform.getScaleInstance(20, 20)
      g.drawRenderedImage(bi, at)
      bi
    }
  }
}


/**
  * Created by nyxos on 10.03.16.
  */
case class PowerFire(_x: Int, _y: Int, _width: Int, _height: Int, initLife: Int, up: Boolean) extends ElementOfPower {

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
      x -= 1
      if (up)
        y -= 1
      width += 1
      height += 1
    }
    !alive
  }

  def alive: Boolean = {
    life > System.currentTimeMillis() //&& width >= 0 && width <= _width
  }

  override def render(g: Graphics, dxx: Int, dyy: Int): Unit = {
    if (alive) {
      val g2d: Graphics2D = g.create.asInstanceOf[Graphics2D]
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,   ( life-System.currentTimeMillis()) / initLife.toFloat))
      if (currentImage != null) g2d.drawImage(currentImage, x, y, width, height, null)
      g2d.dispose()
    }
  }
}
