package com.bmesta.powermode.element

import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.{AlphaComposite, Graphics, Graphics2D}
import java.io.File
import javax.imageio.ImageIO

import com.bmesta.powermode.PowerMode
import com.intellij.util.PathUtil

/**
  * Created by nyxos on 10.03.16.
  */
object PowerFire {

  lazy val images = {
    val file = new File(PathUtil.getJarPathForClass(classOf[PowerFire]), s"fire/animated/$resolution")
    val imageFiles = file.listFiles()
    val fileImages = imageFiles match {
      case null =>
        getBufferedImagesFromJar
      case files =>
        getBufferedImagesFromDebugDir(files)
    }
    fileImages.map { img =>
      val bufferedImage = new BufferedImage(img.getWidth, img.getHeight, BufferedImage.TYPE_INT_ARGB)
      val graphics = bufferedImage.getGraphics
      graphics.drawImage(img, 0, 0, null)

      val graphics2D = bufferedImage.createGraphics()
      graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f))

      val at = AffineTransform.getScaleInstance(resolution, resolution)
      graphics2D.drawRenderedImage(bufferedImage, at)
      bufferedImage
    }
  }
  val resolution = 256
  val frames = 25

  private def getBufferedImagesFromDebugDir(files: Array[File]): List[BufferedImage] = {
    files.toList.filter(_.isFile).map { f =>
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

  private def getBufferedImagesFromJar: IndexedSeq[BufferedImage] = {
    val imageUrls = (1 to frames).map(i => if (i > 9) s"$i" else s"0$i")
      .map(i => classOf[PowerFire].getResourceAsStream(s"/fire/animated/$resolution/fire1_ $i.png"))
    imageUrls.map(ImageIO.read)
  }
}


case class PowerFire(_x: Int, _y: Int, _width: Int, _height: Int, initLife: Long, up: Boolean, powerMode: PowerMode)
  extends ElementOfPower {

  val life = System.currentTimeMillis() + initLife
  var x = _x
  var y = _y
  var width = 0
  var height = 0

  var i = 0
  var currentImage: BufferedImage = null

  override def update: Boolean = {
    if (alive) {
      currentImage = PowerFire.images(i % PowerFire.frames)
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


}
