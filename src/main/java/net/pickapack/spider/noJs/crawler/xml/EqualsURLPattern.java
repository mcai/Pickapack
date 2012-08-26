package net.pickapack.spider.noJs.crawler.xml;

public class EqualsURLPattern extends URLPattern {
    private String url;

    public EqualsURLPattern(String id, String url) {
        super(id);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean match(String url) {
        return url.equals(this.url);
    }
}
