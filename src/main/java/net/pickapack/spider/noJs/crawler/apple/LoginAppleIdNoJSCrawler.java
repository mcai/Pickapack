package net.pickapack.spider.noJs.crawler.apple;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.action.Action1;
import net.pickapack.io.file.IterableBigTextFile;
import net.pickapack.spider.noJs.crawler.CrawlerLoggingEvent;
import net.pickapack.spider.noJs.spider.Page;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoginAppleIdNoJSCrawler extends iTunesNoJSCrawler {
    protected String email;
    protected String appleIdPassword;
    protected String passwordToken;
    protected String creditBalance;

    public LoginAppleIdNoJSCrawler(String userAgent, String machineName, String guid, String email, String appleIdPassword, String proxyHost, int proxyPort) {
        super(userAgent, machineName, guid, proxyHost, proxyPort);

        this.email = email;
        this.appleIdPassword = appleIdPassword;
    }

    public boolean login(String why) {
        autoDelay();

        this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 登录Apple ID] 登录Apple ID (原因: %s, 邮箱: %s, 密码: %s)", DateHelper.toString(new Date()), why, email, appleIdPassword)));

        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
        requestParameters.add(new BasicNameValuePair("machineName", machineName));
        requestParameters.add(new BasicNameValuePair("guid", guid));
        requestParameters.add(new BasicNameValuePair("attempt", "0"));
        requestParameters.add(new BasicNameValuePair("why", why));
        requestParameters.add(new BasicNameValuePair("appleId", email));
        requestParameters.add(new BasicNameValuePair("password", appleIdPassword));
        requestParameters.add(new BasicNameValuePair("createSession", "true"));

        String pod = this.getCookieValue("Pod");
        if (pod == null) {
            pod = "24";
        }

        Page page = this.httpGet("https://p" + pod + "-buy.itunes.apple.com/WebObjects/MZFinance.woa/wa/authenticate", requestParameters);

        if (page.getResponse().getStatusCode() == 302 || page.getResponse().getStatusCode() == 307) {
            String location = page.getResponse().getHeader("location");
            page = this.httpGet(location, null);
        }

        savePage(page, "login.html");

        if (page.getText().contains("You have not verified your account")) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 登录Apple ID] Apple ID未激活 (原因: %s, 邮箱: %s, 密码: %s)", DateHelper.toString(new Date()), why, email, appleIdPassword)));
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 登录Apple ID] Apple ID登录失败 (原因: %s, 邮箱: %s, 密码: %s)", DateHelper.toString(new Date()), why, email, appleIdPassword)));
            return false;
        } else if (page.getText().contains("Your AppleID or password was entered incorrectly")) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 登录Apple ID] Apple ID或密码错误 (原因: %s, 邮箱: %s, 密码: %s)", DateHelper.toString(new Date()), why, email, appleIdPassword)));
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 登录Apple ID] Apple ID登录失败 (原因: %s, 邮箱: %s, 密码: %s)", DateHelper.toString(new Date()), why, email, appleIdPassword)));
            return false;
        }

        IterableBigTextFile file = new IterableBigTextFile(new StringReader(page.getText()));
        for (String line : file) {
            if (line.contains("passwordToken")) {
                passwordToken = line.substring(line.indexOf("<key>passwordToken</key><string>") + "<key>passwordToken</key><string>".length(), line.lastIndexOf("<"));
                break;
            } else if (line.contains("creditBalance")) {
                creditBalance = line.substring(line.indexOf("<key>creditBalance</key><string>") + "<key>creditBalance</key><string>".length(), line.lastIndexOf("<"));
                break;
            }
        }

        if (passwordToken == null) {
            throw new IllegalArgumentException();
        }

        String dsid = getCookieValue("X-Dsid");

        if (dsid != null && !dsid.isEmpty()) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 登录Apple ID] dsid = %s", DateHelper.toString(new Date()), dsid)));
        }

        boolean loggedIn = page.getText().contains("<key>accountInfo</key>");

        if (loggedIn) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 登录Apple ID] Apple ID登录成功 (原因: %s, 邮箱: %s, 密码: %s)", DateHelper.toString(new Date()), why, email, appleIdPassword)));
        } else {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 登录Apple ID] Apple ID登录失败 (原因: %s, 邮箱: %s, 密码: %s)", DateHelper.toString(new Date()), why, email, appleIdPassword)));
        }

        return loggedIn;
    }

    public boolean login() {
        return this.login("signIn");
    }

    @Override
    protected List<Header> prepareHeaders() {
        List<Header> headers = new ArrayList<Header>();
//        headers.add(new BasicHeader("Origin", "https://p33-buy.itunes.apple.com"));

        headers.add(new BasicHeader("X-Apple-Store-Front", mainlandChina ? "143465-2,12" : "143441-1,12")); //Mainland China/US

        headers.add(new BasicHeader("X-Apple-Tz", "28800"));
//        headers.add(new BasicHeader("Accept-Language", "zh-cn, zh;q=0.75, en-us;q=0.50, en;q=0.25"));
//        headers.add(new BasicHeader("Host", "p33-buy.itunes.apple.com"));
//        headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
//        headers.add(new BasicHeader("Referer", "http://itunes.apple.com/WebObjects/MZStore.woa/wa/viewGrouping?id=38&s=143441"));

        headers.add(new BasicHeader("X-Dsid", getCookieValue("X-Dsid")));

        if (this.passwordToken != null) {
            headers.add(new BasicHeader("X-Token", this.passwordToken));
        }
        return headers;
    }

    public void retrievePurchasedApps() {
        if (login()) {
            List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
            requestParameters.add(new BasicNameValuePair("guid", guid));
            requestParameters.add(new BasicNameValuePair("mt", "8"));

            Page page = this.httpGet("https://se.itunes.apple.com/WebObjects/MZStoreElements.woa/wa/purchases", requestParameters);

            savePage(page, "purchases.html");

//            String text = page.getFirstByXPath("//div/script[@type='text/json']/text()").getNodeValue();
//
//            System.out.println(text);

//            List<Object> appIdObjs = JsonPath.read(text, "$.contentIds[*]");
//
//            for(Object appIdObj : appIdObjs) {
//                Integer appId = (Integer) appIdObj;
//                System.out.println(getAppNameById(appId));
//            }
        }
    }

    public static boolean loginAppleId(String machineName, String guid, String email, String appleIdPassword, Action1<CrawlerLoggingEvent> eventCallback, String proxyHost, int proxyPort) {
        LoginAppleIdNoJSCrawler loginAppleIdCrawler = new LoginAppleIdNoJSCrawler(USER_AGENT_ITUNES, machineName, guid, email, appleIdPassword, proxyHost, proxyPort);
        loginAppleIdCrawler.getEventDispatcher().addListener(CrawlerLoggingEvent.class, eventCallback);

        return loginAppleIdCrawler.login();
    }
}
