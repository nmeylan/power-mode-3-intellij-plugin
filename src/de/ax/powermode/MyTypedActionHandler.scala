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
class MyTypedActionHandler(typedActionHandler: TypedActionHandler)
    extends TypedActionHandler
    with Power {

  def execute(editor: Editor, c: Char, dataContext: DataContext) {
    if (powerMode.isEnabled) {
      powerMode.increaseHeatup(dataContext = Some(dataContext))
      if (!powerMode.caretAction) {
        initializeAnimationByTypedAction(editor)
      }
    }
    try {
      typedActionHandler.execute(editor, c, dataContext)
    } catch {
      case x: IndexOutOfBoundsException =>
        logger.info(x.getMessage, x)
    }
  }

  def getEditorCaretPositions(editor: Editor): Seq[Point] = {
    editor.getCaretModel.getAllCarets
      .map({ c =>
        Util.getCaretPosition(editor, c)
      })
      .filter(_.isSuccess)
      .map(_.get)
  }

  def initializeAnimationByTypedAction(editor: Editor): Unit = {
    val triedBoolean = Try {
      editor.getColorsScheme.getClass.getName.contains("EditorImpl")
    }
    val isActualEditor = triedBoolean.getOrElse(false)
    if (isActualEditor) {
      val positions = getEditorCaretPositions(editor)
      positions.foreach(pos => {
        powerMode.maybeElementOfPowerContainerManager.foreach(
          _.initializeAnimation(editor, pos))
      })
    }
  }
}
