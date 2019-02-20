package tilesheet;

import static tilesheet.ConversionContext.Majority.*;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.List;

public class TilesheetConverter {
    public static BufferedImage stitch(ConversionContext context, BufferedImage image) {
        Rectangle tileRect;
        if(context.targetMemoryOrder == ROW) {
            tileRect = new Rectangle(0, 0, context.tileSize.x, context.tileSize.y);
        } else {
            tileRect = new Rectangle(0, 0, context.tileSize.y, context.tileSize.x);
        }
        List<Rectangle> tiles = new ImageTileList(image, context);

        BufferedImage out;
        out = new BufferedImage(tileRect.width, tileRect.height * tiles.size(), context.colorType.colorType);

        Graphics2D graphics = (Graphics2D)out.getGraphics();
        AffineTransformOp op = getTransposeOp();

        for(Rectangle tileSourceRect : tiles) {
            BufferedImage tile = image.getSubimage(tileSourceRect.x, tileSourceRect.y, tileSourceRect.width, tileSourceRect.height);

            if(context.targetMemoryOrder == ROW) {
                graphics.drawImage(tile, tileRect.x, tileRect.y, null);
            } else {
                graphics.drawImage(tile, op, tileRect.x, tileRect.y);
            }
            tileRect.translate(0, tileRect.height);
        }
        return out;
    }

    private static AffineTransformOp getTransposeOp() {
        AffineTransform transform = new AffineTransform(0, 1, 1, 0, 0, 0);
        return new AffineTransformOp(transform, null);
    }
}
