package net.pickapack.spider.noJs.crawler.example;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.action.Function1;
import net.pickapack.net.url.URLHelper;
import net.pickapack.spider.noJs.crawler.NoJSCrawler;
import net.pickapack.spider.noJs.spider.HttpMethod;
import net.pickapack.spider.noJs.spider.NoJSSpider;
import net.pickapack.spider.noJs.spider.Page;
import net.pickapack.text.XPathHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JPTorrentNoJSCrawler extends NoJSCrawler {
    private String folderTorrents;
    private int numPages;
    private int maxNumPages;

    public JPTorrentNoJSCrawler(String userAgent, String folderTorrentsPostfix, int maxNumPages) {
        this(userAgent, folderTorrentsPostfix, maxNumPages, null, -1);
    }

    public JPTorrentNoJSCrawler(String userAgent, String folderTorrentsPostfix, int maxNumPages, String proxyHost, int proxyPort) {
        super(userAgent, 60000, proxyHost, proxyPort);
        this.folderTorrents = FileUtils.getUserDirectoryPath() + folderTorrentsPostfix;
        new File(this.folderTorrents).mkdirs();
        this.maxNumPages = maxNumPages;
    }

    public void downloadJpTorrent(String host, String torrentId) {
        try {
            List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
            requestParameters.add(new BasicNameValuePair("ref", torrentId));
            requestParameters.add(new BasicNameValuePair("submit", "Download"));

            Page page1 = this.getPage(new URL("http://" + host + "/download.php"), HttpMethod.POST, requestParameters, null, null);

            InputStream is = page1.getResponse().getResponseBody().getInputStream();
            OutputStream os = new FileOutputStream(folderTorrents + "/" + torrentId + ".torrent");
            IOUtils.copy(is, os);
            is.close();
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadJPTorrents(String url) {
        try {
            Page page1 = this.getPage(new URL(url), HttpMethod.GET, null, null, null, "Shift_JIS");
            this.visitPage(page1, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());

        List<Node> nodePosts = page.getByXPath("//tbody[//tr//td[@class='bgb']]");
        for (Node nodePost : nodePosts) {
            Node nodeTitle = XPathHelper.getFirstByXPath(nodePost, ".//td[@class='bgb']/font[@size='4']//text()");
            if (nodeTitle != null) {
                String title = nodeTitle.getNodeValue();

                if (!title.startsWith("Re:")) {
                    System.out.printf("  %s%n", title);

                    List<Node> links = XPathHelper.getByXPath(nodePost, ".//td[@class='bgc']/blockquote/a/@href[contains(.,'jptorrent')]");
                    for (Node link : links) {
                        String href = link.getNodeValue();
                        System.out.printf("    %s%n", href);
                        String torrentId = URLHelper.getQueryParameterFromUrl(href, "ref");
                        try {
                            this.downloadJpTorrent(new URL(href).getHost(), torrentId);
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    System.out.println();
                }
            }
        }
        System.out.println();

        this.numPages++;
        if (this.numPages < this.maxNumPages) {
            Function1<Node, String> getNodeUrlCallback = new Function1<Node, String>() {
                @Override
                public String apply(Node param) {
                    return "http://www.freedl.org" + param.getAttributes().getNamedItem("href").getNodeValue();
                }
            };

            Node linkNextPage = page.getFirstByXPath("//a[text()='次の 20 件']");
            if (linkNextPage != null) {
                try {
                    this.visitPage(this.getPage(new URL(getNodeUrlCallback.apply(linkNextPage)), HttpMethod.GET, null, null, null, "Shift_JIS"), context);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (XPathExpressionException e) {
                    throw new RuntimeException(e);
                } catch (TransformerException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public int getNumPages() {
        return numPages;
    }

    public int getMaxNumPages() {
        return maxNumPages;
    }

    public static void main(String[] args) {
        JPTorrentNoJSCrawler crawler = new JPTorrentNoJSCrawler(NoJSSpider.FIREFOX_3_6, "/Desktop/torrents", 100, "localhost", 8888);
//        JPTorrentNoJSCrawler crawler = new JPTorrentNoJSCrawler(NoJSSpider.FIREFOX_3_6, "/Desktop/torrents", 100);
        crawler.downloadJPTorrents("http://www.freedl.org/treebbs2rss/treebbs2rss/tree.php?mode=dump&page=1");
    }
}
