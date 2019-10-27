package com.nmeylan.powermode.util;

public class Pair <X, Y> {
  private X x;
  private Y y;

  public Pair(X x, Y y) {
    this.x = x;
    this.y = y;
  }

  public static Pair with(Object x, Object y) {
    return new Pair<>(x, y);
  }

  public X first() {
    return x;
  }

  public Y last() {
    return y;
  }

}
