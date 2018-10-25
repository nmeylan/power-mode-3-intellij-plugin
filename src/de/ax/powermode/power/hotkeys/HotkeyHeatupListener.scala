package de.ax.powermode.power.hotkeys

import java.awt._
import java.awt.event._
import javax.swing._

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem._
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.keymap.KeymapManager
import de.ax.powermode.Power

/**
  * Date: 04.09.2006
  * Time: 14:11:03
  */
class HotkeyHeatupListener
    extends ApplicationComponent
    with AWTEventListener
    with Power {
  lazy val allActionKeyStrokes: Set[KeyStroke] =
    actionsToKeyStrokes.values.flatten.toSet

  override def eventDispatched(e: AWTEvent): Unit = {
    if (powerMode.isEnabled) {
      e match {
        case event: KeyEvent => {
          if ((event.getModifiersEx & (InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) > 0) {

            val eventKeyStroke =
              KeyStroke.getKeyStroke(event.getKeyCode, event.getModifiersEx)
            val isHotkey = allActionKeyStrokes.contains(eventKeyStroke)
            if (isHotkey) {
              powerMode.increaseHeatup(
                Some(
                  DataManager.getInstance().getDataContext(event.getComponent)),
                Some(eventKeyStroke))
            }
          }
        }
        case _ =>
      }
    }
  }

  lazy val actionsToKeyStrokes = {
    Map(
      KeymapManager.getInstance.getActiveKeymap.getActionIds.seq
        .map(ActionManager.getInstance.getAction)
        .filter(a => a != null && a.getShortcutSet != null)
        .map { a =>
          val keyStrokes = a.getShortcutSet.getShortcuts.seq
            .filter(_.isKeyboard)
            .map(_.asInstanceOf[KeyboardShortcut])
            .flatMap(a => Seq(a.getFirstKeyStroke, a.getSecondKeyStroke))
          (a, keyStrokes)
        }: _*)
  }

  override def initComponent(): Unit = {
    Toolkit.getDefaultToolkit.addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK)
  }

  override def disposeComponent(): Unit = {}

  override def getComponentName: String = "HotkeyHeatupListener"
}
