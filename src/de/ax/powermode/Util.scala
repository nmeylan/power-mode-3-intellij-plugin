package de.ax.powermode

import java.awt.Point

import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.{Caret, Editor, EditorKind, VisualPosition}

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

  def isActualEditor(editor: Editor): Boolean = {
    editor match {
      case impl: EditorImpl =>
        try {
          Set(EditorKind.UNTYPED, EditorKind.MAIN_EDITOR, EditorKind.DIFF)
            .contains(impl.getEditorKind) &&
          !impl.isOneLineMode
        } catch {
          case _ => false
        }
      case _ =>
        false
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

  def interceptError[T](f: => T): T = {
    try {
      f
    } catch {
      case e: Throwable =>
      throw e
    }
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
