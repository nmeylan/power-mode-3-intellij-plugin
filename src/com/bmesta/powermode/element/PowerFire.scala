package com.bmesta.powermode.element

import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.{AlphaComposite, Graphics, Graphics2D}
import java.io.File
import javax.imageio.ImageIO

import com.bmesta.powermode.PowerMode
import com.intellij.util.PathUtil

object PowerFire {

  val resolution = 256
  val frames = 25

  lazy val images = {
    val file = new File(PathUtil.getJarPathForClass(classOf[PowerFire]), s"fire/animated/$resolution")
    println(s"IMAGEFOLDER: ${file.getAbsolutePath}")
    val imageFiles = file.listFiles()
    val fileImages = imageFiles match {
      case null =>

        val imageUrls = (1 to frames).map(i => if (i > 9) s"$i" else s"0$i").map(i=>classOf[PowerFire].getResourceAsStream(s"/fire/animated/$resolution/fire1_ $i.png"))
        imageUrls.map(ImageIO.read)
      case files => files.toList.filter(_.isFile).map { f =>
        try {
          ImageIO.read(f)
        } catch {
          case e =>
            e.printStackTrace()
            System.exit(1)
            throw e
        }
      }
    }
    fileImages.map { img =>
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

  val life = System.currentTimeMillis() + initLife
  var x = _x
  var y = _y
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

  def alive: Boolean = {
    life > System.currentTimeMillis() //&& width >= 0 && width <= _width
  }

  def lifeFactor: Float = {
    1 - ((life - System.currentTimeMillis()) / initLife.toFloat) toFloat
  }
}
