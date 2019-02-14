package tilesheet;

public class HexUtil {
    /**
     * Generates a constant C array of uint8_t containing the given bytes.
     * @return C source code for the given bytes.
     */
    public static String getCArray(String varName, byte[] bytes, int perRow) {
        StringBuilder builder = new StringBuilder(bytes.length * 6 + 32);
        builder.append("const uint8_t ").append(varName).append("[] = {");

        for(int i = 0; i < bytes.length; i++) {
            if(i % perRow == 0) {
                builder.append(System.lineSeparator());
                builder.append("   ");
            }
            builder.append(" 0x");
            appendHexByte(builder, bytes[i]);

            if(i < bytes.length - 1) builder.append(',');
        }
        builder.append(System.lineSeparator()).append('}');

        return builder.toString();
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
}
