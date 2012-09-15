package net.pickapack.notice.helper.crawler;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TianyaNoJSCrawler extends NoJSCrawler {
    private String folderImages;

    public TianyaNoJSCrawler(String userAgent, String folderImages) {
        this(userAgent, null, -1, folderImages);
    }

    public TianyaNoJSCrawler(String userAgent, String proxyHost, int proxyPort, String folderImages) {
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

    @SuppressWarnings("unchecked")
    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());

        String pageUrl = page.getUrl().toString();

        if (pageUrl.startsWith("http://www.tianya.cn/publicforum/articleslist")) {
            this.visitLinks(page, "//a[starts-with(@href,'http://www.tianya.cn/publicforum/content/tianyamyself/')][@target='_blank']", null, new Function1<Node, String>() {
                @Override
                public String apply(Node param) {
                    return param.getAttributes().getNamedItem("href").getNodeValue();
                }
            });
            this.visitLinks(page, "//td[@align='right']//a[starts-with(@href,'http://www.tianya.cn/new/publicforum/articleslist')][contains(.,'下一页')]", null, new Function1<Node, String>() {
                @Override
                public String apply(Node param) {
                    return param.getAttributes().getNamedItem("href").getNodeValue();
                }
            });
        } else if (pageUrl.startsWith("http://www.tianya.cn/publicforum/content")) {
            List<Node> images = page.getByXPath("//img[contains(@original,'.jpg')]/@original");

            List<URL> urls = new ArrayList<URL>();
            for (Node image : images) {
                try {
                    String urlStr = image.getNodeValue();
                    if (urlStr.contains(":")) {
                        urls.add(new URL(urlStr));
                    } else {
                        System.out.printf("[%s] Invalid document url found: %s\n", DateHelper.toString(new Date()), urlStr);
                    }
                } catch (MalformedURLException e) {
                    recordException(e);
                }
            }

            this.downloadDocumentByUrls(page.getUrl(), FileUtils.getUserDirectoryPath() + this.folderImages, urls);

            if (images.size() > 0) {
                this.visitLinks(page, "//div[@id='pageDivTop']//a[starts-with(@href,'http://www.tianya.cn/publicforum/content')][contains(.,'下一页')]", null, new Function1<Node, String>() {
                    @Override
                    public String apply(Node param) {
                        return param.getAttributes().getNamedItem("href").getNodeValue();
                    }
                });
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String folderImages = "/OOXXPicsSpider/Tianya";
        String url = "http://www.tianya.cn/publicforum/articleslist/0/tianyamyself.shtml";

        if (args.length == 1) {
            url = args[0];
        }

//        TianyaNoJSCrawler crawler = new TianyaNoJSCrawler(NoJSSpider.FIREFOX_3_6, "10.26.27.29", 3128, folderImages);
        TianyaNoJSCrawler crawler = new TianyaNoJSCrawler(NoJSSpider.FIREFOX_3_6, folderImages);
        crawler.run(url);
        crawler.close();
    }
}
