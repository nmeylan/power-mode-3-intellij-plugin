package de.ax.powermode

import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.{AlphaComposite, Point}
import java.io.File
import java.net.URL
import java.net.URI
import javax.imageio.ImageIO

import de.ax.powermode.cache.Cache
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.{Caret, Editor, VisualPosition}
import com.intellij.util.PathUtil
import de.ax.powermode.power.element.PowerFlame

import scala.util.Try
import de.ax.powermode.cache.Cache

object ImageUtil {

  val imageCache =
    new Cache[URI, BufferedImage, Long](
      fname => new File(fname).lastModified(),
      fname => !new File(fname).exists())

  def images(imagesPath: String): List[() => BufferedImage] = {
    val imageUrls = getImageUrls(imagesPath)
    getImagesCached(imageUrls).map(img => { () => ImageUtil.deepCopy(img) })
  }

  import java.awt.image.BufferedImage
  import java.awt.image.ColorModel
  import java.awt.image.WritableRaster

  def deepCopy(bi: BufferedImage): BufferedImage = {
    val cm = bi.getColorModel
    val isAlphaPremultiplied = cm.isAlphaPremultiplied
    val raster = bi.copyData(null)
    new BufferedImage(cm, raster, isAlphaPremultiplied, null)
  }

  private def getImagesCached(imageUrls: List[URI]) = {
    imageUrls.map(url => imageCache.getOrUpdate(url) {
      try {
        val img = ImageIO.read(url.toURL)
        val bufferedImage = new BufferedImage(img.getWidth, img.getHeight, BufferedImage.TYPE_INT_ARGB)
        val graphics = bufferedImage.getGraphics
        graphics.drawImage(img, 0, 0, null)
        Some(bufferedImage)
      } catch {
        case e: Exception =>
//          println(s"ERROR: ${e.getMessage}")
          e.printStackTrace()
          None
      }
    }).flatten
  }

  private def getImageUrls(imagesPath: String): List[URI] = {
    if (debugFolderExists(imagesPath)) {
      getImageUrlsFromDebugDir(imagesPath)
    } else {
      getImageUrlsFromResources(imagesPath)
    }
  }

  private def debugFolderExists(imagesPath: String): Boolean = {
    val file = new File(PathUtil.getJarPathForClass(classOf[PowerFlame]), imagesPath)

    file.exists()
  }

  private def getImageUrlsFromDebugDir(imagesPath: String): List[URI] = {
    Option(new File(PathUtil.getJarPathForClass(classOf[PowerFlame]), imagesPath).listFiles())
      .map(_.toList).toList.flatten.filter(_.isFile).map(_.toURI)
  }


  def getResourceFolderFiles(folder: String): List[File] = {
    val loader = Thread.currentThread.getContextClassLoader
    val url = loader.getResource(folder)
    val path = url.getPath
    Option(new File(path).listFiles).map(_.toList.sortBy(_.getName)).getOrElse(List.empty)
  }

  private def getImageUrlsFromResources(imagesFolder: String): List[URI] = {
    getResourceFolderFiles(imagesFolder).filter(_.isFile).map(_.toURI)
  }
}