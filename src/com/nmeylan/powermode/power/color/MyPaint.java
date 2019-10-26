package com.nmeylan.powermode.power.color;

import java.awt.Color;

public class MyPaint {
  private int r;
  private int g;
  private int b;
  private int a;

  public MyPaint() {
  }


  public MyPaint(int r, int g, int b, int a) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }

  public MyPaint withRed(int r) {
    MyPaint p = new MyPaint();
    p.setR(r);
    return p;
  }

  public MyPaint withGreen(int g) {
    MyPaint p = new MyPaint();
    p.setG(g);
    return p;
  }

  public MyPaint withBlue(int b) {
    MyPaint p = new MyPaint();
    p.setB(b);
    return p;
  }

  public MyPaint withAlpha(int a) {
    MyPaint p = new MyPaint();
    p.setA(a);
    return p;
  }

  public int getR() {
    return r;
  }

  public void setR(int r) {
    this.r = r;
  }

  public int getG() {
    return g;
  }

  public void setG(int g) {
    this.g = g;
  }

  public int getB() {
    return b;
  }

  public void setB(int b) {
    this.b = b;
  }

  public int getA() {
    return a;
  }

  public void setA(int a) {
    this.a = a;
  }

  public Color color() {
    return new Color(r, g, b, a);
  }
}
