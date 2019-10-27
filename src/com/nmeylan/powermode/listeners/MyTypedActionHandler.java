package com.nmeylan.powermode.listeners;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.nmeylan.powermode.Power;
import com.nmeylan.powermode.PowerMode;
import com.nmeylan.powermode.util.Util;
import org.jetbrains.annotations.NotNull;

import java.awt.Point;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class MyTypedActionHandler implements TypedActionHandler, Power {

  private TypedActionHandler typedActionHandler;

  public MyTypedActionHandler(TypedActionHandler typedActionHandler) {
    this.typedActionHandler = typedActionHandler;
  }

  public Set<Point> getEditorCaretPositions(Editor editor) {
    return editor.getCaretModel().getAllCarets().stream()
      .map(c -> Util.getCaretPosition(editor, c))
      .collect(Collectors.toSet());
  }

  public void initializeAnimationByTypedAction(Editor editor) {
    boolean isActualEditor = Util.isActualEditor(editor);
    if (isActualEditor) {
      Set<Point> positions = getEditorCaretPositions(editor);
      positions.stream().forEach(pos -> {
        powerMode().getMaybeElementOfPowerContainerManager().ifPresent(e -> e.initializeAnimation(editor, pos));
      });
    }
  }

  @Override
  public void execute(@NotNull Editor editor, char c, @NotNull DataContext dataContext) {
    if (powerMode().isEnabled()) {
      powerMode().increaseHeatup(Optional.of(dataContext), null);
      if (!powerMode().isCaretActionEnabled()) {
        initializeAnimationByTypedAction(editor);
      }
    }
    try {
      typedActionHandler.execute(editor, c, dataContext);
    } catch (IllegalStateException x) {
      PowerMode.logger().info(x.getMessage(), x);
    } catch (IndexOutOfBoundsException x) {
      PowerMode.logger().info(x.getMessage(), x);
    }
  }
}
