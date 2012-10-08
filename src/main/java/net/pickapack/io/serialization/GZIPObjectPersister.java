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
