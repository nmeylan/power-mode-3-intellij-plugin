package com.nmeylan.powermode.util;

import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.NotNull;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

public class Util {
  private static List<EditorKind> EDITOR_KINDS = Arrays.asList(EditorKind.UNTYPED, EditorKind.MAIN_EDITOR, EditorKind.DIFF);
  public static float alpha(float f) {
    if (f < 0) {
      return 0f;
    } else if (f > 1) {
      return 1f;
    } else {
      return f;
    }
  }

  public static boolean isFileEditor(@NotNull Editor editor) {
    final VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
    return virtualFile != null && !(virtualFile instanceof LightVirtualFile);
  }

  public static boolean isActualEditor(Editor editor) {
    if (editor instanceof  EditorImpl) {
      return EDITOR_KINDS.contains(editor.getEditorKind()) && isFileEditor(editor) && !editor.isOneLineMode();
    }
    return  false;
  }

  public static boolean editorOk(Editor editor, int maxSize) {
    if (editor instanceof  EditorImpl) {
      EditorImpl impl = (EditorImpl) editor;
      return !(impl.getPreferredSize().height < maxSize || impl.getPreferredSize().width < maxSize);
    }
    return true;
  }

  public static Point getPoint(VisualPosition position, Editor editor) {
    Point p = editor.visualPositionToXY(position);
    Point location = editor.getScrollingModel().getVisibleArea().getLocation();
    p.translate(-location.x, -location.y);
    return p;
  }

  public static Point getCaretPosition(Caret caret) {
    return getPoint(caret.getVisualPosition(), caret.getEditor());
  }

  public static Point getCaretPosition(Editor editor, Caret c) {
    return getPoint(c.getVisualPosition(), editor);
  }

}
