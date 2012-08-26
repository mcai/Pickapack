package net.pickapack.spider.noJs.crawler.apple;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.action.Action1;
import net.pickapack.spider.noJs.crawler.CrawlerLoggingEvent;
import net.pickapack.spider.noJs.spider.Page;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BuyAppNoJSCrawler extends LoginAppleIdNoJSCrawler {
    private String kbsync;
    private String needDiv;
    private String origPage;
    private String origPage2;
    private String origPageCh;
    private String origPageCh2;
    private String origPageLocation;
    private String origPageLocation2;
    private String refererUrl;
    private String cardNumber;
    private String ccv;
    private String appId;

    public BuyAppNoJSCrawler(String machineName, String guid, String email, String appleIdPassword, String kbsync, String cardNumber, String ccv, String appId, String needDiv, String origPage, String origPage2, String origPageCh, String origPageCh2, String origPageLocation, String origPageLocation2, String refererUrl, Action1<CrawlerLoggingEvent> eventCallback) {
        this(machineName, guid, email, appleIdPassword, kbsync, cardNumber, ccv, appId, needDiv, origPage, origPage2, origPageCh, origPageCh2, origPageLocation, origPageLocation2, refererUrl, eventCallback, null, -1
        );
    }

    public BuyAppNoJSCrawler(String machineName, String guid, String email, String appleIdPassword, String kbsync, String cardNumber, String ccv, String appId, String needDiv, String origPage, String origPage2, String origPageCh, String origPageCh2, String origPageLocation, String origPageLocation2, String refererUrl, Action1<CrawlerLoggingEvent> eventCallback, String proxyHost, int proxyPort) {
        super(USER_AGENT_ITUNES, machineName, guid, email, appleIdPassword, proxyHost, proxyPort);
        this.kbsync = kbsync;
        this.cardNumber = cardNumber;
        this.ccv = ccv;
        this.appId = appId;

        this.needDiv = needDiv;
        this.origPage = origPage;
        this.origPage2 = origPage2;
        this.origPageCh = origPageCh;
        this.origPageCh2 = origPageCh2;
        this.origPageLocation = origPageLocation;
        this.origPageLocation2 = origPageLocation2;
        this.refererUrl = refererUrl;

        this.getEventDispatcher().addListener(CrawlerLoggingEvent.class, eventCallback);
    }

    private boolean buyApp() {
        this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 购买App] 开始购买App (邮箱: %s, App ID: %s)", DateHelper.toString(new Date()), email, appId)));

        this.getHttpClient().getCookieStore().addCookie(newCookie("itsMetricsR", "Genre-US-Mobile%20Software%20Applications-36-iphone@@Mobile%20Software%20Applications-main@@Titledbox_Top%20Charts%7CListbox_Top%20Grossing%7CLockup_2%7CCopyLink@@", ".apple.com", "/"));

        boolean bought = login() && buyAppStep1(getAppInfo(this.appId), false);

        if (bought) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 购买App] 购买App成功 (邮箱: %s, App ID: %s)", DateHelper.toString(new Date()), email, appId)));
        } else {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 购买App] 购买App失败 (邮箱: %s, App ID: %s)", DateHelper.toString(new Date()), email, appId)));
        }

        return bought;
    }

    private boolean buyAppStep1(AppInfo appInfo, boolean confirm) {
        if (appInfo == null) {
            return false;
        }

        autoDelay();

        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<plist version=\"1.0\">\n" +
                "<dict>\n" +
                "\t<key>appExtVrsId</key>\n" +
                "\t<string>" +
                appInfo.getAppExtVrsId() +
                "</string>\n" +

//                    "\t<key>creditDisplay</key>\n" +
//                    "\t<string>" +
//                    creditBalance +
//                    "</string>\n" +

                "\t<key>guid</key>\n" +
                "\t<string>" +
                guid +
                "</string>\n" +
                "\t<key>kbsync</key>\n" +
                "\t<data>\n\t" +
                kbsync +
                "\n" +
                "\t</data>\n" +
                "\t<key>machineName</key>\n" +
                "\t<string>" +
                machineName +
                "</string>\n" +
                "\t<key>needDiv</key>\n" +
                "\t<string>" +
                needDiv +
                "</string>\n" +
                "\t<key>origPage</key>\n" +
                "\t<string>" +
                origPage +
                "</string>\n" +
                "\t<key>origPage2</key>\n" +
                "\t<string>" +
                origPage2 +
                "</string>\n" +
                "\t<key>origPageCh</key>\n" +
                "\t<string>" +
                origPageCh +
                "</string>\n" +
                "\t<key>origPageCh2</key>\n" +
                "\t<string>" +
                origPageCh2 +
                "</string>\n" +
                "\t<key>origPageLocation</key>\n" +
                "\t<string>" +
                origPageLocation +
                "</string>\n" +
                "\t<key>origPageLocation2</key>\n" +
                "\t<string>" +
                origPageLocation2 +
                "</string>\n" +
                "\t<key>price</key>\n" +
                "\t<string>" +
                appInfo.getPrice() +
                "</string>\n" +
                "\t<key>pricingParameters</key>\n" +
                "\t<string>" +
                appInfo.getPricingParameters() +
                "</string>\n" +
                "\t<key>productType</key>\n" +
                "\t<string>" +
                appInfo.getProductType() +
                "</string>\n" +
                "\t<key>salableAdamId</key>\n" +
                "\t<string>" +
                appInfo.getSalableAdamId() +
                "</string>\n" +

                (!confirm ? "" : "\t<key>wasWarnedAboutFirstTimeBuy</key>\n" +
                        "\t<string>true</string>\n") +

                "</dict>\n" +
                "</plist>";

        String pod = this.getCookieValue("Pod");

        Page page = this.httpPost(refererUrl, "https://p" + pod + "-buy.itunes.apple.com/WebObjects/MZBuy.woa/wa/buyProduct", body);

        savePage(page, "buyAppStep1.html");

        if (page.getText().contains("Verification is required")) {
            String gotoUrl = page.getFirstByXPath("//string[starts-with(.,'https://')]/text()").getNodeValue();
            if (login("serverDialog")) {
                return buyAppStep2(appInfo, gotoUrl);
            }
        } else if (page.getText().contains("Are you sure you want to buy and download")) {
            return buyAppStep1(appInfo, true);
        } else if (page.getText().contains("<key>set-auth-token</key>")) {
            return true;
        } else if (page.getText().contains("You have already purchased this item")) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 购买App] 已经购买该App (邮箱: %s, App ID: %s)", DateHelper.toString(new Date()), email, appId)));
            return true;
        }

        return false;
    }

    private boolean buyAppStep2(AppInfo appInfo, String gotoUrl) {
        autoDelay();

        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
        requestParameters.add(new BasicNameValuePair("xToken", this.passwordToken));
        requestParameters.add(new BasicNameValuePair("appExtVrsId", appInfo.getAppExtVrsId()));
        requestParameters.add(new BasicNameValuePair("needDiv", this.needDiv));
        requestParameters.add(new BasicNameValuePair("origPage", this.origPage));
        requestParameters.add(new BasicNameValuePair("origPage2", this.origPage2));
        requestParameters.add(new BasicNameValuePair("origPageCh", this.origPageCh));
        requestParameters.add(new BasicNameValuePair("origPageCh2", this.origPageCh2));
        requestParameters.add(new BasicNameValuePair("origPageLocation", this.origPageLocation));
        requestParameters.add(new BasicNameValuePair("origPageLocation2", this.origPageLocation2));
        requestParameters.add(new BasicNameValuePair("price", appInfo.getPrice()));
        requestParameters.add(new BasicNameValuePair("pricingParameters", appInfo.getPricingParameters()));
        requestParameters.add(new BasicNameValuePair("productType", appInfo.getProductType()));
        requestParameters.add(new BasicNameValuePair("salableAdamId", appInfo.getSalableAdamId()));
        requestParameters.add(new BasicNameValuePair("wasWarnedAboutFirstTimeBuy", "true"));
        requestParameters.add(new BasicNameValuePair("machineName", machineName));
        requestParameters.add(new BasicNameValuePair("guid", guid));

        Page page = this.httpGet(refererUrl, gotoUrl, requestParameters);

        savePage(page, "buyAppStep2.html");

        boolean hasCard = page.getFirstByXPath("//input[@name='cc-number']") != null;

        String inputCreditCardTypeName = page.getFirstByXPath("//input[@name='credit-card-type']/@name").getNodeValue();
        String inputCreditCardNumberName = !hasCard ? "" : page.getFirstByXPath("//input[@id='cc_number']/@name").getNodeValue();
        String selectCreditCardMonthName = !hasCard ? "" : page.getFirstByXPath("//select[@id='cc_month']/@name").getNodeValue();
        String selectCreditCardYearName = !hasCard ? "" : page.getFirstByXPath("//select[@id='cc_year']/@name").getNodeValue();
        String inputCreditCardCcvName = !hasCard ? "" : page.getFirstByXPath("//input[@id='cc_ccv']/@name").getNodeValue();
        String inputSalutationName = page.getFirstByXPath("//select[@id='salutation']/@name").getNodeValue();
        String inputFirstNameName = page.getFirstByXPath("//input[@id='firstName']/@name").getNodeValue();
        String inputLastNameName = page.getFirstByXPath("//input[@id='lastName']/@name").getNodeValue();
        String inputStreet1Name = page.getFirstByXPath("//input[@id='street1']/@name").getNodeValue();
        String inputStreet2Name = page.getFirstByXPath("//input[@id='street2']/@name").getNodeValue();
        String inputCityName = page.getFirstByXPath("//input[@id='city']/@name").getNodeValue();
        String selectStateName = page.getFirstByXPath("//select[@id='state']/@name").getNodeValue();
        String inputPostalCodeName = page.getFirstByXPath("//input[@id='postalcode']/@name").getNodeValue();
        String inputPhone1AreaCodeName = page.getFirstByXPath("//input[@id='phone1AreaCode']/@name").getNodeValue();
        String inputPhone1NumberName = page.getFirstByXPath("//input[@id='phone1Number']/@name").getNodeValue();
        String inputDoneName = page.getFirstByXPath("//input[@value='Done']/@name").getNodeValue();

        String creditCardType = page.getFirstByXPath("//input[@name='credit-card-type']/@value").getNodeValue();
//        String creditCardNumber = !hasCard ? "" : page.getFirstByXPath("//input[@id='cc_number']/@value").getNodeValue();
        String creditCardMonth = !hasCard ? "" : page.getFirstByXPath("//select[@id='cc_month']/option[@selected='selected']/@value").getNodeValue();
        String creditCardYear = !hasCard ? "" : page.getFirstByXPath("//select[@id='cc_year']/option[@selected='selected']/@value").getNodeValue();
//        String creditCardCcv = !hasCard ? "" : page.getFirstByXPath("//input[@id='cc_ccv']/@value").getNodeValue();
        String salutation = page.getFirstByXPath("//select[@id='salutation']/option[@selected='selected']/@value").getNodeValue();
        String firstName = page.getFirstByXPath("//input[@id='firstName']/@value").getNodeValue();
        String lastName = page.getFirstByXPath("//input[@id='lastName']/@value").getNodeValue();
        String street1 = page.getFirstByXPath("//input[@id='street1']/@value").getNodeValue();
//        String street2 = page.getFirstByXPath("//input[@id='street2']/@value") == null ? "" : page.getFirstByXPath("//input[@id='street2']/@value").getNodeValue();
        String city = page.getFirstByXPath("//input[@id='city']/@value").getNodeValue();
        String state = page.getFirstByXPath("//select[@id='state']/option[@selected='selected']/@value").getNodeValue();
        String postalCode = page.getFirstByXPath("//input[@id='postalcode']/@value").getNodeValue();
        String phone1AreaCode = page.getFirstByXPath("//input[@id='phone1AreaCode']/@value").getNodeValue();
        String phone1Number = page.getFirstByXPath("//input[@id='phone1Number']/@value").getNodeValue();

        String url = page.getFirstByXPath("//form[@action]/@action").getNodeValue();

        return buyAppStep3(
                inputCreditCardTypeName, inputCreditCardNumberName,
                selectCreditCardMonthName, selectCreditCardYearName, inputCreditCardCcvName,
                inputSalutationName,
                inputFirstNameName, inputLastNameName,
                inputStreet1Name, inputStreet2Name,
                inputCityName, selectStateName,
                inputPostalCodeName, inputPhone1AreaCodeName, inputPhone1NumberName, inputDoneName,

                creditCardType, creditCardMonth, creditCardYear, salutation, firstName, lastName, street1, city, state, postalCode, phone1AreaCode, phone1Number,

                gotoUrl, url);
    }

    private boolean buyAppStep3(String inputCreditCardTypeName, String inputCreditCardNumberName, String selectCreditCardMonthName, String selectCreditCardYearName, String inputCreditCardCcvName, String inputSalutationName, String inputFirstNameName, String inputLastNameName, String inputStreet1Name, String inputStreet2Name, String inputCityName, String selectStateName, String inputPostalCodeName, String inputPhone1AreaCode, String inputPhone1Number, String inputDone, String creditCardType, String creditCardMonth, String creditCardYear, String salutation, String firstName, String lastName, String street1, String city, String state, String postalCode, String phone1AreaCode, String phone1Number, String refererUrl, String url) {
        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();

        requestParameters.add(new BasicNameValuePair(inputCreditCardTypeName, creditCardType));
        requestParameters.add(new BasicNameValuePair(inputCreditCardNumberName, cardNumber));
        requestParameters.add(new BasicNameValuePair(selectCreditCardMonthName, creditCardMonth));
        requestParameters.add(new BasicNameValuePair(selectCreditCardYearName, creditCardYear));
        requestParameters.add(new BasicNameValuePair(inputCreditCardCcvName, ccv));
        requestParameters.add(new BasicNameValuePair(inputSalutationName, salutation));
        requestParameters.add(new BasicNameValuePair(inputFirstNameName, firstName));
        requestParameters.add(new BasicNameValuePair(inputLastNameName, lastName));
        requestParameters.add(new BasicNameValuePair(inputStreet1Name, street1));
        requestParameters.add(new BasicNameValuePair(inputStreet2Name, ""));
        requestParameters.add(new BasicNameValuePair(inputCityName, city));
        requestParameters.add(new BasicNameValuePair(selectStateName, state));
        requestParameters.add(new BasicNameValuePair(inputPostalCodeName, postalCode));
        requestParameters.add(new BasicNameValuePair(inputPhone1AreaCode, phone1AreaCode));
        requestParameters.add(new BasicNameValuePair(inputPhone1Number, phone1Number));

        requestParameters.add(new BasicNameValuePair(inputDone, "Done"));

        Page page = this.httpPost(refererUrl, "https://buy.itunes.apple.com" + url, requestParameters);

        savePage(page, "buyAppStep3.html");

        if (page.getText().contains("Please contact iTunes support to complete this transaction")) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 购买App] 联系iTunes支持 (邮箱: %s, App ID: %s)", DateHelper.toString(new Date()), email, appId)));
            return false;
        } else if (page.getText().contains("The credit card number you entered is not a valid number")) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 购买App] 信用卡号码错误 (邮箱: %s, App ID: %s)", DateHelper.toString(new Date()), email, appId)));
            return false;
        } else if (page.getText().contains("Connections Get Started")) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 购买App] 返回页面错误 (邮箱: %s, App ID: %s)", DateHelper.toString(new Date()), email, appId)));
            return false;
        }

        return false;  //TODO
    }

    public static boolean buyApp(String machineName, String guid, String email, String appleIdPassword, String kbsync, String cardNumber, String ccv, String appId, Action1<CrawlerLoggingEvent> eventCallback, String proxyHost, int proxyPort) {
//        String needDiv = "1";
        String needDiv = "0";

        String origPage = "Genre-US-Mobile Software Applications-36-iphone";
        String origPage2 = "Genre-US-Mobile Software Applications-36-iphone";
        String origPageCh = "Mobile Software Applications-main";
        String origPageCh2 = "Mobile Software Applications-main";
        String origPageLocation = "Titledbox_Top Charts|Listbox_Top Grossing|Lockup_1|Buy";
        String origPageLocation2 = "Titledbox_Top Charts|Listbox_Top Grossing|Lockup_2|CopyLink";

        String refererUrl = "http://itunes.apple.com/WebObjects/MZStore.woa/wa/viewGrouping?id=25204&mt=8&s=143441";

        return buyApp(
                machineName, guid,
                email, appleIdPassword, kbsync, cardNumber, ccv,
                appId, needDiv,
                origPage, origPage2,
                origPageCh, origPageCh2,
                origPageLocation, origPageLocation2,
                refererUrl, eventCallback,
                proxyHost, proxyPort);
    }

    public static boolean buyApp(String machineName, String guid, String email, String appleIdPassword, String kbsync, String cardNumber, String ccv, String appId, String needDiv, String origPage, String origPage2, String origPageCh, String origPageCh2, String origPageLocation, String origPageLocation2, String refererUrl, Action1<CrawlerLoggingEvent> eventCallback, String proxyHost, int proxyPort) {
        BuyAppNoJSCrawler buyAppCrawler = new BuyAppNoJSCrawler(
                machineName, guid,
                email, appleIdPassword, kbsync, cardNumber, ccv,
                appId, needDiv,
                origPage, origPage2,
                origPageCh, origPageCh2,
                origPageLocation, origPageLocation2,
                refererUrl, eventCallback,
                proxyHost, proxyPort
        );

        return buyAppCrawler.buyApp();
    }
}
