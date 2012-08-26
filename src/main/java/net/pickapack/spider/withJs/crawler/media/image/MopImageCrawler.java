package net.pickapack.spider.withJs.crawler.media.image;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.spider.withJs.crawler.WebCrawler;
import org.apache.commons.io.FileUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MopImageCrawler extends WebCrawler {
    private String folderImages;

    public MopImageCrawler(String folderImages, boolean doFaceRecognition) {
        super(BrowserVersion.INTERNET_EXPLORER_8);
        this.folderImages = folderImages;
        this.getWebClient().setJavaScriptEnabled(false);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());

        if(page instanceof HtmlPage && page.getUrl().toString().startsWith("http://tt.mop.com/topic/")) {
            this.visitLinks((HtmlPage) page, "//a[starts-with(@href,'/read_')][@target='_blank']", null, false);
            this.visitLinks((HtmlPage) page, "//div[@class='tiezi_page_fy']//a[starts-with(@href,'/topic/list')][contains(.,'下一页')]", null, false);
        }
        else if(page instanceof HtmlPage && page.getUrl().toString().startsWith("http://tt.mop.com/read_")) {
            List<DomAttr> subjectContentUrlAttributes = (List<DomAttr>) ((HtmlPage) page).getByXPath("//img[@name='subjContentImg'][contains(@data-original,'.jpg')]/@data-original");
            List<DomAttr> replyUrlAttributes = (List<DomAttr>) ((HtmlPage) page).getByXPath("//div[@class='box2 js-reply'][.//li[@class='username'][contains(.,'楼主')]]//div[@class='mainpart js-reply-body']//img[@name='replyImgs'][contains(@data-original,'.jpg')]/@data-original");

            List<String> urls = new ArrayList<String>();
            for(DomAttr urlAttribute : subjectContentUrlAttributes) {
                urls.add(urlAttribute.getValue());
            }
            for(DomAttr urlAttribute : replyUrlAttributes) {
                urls.add(urlAttribute.getValue());
            }

            this.downloadDocumentByUrls(urls, FileUtils.getUserDirectoryPath() + this.folderImages);

            if(!urls.isEmpty()) {
                this.visitLinks((HtmlPage) page, "//div[@class='area ztnr']//div[@class='page']//div[@class='inner']//a[@class='end'][starts-with(@href,'/read_')][contains(.,'下一页')]", null, false);
            }
        }
    }

    public static void main(String[] args) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                VgirlCrawler spider = new VgirlCrawler("/OOXXPicsSpider/mop_3", true);
                spider.setProxy("10.26.27.29", 3128);

                spider.visit("http://tt.mop.com/topic/list_1_72_0_0.html");
            }
        }, 0, 30, TimeUnit.SECONDS);
    }
}
