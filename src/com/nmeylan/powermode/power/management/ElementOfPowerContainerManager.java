package com.nmeylan.powermode.power.management;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryAdapter;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.nmeylan.powermode.Power;
import com.nmeylan.powermode.PowerMode;
import com.nmeylan.powermode.Util;
import org.jetbrains.annotations.NotNull;

import javax.swing.SwingUtilities;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class ElementOfPowerContainerManager extends EditorFactoryAdapter implements Power {

  private static Map<Editor, ElementOfPowerContainer> elementsOfPowerContainers = new HashMap<>();
  private Thread elementsOfPowerUpdateThread;

  public ElementOfPowerContainerManager() {
    elementsOfPowerUpdateThread = new Thread(() -> {
      while (true) {
        try {
          if (powerMode() != null) {
            powerMode().reduceHeatup();
            updateContainers();
            try {
              Thread.sleep(1000 / powerMode().getFrameRate());
            } catch (InterruptedException e) {
              // Do nothing
            }
          }
        } catch (Exception e) {
          PowerMode.logger().error(e.getMessage(), e);
        }
      }
    });
    elementsOfPowerUpdateThread.start();
  }


  void updateContainers() {
    elementsOfPowerContainers.values().forEach(ElementOfPowerContainer::updateElementsOfPower);
  }


  @Override
  public void editorCreated(@NotNull EditorFactoryEvent event) {
    Editor editor = event.getEditor();
    if (Util.isActualEditor(editor)) {
      elementsOfPowerContainers.put(editor, new ElementOfPowerContainer(editor));
    }
  }

  @Override
  public void editorReleased(@NotNull EditorFactoryEvent event) {
    elementsOfPowerContainers.remove(event.getEditor());
  }

  public void initializeAnimation(Editor editor, Point position) {
    if (powerMode().isEnabled()) {
      SwingUtilities.invokeLater(() -> initializeInUI(editor, position));
    }
  }

  public void initializeInUI(Editor editor, Point position) {
    elementsOfPowerContainers.get(editor).initializeAnimation(position);
  }

  public void dispose() {
    elementsOfPowerUpdateThread.interrupt();
    elementsOfPowerContainers.clear();
  }
}
