package de.ax.powermode.power.color;

import javax.swing.*;

/**
 * Created by nyxos on 21.06.16.
 */
public class UiTest {
    private JPanel colorView;
    private JPanel root;


    static double c = 0;
    static double f = 60.0;
    static double x = (255 / 5000.0) * f;

    public static void main(String[] args) {
        JFrame frame = new JFrame("UiTest");
        UiTest uiTest = new UiTest();
        frame.setContentPane(uiTest.root);
        frame.setSize(500, 500);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


    }

    private void createUIComponents() {
//        colorView = new MultiGradientPanel(500, new ColorEdges());
//        new ColorViewController((MultiGradientPanel) colorView);
    }

}
