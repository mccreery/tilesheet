package tilesheet;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import tilesheet.ArgsHandler;

public class Main {
    public static void main(String[] args) {
        ArgsHandler argsHandler = new ArgsHandler(args);

        if(argsHandler.shouldShowGui()) {
            JOptionPane.showMessageDialog(null, "gui");
        } else if(argsHandler.isHelpCommand()) {
            argsHandler.printHelp();
        } else if(argsHandler.getImageFiles().isEmpty()) {
            System.err.println("error: no images specified");
            argsHandler.printHelp();
        } else {
            System.out.println(argsHandler.getContext().getDetails());
            System.out.println();

            for(Path path : argsHandler.getImageFiles()) {
                if(tryConvertImage(path, path.resolveSibling("converted-" + path.getFileName()), argsHandler.getContext())) {
                    System.out.println(path + " successfully converted");
                }
            }
        }
    }

    /**
     * @return {@code true} if the image was successfully converted.
     */
    private static boolean tryConvertImage(Path inPath, Path outPath, ConversionContext context) {
        BufferedImage image;
        try {
            image = ImageIO.read(inPath.toFile());
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }

        BufferedImage stitched = TilesheetConverter.stitch(context, image);
        try {
            ImageIO.write(stitched, "png", outPath.toFile());
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
