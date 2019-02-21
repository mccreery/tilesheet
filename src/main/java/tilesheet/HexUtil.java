package tilesheet;

import java.io.File;

public class HexUtil {
    /*
     * Argument 1: clean filename (no extension)
     * Argument 2: number of characters
     * Argument 3: bits per pixel
     * Argument 4: tile width
     * Argument 5: tile height
     * Argument 6: column major (true/false)
     */
    private static final String C_HEADER_TEMPLATE =
        "/*%n" +
        " * This is an automatically generated file.%n" +
        " */%n" +
        "#ifndef %1$S_H_%n" +
        "#define %1$S_H_%n" +
        "%n" +
        "#include <stdint.h>%n" +
        "#include <stdbool.h>%n" +
        "%n" +
        "#define %1$S_NUM_CHARS   %2$d%n" +
        "#define %1$S_BPP         %3$d%n" +
        "#define %1$S_TILE_WIDTH  %4$d%n" +
        "#define %1$S_TILE_HEIGHT %5$d%n" +
        "#define %1$S_COLUMN_MAJOR %6$b%n" +
        "%n" +
        "extern const uint8_t %1$S[];%n" +
        "%n" +
        "#endif%n";

    /*
     * Argument 1: raw header filename
     * Argument 2: clean filename (no extension)
     * Argument 3: byte array
     */
    private static final String C_SOURCE_TEMPLATE =
        "#include \"%1$s\"%n" +
        "%n" +
        "const uint8_t %2$S[] = {%n" +
        "    %3$s%n" +
        "};%n";

    public static String getCHeader(File file, int numChars, int bpp, int tileWidth, int tileHeight, boolean columnMajor) {
        String cleanFilename = FileUtil.filenameNoExt(file.getPath()).replaceAll("[^a-zA-Z]", "");
        return String.format(C_HEADER_TEMPLATE, cleanFilename, numChars, bpp, tileWidth, tileHeight, columnMajor);
    }

    public static String getCSource(File headerFile, byte[] bytes) {
        String filename = headerFile.getName();
        String cleanFilename = FileUtil.filenameNoExt(filename).replaceAll("[^a-zA-Z]", "");
        String byteArray = getByteArray(bytes, ", ", String.format(",%n    "), 8);

        return String.format(C_SOURCE_TEMPLATE, filename, cleanFilename, byteArray);
    }

    /**
     * Generates a block of AVR assembly source code for a .db directive
     * containing the given bytes.
     * @return AVR assembly source code for the given bytes.
     */
    public static String getAvrDb(String labelName, byte[] bytes, int perRow) {
        StringBuilder builder = new StringBuilder(bytes.length * 6 + 32);
        builder.append(labelName).append(':').append(System.lineSeparator()).append(".db");

        for(int i = 0; i < bytes.length; i++) {
            if(i != 0 && i % perRow == 0) {
                builder.append(" \\").append(System.lineSeparator()).append("   ");
            }
            builder.append(" 0x");
            appendHexByte(builder, bytes[i]);

            if(i < bytes.length - 1) builder.append(',');
        }
        return builder.toString();
    }

    /**
     * Appends 2 characters to a string builder representing a byte in hexidecimal.
     * @param builder The string builder.
     * @param x The byte.
     */
    public static void appendHexByte(StringBuilder builder, byte x) {
        builder.append(getHexDigit((byte)(x >> 4))).append(getHexDigit(x));
    }

    /**
     * Gets the low hexidecimal digit of a byte.
     * @param x The input byte.
     * @return The hexidecimal digit corresponding to the lower 4 bits of {@code x}.
     */
    public static char getHexDigit(byte x) {
        x &= (byte)0x0f;
        if(x > 9) {
            return (char)('A' + (x - 10));
        } else {
            return (char)('0' + x);
        }
    }

    /**
     * Generates a separated hexidecimal representation of a byte array,
     * written in the format 0x00 - 0xFF.
     *
     * @param bytes The array to convert.
     * @param valueSep The separator between each value.
     * @param perRow The separator between each row.
     * @return A separated hexidecimal representation of a byte array.
     */
    private static final String getByteArray(byte[] bytes, String valueSep, String rowSep, int perRow) {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < bytes.length; i++) {
            builder.append("0x");
            appendHexByte(builder, bytes[i]);

            if(i < bytes.length - 1) {
                if(i % perRow == perRow - 1) {
                    builder.append(rowSep);
                } else {
                    builder.append(valueSep);
                }
            }
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println("cum");
    }
}
