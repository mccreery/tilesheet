package tilesheet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GuiFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final TilesheetViewer viewer;
    private final RightPanel rightPanel;

    private ConversionContext context;
    private BufferedImage image;
    private File file;

    public GuiFrame() {
        viewer = new TilesheetViewer();
        viewer.setPreferredSize(new Dimension(500, 500));
        add(viewer, BorderLayout.CENTER);

        rightPanel = new RightPanel();
        rightPanel.setPreferredSize(new Dimension(300, 0));
        add(rightPanel, BorderLayout.LINE_END);

        JButton loadButton = new JButton(new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(file);
                chooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "png", "gif", "bmp"));

                if(chooser.showOpenDialog(GuiFrame.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        BufferedImage image = ImageIO.read(chooser.getSelectedFile());
                        setImage(chooser.getSelectedFile(), image);
                    } catch(IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(GuiFrame.this, String.format("Could not open image.%n%s", ex.getMessage()), "Open failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        loadButton.setText("Load");
        add(loadButton, BorderLayout.PAGE_START);

        JButton saveButton = new JButton(new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(file);
                chooser.setFileFilter(new FileNameExtensionFilter("PNG image files", "png"));
                chooser.setAcceptAllFileFilterUsed(false);

                if(chooser.showSaveDialog(GuiFrame.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        ImageIO.write(TilesheetConverter.stitch(context, image), "png", chooser.getSelectedFile());
                    } catch(IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(GuiFrame.this, String.format("Could not save image.%n%s", ex.getMessage()), "Save failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        saveButton.setText("Save");
        add(saveButton, BorderLayout.PAGE_END);

        setTitle("Tilesheet Converter");
        setLocationByPlatform(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }

    public void setImage(File file, BufferedImage image) {
        this.file = file;
        this.image = image;
        viewer.setImage(image);
    }

    public void setContext(ConversionContext context) {
        this.context = context;
        viewer.setContext(context);
        rightPanel.setContext(context);
    }
}
