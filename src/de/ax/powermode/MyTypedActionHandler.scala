package de.ax.powermode

import java.awt.Point

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.TypedActionHandler

import scala.collection.JavaConversions._
import scala.util.Try

/**
  * Created by nyxos on 04.01.17.
  */
class MyTypedActionHandler(typedActionHandler: TypedActionHandler) extends TypedActionHandler with Power{

  def execute(editor: Editor, c: Char, dataContext: DataContext) {
    if (powerMode.isEnabled) {
      powerMode.increaseHeatup
      if (!powerMode.caretAction) {
        initializeAnimationByTypedAction(editor)
      }
    }
    typedActionHandler.execute(editor, c, dataContext)
  }

  def getEditorCaretPositions(editor: Editor): Seq[Point] = {
    editor.getCaretModel.getAllCarets.map({ c =>
      Util.getCaretPosition(editor, c)
    }).filter(_.isFailure)
      .map(_.get)
  }

  def initializeAnimationByTypedAction(editor: Editor): Unit = {
    val isActualEditor = Try {
      editor.getColorsScheme.getClass.getName.contains("EditorImpl")
    }.getOrElse(false)
    if (isActualEditor) {
      getEditorCaretPositions(editor).foreach(pos =>
        powerMode.maybeElementOfPowerContainerManager.foreach(_.initializeAnimation(editor, pos)))
    }
  }
}
