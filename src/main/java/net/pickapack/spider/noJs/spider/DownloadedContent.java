package net.pickapack.spider.noJs.spider;

import org.apache.commons.lang.ArrayUtils;

import java.io.*;

public interface DownloadedContent extends Serializable {
    static class InMemory implements DownloadedContent {
        private final byte[] bytes_;
        public InMemory(final byte[] byteArray) {
            if (byteArray == null) {
                bytes_ = ArrayUtils.EMPTY_BYTE_ARRAY;
            }
            else {
                bytes_ = byteArray;
            }
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(bytes_);
        }
    }

    static class OnFile implements DownloadedContent {
        private final File file_;
        public OnFile(final File file) {
            file_ = file;
        }

        public InputStream getInputStream() throws FileNotFoundException {
            return new FileInputStream(file_);
        }
    }

    InputStream getInputStream() throws IOException;
}