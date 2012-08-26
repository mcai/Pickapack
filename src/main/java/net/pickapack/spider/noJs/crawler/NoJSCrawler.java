package net.pickapack.spider.noJs.crawler;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.action.Function1;
import net.pickapack.event.BlockingEvent;
import net.pickapack.event.BlockingEventDispatcher;
import net.pickapack.spider.noJs.crawler.media.FileReactorDownloader;
import net.pickapack.spider.noJs.crawler.media.MediaFileDownloader;
import net.pickapack.spider.noJs.spider.NoJSSpider;
import net.pickapack.spider.noJs.spider.Page;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.cookie.Cookie;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class NoJSCrawler extends NoJSSpider {
    private List<String> hashedDocumentUrls;
    private List<String> hashedPageUrls;
    private ExecutorService downloadDocumentsService;
    private Map<String, MediaFileDownloader> mediaFileDownloaders;
    private BlockingEventDispatcher<BlockingEvent> eventDispatcher;
    private List<String> blacklistedMediaFileHosts;

    public NoJSCrawler(String userAgent, int timeout, String proxyHost, int proxyPort) {
        super(userAgent, proxyHost != null ? new HttpHost(proxyHost, proxyPort) : null, timeout);

        this.hashedDocumentUrls = new ArrayList<String>();
        this.hashedPageUrls = new ArrayList<String>();

        this.downloadDocumentsService = Executors.newFixedThreadPool(20);

        this.mediaFileDownloaders = new HashMap<String, MediaFileDownloader>();
//        this.mediaFileDownloaders.put(LumfileDownloader.HOST, new LumfileDownloader(this));
        this.mediaFileDownloaders.put(FileReactorDownloader.HOST, new FileReactorDownloader(this));

        this.eventDispatcher = new BlockingEventDispatcher<BlockingEvent>();

        this.blacklistedMediaFileHosts = new ArrayList<String>();
        this.blacklistedMediaFileHosts.add("www.filesonic.com");
        this.blacklistedMediaFileHosts.add("groovefile.com");
    }

    public NoJSCrawler(String userAgent, int timeout) {
        this(userAgent, timeout, null, -1);
    }

    public void visit(URL url) {
        try {
            visitPage(this.getPage(url), null);
        } catch (IOException e) {
            recordException(e);
        } catch (XPathExpressionException e) {
            recordException(e);
        } catch (TransformerException e) {
            recordException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void visitLinks(Page page1, String linksXPath, Map<String, Object> context, Function1<Node, String> getNodeUrlCallback) {
        List<Node> links = page1.getByXPath(linksXPath);
        for (Node link : links) {
            visitLink(link, context, getNodeUrlCallback);
        }
    }

    @SuppressWarnings("unchecked")
    public void visitFirstLink(Page page1, String linksXPath, Map<String, Object> context, Function1<Node, String> getNodeUrlCallback) {
        visitLink(page1.getFirstByXPath(linksXPath), context, getNodeUrlCallback);
    }

    protected void visitLink(Node link, Map<String, Object> context, Function1<Node, String> getNodeUrlCallback) {
        try {
            visitPage(this.getPage(new URL(getNodeUrlCallback.apply(link))), context);
        } catch (IOException e) {
            recordException(e);
        } catch (XPathExpressionException e) {
            recordException(e);
        } catch (TransformerException e) {
            recordException(e);
        }
    }

    protected void removePageByUrl(String url) {
        String hashedUrl = getHashedUrl(url);
        if (hashedPageUrls.contains(hashedUrl)) {
            hashedPageUrls.remove(hashedUrl);
        }
    }

    public String getHashedUrl(String url) {
        return DigestUtils.md5Hex(url);
    }

    public void visitPage(Page page, Map<String, Object> context) {
        String url = page.getUrl().toString();
        String hashedUrl = getHashedUrl(url);

        if (!hashedPageUrls.contains(hashedUrl)) {
            hashedPageUrls.add(hashedUrl);
            onPageVisited(page, context);
        }
    }

    public void downloadDocumentByUrls(URL refererUrl, final String storageFolder, URL... urls) {
        this.downloadDocumentByUrls(refererUrl, storageFolder, Arrays.asList(urls));
    }

    @SuppressWarnings("unchecked")
    public void downloadDocumentByUrls(final URL refererUrl, final String storageFolder, List<URL> urls) {
        for (final URL url : urls) {
            downloadDocumentsService.submit(new Runnable() {
                @Override
                public void run() {
                    String hashedUrl = getHashedUrl(url.toString());
                    if (!hashedDocumentUrls.contains(hashedUrl) && url.toString().contains(".")) {
                        hashedDocumentUrls.add(hashedUrl);

//                String postfix = url.toString().contains(".") ? url.toString().substring(url.toString().lastIndexOf('.'), url.toString().length()) : ".jpg";
                        File localFile = new File(storageFolder, hashedUrl + ".jpg");
                        downloadDocument(refererUrl, url, localFile);
                    } else {
                        System.out.printf("[%s] Duplicate document url found: %s\n", DateHelper.toString(new Date()), url);
                    }
                }
            });
        }
    }

    public void downloadMediaFiles(final String storageFolder, URL... urls) {
        this.downloadMediaFiles(storageFolder, Arrays.asList(urls));
    }

    @SuppressWarnings("unchecked")
    public void downloadMediaFiles(final String storageFolder, List<URL> urls) {
        for (final URL url : urls) {
            final String host = url.getHost();
            if (this.blacklistedMediaFileHosts.contains(host)) {
                System.out.println("Cannot download file from blacklisted " + host + ": " + url.toString());
            } else {
                if (this.mediaFileDownloaders.containsKey(host)) {
                    mediaFileDownloaders.get(host).downloadMediaFile(storageFolder, url);
                } else {
                    System.out.println("Don't know how to download file from " + host + ": " + url.toString());
                }
            }
        }
    }

    protected abstract void onPageVisited(Page page, Map<String, Object> context);

    public void close() {
        this.downloadDocumentsService.shutdown();
        while (!this.downloadDocumentsService.isTerminated()) {
            try {
                this.downloadDocumentsService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        for (MediaFileDownloader downloader : this.mediaFileDownloaders.values()) {
            downloader.close();
        }
    }

    public byte[] urlToBytes(String url) {
        try {
            Page page = this.getPage(new URL(url));
            InputStream in = page.getResponse().getResponseBody().getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(in, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public BlockingEventDispatcher<BlockingEvent> getEventDispatcher() {
        return eventDispatcher;
    }

    public String getCookieValue(String name) {
        List<Cookie> cookies = this.getHttpClient().getCookieStore().getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
