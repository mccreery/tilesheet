package tilesheet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
                chooser.addChoosableFileFilter(new FileNameExtensionFilter("C source code", "c"));
                chooser.addChoosableFileFilter(new FileNameExtensionFilter("AVR assembly source code", "asm"));

                chooser.setAcceptAllFileFilterUsed(false);

                if(chooser.showSaveDialog(GuiFrame.this) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    String ext;

                    FileNameExtensionFilter filter = (FileNameExtensionFilter)chooser.getFileFilter();
                    if(!filter.accept(file)) {
                        ext = filter.getExtensions()[0];
                        file = new File(file.getAbsolutePath() + '.' + ext);
                    } else {
                        ext = getExtension(file);
                    }

                    String cleanName = GuiFrame.this.file.getName().replaceAll("[^a-zA-Z]", "");
                    BufferedImage stitched = TilesheetConverter.stitch(context, image);

                    try {
                        if(ext.equals("png")) {
                            ImageIO.write(stitched, "png", file);
                        } else {
                            byte[] data = getRawBytes(stitched.getRaster().getDataBuffer(), ByteOrder.BIG_ENDIAN);
                            String source = ext.equals("asm") ? HexUtil.getAvrDb(cleanName, data, 16) : HexUtil.getCArray(cleanName, data, 16);

                            try(PrintWriter writer = new PrintWriter(file)) {
                                writer.write(source);
                            }
                        }
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

    /**
     * Gets the file extension from a file, if any, without the dot.
     * If a file has no extension this returns an empty string.
     */
    private static String getExtension(File file) {
        String name = file.getName();
        int i = name.lastIndexOf('.');
        return name.substring(i == -1 ? name.length() : i+1);
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

    private static byte[] getRawBytes(DataBuffer buffer, ByteOrder order) {
        if(buffer instanceof DataBufferByte) {
            return ((DataBufferByte)buffer).getData();
        } else if(buffer instanceof DataBufferUShort) {
            return shortsToBytes(((DataBufferUShort)buffer).getData(), order);
        } else if(buffer instanceof DataBufferShort) {
            return shortsToBytes(((DataBufferUShort)buffer).getData(), order);
        } else if(buffer instanceof DataBufferInt) {
            return intsToBytes(((DataBufferInt)buffer).getData(), order);
        } else {
            throw new IllegalArgumentException("Invalid data buffer type " + buffer.getClass());
        }
    }

    private static byte[] shortsToBytes(short[] shorts, ByteOrder order) {
        byte[] bytes = new byte[shorts.length * 2];

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.order(order);
        byteBuffer.asShortBuffer().put(shorts);
        byteBuffer.flip();

        return bytes;
    }

    private static byte[] intsToBytes(int[] ints, ByteOrder order) {
        byte[] bytes = new byte[ints.length * 4];

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.order(order);
        byteBuffer.asIntBuffer().put(ints);
        byteBuffer.flip();

        return bytes;
    }
}
