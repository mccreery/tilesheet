package tilesheet;

import java.awt.Point;
import static tilesheet.ConversionContext.Majority.*;

public class ConversionContext {
    public Point tileSize = new Point(8, 8);
    public Point tileSpacing = new Point();
    public Majority tileOrder = ROW;
    public Majority targetMemoryOrder = ROW;

    public enum Majority {
        ROW("row-major"), COLUMN("column-major");

        private final String name;
        private Majority(String name) { this.name = name; }

        @Override
        public String toString() {
            return name;
        }
    }

    public String getDetails() {
        return String.format("tile size %dx%d%ntile spacing %dx%d%ntile order %s%ntarget memory order %s",
            tileSize.x, tileSize.y, tileSpacing.x, tileSpacing.y,
            tileOrder.toString().toLowerCase(), targetMemoryOrder.toString().toLowerCase());
    }
}
