package net.pickapack.spider.noJs.crawler;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.spider.noJs.spider.Page;

import java.util.Date;
import java.util.Map;

public class SimpleNoJSCrawler extends NoJSCrawler {
    public SimpleNoJSCrawler(String userAgent) {
        this(userAgent, null, -1);
    }

    public SimpleNoJSCrawler(String userAgent, String proxyHost, int proxyPort) {
        super(userAgent, 60000, proxyHost, proxyPort);
    }

    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());
    }
}
