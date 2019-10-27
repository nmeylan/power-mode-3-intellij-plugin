package com.nmeylan.powermode.color;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;


public class MultiGradientPanel extends JPanel {
    int size;

    public void setColorEdges(ColorEdges colorEdges) {
        this.colorEdges = colorEdges;
    }

    private ColorEdges colorEdges;



    public MultiGradientPanel(int size, ColorEdges colorEdges) {
        super();
        this.size = size;
        this.colorEdges = colorEdges;
        this.setPreferredSize(new Dimension(size, size));
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint twoColorGradient = new GradientPaint(
                size, 0f, colorEdges.getRightTop(), 0, size, colorEdges.getLeftBottom());

        float radius = size - (size / 4);
        float[] dist = {0f, 1.0f};
        Point2D center = new Point2D.Float(0f, 0f);
        Color noColor = new Color(0f, 0f, 0f, 0f);
        Color[] colors = {colorEdges.getLeftTop(), noColor};
        RadialGradientPaint thirdColor = new RadialGradientPaint(center, radius, dist, colors);


        center = new Point2D.Float(size, size);
        Color[] colors2 = {colorEdges.getRightBottom(), noColor};
        RadialGradientPaint fourthColor = new RadialGradientPaint(center, radius, dist, colors2);

        g2d.setPaint(twoColorGradient);
        g2d.fillRect(0, 0, size, size);

        g2d.setPaint(thirdColor);
        g2d.fillRect(0, 0, size, size);

        g2d.setPaint(fourthColor);
        g2d.fillRect(0, 0, size, size);
    }

    public void doUpdate(double c) {
        int c1 = (int) c;
        colorEdges.updateColors(c1);
    }


}