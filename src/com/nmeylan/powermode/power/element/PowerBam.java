package com.nmeylan.powermode.power.element;

import com.nmeylan.powermode.ImageUtil;
import com.nmeylan.powermode.Util;
import com.nmeylan.powermode.power.ElementOfPower;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PowerBam implements ElementOfPower {

  private static Map<String, Image> bamImages = new HashMap<>();
  private float x;
  private float _x;
  private float y;
  private float _y;
  private int width;
  private int _width;
  private int height;
  private int _height;
  private long initLife;
  private long life;
  private Image currentImage;

  public PowerBam(float _x, float _y, int _width, int _height, long initLife) {
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
    this.currentImage = findBamImage();
  }

  private Image findBamImage() {
    Image bamImage = bamImages.get(powerMode().bamImageFolder().get().getAbsolutePath());
    if (bamImage != null) {
      return bamImage;
    }
    File bamImageFolder = powerMode().bamImageFolder().orElse(null);
    if (bamImageFolder != null) {
      bamImages.clear();
      BufferedImage bam = ImageUtil.imagesForPath(powerMode().bamImageFolder()).get(0);
      bamImages.put(powerMode().bamImageFolder().get().getAbsolutePath(), bam);
      return bam;
    }
    return null;
  }


  @Override
  public boolean update(double delta) {
    if (alive()) {
      x = (float) (_x + (0.5 * _width) - (0.5 * _width * lifeFactor()));
      y = (float) (_y + (0.5 * _height) - (0.5 * _height * lifeFactor()));
      width = (int) (_width * lifeFactor());
      height = (int) (_height * lifeFactor());
    }
    return !alive();
  }

  @Override
  public void render(Graphics g, int dxx, int dyy) {
    if (alive() && currentImage != null) {
      Graphics2D g2d = (Graphics2D) g.create();
      g2d.setComposite(
        AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
          Util.alpha(0.9f * (1 - lifeFactor()))));
      g2d.drawImage(currentImage, (int) x + dxx, (int) y + dyy, width, height, null);
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
