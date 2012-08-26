package net.pickapack.spider.noJs.crawler.media;

import net.pickapack.event.BlockingEvent;

public class MediaFileBeginDownloadingEvent implements BlockingEvent {
    private String url;
    private String localFileName;

    public MediaFileBeginDownloadingEvent(String url, String localFileName) {
        this.url = url;
        this.localFileName = localFileName;
    }

    public String getUrl() {
        return url;
    }

    public String getLocalFileName() {
        return localFileName;
    }
}
