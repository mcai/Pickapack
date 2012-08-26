package net.pickapack.spider.withJs.google;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import net.pickapack.spider.withJs.WebSpider;
import nu.xom.*;

import java.io.*;

public class GooglePlusSpider extends WebSpider {
    private PrintWriter pw;
    private long numSites;

    public GooglePlusSpider() {
        super(BrowserVersion.INTERNET_EXPLORER_8);
        this.getWebClient().setJavaScriptEnabled(false);
    }

    public void run() {
        try {
            this.pw = new PrintWriter(new FileWriter("/home/itecgo/Desktop/gg.txt"), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            XmlPage page1 = this.getWebClient().getPage("http://www.gstatic.com/s2/sitemaps/profiles-sitemap.xml");
            Builder builder = new Builder(false);
            String content = page1.getContent();
            content = content.replaceFirst("sitemapindex xmlns='http://www.sitemaps.org/schemas/sitemap/0.9'", "sitemapindex");
            this.getWebClient().closeAllWindows();

            Document doc = builder.build(new StringReader(content));
            Elements sitemaps = doc.getRootElement().getChildElements("sitemap");
            for (int i = 0; i < sitemaps.size(); i++) {
                Element sitemap = sitemaps.get(i);
                Element loc = sitemap.getChildElements("loc").get(0);
                grabSiteMapText(loc.getValue());
            }
            this.getWebClient().closeAllWindows();
            this.pw.close();
        } catch (IOException e) {
            recordException(e);
        } catch (ValidityException e) {
            recordException(e);
        } catch (ParsingException e) {
            recordException(e);
        }
    }

    private void grabSiteMapText(String url) {
        try {
            TextPage pageSiteMap = this.getWebClient().getPage(url);
            String content = pageSiteMap.getContent();
            this.getWebClient().closeAllWindows();
            BufferedReader br = new BufferedReader(new StringReader(content));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("https://plus.google.com/")) {
                    grabSite(line);
                }
            }
            br.close();
        } catch (IOException e) {
            recordException(e);
        }
    }

    private void grabSite(String url) {
        try {
            HtmlPage pageSite = this.getWebClient().getPage(url);
            String title = pageSite.getTitleText();
            String userName = title.substring(0, title.indexOf(" - Google+"));
            System.out.printf("[%d] %s\n", ++numSites, userName + "(" + url + ")");
            this.pw.println(userName + "(" + url + ")");
            this.getWebClient().closeAllWindows();
        } catch (IOException e) {
            recordException(e);
        }
    }

    public static void main(String[] args) {
        GooglePlusSpider spider = new GooglePlusSpider();
//        spider.setProxy("localhost", 8888);
        spider.run();
    }
}
