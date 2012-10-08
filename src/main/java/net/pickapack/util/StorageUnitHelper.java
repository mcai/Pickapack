package net.pickapack.util;

import org.apache.commons.io.FileUtils;

/**
 *
 * @author Min Cai
 */
public class StorageUnitHelper {
    /**
     *
     * @param displaySize
     * @return
     */
    public static long displaySizeToByteCount(String displaySize) {
        String[] parts = displaySize.split(" ");
        if (parts.length == 2) {
            double scale = Double.parseDouble(parts[0]);
            String unit = parts[1];

            if (unit.equals("KB")) {
                return (long) (scale * FileUtils.ONE_KB);
            } else if (unit.equals("MB")) {
                return (long) (scale * FileUtils.ONE_MB);
            } else if (unit.equals("GB")) {
                return (long) (scale * FileUtils.ONE_GB);
            } else if (unit.equals("TB")) {
                return (long) (scale * FileUtils.ONE_TB);
            } else if (unit.equals("PB")) {
                return (long) (scale * FileUtils.ONE_PB);
            } else if (unit.equals("EB")) {
                return (long) (scale * FileUtils.ONE_EB);
            }
        }

        throw new IllegalArgumentException();
    }
}
