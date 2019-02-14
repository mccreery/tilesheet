package tilesheet;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import tilesheet.ConversionContext.Majority;

public class ArgsHandler {
    private final Options options = new Options();
    private final CommandLine commandLine;
    private final List<Path> imageFiles;
    private final ConversionContext context;

    public ArgsHandler(String[] args) {
        options.addOption("h", "help", false, "print this message");
        options.addOption(null, "gui", false, "open a gui");
        options.addOption("w", "tile-width", true, "set the tile width");
        options.addOption("h", "tile-height", true, "set the tile height");
        options.addOption("u", "col-spacing", true, "set the horizontal spacing");
        options.addOption("v", "row-spacing", true, "set the vertical spacing");
        options.addOption(null, "col-major", false, "set the output to column-major order");

        CommandLineParser parser = new DefaultParser();
        try {
            commandLine = parser.parse(options, args);
        } catch(ParseException e) {
            printHelp();
            throw new RuntimeException(e);
        }

        imageFiles = commandLine.getArgList().stream()
            .map(Paths::get).collect(Collectors.toList());

        context = new ConversionContext();
        context.tileSize = new Point(
            Integer.parseUnsignedInt(commandLine.getOptionValue("w", "8")),
            Integer.parseUnsignedInt(commandLine.getOptionValue("h", "8"))
        );
        context.tileSpacing = new Point(
            Integer.parseUnsignedInt(commandLine.getOptionValue("u", "0")),
            Integer.parseUnsignedInt(commandLine.getOptionValue("v", "0"))
        );

        context.targetMemoryOrder = commandLine.hasOption("col-major") ? Majority.COLUMN : Majority.ROW;
    }

    public boolean isHelpCommand() {
        return commandLine.hasOption("help");
    }

    public boolean shouldShowGui() {
        boolean startedFromGui = System.console() == null && !GraphicsEnvironment.isHeadless();
        return startedFromGui || commandLine.hasOption("gui");
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        formatter.printHelp(getUsage(), options);
    }

    public String getUsage() {
        String jarName = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
        return String.format("java -jar %s [OPTION]... [FILE]...", jarName);
    }

    public List<Path> getImageFiles() {
        return Collections.unmodifiableList(imageFiles);
    }

    public ConversionContext getContext() {
        return context;
    }
}
