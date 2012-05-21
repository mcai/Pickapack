package net.pickapack.io.serialization;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPObjectSerializer<T> {
    public void serialize(T obj, String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            GZIPOutputStream gzipos = new GZIPOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(gzipos);
            oos.writeObject(obj);
            gzipos.finish();
            oos.close();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public T deserialize(String fileName) {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(fis));
            T obj = (T) ois.readObject();

            ois.close();
            fis.close();

            return obj;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
