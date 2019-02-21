package tilesheet;

import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class RightPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private ConversionContext context;
    private JSpinner tileWidth, tileHeight, tileSpacingX, tileSpacingY;
    private JComboBox<ColorType> colorType;

    public RightPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createEtchedBorder()));

        tileWidth = new JSpinner();
        tileHeight = new JSpinner();
        tileSpacingX = new JSpinner();
        tileSpacingY = new JSpinner();

        tileWidth.addChangeListener(e -> {context.tileSize.x = (int)tileWidth.getValue(); getParent().repaint();});
        tileHeight.addChangeListener(e -> {context.tileSize.y = (int)tileHeight.getValue(); getParent().repaint();});
        tileSpacingX.addChangeListener(e -> {context.tileSpacing.x = (int)tileSpacingX.getValue(); getParent().repaint();});
        tileSpacingY.addChangeListener(e -> {context.tileSpacing.y = (int)tileSpacingY.getValue(); getParent().repaint();});

        for(JSpinner spinner : new JSpinner[] {tileWidth, tileHeight, tileSpacingX, tileSpacingY}) {
            spinner.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
            spinner.setMaximumSize(spinner.getPreferredSize());
            add(spinner);
        }

        colorType = new JComboBox<>();
        colorType.setModel(new DefaultComboBoxModel<>(ColorType.values()));
        colorType.setSelectedItem(ColorType.INT_ARGB);
        colorType.setMaximumSize(colorType.getPreferredSize());

        colorType.addActionListener(e -> {
            context.colorType = (ColorType)colorType.getSelectedItem();
        });
        add(colorType);
    }

    public void setContext(ConversionContext context) {
        this.context = context;

        tileWidth.setValue(context.tileSize.x);
        tileHeight.setValue(context.tileSize.y);
        tileSpacingX.setValue(context.tileSpacing.x);
        tileSpacingY.setValue(context.tileSpacing.y);
        colorType.setSelectedItem(ColorType.INT_ARGB);
    }

    public enum ColorType {
        INT_ARGB(BufferedImage.TYPE_INT_ARGB),
        USHORT_565_RGB(BufferedImage.TYPE_USHORT_565_RGB),
        USHORT_555_RGB(BufferedImage.TYPE_USHORT_555_RGB),
        PACKED_1BPP(BufferedImage.TYPE_BYTE_BINARY);

        public final int colorType;

        private ColorType(int colorType) {
            this.colorType = colorType;
        }
    }
}
