package de.ax.powermode

import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.{AlphaComposite, Point}
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

import de.ax.powermode.cache.Cache
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.{Caret, Editor, VisualPosition}
import com.intellij.util.PathUtil
import de.ax.powermode.power.element.PowerFlame

import scala.util.Try

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

  def editorOk(editor: Editor, maxSize: Int): Boolean = {
    !(editor match {
      case impl: EditorImpl =>
        try {
          impl.getPreferredSize.height < maxSize || impl.getPreferredSize.width < maxSize
        } catch {
          case _ => true
        }
      case _ =>
        false
    })
  }



  def getPoint(position: VisualPosition, editor: Editor): Point = {
    val p: Point = editor.visualPositionToXY(position)
    val location = editor.getScrollingModel.getVisibleArea.getLocation
    p.translate(-location.x, -location.y)
    p
  }

  def getCaretPosition(caret: Caret): Try[Point] = Try {
    getPoint(caret.getVisualPosition, caret.getEditor)
  }

  def getCaretPosition(editor: Editor, c: Caret): Try[Point] = Try {
    val p: Point = editor.visualPositionToXY(c.getVisualPosition)
    val location = editor.getScrollingModel.getVisibleArea.getLocation
    p.translate(-location.x, -location.y)
    p
  }

}
