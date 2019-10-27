package com.nmeylan.powermode.listeners;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class MyEnterHandler extends EnterHandlerDelegateAdapter {

  @Override
  public Result preprocessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull Ref<Integer> caretOffset, @NotNull Ref<Integer> caretAdvance, @NotNull DataContext dataContext, EditorActionHandler originalHandler) {
    ((MyTypedActionHandler)EditorActionManager.getInstance().getTypedAction().getRawHandler()).powerType(editor,dataContext);
    return super.preprocessEnter(file, editor, caretOffset, caretAdvance, dataContext, originalHandler);
  }
}
