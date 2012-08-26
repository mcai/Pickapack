package net.pickapack.spider.withJs.media.image;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import net.pickapack.spider.withJs.WebSpider;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.action.Function1;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OOXXPicsSpider extends WebSpider {
    public static final String SPIDER_HOME = FileUtils.getUserDirectoryPath() + "/OOXXPicsSpider/mop";
    private int numImages = 0;
    private static List<String> hashedImageUrls = new ArrayList<String>();

    public OOXXPicsSpider(BrowserVersion browserVersion) {
//        this.getWebClient().setJavaScriptEnabled(false);
        super(browserVersion);
    }

    @SuppressWarnings("unchecked")
    public void run(String url, String linksXPath, final String imagesXPath, String anchorPreviousPageXPath, Function1<Object, String> getImageUrlFunc) {
        String folder = SPIDER_HOME;

        if (!new File(folder).exists()) {
            new File(folder).mkdirs();
        }

        try {
            HtmlPage page1 = this.getWebClient().getPage(url);

            for (; ; ) {
                System.out.printf("[%s] Download page: %s\n", DateHelper.toString(new Date()), page1.getUrl());

                if (linksXPath != null) {
                    List<HtmlAnchor> links = (List<HtmlAnchor>) page1.getByXPath(linksXPath);

//                    List<Thread> threads = new ArrayList<Thread>();

                    for (final HtmlAnchor link : links) {
//                        Thread threadDownloadSubPage = new Thread(){
//                            @Override
//                            public void run() {
                        try {
                            HtmlPage page2 = link.click();
                            System.out.printf("[%s] Download sub page: %s\n", DateHelper.toString(new Date()), page2.getUrl());
                            downloadPage(page2, imagesXPath, getImageUrlFunc);
                            ((TopLevelWindow) getWebClient().getCurrentWindow().getTopWindow()).close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
//                            }
//                        };
//                        threadDownloadSubPage.setDaemon(true);
//                        threadDownloadSubPage.start();
//                        threads.add(threadDownloadSubPage);
                    }

//                    for(Thread thread : threads) {
//                        try {
//                            thread.join();
//                        } catch (InterruptedException e) {
//                            throw  new RuntimeException(e);
//                        }
//                    }
                } else {
                    downloadPage(page1, imagesXPath, getImageUrlFunc);
                }

                if (anchorPreviousPageXPath == null) {
                    break;
                }

                HtmlAnchor anchorPreviousPage = page1.getFirstByXPath(anchorPreviousPageXPath);
                if (anchorPreviousPage == null) {
                    break;
                }

                String previousPageUrl = page1.getUrl().toString();
                page1 = anchorPreviousPage.click();
                if (page1.getUrl().toString().equals(previousPageUrl)) {
                    break;
                }
            }

            this.getWebClient().closeAllWindows();

        } catch (IOException e) {
            this.getWebClient().closeAllWindows();
            throw new RuntimeException(e);
        }
    }

    public void downloadImages(String url, String imagesXPath, Function1<Object, String> getImageUrlFunc) {
        try {
            HtmlPage page1 = this.getWebClient().getPage(url);
            this.downloadPage(page1, imagesXPath, getImageUrlFunc);
            this.getWebClient().closeAllWindows();
        } catch (IOException e) {
            this.getWebClient().closeAllWindows();
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void downloadPage(HtmlPage page1, String imagesXPath, Function1<Object, String> getImageUrlFunc) {
        List<?> images = page1.getByXPath(imagesXPath);

        List<String> urls = new ArrayList<String>();

        for (Object image : images) {
            if (image instanceof HtmlImage) {
                if (getImageUrlFunc != null) {
                    urls.add(getImageUrlFunc.apply(image));
                } else {
                    String url = ((HtmlImage) image).getAttribute("src");
                    urls.add(url);
                }
            } else if (image instanceof HtmlAnchor) {
                String url = ((HtmlAnchor) image).getHrefAttribute();
                urls.add(url);
            }
        }

        this.downloadImagesByURL(urls);
    }

    @SuppressWarnings("unchecked")
    private void downloadImagesByURL(List<String> urls) {
        List<Thread> threads = new ArrayList<Thread>();

        for (final String url : urls) {
            Thread threadDownloadImage = new Thread() {
                @Override
                public void run() {
                    String hashedUrl = DigestUtils.md5Hex(url);
                    if (!hashedImageUrls.contains(hashedUrl) && url.contains(".")) {
                        downloadDocument(url, new File(SPIDER_HOME, (numImages++) + url.substring(url.lastIndexOf('.'), url.length())));
                        hashedImageUrls.add(hashedUrl);
                    } else {
                        System.out.printf("[%s] Duplicate image found: %s\n", DateHelper.toString(new Date()), url);
                    }
                }
            };
            threadDownloadImage.setDaemon(true);
            threadDownloadImage.start();
            threads.add(threadDownloadImage);
        }

        for (Thread thread : threads) {
            try {
                thread.join(30000);
//                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        OOXXPicsSpider spider = new OOXXPicsSpider(BrowserVersion.INTERNET_EXPLORER_8);
        spider.setProxy("10.26.27.29", 3128);

//        spider.run(
//                "http://www.meizitu.com/",
//                "//div[@class='postContent']//p//a[@target='_blank'][starts-with(@href,'http://www.meizitu.com/?p=')]",
//                "//div[@class='postContent']//p//img",
//                "//div[@id='wp_page_numbers']//a[contains(.,'>')]",
//                null);
//
//        spider.run("http://jandan.net/ooxx/",
//                null,
//                "//li[starts-with(@id, 'comment-')]//p//img",
//                "//a[@class='previous-comment-page']",
//                null);
//
//        spider.run("http://www.o2gzs.com/ooxx/",
//                null,
//                "//li[starts-with(@id, 'comment-')]//p//img",
//                "//a[@class='prev page-numbers']",
//                null);
//
//        spider.run("http://17t.me/c_meizi/",
//                null,
//                "//div[@class='con']//li[starts-with(@id,'p-')]//img",
//                "//div[@id='pages']//a[contains(.,'下一页')]",
//                null);
//
//        spider.run(
//                "http://www.szs77.com/artlist/34_1.html",
//                "//div[@class='list']//li//a[@class='title']",
//                "//table[@id='table1']//tbody//tr//td//div[@id='postmessage']//img",
//                "//div[@class='tg_pages']//ul//li//a[contains(.,'下一页')]",
//                null);
//
//        spider.run(
//                "http://www.sesezx.info/list.php?id=/tupianqu/YSE/",
//                "//div[@class='list']//li//a",
//                "//div[@class='content']//table//tbody//tr//td//img",
//                "//div[@class='page']//ul//li//a[contains(.,'下一页')]",
//                null);
//
//        spider.run(
//                "http://pic.arting365.com/?act=art&id=7",
//                "//div[@class='circle_box_content list']//ul//li//a[starts-with(@href,'/?act=talk&id=')][@target='_blank']",
//                "//ul[@id='img']//li//a//img",
//                "//div[@class='pager']//a[contains(.,'下一页')][starts-with(@href,'?act=art')]",
//                null);

//        spider.run("http://bbs.xinsilu.com/forum-76-1.html",
//                "//tbody[starts-with(@id,'normalthread')]//td[@class='icn']//a",
//                "//img[starts-with(@zoomfile,'data/attachment') or starts-with(@src,'attachments/dvbbs')]",
//                "//div[@class='pg']//a[contains(.,'下一页')]",
//                new Function1<Object, String>() {
//            @Override
//            public String apply(Object image) {
//                if (image instanceof HtmlImage) {
//                    if(((HtmlImage) image).hasAttribute("zoomfile")) {
//                        return "http://bbs.xinsilu.com/" + ((HtmlImage) image).getAttribute("file");
//                    }
//                    else if(((HtmlImage) image).hasAttribute("src")) {
//                        return "http://bbs.xinsilu.com/" + ((HtmlImage) image).getAttribute("src");
//                    }
//                }
//
//                throw new IllegalArgumentException();
//            }
//        });

        //TODO: use BrowserVersion.FIREFOX_3_6
//        spider.run(
//                "http://bj.58.com/searchjob/?key=" +
//                        URLEncoder.encode("主持人", "UTF-8") +
//                        "&final=1&jump=1",
//                "//tr[contains(.,'女')]//td[contains(.,'图]')]//a[@class='t']",
//                "//img[contains(@src,'/small/') or contains(@src,'/tiny/')]",
//                "//div[@class='pager']//a[@class='next']",
//                new Function1<Object, String>() {
//                    @Override
//                    public String apply(Object param) {
//                        if(param instanceof HtmlImage) {
//                            String src = ((HtmlImage) param).getSrcAttribute();
//
//                            if(src.contains("/tiny/")) {
//                                return src.replaceAll("/tiny/", "/big/");
//                            }
//                            else if(src.contains("/small/")) {
//                                return src.replaceAll("/small/", "/big/");
//                            }
//                        }
//
//                        throw new IllegalArgumentException();
//                    }
//                });

        spider.run(
                "http://tt.mop.com/topic/list_1_72_0_0.html",
                "//a[starts-with(@href,'/read_')][@target='_blank']",
//                "//img[@name='subjContentImg' or @name='replyImgs'][contains(@data-original,'.jpg')]",
                "//img[@name='subjContentImg'][contains(@data-original,'.jpg')]",
                "//div[@class='tiezi_page_fy']//a[starts-with(@href,'/topic/list')][contains(.,'下一页')]",
                new Function1<Object, String>() {
                    @Override
                    public String apply(Object param) {
                        if(param instanceof HtmlImage) {
                            return ((HtmlImage) param).getAttribute("data-original");
                        }

                        throw new IllegalArgumentException();
                    }
                });
    }
}
