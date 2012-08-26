package net.pickapack.spider.withJs.crawler.media.image;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import net.pickapack.spider.withJs.crawler.WebCrawler;
import net.pickapack.dateTime.DateHelper;
import org.apache.commons.io.FileUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TianyaImageCrawler extends WebCrawler {
    private String folderImages;

    public TianyaImageCrawler(String folderImages, boolean doFaceRecognition) {
        super(BrowserVersion.INTERNET_EXPLORER_8);
        this.folderImages = folderImages;
        this.getWebClient().setJavaScriptEnabled(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());

        if(page instanceof HtmlPage && page.getUrl().toString().startsWith("http://www.tianya.cn/publicforum/articleslist")) {
            this.visitLinks((HtmlPage) page, "//a[starts-with(@href,'http://www.tianya.cn/publicforum/content/tianyamyself/')][@target='_blank']", null, false);
            this.visitLinks((HtmlPage) page, "//td[@align='right']//a[starts-with(@href,'http://www.tianya.cn/new/publicforum/articleslist')][contains(.,'下一页')]", null, false);
        }
        else if(page instanceof HtmlPage && page.getUrl().toString().startsWith("http://www.tianya.cn/publicforum/content")) {
            List<HtmlImage> images = (List<HtmlImage>) ((HtmlPage) page).getByXPath("//img[contains(@original,'.jpg')]");

            //TODO: failed to grab images
            this.downloadImages(images, FileUtils.getUserDirectoryPath() + this.folderImages, "./@original");

            if(!images.isEmpty()) {
                this.visitLinks((HtmlPage) page, "//div[@id='pageDivTop']//a[starts-with(@href,'http://www.tianya.cn/publicforum/content')][contains(.,'下一页')]", null, false);
            }
        }
    }

    public static void main(String[] args) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                TianyaImageCrawler spider = new TianyaImageCrawler("/OOXXPicsSpider/tianya_3", false);
                spider.getWebClient().setJavaScriptEnabled(false);
                spider.setProxy("10.26.27.29", 3128);

                spider.visit("http://www.tianya.cn/publicforum/articleslist/0/tianyamyself.shtml");
            }
        }, 0, 30, TimeUnit.SECONDS);
    }
}
