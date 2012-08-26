package net.pickapack.spider.withJs.google;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.*;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.net.url.URLHelper;
import net.pickapack.spider.withJs.WebSpider;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.List;

public class GoogleWebSearchSpider extends WebSpider {  //TODO: 1. rename file if exists; 2. build search result index (e.g., title, url, file)
    public static final String SPIDER_HOME = "/home/itecgo/Desktop/googleWebSearchSpider/";
    private String keywords;
    private int currentResultPageIndex;

    public GoogleWebSearchSpider(String keywords) {
        super(BrowserVersion.INTERNET_EXPLORER_8);
        this.keywords = keywords;
        new File(SPIDER_HOME).mkdirs();
    }

    public void run() throws IOException, URISyntaxException {
        System.out.printf("[%s] Searching for: \"%s\" by Google Web Search\n\n", DateHelper.toString(new Date()), this.keywords);
        
        HtmlPage page1 = this.getWebClient().getPage("https://www.google.com/webhp?hl=en&tab=ww");

        HtmlTextInput inputKeywords = page1.getFirstByXPath("//input[@name='q']");
        HtmlSubmitInput buttonSearch = page1.getFirstByXPath("//input[@name='btnG']");

        inputKeywords.setValueAttribute(keywords);

        HtmlPage page2 = buttonSearch.click();

        System.out.printf("[%s] Processing result page #%d: %s \n\n", DateHelper.toString(new Date()), ++currentResultPageIndex, page2.getUrl());

        processResultPage(page2);

        HtmlPage pageCurrent = page2;

        for (;;) {
            if(pageCurrent.getByXPath("//a/span[@style='display:block;margin-left:53px'][contains(., 'Next')]").isEmpty()) {
                break;
            }

            HtmlSpan spanNext = pageCurrent.getFirstByXPath("//a/span[@style='display:block;margin-left:53px'][contains(., 'Next')]");
            HtmlAnchor anchorNext = (HtmlAnchor) spanNext.getParentNode();
            Page pageNextRaw = anchorNext.click();

            if(pageNextRaw instanceof HtmlPage) {
                HtmlPage pageNext = (HtmlPage) pageNextRaw;

                System.out.printf("[%s] Processing result page #%d: %s \n\n", DateHelper.toString(new Date()), ++currentResultPageIndex, pageNextRaw.getUrl());

                processResultPage(pageNext);

                pageCurrent = pageNext;
            }
            else {
                System.out.println(pageNextRaw.getUrl());
                break;
            }
        }

        this.getWebClient().closeAllWindows();
    }

    @SuppressWarnings("unchecked")
    private void processResultPage(HtmlPage page2) throws IOException, URISyntaxException {
        List<HtmlAnchor> anchors = (List<HtmlAnchor>) page2.getByXPath("//li[@class='g']//h3[@class='r']//a");

        for (HtmlAnchor anchor : anchors) {
            String href = anchor.getHrefAttribute();
            if (href != null && href.startsWith("/url?q=")) {
                System.out.printf("[%s] \tProcessing href: %s\n", DateHelper.toString(new Date()), href);

                String documentUrl = URLHelper.getQueryParameterFromUrl(href, "q");
                if(documentUrl != null) {
                    downloadDocument(documentUrl);
                }
            }
        }
    }

    private void downloadDocument(String url){
        try {
            String localFileName = SPIDER_HOME + url.substring(url.lastIndexOf('/') + 1, url.length());

            System.out.printf("[%s] \tDownloading document: %s to %s\n", DateHelper.toString(new Date()), url, localFileName);

            URLConnection urlConnection = new URL(url).openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.26.27.29", 3128)));  //TODO: specify connect and read timeout

            InputStream is = urlConnection.getInputStream();
            OutputStream os = new BufferedOutputStream(new FileOutputStream(localFileName));
            IOUtils.copy(is, os);
            is.close();
            os.close();

            System.out.printf("[%s] \tDocument downloaded: %s to %s\n\n", DateHelper.toString(new Date()), url, localFileName);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        String keywords = "Computer Architecture";

//        if(args.length == 0) {
//            System.out.println("Usage: GoogleWebSearchSpider <keywords>");
//            System.exit(-1);
//        }
//
//        keywords = args[0];

        GoogleWebSearchSpider spider = new GoogleWebSearchSpider(keywords + " filetype:pdf");
        spider.setProxy("10.26.27.29", 3128);
        spider.run();
    }
}
