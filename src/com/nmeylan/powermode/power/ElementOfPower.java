package com.nmeylan.powermode.power;


import com.nmeylan.powermode.Power;

import java.awt.Graphics;

public interface ElementOfPower extends Power {

  boolean update(double delta);

  void render(Graphics g, int dxx, int dyy);

  long life();

  long initLife();

  default float lifeFactor() {
    return 1 - ((life() - System.currentTimeMillis()) / (float)initLife());
  }
  default boolean alive() {
    return life() > System.currentTimeMillis() && powerMode().isEnabled();
  }
}
