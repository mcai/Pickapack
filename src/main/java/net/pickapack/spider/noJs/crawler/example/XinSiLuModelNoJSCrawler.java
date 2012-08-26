package net.pickapack.spider.noJs.crawler.example;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.action.Function1;
import net.pickapack.spider.noJs.crawler.NoJSCrawler;
import net.pickapack.spider.noJs.spider.NoJSSpider;
import net.pickapack.spider.noJs.spider.Page;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class XinSiLuModelNoJSCrawler extends NoJSCrawler {
    private String folderImages;

    public XinSiLuModelNoJSCrawler(String userAgent, String folderImages) {
        this(userAgent, null, -1, folderImages);
    }

    public XinSiLuModelNoJSCrawler(String userAgent, String proxyHost, int proxyPort, String folderImages) {
        super(userAgent, 60000, proxyHost, proxyPort);
        this.folderImages = folderImages;
    }

    @SuppressWarnings("unchecked")
    public void run(String url) {
        try {
            this.visit(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private Random random = new Random();

    @SuppressWarnings("unchecked")
    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());

        String pageUrl = page.getUrl().toString();

        if (pageUrl.startsWith("http://bbs.xinsilu.com/forum")) {
            this.visitLinks(page, "//tbody[starts-with(@id,'normalthread')]//td[@class='icn']//a", null, new Function1<Node, String>() {
                @Override
                public String apply(Node param) {
                    return "http://bbs.xinsilu.com/" + param.getAttributes().getNamedItem("href").getNodeValue();
                }
            });
            this.visitLinks(page, "//div[@class='pg']//a[contains(.,'下一页')]", null, new Function1<Node, String>() {
                @Override
                public String apply(Node param) {
                    return "http://bbs.xinsilu.com/" + param.getAttributes().getNamedItem("href").getNodeValue();
                }
            });
        } else if (pageUrl.startsWith("http://bbs.xinsilu.com/thread")) {
            List<Node> subjectContentUrlAttributes1 = page.getByXPath("//img[starts-with(@zoomfile,'data/attachment')]/@file");
            List<Node> subjectContentUrlAttributes2 = page.getByXPath("//img[starts-with(@src,'attachments/dvbbs')]/@src");

            List<URL> urls = new ArrayList<URL>();
            for (Node aSubjectContentUrlAttributes1 : subjectContentUrlAttributes1) {
                try {
                    String urlStr = "http://bbs.xinsilu.com/" + aSubjectContentUrlAttributes1.getNodeValue();
                    if (urlStr.contains(":")) {
                        urls.add(new URL(urlStr));
                    } else {
                        System.out.printf("[%s] Invalid document url found: %s\n", DateHelper.toString(new Date()), urlStr);
                    }
                } catch (MalformedURLException e) {
                    recordException(e);
                }
            }
            for (Node aSubjectContentUrlAttributes2 : subjectContentUrlAttributes2) {
                try {
                    String urlStr = "http://bbs.xinsilu.com/" + aSubjectContentUrlAttributes2.getNodeValue();
                    if (urlStr.contains(":")) {
                        urls.add(new URL(urlStr));
                    } else {
                        System.out.printf("[%s] Invalid document url found: %s\n", DateHelper.toString(new Date()), urlStr);
                    }
                } catch (MalformedURLException e) {
                    recordException(e);
                }
            }

            this.downloadDocumentByUrls(null, FileUtils.getUserDirectoryPath() + this.folderImages, urls);
        }
    }

    public static void main(String[] args) throws IOException {
        String folderImages = "/OOXXPicsSpider/XinSiLu";
        String url = "http://bbs.xinsilu.com/forum-76-1.html";

        if (args.length == 1) {
            url = args[0];
        }

//        XinSiLuModelNoJSCrawler crawler = new XinSiLuModelNoJSCrawler(NoJSSpider.FIREFOX_3_6, "10.26.27.29", 3128, folderImages);
        XinSiLuModelNoJSCrawler crawler = new XinSiLuModelNoJSCrawler(NoJSSpider.FIREFOX_3_6, folderImages);
        crawler.run(url);
        crawler.close();
    }
}
