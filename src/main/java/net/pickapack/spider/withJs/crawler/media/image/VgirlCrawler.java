package net.pickapack.spider.withJs.crawler.media.image;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.*;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.spider.withJs.crawler.WebCrawler;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class VgirlCrawler extends WebCrawler {
    private String folderImages;

    public VgirlCrawler(String folderImages, boolean doFaceRecognition) {
        super(BrowserVersion.INTERNET_EXPLORER_8);
        this.folderImages = folderImages;
        this.getWebClient().setJavaScriptEnabled(false);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());

        if(page instanceof HtmlPage && page.getUrl().toString().startsWith("http://vgirl.weibo.com/area.php")) {
            this.visitLinks((HtmlPage) page, "//ul[@class='vg_short']//a[//img[@class='long_img']]", null, false);
            this.visitLinks((HtmlPage) page, "//div[@class='W_pages W_pages_comment vg_txtb vg_linkb mini_blog_wrap noface']//a[@class='W_btn_a'][starts-with(@href,'/area.php?')][contains(.,'下一页')]", null, false);
        }
        else if(page instanceof HtmlPage && page.getUrl().toString().startsWith("http://vgirl.weibo.com/rank.php")) {
            this.visitLinks((HtmlPage) page, "//ul[@class='rlist']//span[@class='shows']//a[//img[@class='vg_line']]", null, false);
            this.visitLinks((HtmlPage) page, "//div[@class='W_pages W_pages_comment vg_txtb vg_linkb mini_blog_wrap noface']//a[@class='W_btn_a'][starts-with(@href,'/rank.php?')][contains(.,'下一页')]", null, false);
        }
        else if(page instanceof HtmlPage && page.getUrl().toString().startsWith("http://vgirl.weibo.com/user/profile.php")) {
            String nickName = ((HtmlPage) page).<HtmlHeading1>getFirstByXPath("//h1[@class='vg_label_name']").asText();
            String birthDay = ((HtmlPage) page).<HtmlDivision>getFirstByXPath("//div[@class='infomationbox']//ul[@class='basiclist']//li[.//p[contains(.,'生日')]]//div").asText();
            String place = ((HtmlPage) page).<HtmlDivision>getFirstByXPath("//div[@class='infomationbox']//ul[@class='basiclist']//li[.//p[contains(.,'所在地')]]//div").asText();
            String weight = ((HtmlPage) page).<HtmlDivision>getFirstByXPath("//div[@class='infomationbox']//ul[@class='basiclist']//li[.//p[contains(.,'体重')]]//div").asText();
            String height = ((HtmlPage) page).<HtmlDivision>getFirstByXPath("//div[@class='infomationbox']//ul[@class='basiclist']//li[.//p[contains(.,'身高')]]//div").asText();

            String bust = "";
            String waist = "";
            String hip = "";

            List<HtmlSpan> spanVitalStatistics = (List<HtmlSpan>) ((HtmlPage) page).getByXPath("//div[@class='infomationbox']//ul[@class='basiclist']//li[.//p[contains(.,'三围')]]//div//span[@class='txtcon']");
            if(spanVitalStatistics.size() == 3) {
                bust = spanVitalStatistics.get(0).asText();
                waist = spanVitalStatistics.get(1).asText();
                hip = spanVitalStatistics.get(2).asText();
            }

            System.out.println(nickName);
            System.out.println(birthDay + ", " + place);
            System.out.println(weight + ", " + height);
            System.out.println(bust + ", " + waist + ", " + hip);
        }
        else if(page instanceof HtmlPage && page.getUrl().toString().startsWith("http://vgirl.weibo.com/album/index.php")) {
            try {
                List<HtmlAnchor> anchorAlbums = (List<HtmlAnchor>) ((HtmlPage) page).getByXPath("//li[@class='vg_line album_item']//a[.//img[@class=' user_photo']]");

                for(HtmlAnchor anchorAlbum : anchorAlbums) {
                    this.visitPage(page, anchorAlbum.click(), null);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if(page instanceof HtmlPage && page.getUrl().toString().startsWith("http://vgirl.weibo.com/album/show.php")) {
            List<DomAttr> imageUrlAttributes = (List<DomAttr>) ((HtmlPage) page).getByXPath("//div[@class='photo_show']//img[@class='photos']/@src");

            List<String> urls = new ArrayList<String>();
            for(DomAttr urlAttribute : imageUrlAttributes) {
                urls.add(urlAttribute.getValue());
            }

            this.downloadDocumentByUrls(urls, FileUtils.getUserDirectoryPath() + this.folderImages);

            if(!urls.isEmpty()) {
                this.visitLinks((HtmlPage) page, "//div[@id='photo_page']//a[@class='vg_linkb vg_line next'][starts-with(@href,'show.php?')][contains(.,'下一页')]", null, false);
            }
        }
        else if(page instanceof HtmlPage && page.getUrl().toString().startsWith("http://vgirl.weibo.com/")) {
            try {
                this.visitPage(page, ((HtmlPage) page).<HtmlAnchor>getFirstByXPath("//div[@class='vg_navbox']//li//a[contains(.,'个人资料')]").click(), null);
                this.visitPage(page, ((HtmlPage) page).<HtmlAnchor>getFirstByXPath("//div[@class='vg_navbox']//li//a[contains(.,'相册')]").click(), null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        VgirlCrawler spider = new VgirlCrawler("/OOXXPicsSpider/vgirl_test", false);
        spider.getWebClient().setJavaScriptEnabled(false);
        spider.setProxy("10.26.27.29", 3128);

//                spider.visit("http://vgirl.weibo.com/area.php?type=1");
//                spider.visit("http://vgirl.weibo.com/rank.php");
//        spider.visit("http://vgirl.weibo.com/meromerogirl");

//        spider.visit("http://vgirl.weibo.com/meromerogirl");
//        spider.visit("http://vgirl.weibo.com/suxiaolai88");
//        spider.visit("http://vgirl.weibo.com/jiaru168168");
//        spider.visit("http://vgirl.weibo.com/ninibabelin");
//        spider.visit("http://vgirl.weibo.com/yayaelf");
        spider.visit("http://vgirl.weibo.com/2156654824");
    }
}
