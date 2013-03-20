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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Min Cai
 * @param <T>
 */
public abstract class GZIPObjectPersister<T> {
    /**
     *
     * @param obj
     * @param fileName
     */
    public void serialize(T obj, String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            GZIPOutputStream gzipos = new GZIPOutputStream(fos);

            this.write(obj, gzipos);

            gzipos.finish();
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param clz
     * @param fileName
     * @return
     */
    public T deserialize(Class<? extends T> clz, String fileName) {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            GZIPInputStream gzipis = new GZIPInputStream(fis);

            T obj = this.read(clz, gzipis);

            fis.close();
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param obj
     * @param gzipos
     */
    protected abstract void write(T obj, GZIPOutputStream gzipos);

    /**
     *
     * @param clz
     * @param gzipis
     * @return
     */
    protected abstract T read(Class<? extends T> clz, GZIPInputStream gzipis);
}
