package com.nmeylan.powermode.power.color;

import java.awt.Color;

public class ColorEdges {
  private MyPaint leftTop = new MyPaint(0, 0, 0, 255);
  private MyPaint rightTop = new MyPaint(255, 0, 0, 255);
  private MyPaint leftBottom = new MyPaint(0, 255, 0, 255);
  private MyPaint rightBottom = new MyPaint(255, 255, 0, 255);

  public ColorEdges() {
  }

  public Color getLeftTop() {
    return leftTop.color();
  }

  public Color getRightTop() {
    return rightTop.color();
  }

  public Color getLeftBottom() {
    return leftBottom.color();
  }

  public Color getRightBottom() {
    return rightBottom.color();
  }

  public void updateColors(int c1) {
    leftTop = updateMyPaint(c1, leftTop);
    rightTop = updateMyPaint(c1, rightTop);
    leftBottom = updateMyPaint(c1, leftBottom);
    rightBottom = updateMyPaint(c1, rightBottom);
  }

  private MyPaint updateMyPaint(int c, MyPaint MyPaint) {
    return MyPaint.withBlue(c);
  }

  public void setLeftTop(MyPaint leftTop) {
    this.leftTop = leftTop;
  }

  public void setRightTop(MyPaint rightTop) {
    this.rightTop = rightTop;
  }

  public void setLeftBottom(MyPaint leftBottom) {
    this.leftBottom = leftBottom;
  }

  public void setRightBottom(MyPaint rightBottom) {
    this.rightBottom = rightBottom;
  }

  public void setRedFrom(int redFrom) {
    this.leftTop = leftTop.withRed(redFrom);
    this.leftBottom = leftBottom.withRed(redFrom);
  }

  public void setRedTo(int redTo) {
    this.rightTop = rightTop.withRed(redTo);
    this.rightBottom = rightBottom.withRed(redTo);
  }

  public void setGreenFrom(int redFrom) {
    this.leftTop = leftTop.withGreen(redFrom);
    this.rightTop = rightTop.withGreen(redFrom);
  }

  public void setGreenTo(int redTo) {
    this.leftBottom = leftBottom.withGreen(redTo);
    this.rightBottom = rightBottom.withGreen(redTo);
  }

  public void setBlueFrom(int redFrom) {
    this.leftTop = leftTop.withBlue(redFrom);
    this.leftBottom = leftBottom.withBlue(redFrom);
  }

  public void setBlueTo(int redTo) {
    this.rightTop = rightTop.withBlue(redTo);
    this.rightBottom = rightBottom.withBlue(redTo);
  }

  public void setAlpha(int alpha) {
    this.leftTop = leftTop.withAlpha(alpha);
    this.leftBottom = leftBottom.withAlpha(alpha);
    this.rightTop = rightTop.withAlpha(alpha);
    this.rightBottom = rightBottom.withAlpha(alpha);
  }
}
