/*******************************************************************************
 * Copyright (c) 2010-2012 by Min Cai (min.cai.china@gmail.com).
 *
 * This file is part of the PickaPack library.
 *
 * PickaPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PickaPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PickaPack. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.pickapack.io.serialization;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Min Cai
 */
public class MapHelper {
    /**
     *
     * @param map
     * @param fileName
     */
    public static void load(Map<String, String> map, String fileName) {
        if (new File(fileName).exists()) {
            try {
                load(map, new FileInputStream(fileName));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     *
     * @param map
     * @param in
     */
    public static void load(Map<String, String> map, InputStream in) {
        try {
            Properties prop = new Properties();

            prop.load(in);

            for (Object key : prop.keySet()) {
                map.put(key.toString(), prop.get(key).toString());
            }

            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param map
     * @param fileName
     */
    public static void save(Map<String, String> map, String fileName) {
        try {
            save(map, new FileOutputStream(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param map
     * @param out
     */
    public static void save(Map<String, String> map, OutputStream out) {
        if (map.size() > 0) {
            PrintWriter pw = new PrintWriter(out);

            for (Map.Entry<String, String> entry : map.entrySet()) {
                pw.println(entry.getKey() + "=" + entry.getValue());
            }

            pw.close();
        }
    }
}
