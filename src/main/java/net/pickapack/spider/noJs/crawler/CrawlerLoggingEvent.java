package net.pickapack.spider.noJs.crawler;

public class CrawlerLoggingEvent extends CrawlerEvent {
    private String message;

    public CrawlerLoggingEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
