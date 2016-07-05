package de.ax.powermode.power.color;

import de.ax.powermode.PowerMode;

/**
 * Created by nyxos on 05.07.16.
 */
public class ColorViewController {
    private final PowerMode powerMode;
    double c = 0;
    double f = 60.0;
    //    double x = (255 / 5000.0) * f;
    int dir = 1;

    public double genX() {
        return dir * ((powerMode.getBlueTo() - powerMode.getBlueFrom()) / 5000.0) * f;
    }

    public ColorViewController(MultiGradientPanel colorView, PowerMode powerMode) {
        this.powerMode = powerMode;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (colorView.isVisible()) {
                    long t0 = System.currentTimeMillis();
                    c = Math.max(Math.min((c + genX()), powerMode.blueTo()), powerMode.blueFrom());
                    if (c >= powerMode.blueTo()) {
                        c = powerMode.blueTo();
                        dir *= -1;
                    }
                    if (c <= powerMode.blueFrom()) {
                        c = powerMode.blueFrom();
                        dir *= -1;
                    }
                    colorView.doUpdate(c);
                    colorView.repaint();
                    try {
                        Thread.sleep((long) (Math.max(0, (1000 / f) - (System.currentTimeMillis() - t0))));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
}
