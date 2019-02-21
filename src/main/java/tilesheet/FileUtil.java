package tilesheet;

import java.io.File;

public class FileUtil {
    /**
     * Strips all but the last subpath and removes the extension.
     * @return The filename without any folder or extension.
     */
    public static String filenameNoExt(String filename) {
        int slash = filename.lastIndexOf(File.separatorChar);
        int dot = filename.lastIndexOf('.');

        if(dot <= slash) dot = filename.length();

        return filename.substring(slash + 1, dot);
    }

    /**
     * Strips the last extension.
     * @return The file path without its last extension, if any.
     */
    public static String filePathNoExt(String filePath) {
        int slash = filePath.lastIndexOf(File.separatorChar);
        int dot = filePath.lastIndexOf('.');

        return dot <= slash ? filePath : filePath.substring(0, dot);
    }
}
