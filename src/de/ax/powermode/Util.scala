package de.ax.powermode

import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.{AlphaComposite, Point}
import java.io.File
import javax.imageio.ImageIO

import com.intellij.openapi.editor.{Editor, VisualPosition}
import com.intellij.util.PathUtil

/**
  * Created by nyxos on 30.09.16.
  */
object Util {
  def alpha(f: Float): Float = {
    if (f < 0) {
      0f
    } else if (f > 1) {
      1f
    } else {
      f
    }
  }

  lazy val powerBamImage = {
    val file = new File(PathUtil.getJarPathForClass(Util.getClass), "bam/bam.png")
    val imgFile = if (file.exists()) {
      ImageIO.read(file)
    } else {
      ImageIO.read(Util.getClass.getResourceAsStream("/bam/bam.png"))
    }
    val bufferedImage = new BufferedImage(imgFile.getWidth, imgFile.getHeight, BufferedImage.TYPE_INT_ARGB)
    val graphics = bufferedImage.getGraphics
    graphics.drawImage(imgFile, 0, 0, null)

    val graphics2D = bufferedImage.createGraphics()
    graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f))

    val at = AffineTransform.getScaleInstance(imgFile.getWidth, imgFile.getWidth)
    graphics2D.drawRenderedImage(bufferedImage, at)
    bufferedImage
  }

  def getPoint(position: VisualPosition, editor: Editor): Point = {
    val p: Point = editor.visualPositionToXY(position)
    val location = editor.getScrollingModel.getVisibleArea.getLocation
    p.translate(-location.x, -location.y)
    p
  }
}
