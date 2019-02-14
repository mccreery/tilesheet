package tilesheet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class GuiFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final TilesheetViewer viewer;
    private final RightPanel rightPanel;

    public GuiFrame() {
        viewer = new TilesheetViewer();
        viewer.setPreferredSize(new Dimension(500, 500));
        add(viewer, BorderLayout.CENTER);

        rightPanel = new RightPanel();
        rightPanel.setPreferredSize(new Dimension(300, 0));
        add(rightPanel, BorderLayout.LINE_END);

        setTitle("Tilesheet Converter");
        setLocationByPlatform(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }

    public void setImage(BufferedImage image) {
        viewer.setImage(image);
    }

    public void setContext(ConversionContext context) {
        viewer.setContext(context);
        rightPanel.setContext(context);
    }
}
