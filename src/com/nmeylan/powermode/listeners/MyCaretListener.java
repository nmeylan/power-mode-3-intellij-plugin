package com.nmeylan.powermode.listeners;

import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.nmeylan.powermode.Power;
import com.nmeylan.powermode.util.Util;
import org.jetbrains.annotations.NotNull;

import java.awt.Point;

public class MyCaretListener implements CaretListener, Power {
  private boolean modified = true;

  @Override
  public void caretPositionChanged(@NotNull CaretEvent event) {
    if (!modified && powerMode().isCaretActionEnabled()) {
      initializeAnimationByCaretEvent(event.getCaret());
    }
    modified = false;
  }

  @Override
  public void caretAdded(@NotNull CaretEvent event) {
    modified = true;
  }

  @Override
  public void caretRemoved(@NotNull CaretEvent event) {
    modified = true;
  }

  private void initializeAnimationByCaretEvent(Caret caret) {
    if (Util.isActualEditor(caret.getEditor())) {
      Point position = Util.getCaretPosition(caret);
      powerMode().getMaybeElementOfPowerContainerManager().ifPresent(e -> e.initializeAnimation(caret.getEditor(), position));
    }
  }
}
