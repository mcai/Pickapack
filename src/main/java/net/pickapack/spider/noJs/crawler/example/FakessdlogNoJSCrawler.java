package net.pickapack.spider.noJs.crawler.example;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.spider.noJs.crawler.NoJSCrawler;
import net.pickapack.spider.noJs.spider.NoJSSpider;
import net.pickapack.spider.noJs.spider.Page;
import net.pickapack.text.XPathHelper;
import org.w3c.dom.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FakessdlogNoJSCrawler extends NoJSCrawler {
    private List<String> userIds;
    private List<String> passwords;
    private int numLinesScanned;

    public FakessdlogNoJSCrawler(String userAgent) {
        this(userAgent, null, -1);
    }

    public FakessdlogNoJSCrawler(String userAgent, String proxyHost, int proxyPort) {
        super(userAgent, 60000, proxyHost, proxyPort);
        this.userIds = new ArrayList<String>();
        this.passwords = new ArrayList<String>();
    }

    @SuppressWarnings("unchecked")
    public void run(String url) {
        try {
            this.visit(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());

        List<Node> rows = page.getByXPath("//table[@id='tblMain']//tbody//tr[@dir='ltr']");

        for (Node row : rows) {
            String userId = XPathHelper.getFirstByXPath(row, ".//td[3]//text()").getNodeValue();
            String password = XPathHelper.getFirstByXPath(row, ".//td[4]//text()").getNodeValue();
            numLinesScanned++;
            if (!this.userIds.contains(userId)) {
                this.userIds.add(userId);
                if (this.userIds.size() % 100 == 0)
                    System.out.printf("[lines scanned: %d, userIds: %d] new userId found: %s%n", numLinesScanned, userIds.size(), userId);
            }
            if (!this.passwords.contains(password)) {
                this.passwords.add(password);
                if (this.passwords.size() % 100 == 0)
                    System.out.printf("[lines scanned: %d, passwords: %d] new password found: %s%n", numLinesScanned, passwords.size(), password);
            }
        }
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public List<String> getPasswords() {
        return passwords;
    }

    public static final String FILE_NAME_USER_IDS = "./userIds.txt";
    public static final String FILE_NAME_PASSWORDS = "./passwords.txt";

    public static void main(String[] args) throws IOException {
        String url = "https://spreadsheets.google.com/pub?key=pj62VKrg9JNMO9SbmF2eIRA+";

        FakessdlogNoJSCrawler crawler = new FakessdlogNoJSCrawler(NoJSSpider.FIREFOX_3_6, "10.26.27.29", 3128);
        crawler.run(url);
        crawler.close();

        PrintWriter pwUserIds = new PrintWriter(new FileWriter(FILE_NAME_USER_IDS));
        for (String userId : crawler.getUserIds()) {
            pwUserIds.println(userId);
        }
        pwUserIds.close();

        PrintWriter pwPasswords = new PrintWriter(new FileWriter(FILE_NAME_PASSWORDS));
        for (String password : crawler.getPasswords()) {
            pwPasswords.println(password);
        }
        pwPasswords.close();
    }
}
