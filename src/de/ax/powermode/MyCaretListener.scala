package de.ax.powermode

import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.event.{CaretEvent, CaretListener}

import scala.util.Try

/**
  * Created by nyxos on 04.01.17.
  */
class MyCaretListener extends CaretListener with Power {
  var modified = true


  override def caretPositionChanged(caretEvent: CaretEvent): Unit = {
    if (!modified && powerMode.caretAction) {
      initializeAnimationByCaretEvent(caretEvent.getCaret)
    }
    modified = false
  }

  override def caretRemoved(caretEvent: CaretEvent): Unit = {
    modified = true
  }

  override def caretAdded(caretEvent: CaretEvent): Unit = {
    modified = true
  }

  private def initializeAnimationByCaretEvent(caret: Caret) {
    val isActualEditor = Try {
      caret.getEditor.getColorsScheme.getClass.getName.contains("EditorImpl")
    }.getOrElse(false)
    if (isActualEditor) {
      Util.getCaretPosition(caret).toOption.foreach(p => powerMode.maybeElementOfPowerContainerManager.foreach(_.initializeAnimation(caret.getEditor, p)))
    }
  }
}
