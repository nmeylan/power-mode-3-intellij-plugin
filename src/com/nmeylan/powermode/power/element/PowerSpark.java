package com.nmeylan.powermode.power.element;

import com.nmeylan.powermode.Util;
import com.nmeylan.powermode.power.ElementOfPower;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class PowerSpark implements ElementOfPower {

  private float x;
  private float y;
  private float dx;
  private float dy;
  private int size;
  private long initLife;
  private long life;
  private float[] colors;
  private float gravityFactor;

  public PowerSpark(float x, float y, float dx, float dy, int size, long initLife, float[] colors, float gravityFactor) {
    this.x = x;
    this.y = y;
    this.dx = dx;
    this.dy = dy;
    this.size = size;
    this.initLife = initLife;
    this.colors = colors;
    this.gravityFactor = gravityFactor;
    this.life = System.currentTimeMillis() + initLife;
  }

  @Override
  public boolean update(double delta) {
    dy += (0.07f * gravityFactor) * delta;
    x += dx * delta;
    y += dy * delta;
    return !alive();
  }

  @Override
  public void render(Graphics g, int dxx, int dyy) {
    if (alive()) {
      Graphics2D g2d = (Graphics2D) g.create();
      g2d.setColor(new Color(colors[0], colors[1], colors[2], Util.alpha(colors[3])));
      g2d.fillOval((int)(dxx + x - (size / 2)), (int)(dyy + y - (size / 2)), size,size);
      g2d.dispose();
    }
  }

  @Override
  public long life() {
    return life;
  }

  @Override
  public long initLife() {
    return this.initLife;
  }
}
