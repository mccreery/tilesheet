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
        Rectangle tileRect = new Rectangle(0, 0, context.tileSize.x, context.tileSize.y);
        List<BufferedImage> tiles = new ImageTileList(image, context);

        BufferedImage out;
        if(context.targetMemoryOrder == ROW) {
            out = new BufferedImage(tileRect.width, tileRect.height * tiles.size(), context.colorType);
        } else {
            out = new BufferedImage(tileRect.width * tiles.size(), tileRect.height, context.colorType);
        }

        Graphics2D graphics = (Graphics2D)out.getGraphics();
        AffineTransformOp op = getTransposeOp(tileRect.width, tileRect.height);

        for(BufferedImage tile : tiles) {
            if(context.targetMemoryOrder == ROW) {
                graphics.drawImage(tile, tileRect.x, tileRect.y, null);
                tileRect.translate(0, tileRect.height);
            } else {
                graphics.drawImage(tile, op, tileRect.x, tileRect.y);
                tileRect.translate(tileRect.width, 0);
            }
        }
        return out;
    }

    private static AffineTransformOp getTransposeOp(int width, int height) {
        AffineTransform transform = new AffineTransform();
        transform.concatenate(AffineTransform.getTranslateInstance(width, height));
        transform.concatenate(AffineTransform.getScaleInstance(-1, -1));

        return new AffineTransformOp(transform, null);
    }
}
