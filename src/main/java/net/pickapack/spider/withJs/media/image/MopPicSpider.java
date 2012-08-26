package net.pickapack.spider.withJs.media.image;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import net.pickapack.spider.withJs.WebSpider;
import net.pickapack.dateTime.DateHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MopPicSpider extends WebSpider {
    public static final String SPIDER_HOME = FileUtils.getUserDirectoryPath() + "/MopPicSpider/";

    public MopPicSpider() {
        super(BrowserVersion.INTERNET_EXPLORER_8);
        this.getWebClient().setJavaScriptEnabled(false);
        this.getWebClient().setJavaScriptTimeout(60000);
        this.getWebClient().setTimeout(120000);
    }

    @SuppressWarnings("unchecked")
    public void downloadPosts() {
        String folder = SPIDER_HOME;

        if (!new File(folder).exists()) {
            new File(folder).mkdirs();
        }

        try {
            HtmlPage page1 = this.getWebClient().getPage("http://tt.mop.com/topic/list_1_72_0_0.html");

            for (; ; ) {
                List<HtmlAnchor> anchorPosts = (List<HtmlAnchor>) page1.getByXPath("//a[starts-with(@href,'/read_')][@target='_blank']");

                for (HtmlAnchor anchorPost : anchorPosts) {
                    String title = anchorPost.getTextContent().trim();
                    String url = "http://tt.mop.com" + anchorPost.getHrefAttribute();
                    String hashedUrl = DigestUtils.md5Hex(url);
                    if (!hashedPosts.containsKey(hashedUrl)) {
                        Post post = new Post(title, url, DateHelper.toTick(new Date()));
                        hashedPosts.put(hashedUrl, post);
                        this.downloadPost(post);
                    }
                }

                HtmlAnchor anchorNextPage = page1.getFirstByXPath("//div[@class='tiezi_page_fy']//a[starts-with(@href,'/topic/list')][contains(.,'下一页')]");
                if (anchorNextPage == null) {
                    break;
                }

                page1 = anchorNextPage.click();
            }
        } catch (IOException e) {
            this.getWebClient().closeAllWindows();
            throw new RuntimeException(e);
        }

        this.getWebClient().closeAllWindows();
    }

    @SuppressWarnings("unchecked")
    public void downloadPost(Post post) throws IOException {
        System.out.printf("[%s] Download post: %s\n", DateHelper.toString(new Date()), post);
        HtmlPage pagePost = this.getWebClient().getPage(post.getUrl());
        String pageUrl = pagePost.getUrl().toString();

        for (; ; ) {
            boolean hasImagesInCurrentPage = false;

            List<HtmlImage> images = new ArrayList<HtmlImage>();
            images.addAll((List<HtmlImage>) pagePost.getByXPath(".//img[@name='subjContentImg'][contains(@data-original,'.jpg')]"));
            images.addAll((List<HtmlImage>) pagePost.getByXPath(".//img[@name='replyImgs'][contains(@data-original,'.jpg')]"));
//        images.addAll((List<HtmlImage>) pagePost.getByXPath(".//img[@name='replyImgs']"));

            for (HtmlImage image : images) {
                hasImagesInCurrentPage = true;

                final String url = image.getAttribute("data-original");
                final String localFileName = SPIDER_HOME + pageUrl.substring(pageUrl.lastIndexOf('/') + 1, pageUrl.length()) + "_" + url.substring(url.lastIndexOf('/') + 1, url.length());

                Thread threadDownloadImage = new Thread() {
                    @Override
                    public void run() {
                        downloadDocument(url, localFileName);
                    }
                };
                threadDownloadImage.setDaemon(true);
                threadDownloadImage.start();
            }

            if (!hasImagesInCurrentPage) {
                break;
            }

            HtmlAnchor anchorNextPage = pagePost.getFirstByXPath("//a[@class='end'][starts-with(@href,'/read_')][contains(.,'下一页')]");
            if (anchorNextPage == null) {
                break;
            }

            pagePost = anchorNextPage.click();
        }

        this.getWebClient().closeAllWindows();
    }

    public static class Post {
        private String title;
        private String url;
        private long timeCreated;

        public Post(String title, String url, long timeCreated) {
            this.title = title;
            this.url = url;
            this.timeCreated = timeCreated;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return String.format("%s(url: %s, timeCreated: %s)", title, url, DateHelper.toString(DateHelper.fromTick(this.timeCreated)));
        }
    }

    private static Map<String, Post> hashedPosts = new HashMap<String, Post>();

    public static void main(String[] args) throws IOException {
        for (; ; ) {
            MopPicSpider spider = new MopPicSpider();
            spider.setProxy("10.26.27.29", 3128);

            spider.downloadPosts();

            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
