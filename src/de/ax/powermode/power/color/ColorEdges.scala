package de.ax.powermode

import java.awt.Color

import de.ax.powermode.power.color.MyPaint

class ColorEdges(var leftTop: MyPaint = MyPaint(0, 0, 0, 255),
                 var rightTop: MyPaint = MyPaint(255, 0, 0, 255),
                 var leftBottom: MyPaint = MyPaint(0, 255, 0, 255),
                 var rightBottom: MyPaint = MyPaint(255, 255, 0, 255)) {


  implicit def mp2Color(mp: MyPaint): Color = mp.color


  def getLeftTop: Color = {
    return leftTop
  }

  def getRightTop: Color = {
    return rightTop
  }

  def getLeftBottom: Color = {
    return leftBottom
  }

  def getRightBottom: Color = {
    return rightBottom
  }

  def updateColors(c1: Int) {
    leftTop = updateMyPaint(c1, leftTop)
    rightTop = updateMyPaint(c1, rightTop)
    leftBottom = updateMyPaint(c1, leftBottom)
    rightBottom = updateMyPaint(c1, rightBottom)
  }

  private def updateMyPaint(c: Int, MyPaint: MyPaint): MyPaint = {
    return MyPaint.withBlue(c)
  }

  def setLeftTop(leftTop: MyPaint) {
    this.leftTop = leftTop
  }

  def setRightTop(rightTop: MyPaint) {
    this.rightTop = rightTop
  }

  def setLeftBottom(leftBottom: MyPaint) {
    this.leftBottom = leftBottom
  }

  def setRightBottom(rightBottom: MyPaint) {
    this.rightBottom = rightBottom
  }

  def setRedFrom(redFrom: Int) {
    this.leftTop = leftTop.withRed(redFrom)
    this.leftBottom = leftBottom.withRed(redFrom)
  }

  def setRedTo(redTo: Int) {
    this.rightTop = rightTop.withRed(redTo)
    this.rightBottom = rightBottom.withRed(redTo)
  }

  def setGreenFrom(redFrom: Int) {
    this.leftTop = leftTop.withGreen(redFrom)
    this.rightTop = rightTop.withGreen(redFrom)
  }

  def setGreenTo(redTo: Int) {
    this.leftBottom = leftBottom.withGreen(redTo)
    this.rightBottom = rightBottom.withGreen(redTo)
  }

  def setBlueFrom(redFrom: Int) {
    this.leftTop = leftTop.withBlue(redFrom)
    this.leftBottom = leftBottom.withBlue(redFrom)
  }

  def setBlueTo(redTo: Int) {
    this.rightTop = rightTop.withBlue(redTo)
    this.rightBottom = rightBottom.withBlue(redTo)
  }

  def setAlpha(alpha: Int) {
    this.leftTop = leftTop.withAlpha(alpha)
    this.leftBottom = leftBottom.withAlpha(alpha)
    this.rightTop = rightTop.withAlpha(alpha)
    this.rightBottom = rightBottom.withAlpha(alpha)
  }
}