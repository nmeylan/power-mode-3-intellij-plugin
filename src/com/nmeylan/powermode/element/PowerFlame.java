package com.nmeylan.powermode.element;

import com.nmeylan.powermode.util.ImageUtil;
import com.nmeylan.powermode.util.Util;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerFlame implements ElementOfPower {

  private static Map<String, List<BufferedImage>> flameImagesCache = new HashMap<>();
  private float x;
  private int i;
  private float _x;
  private float y;
  private float _y;
  private int width;
  private int _width;
  private int height;
  private int _height;
  private long initLife;
  private long life;
  private boolean up;
  private String cacheKey;

  public PowerFlame(float _x, float _y, int _width, int _height, long initLife, boolean up) {
    this.x = _x;
    this.y = _y;
    this.width = 0;
    this.height = 0;
    this._x = _x;
    this._y = _y;
    this._width = _width;
    this._height = _height;
    this.initLife = initLife;
    this.life = System.currentTimeMillis() + initLife;
    this.up = up;
    this.cacheKey = powerMode().flameImageFolder().get().getAbsolutePath();
    findFlameImages();
  }

  private Image findFlameImages() {
    List<BufferedImage> flameImages = flameImagesCache.get(cacheKey);
    if (flameImages != null && !flameImages.isEmpty()) {
      return flameImages.get(0);
    }
    File flameImageFolder = powerMode().flameImageFolder().orElse(null);
    if (flameImageFolder != null) {
      flameImagesCache.clear();
      List<BufferedImage> flames = ImageUtil.imagesForPath(powerMode().flameImageFolder());
      flameImagesCache.put(cacheKey, flames);
      return flames.get(0);
    }
    return null;
  }

  @Override
  public boolean update(double delta) {
    if (alive()) {
      i += 1;
      x = _x - (int) (0.5 * _width * lifeFactor());
      if (up) {
        y = _y - (int) (1.1 * _height * lifeFactor());
      } else {
        y = _y + (int) (0.25 * _height * lifeFactor());
      }
      width = (int) (_width * lifeFactor());
      height = (int) (_height * lifeFactor());
    }
    return !alive();
  }

  @Override
  public void render(Graphics g, int dxx, int dyy) {
    if (alive() && flameImagesCache.get(cacheKey) != null) {
      int flameImagesCount = flameImagesCache.get(cacheKey).size();
      Graphics2D g2d = (Graphics2D) g.create();
      g2d.setComposite(
        AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
          Util.alpha(0.9f * (1 - lifeFactor()))));
      if (up) {
        g2d.drawImage(flameImagesCache.get(cacheKey).get(i % flameImagesCount), (int) x + dxx, (int) y + dyy, width, height, null);
      } else {
        // flip horizontally
        g2d.drawImage(flameImagesCache.get(cacheKey).get(i % flameImagesCount), (int) x + dxx, (int) (y + dyy + height), width, -height, null);
      }

      g2d.dispose();
    }
  }

  @Override
  public long life() {
    return this.life;
  }

  @Override
  public long initLife() {
    return this.initLife;
  }

}
