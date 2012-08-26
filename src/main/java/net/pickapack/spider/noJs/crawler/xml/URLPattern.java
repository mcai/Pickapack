package net.pickapack.spider.noJs.crawler.xml;

public abstract class URLPattern {
    private String id;

    URLPattern(String id) {
        this.id = id;
    }

    public abstract boolean match(String url);

    public String getId() {
        return id;
    }
}
