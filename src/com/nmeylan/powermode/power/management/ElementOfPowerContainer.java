package com.nmeylan.powermode.power.management;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollingModel;
import com.intellij.openapi.editor.event.CaretAdapter;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.nmeylan.powermode.Pair;
import com.nmeylan.powermode.Power;
import com.nmeylan.powermode.Util;
import com.nmeylan.powermode.power.ElementOfPower;
import com.nmeylan.powermode.power.element.PowerBam;
import com.nmeylan.powermode.power.element.PowerFlame;
import com.nmeylan.powermode.power.element.PowerSpark;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class ElementOfPowerContainer extends JComponent implements ComponentListener, Power {

  private Editor editor;

  private List<JComponent> shakeComponents;
  private List<Pair<ElementOfPower, Point>> elementsOfPower;
  private long lastShake;
  private long lastUpdate;
  private List<Point> shakeData;
  private List<List<Point>> lastPositionsPerCarets;

  public ElementOfPowerContainer(Editor editor) {
    super();
    this.editor = editor;
    this.shakeComponents = Arrays.asList(editor.getComponent(), editor.getContentComponent());
    this.elementsOfPower = new ArrayList<>();
    this.lastPositionsPerCarets = new ArrayList<>();
    this.shakeData = new ArrayList<>();
    this.lastShake = System.currentTimeMillis();
    this.lastUpdate = System.currentTimeMillis();
    JComponent myParent = editor.getContentComponent();
    myParent.add(this);
    this.setBounds(myParent.getBounds());
    setVisible(true);
    myParent.addComponentListener(this);
    editor.getCaretModel().addCaretListener(new CaretAdapter() {
      public void changeCarets() {
        lastPositionsPerCarets = editor.getCaretModel().getAllCarets().stream().map(
          caret -> Arrays.asList(
            Util.getPoint(editor.offsetToVisualPosition(caret.getSelectionStart()), caret.getEditor()),
            Util.getPoint(editor.offsetToVisualPosition(caret.getSelectionEnd()), caret.getEditor()))).collect(Collectors.toList());
      }

      @Override
      public void caretPositionChanged(@NotNull CaretEvent event) {
        changeCarets();
      }

      @Override
      public void caretAdded(@NotNull CaretEvent event) {
        changeCarets();
      }

      @Override
      public void caretRemoved(@NotNull CaretEvent event) {
        changeCarets();
      }
    });

    editor.getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      public void documentChanged(@NotNull DocumentEvent event) {
        if (powerMode().isBamEnabled()) {
          boolean shouldAnimate = (
            event.getNewFragment().length() > 100 ||
              event.getOldFragment().length() > 100 ||
              event.getOldFragment().toString().contains("\n") ||
              event.getNewFragment().toString().contains("\n")
          );
          if (shouldAnimate) {
            List<String> lineFeeds = Arrays.asList((event.getOldFragment().toString() + event.getNewFragment().toString()).split("\n"));
            int width = lineFeeds.isEmpty() ? 0 : (int) (lineFeeds.stream().max(Comparator.comparing(String::length)).get().length() / 2.0) * editor.getLineHeight();
            if (!lastPositionsPerCarets.isEmpty()) {
              SwingUtilities.invokeLater(() -> lastPositionsPerCarets.stream()
                .forEach(caretPositions -> initializeAnimation(caretPositions.get(0), caretPositions.get(1), width)));
            }
          }
        }
      }

    });
  }


  private List<Point> getAllCaretPositions() {
    return editor.getCaretModel().getAllCarets().stream()
      .map(caret -> Util.getPoint(caret.getVisualPosition(), caret.getEditor()))
      .collect(Collectors.toList());
  }

  public void updateElementsOfPower() {
    long delta = System.currentTimeMillis() - lastUpdate;
    if (delta > (1000.0 / powerMode().getFrameRate()) * 2) {
      delta = 16;
    }
    lastUpdate = System.currentTimeMillis();
    double db = 1000.0 / 16;
    long deltaa = delta;
    if (!elementsOfPower.isEmpty()) {
      elementsOfPower =
        elementsOfPower.stream().filter(p -> {
          long start = System.nanoTime();
          p.first().update((deltaa / db));

          long diff = (System.nanoTime() - start);
          System.out.println(diff / 1000.0 + " ms");
          return p.first().alive();
        }).collect(Collectors.toList());
      repaint();
    }
  }

  public void initializeAnimation(Point point) {
    this.setBounds(getMyBounds());

    if (powerMode().isSparksEnabled()) {
      addSparks(point);
    }
    if (powerMode().isFlamesEnabled()) {
      addFlames(point);
    }

    if (powerMode().isShakeEnabled()) {
      doShake(shakeComponents);
    }
    repaint();
  }

  private void initializeAnimation(Point a, Point b, int lineWidth) {
    int x = a.x;
    int y = a.y;
    int width = Math.max(b.x - x, 50);
    int height = Math.max(b.y - y, 50);
    int dim = (int) (Arrays.asList(lineWidth, width, height, 50).stream().mapToInt(i -> i).max().getAsInt() * powerMode().valueFactor());
    if (b.y - Math.abs(y) < dim) {
      y = y - dim / 2;
    }
    elementsOfPower.add(
      Pair.with(
        new PowerBam(x, y, dim, dim, (long) (powerMode().getBamLife() * powerMode().valueFactor())),
        getScrollPosition()));
  }

  private void addFlames(Point point) {
    float base = 0.3f;
    int wh = (int) ((powerMode().getMaxFlameSize() * base +
      ((Math.random() * powerMode().getMaxFlameSize() * (1 - base)) * powerMode().valueFactor())));
    int initLife = (int) (powerMode().getMaxFlameLife() * powerMode().valueFactor());
    if (initLife > 100) {
      elementsOfPower.add(
        Pair.with(
          new PowerFlame(point.x + 5, point.y - 1, wh, wh, initLife, true),
          getScrollPosition()));
      elementsOfPower.add(
        Pair.with(
          new PowerFlame(point.x + 5, point.y + 15, wh, wh, initLife, false),
          getScrollPosition()));
    }
  }

  private void addSparks(Point point) {
    for (int i = 0; i < (int) (powerMode().getSparkCount() * powerMode().valueFactor()); i++) {
      addSpark(point.x, point.y);
    }
  }

  private void addSpark(int x, int y) {
    float dx = (float) ((Math.random() * 2) * (Math.random() > 0.5 ? -1 : 1) * powerMode().getSparkVelocityFactor());
    float dy = (float) (((Math.random() * -3) - 1) * powerMode().getSparkVelocityFactor());
    int size = (int) ((Math.random() * powerMode().getSparkSize()) + 1);
    int life = (int) (Math.random() * powerMode().getSparkLife() * powerMode().valueFactor());
    elementsOfPower.add(
      Pair.with(
        new PowerSpark(x,
          y,
          dx,
          dy,
          size,
          life,
          genNextColor(),
          (float) powerMode().getGravityFactor()),
        getScrollPosition()
      ));
  }

  private float[] genNextColor() {
    return new float[]{
      getColorPart(powerMode().getRedFrom(), powerMode().getRedTo()),
      getColorPart(powerMode().getGreenFrom(), powerMode().getGreenTo()),
      getColorPart(powerMode().getBlueFrom(), powerMode().getBlueTo()),
      powerMode().getColorAlpha() / 255f};
  }

  private float getColorPart(int from, int to) {
    return (float) (((Math.random() * (to - from)) + from) / 255);
  }

  private Point getScrollPosition() {
    return new Point(editor.getScrollingModel().getHorizontalScrollOffset(),
      editor.getScrollingModel().getVerticalScrollOffset());

  }

  private void doShake(List<JComponent> myShakeComponents) {
    if (Util.editorOk(editor, 100)) {
      int x, y;
      if (shakeData.size() >= 1) {
        x = shakeData.get(0).x;
        y = shakeData.get(0).y;
        shakeData.clear();
      } else {
        x = generateShakeOffset();
        y = generateShakeOffset();
        int scrollX = editor.getScrollingModel().getHorizontalScrollOffset();
        int scrollY = editor.getScrollingModel().getVerticalScrollOffset();
        shakeData.add(new Point(x, y));
        shakeData.add(new Point(scrollX, scrollY));
      }
      myShakeComponents.stream().forEach(component -> {
        Rectangle bounds = component.getBounds();
        component.setBounds(
          bounds.x + x,
          bounds.y + y,
          bounds.width,
          bounds.height
        );
      });
      lastShake = System.currentTimeMillis();
    }
  }

  private int generateShakeOffset() {
    int range = (int) (powerMode().getShakeRange() * powerMode().valueFactor());
    return (int) (range - (Math.random() * 2 * range));
  }

  public void componentResized(ComponentEvent e) {
    setBounds(getMyBounds());
    powerMode().logger().debug("Resized");
  }

  public void componentMoved(ComponentEvent e) {
    setBounds(getMyBounds());
    powerMode().logger().debug("Moved");
  }

  @Override
  public void componentShown(ComponentEvent e) {

  }

  @Override
  public void componentHidden(ComponentEvent e) {

  }

  private Rectangle getMyBounds() {
    Rectangle area = editor.getScrollingModel().getVisibleArea();
    return new Rectangle(area.x, area.y, area.width, area.height);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (powerMode().isEnabled()) {
      if (shakeData != null && shakeData.size() >= 2 &&
        System.currentTimeMillis() - lastShake > 100 &&
        Math.abs(shakeData.get(0).x) < 50 && Math.abs(shakeData.get(1).y) < 50) {
        doShake(Arrays.asList(editor.getComponent()));
      }
      renderElementsOfPower(g);
    }
  }

  private void renderElementsOfPower(Graphics g) {
    ScrollingModel scrollingModel = editor.getScrollingModel();
    Point newElementPosition = new Point(scrollingModel.getHorizontalScrollOffset(), scrollingModel.getVerticalScrollOffset());

    elementsOfPower.stream().forEach(elementOfPowerPointPair -> {
      int x = elementOfPowerPointPair.last().x - newElementPosition.x;
      int y = elementOfPowerPointPair.last().y - newElementPosition.y;
      elementOfPowerPointPair.first().render(g, x, y);
    });

  }
}