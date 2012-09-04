package net.pickapack.spider.noJs.crawler.apple;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.action.Action1;
import net.pickapack.mail.EmailHelper;
import net.pickapack.spider.noJs.crawler.CrawlerLoggingEvent;
import net.pickapack.spider.noJs.spider.Page;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BuyGiftCardNoJSCrawler extends LoginAppleIdNoJSCrawler {
    public BuyGiftCardNoJSCrawler(String machineName, String guid, String email, String appleIdPassword, Action1<CrawlerLoggingEvent> eventCallback) {
        this(machineName, guid, email, appleIdPassword, eventCallback, null, -1);
    }

    public BuyGiftCardNoJSCrawler(String machineName, String guid, String email, String appleIdPassword, Action1<CrawlerLoggingEvent> eventCallback, String proxyHost, int proxyPort) {
        super(USER_AGENT_ITUNES, machineName, guid, email, appleIdPassword, proxyHost, proxyPort);
        this.getEventDispatcher().addListener(CrawlerLoggingEvent.class, eventCallback);
    }

    public boolean buyGiftCard() {
        this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 购买礼品卡] 开始购买礼品卡 (邮箱: %s)", DateHelper.toString(new Date()), email)));

        boolean bought = login() && buyGiftCardStep1();

        if (bought) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 购买礼品卡] 购买礼品卡成功 (邮箱: %s)", DateHelper.toString(new Date()), email))); //TODO: add missing fields
        } else {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 购买礼品卡] 购买礼品卡失败 (邮箱: %s)", DateHelper.toString(new Date()), email)));
        }

        return bought;
    }

    private boolean buyGiftCardStep1() {
        autoDelay();

        String pod = this.getCookieValue("Pod");

        Page page = this.httpGet("https://p" + pod + "-buy.itunes.apple.com/WebObjects/MZFinance.woa/wa/buyLandingPage", null);

        String url = page.getFirstByXPath("//*[contains(@url,'deliveryMethod=print')]/@url").getNodeValue();

        page = this.httpGet(url, null);

        savePage(page, "buyGiftCardStep1a.html");

        autoDelay();

        url = page.getFirstByXPath("//*[@formviewname='buyXCardForm'][2]/@url").getNodeValue();

        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
        requestParameters.add(new BasicNameValuePair("fromName", EmailHelper.generateEmailPrefix()));
        requestParameters.add(new BasicNameValuePair("toName", EmailHelper.generateEmailPrefix()));
        requestParameters.add(new BasicNameValuePair("buyButton", "submit"));
        requestParameters.add(new BasicNameValuePair("amount", "1"));
        requestParameters.add(new BasicNameValuePair("message", EmailHelper.generateEmailPrefix()));

        page = this.httpPost("https://buy.itunes.apple.com" + url, requestParameters);

        savePage(page, "buyGiftCardStep1b.html");

        if (page.getText().contains("<key>failureType</key><string>5010</string>")) {
            if (login("serverDialog")) {
                return buyGiftCardStep2();
            }
        }

        return false;
    }

    private boolean buyGiftCardStep2() {
        autoDelay();

        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
        requestParameters.add(new BasicNameValuePair("fromName", EmailHelper.generateEmailPrefix()));
        requestParameters.add(new BasicNameValuePair("toName", EmailHelper.generateEmailPrefix()));
        requestParameters.add(new BasicNameValuePair("buyButton", "submit"));
        requestParameters.add(new BasicNameValuePair("amount", "1"));
        requestParameters.add(new BasicNameValuePair("message", EmailHelper.generateEmailPrefix()));

        Page page = this.httpGet("https://buy.itunes.apple.com/WebObjects/MZFinance.woa/wa/com.apple.jingle.app.finance.DirectAction/buyGiftCertificate", requestParameters);

        savePage(page, "buyGiftCardStep2.html");

        String url = page.getByXPath("//*[@formviewname='buyXCardForm']/@url").get(2).getNodeValue();

        return buyGiftCardStep3(url);
    }

    private boolean buyGiftCardStep3(String url) {
        autoDelay();

        Page page;
        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
        requestParameters.add(new BasicNameValuePair("buyButton", "submit"));

        page = this.httpPost("https://buy.itunes.apple.com" + url, requestParameters);

        savePage(page, "buyGiftCardStep3.html");

        return false; //TODO: add condition checking logic
    }

    public static boolean buyGiftCard(String machineName, String guid, String email, String appleIdPassword, Action1<CrawlerLoggingEvent> eventCallback, String proxyHost, int proxyPort) {
        BuyGiftCardNoJSCrawler buyGiftCardCrawler = new BuyGiftCardNoJSCrawler(machineName, guid, email, appleIdPassword, eventCallback, proxyHost, proxyPort);
        return buyGiftCardCrawler.buyGiftCard();
    }
}
