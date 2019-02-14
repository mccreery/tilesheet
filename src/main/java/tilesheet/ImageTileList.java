package tilesheet;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.AbstractList;

import static tilesheet.ConversionContext.Majority.*;

public class ImageTileList extends AbstractList<Rectangle> {
    private final ConversionContext context;
    private final int tileCountX, tileCountY, size;

    public ImageTileList(BufferedImage image, ConversionContext context) {
        this.context = context;

        tileCountX = (image.getWidth() + context.tileSpacing.x) / context.tileSize.x;
        tileCountY = (image.getHeight() + context.tileSpacing.y) / context.tileSize.y;
        size = tileCountX * tileCountY;
    }

    @Override
    public Rectangle get(int index) {
        if(index < 0 || index >= size) throw new IndexOutOfBoundsException();

        int tileX, tileY;
        if(context.tileOrder == ROW) {
            tileX = index % tileCountX;
            tileY = index / tileCountX;
        } else {
            tileX = index / tileCountY;
            tileY = index % tileCountY;
        }

        return new Rectangle(
            tileX * (context.tileSize.x + context.tileSpacing.x),
            tileY * (context.tileSize.y + context.tileSpacing.y),
            context.tileSize.x, context.tileSize.y);
    }

    @Override
    public int size() {
        return size;
    }
}
