package net.pickapack.spider.noJs.crawler.xml;

public class StartsWithURLPattern extends URLPattern {
    private String prefix;

    public StartsWithURLPattern(String id, String prefix) {
        super(id);
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(this.prefix);
    }
}
