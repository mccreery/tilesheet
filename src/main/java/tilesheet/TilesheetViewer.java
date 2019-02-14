package tilesheet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class TilesheetViewer extends JPanel {
    private static final long serialVersionUID = 1L;

    private BufferedImage image;
    private ConversionContext context;

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    public void setContext(ConversionContext context) {
        this.context = context;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if(image == null || context == null) return;

        Rectangle imageBounds = new Rectangle(image.getWidth(), image.getHeight());
        Graphics graphicsCopy = g.create();

        if(graphicsCopy instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D)graphicsCopy;
            g2d.transform(mapSpace(imageBounds, letterBox(imageBounds, g.getClipBounds())));

            paintImageAndTiles(g2d);
        }
    }

    private void paintImageAndTiles(Graphics2D g) {
        g.drawImage(image, null, 0, 0);
        g.setColor(Color.RED);

        for(Rectangle rectangle : new ImageTileList(image, context)) {
            g.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
    }

    /**
     * Maps the original coordinate system onto the new coordinate system.
     * @return An affine transform for converting points in source space to dest space.
     */
    private static AffineTransform mapSpace(Rectangle source, Rectangle dest) {
        AffineTransform transform = new AffineTransform();

        // Scale around point (source.xy), then translate to point (dest.xy)
        transform.translate(dest.x, dest.y);
        transform.scale(dest.getWidth() / source.getWidth(), dest.getHeight() / source.getHeight());
        transform.translate(-source.x, -source.y);

        return transform;
    }

    /**
     * Scales the inner rectangle to fit perfectly inside the outer rectangle.
     * @return A scaled and translated rectangle.
     */
    private static Rectangle letterBox(Rectangle inner, Rectangle outer) {
        float innerAspect = inner.width / inner.height;
        float outerAspect = outer.width / outer.height;

        if(innerAspect > outerAspect) {
            // top/bottom bars ('tall')
            int height = (int)Math.round(outer.getWidth() / innerAspect);
            return new Rectangle(outer.x, outer.y + (outer.height - height) / 2, outer.width, height);
        } else {
            // left/right bars ('wide')
            int width = (int)Math.round(outer.getHeight() * innerAspect);
            return new Rectangle(outer.x + (outer.width - width) / 2, outer.y, width, outer.height);
        }
    }
}