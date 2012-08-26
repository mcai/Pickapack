package net.pickapack.spider.withJs.crawler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.spider.withJs.WebSpider;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;

public abstract class WebCrawler extends WebSpider {
    private List<String> hashedDocumentUrls;
    private List<String> hashedPageUrls;
    protected WebCrawler(BrowserVersion browserVersion) {
        super(browserVersion);

        this.hashedDocumentUrls = new ArrayList<String>();
        this.hashedPageUrls = new ArrayList<String>();
    }

    public void visit(String url) {
        try {
            visitPage(null, this.getWebClient().getPage(url), null);
        } catch (IOException e) {
            recordException(e);
        } catch (Exception e) {
            recordException(e);
        }

        this.getWebClient().closeAllWindows();
    }

    @SuppressWarnings("unchecked")
    protected void visitLinks(HtmlPage page1, String linksXPath, Map<String, Object> context, boolean byHref) {
        List<HtmlAnchor> links = (List<HtmlAnchor>) page1.getByXPath(linksXPath);

        for (HtmlAnchor link : links) {
            visitLink(page1, link, context, byHref);
        }
    }

    @SuppressWarnings("unchecked")
    protected void visitFirstLink(HtmlPage page1, String linksXPath, Map<String, Object> context, boolean byHref) {
        HtmlAnchor link = page1.getFirstByXPath(linksXPath);
        visitLink(page1, link, context, byHref);
    }

    protected void visitLink(HtmlPage page1, HtmlAnchor link, Map<String, Object> context, boolean byHref) {
        try {
            if(byHref) {
                visitPage(page1, this.getWebClient().getPage(link.getHrefAttribute()), context);
            }
            else {
                visitPage(page1, link.<HtmlPage>click(), context);
            }
        } catch (IOException e) {
            recordException(e);
        }
    }

    protected void removePageByUrl(String url) {
        String hashedUrl = DigestUtils.md5Hex(url);
        if (hashedPageUrls.contains(hashedUrl)) {
            hashedPageUrls.remove(hashedUrl);
        }
    }

    protected void visitPage(Page page1, Page page2, Map<String, Object> context) {
        String url = page2.getUrl().toString();
        String hashedUrl = DigestUtils.md5Hex(url);

        if (!hashedPageUrls.contains(hashedUrl)) {
            hashedPageUrls.add(hashedUrl);
            onPageVisited(page2, context);
        }
        if (page1 == null || getWebClient().getCurrentWindow().getTopWindow() != page1.getEnclosingWindow().getTopWindow()) {
            ((TopLevelWindow) getWebClient().getCurrentWindow().getTopWindow()).close();
        }
    }

    @SuppressWarnings("unchecked")
    protected void downloadImages(List<HtmlImage> images, final String storageFolder, final String imageUrlXPath) {
//        ExecutorService downloadDocumentsService = Executors.newFixedThreadPool(5);

        for (final HtmlImage image : images) {
//            downloadDocumentsService.submit(new Runnable() {
//                @Override
//                public void run() {
                    String url = image.<DomAttr>getFirstByXPath(imageUrlXPath).getValue();
                    String hashedUrl = DigestUtils.md5Hex(url);
                    if (!hashedDocumentUrls.contains(hashedUrl) && url.contains(".")) {
                        hashedDocumentUrls.add(hashedUrl);

                        final File localFile = new File(storageFolder, hashedUrl + url.substring(url.lastIndexOf('.'), url.length()));

                        downloadImage(image, localFile);
                    }  else {
                        System.out.printf("[%s] Duplicate image found: %s\n", DateHelper.toString(new Date()), url);
                    }
//                }
//            });
        }

//        downloadDocumentsService.shutdown();
//        try {
//            downloadDocumentsService.awaitTermination(images.size() * 30, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    @SuppressWarnings("unchecked")
    protected void downloadDocumentByUrls(List<String> urls, final String storageFolder) {
//        ExecutorService downloadDocumentsService = Executors.newFixedThreadPool(5);

        for (final String url : urls) {
//            downloadDocumentsService.submit(new Runnable() {
//                @Override
//                public void run() {
                    String hashedUrl = DigestUtils.md5Hex(url);
                    if (!hashedDocumentUrls.contains(hashedUrl) && url.contains(".")) {
                        hashedDocumentUrls.add(hashedUrl);

                        final File localFile = new File(storageFolder, hashedUrl + url.substring(url.lastIndexOf('.'), url.length()));
                        downloadDocument(url, localFile);
                    }  else {
                        System.out.printf("[%s] Duplicate image found: %s\n", DateHelper.toString(new Date()), url);
                    }
//                }
//            });
        }

//        downloadDocumentsService.shutdown();
//        try {
//            downloadDocumentsService.awaitTermination(urls.size() * 30, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    protected abstract void onPageVisited(Page page, Map<String, Object> context);
}
