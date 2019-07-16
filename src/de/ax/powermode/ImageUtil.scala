package de.ax.powermode

import java.awt.image.BufferedImage
import java.io.{BufferedOutputStream, File, FileOutputStream, InputStream}
import java.net.{URI, URL}

import javax.imageio.{IIOException, ImageIO}
import com.intellij.util.PathUtil
import de.ax.powermode.cache.Cache
import de.ax.powermode.power.element.PowerFlame

import scala.util.{Failure, Success, Try}

object ImageUtil {
  def imagesForPath(folder: Option[File]): scala.List[BufferedImage] = {
    val orElse = folder.getOrElse(new File("UNDEFINED"))
    //println(s"imagesForPath $folder")
    ImageUtil.listCache
      .getOrUpdate(orElse) {
        Some(folder.map(ImageUtil.images).toList.flatten.map(f => f()))
      }
      .toList
      .flatten
  }

  val imageCache =
    new Cache[URI, BufferedImage, Long](fname => new File(fname).lastModified(),
                                        fname => !new File(fname).exists())

  var lastUpdate = System.currentTimeMillis()

  val listCache = new Cache[File, List[BufferedImage], File](
    f => f,
    f =>
      if (System.currentTimeMillis() - 5000 > lastUpdate) {
        lastUpdate = System.currentTimeMillis()
        true
      } else false)

  def images(imagesPath: File): List[() => BufferedImage] = {
    val imageUrls = getImageUrls(imagesPath)
    getImagesCached(imageUrls).map(img => { () =>
      ImageUtil.deepCopy(img)
    })
  }

  import java.awt.image.BufferedImage

  def deepCopy(bi: BufferedImage): BufferedImage = {
    val cm = bi.getColorModel
    val isAlphaPremultiplied = cm.isAlphaPremultiplied
    val raster = bi.copyData(null)
    new BufferedImage(cm, raster, isAlphaPremultiplied, null)
  }

  def getUrl(uri: URI): Try[URL] = Try {
    uri.toURL
  }

  private def getImagesCached(imageUrls: List[URI]) = {
    imageUrls.flatMap(uri =>
      imageCache.getOrUpdate(uri) {
        try {
          val maybeImg = Try(ImageIO.read(uri.toURL))

          maybeImg match {
            case Success(img) =>
              val bufferedImage = new BufferedImage(img.getWidth,
                                                    img.getHeight,
                                                    BufferedImage.TYPE_INT_ARGB)
              val graphics = bufferedImage.getGraphics
              graphics.drawImage(img, 0, 0, null)
              Some(bufferedImage)
            case Failure(e) =>
               e match {
                 case io:IIOException =>
                   PowerMode.logger
                     .error(
                       s"could not load image file! Please try to store your PowerMode " +
                         s"Images/Animations in a different folder and restart the application! File not found: '$uri'!",
                       e)
                 case ex =>
                   PowerMode.logger
                     .error(
                       s"could not load image file! Please try to store your PowerMode " +
                         s"Images/Animations in a different folder and restart the application! File not found: '$uri'!",
                       ex)
               }
              None
          }
        }
    })
  }

  private def getImageUrls(imagesPath: File): List[URI] = {
    try {
      //println(s"FILE: $imagesPath")
      val urls = if (imagesPath.exists()) {
        getFileImages(imagesPath)
      } else if (debugFolderExists(imagesPath)) {
        getImageUrlsFromDebugDir(imagesPath)
      } else {
        getImageUrlsFromResources(imagesPath)
      }
      //println(s"URLS: $urls")
      urls
    } catch {
      case e: Throwable =>
        PowerMode.logger.error(
          s"error getting image urls from '${imagesPath}': ${e.getMessage}",
          e)
        throw e
    }
  }

  private def getFileImages(imagesPath: File): List[URI] = {
    //println(s"LOADING FROM normal path: $imagesPath")

    val files = if (imagesPath.isFile) {
      List(imagesPath)
    } else {
      Option(imagesPath.getAbsoluteFile.listFiles())
        .map(_.toList)
        .toList
        .flatten
        .filter(_.isFile)
    }
    files.map(_.getAbsoluteFile.toURI)
  }

  private def debugFolderExists(imagesPath: File): Boolean = {
    val file = new File(PathUtil.getJarPathForClass(classOf[PowerFlame]),
                        imagesPath.getPath)
    file.exists()
  }

  private def getImageUrlsFromDebugDir(imagesPath: File): List[URI] = {
    val file = new File(PathUtil.getJarPathForClass(classOf[PowerFlame]),
                        imagesPath.getPath)
    //println(s"LOADING FROM exploded sandbox: $file")
    Option(file.listFiles())
      .map(_.toList)
      .toList
      .flatten
      .filter(_.isFile)
      .map(_.toURI)
  }

  def writeBytes(data: Stream[Byte], file: File) = {
    val target = new BufferedOutputStream(new FileOutputStream(file))
    try data.foreach(target.write(_))
    finally target.close()
  }

  lazy val fireUrls = (1 to 25)
    .map(i => if (i > 9) s"$i" else s"0$i")
    .map { i =>
      mkTmpImg(
        classOf[PowerFlame].getResourceAsStream(
          s"/fire/animated/256/fire1_ $i.png"))
    }
    .toList

  lazy val bamUrls =
    Option(classOf[PowerFlame].getResourceAsStream(s"/bam/bam.png"))
      .map(mkTmpImg)
      .toList

  private def getImageUrlsFromResources(imagesFolder: File): List[URI] = {
    val loader = this.getClass().getClassLoader()
    //println(s"LOADING FROM JAR: $imagesFolder")
    val uRLs: List[URL] = if (imagesFolder.getPath.contains("fire")) {
      fireUrls
    } else if (imagesFolder.getPath.contains("bam")) {
      bamUrls
    } else {
      None.toList
    }
    uRLs.map(_.toURI)
  }

  private def mkTmpImg(stream: InputStream): URL = {
    import java.io.File
    val tempFile =
      File.createTempFile(System.currentTimeMillis() + "_pmtempfile_", ".png")
    tempFile.deleteOnExit()
    writeBytes(Stream.continually(stream.read).takeWhile(_ != -1).map(_.toByte),
               tempFile)
    tempFile.toURI.toURL
  }
}
