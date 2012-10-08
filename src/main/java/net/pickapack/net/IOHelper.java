package net.pickapack.net;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Min Cai
 */
public class IOHelper {
    /**
     *
     * @param path
     */
    public static void extractResource(String path) {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            IOUtils.copy(IOHelper.class.getResourceAsStream("/" + path), new FileOutputStream(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
