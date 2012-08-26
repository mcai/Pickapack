package net.pickapack.spider.noJs.crawler.apple;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.action.Action1;
import net.pickapack.mail.EmailPrefixHelper;
import net.pickapack.net.url.URLHelper;
import net.pickapack.spider.noJs.crawler.CrawlerLoggingEvent;
import net.pickapack.spider.noJs.spider.Page;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegisterAppleIdNoJSCrawler extends iTunesNoJSCrawler {
    private String email;
    private String appleIdPassword;
    private String recoveryEmail;
    private int birthMonth;
    private int birthDay;
    private int birthYear;
    private String cardNumber;
    private String ccv;
    private int expirationMonth;
    private int expirationYear;
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String areaCode;
    private String phone;

    public RegisterAppleIdNoJSCrawler(String machineName, String guid, String email, String appleIdPassword, String recoveryEmail, int birthMonth, int birthDay, int birthYear, String cardNumber, String ccv, int expirationMonth, int expirationYear, String firstName, String lastName, String street, String city, String state, String postalCode, String areaCode, String phone, Action1<CrawlerLoggingEvent> eventCallback) {
        this(machineName, guid, email, appleIdPassword, recoveryEmail, birthMonth, birthDay, birthYear, cardNumber, ccv, expirationMonth, expirationYear, firstName, lastName, street, city, state, postalCode, areaCode, phone, eventCallback, null, -1);
    }

    public RegisterAppleIdNoJSCrawler(String machineName, String guid, String email, String appleIdPassword, String recoveryEmail, int birthMonth, int birthDay, int birthYear, String cardNumber, String ccv, int expirationMonth, int expirationYear, String firstName, String lastName, String street, String city, String state, String postalCode, String areaCode, String phone, Action1<CrawlerLoggingEvent> eventCallback, String proxyHost, int proxyPort) {
        super("iTunes/10.5.3 (Windows; Microsoft Windows XP Professional Service Pack 2 (Build 2600)) AppleWebKit/534.52.7", machineName, guid, proxyHost, proxyPort);
        this.email = email;
        this.appleIdPassword = appleIdPassword;
        this.recoveryEmail = recoveryEmail;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.birthYear = birthYear;
        this.cardNumber = cardNumber;
        this.ccv = ccv;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.areaCode = areaCode;
        this.phone = phone;

        this.getEventDispatcher().addListener(CrawlerLoggingEvent.class, eventCallback);
    }

    public boolean run() {
        this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 注册 Apple ID] 开始注册 Apple ID (邮箱: %s, 密码: %s, 信用卡号码: %s)", DateHelper.toString(new Date()), email, appleIdPassword, cardNumber != null ? cardNumber : "")));

        boolean registered = registerAppleIdStep1();

        this.close();

        if (registered) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 注册 Apple ID] Apple ID注册成功 (邮箱: %s, 密码: %s, 信用卡号码: %s)", DateHelper.toString(new Date()), email, appleIdPassword, cardNumber != null ? cardNumber : "")));
        } else {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 注册 Apple ID] Apple ID注册失败 (邮箱: %s, 密码: %s, 信用卡号码: %s)", DateHelper.toString(new Date()), email, appleIdPassword, cardNumber != null ? cardNumber : "")));
        }

        return registered;
    }

    private boolean registerAppleIdStep1() {
        autoDelay();

        this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 注册 Apple ID] 第1步", DateHelper.toString(new Date()))));

        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
        requestParameters.add(new BasicNameValuePair("machineName", machineName));
        requestParameters.add(new BasicNameValuePair("guid", guid));

        //TODO
        requestParameters.add(new BasicNameValuePair("product", "productType=C&price=0&salableAdamId=525463029&pricingParameters=STDQ&appExtVrsId=7899238&origPage=Genre-US-Mobile%20Software%20Applications-36-iphone&origPageCh=Mobile%20Software%20Applications-main&origPageLocation=Titledbox_Top%20Charts%7CListbox_Free%20Apps%7CLockup_4%7CBuy&origPage2=Genre-US-Main%20Main-38&origPageCh2=Main-main"));

        String url = "/WebObjects/MZFinance.woa/wa/signupWizard";

        Page page = this.httpGet("https://p" +
                pod +
                "-buy.itunes.apple.com" +
                url, requestParameters);

        if (page.getResponse().getStatusCode() == 302) {
            String location = page.getResponse().getHeader("location");
            pod = URLHelper.getQueryParameterPairFromUrl(location, "Pod").getValue();
            page = this.httpGet(location, null);
        }

        savePage(page, "registerAppleIdStep1.html");

        String refererUrl = url;

        url = page.getFirstByXPath("//a[@href][@role='button'][2]/@href").getNodeValue();

        return registerAppleIdStep2(refererUrl, url);
    }

    private boolean registerAppleIdStep2(String refererUrl, String url) {
        autoDelay();

        this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 注册 Apple ID] 第2步", DateHelper.toString(new Date()))));

        this.getHttpClient().getCookieStore().addCookie(newCookie("itsMetricsR", "Room-US-Great Free Apps-385787693@@RoomPages@@@@", ".apple.com", "/"));

        Page page = this.httpGet("https://p" + pod + "-buy.itunes.apple.com" + refererUrl, "https://p" + pod + "-buy.itunes.apple.com" + url, null);

        savePage(page, "registerAppleIdStep2.html");

        refererUrl = url;

        url = page.getFirstByXPath("//div[@class='accept-terms']//form[@action]/@action").getNodeValue();

        String inputIAgreeName = page.getFirstByXPath("//input[@id='iagree']/@name").getNodeValue();

        return registerAppleIdStep3(inputIAgreeName, refererUrl, url);
    }

    private boolean registerAppleIdStep3(String inputIAgreeName, String refererUrl, String url) {
        autoDelay();

        this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 注册 Apple ID] 第3步", DateHelper.toString(new Date()))));

        this.getHttpClient().getCookieStore().addCookie(newCookie("itsMetricsR", "Signup%20View%20Terms-US@@Signup@@@@", ".apple.com", "/"));

        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();

        requestParameters.add(new BasicNameValuePair(inputIAgreeName, inputIAgreeName));

        Page page = this.httpPost("https://p" + pod + "-buy.itunes.apple.com" + refererUrl, "https://p" + pod + "-buy.itunes.apple.com" + url, requestParameters);

        savePage(page, "registerAppleIdStep3.html");

        String inputEmailAddressName = page.getFirstByXPath("//input[@id='emailAddress']/@name").getNodeValue();

        String inputPassword1Name = page.getFirstByXPath("//input[@id='pass1']/@name").getNodeValue();
        String inputPassword2Name = page.getFirstByXPath("//input[@id='pass2']/@name").getNodeValue();

        String selectQuestion1Name = page.getFirstByXPath("//select[@id='question1']/@name").getNodeValue();
        String inputAnswer1Name = page.getFirstByXPath("//input[@id='answer1']/@name").getNodeValue();

        String selectQuestion2Name = page.getFirstByXPath("//select[@id='question2']/@name").getNodeValue();
        String inputAnswer2Name = page.getFirstByXPath("//input[@id='answer2']/@name").getNodeValue();

        String selectQuestion3Name = page.getFirstByXPath("//select[@id='question3']/@name").getNodeValue();
        String inputAnswer3Name = page.getFirstByXPath("//input[@id='answer3']/@name").getNodeValue();

        String inputRecoveryEmailName = page.getFirstByXPath("//input[@id='recoveryEmail']/@name").getNodeValue();

        String selectBirthMonthName = page.getFirstByXPath("//select[@id='birthMonthPopup']/@name").getNodeValue();
        String selectBirthDayName = page.getFirstByXPath("//select[@id='birthDayPopup']/@name").getNodeValue();
        String inputBirthYearName = page.getFirstByXPath("//input[@id='birthYear']/@name").getNodeValue();

        String inputNewsLetterName = page.getFirstByXPath("//input[@id='newsletter']/@name").getNodeValue();
        String inputMarketingName = page.getFirstByXPath("//input[@id='marketing']/@name").getNodeValue();

        String inputContinueName = page.getFirstByXPath("//input[@value='Continue']/@name").getNodeValue();

        refererUrl = url;

        url = page.getFirstByXPath("//form[@action]/@action").getNodeValue();

        return registerAppleIdStep4(
                inputEmailAddressName,
                inputPassword1Name, inputPassword2Name,
                selectQuestion1Name, inputAnswer1Name,
                selectQuestion2Name, inputAnswer2Name,
                selectQuestion3Name, inputAnswer3Name,
                inputRecoveryEmailName,
                selectBirthMonthName, selectBirthDayName, inputBirthYearName,
                inputNewsLetterName, inputMarketingName, inputContinueName,
                refererUrl, url
        );
    }

    private boolean registerAppleIdStep4(String inputEmailAddressName, String inputPassword1Name, String inputPassword2Name, String selectQuestion1Name, String inputAnswer1Name, String selectQuestion2Name, String inputAnswer2Name, String selectQuestion3Name, String inputAnswer3Name, String inputRecoveryEmailName, String selectBirthMonthName, String selectBirthDayName, String inputBirthYearName, String inputNewsLetterName, String inputMarketingName, String inputContinueName, String refererUrl, String url) {
        autoDelay();

        this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 注册 Apple ID] 第4步", DateHelper.toString(new Date()))));

        this.getHttpClient().getCookieStore().addCookie(newCookie("itsMetricsR", "Signup%20View%20Terms-US@@Signup@@@@", ".apple.com", "/"));

        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();

        requestParameters.add(new BasicNameValuePair(inputEmailAddressName, this.email));
        requestParameters.add(new BasicNameValuePair(inputPassword1Name, this.appleIdPassword));
        requestParameters.add(new BasicNameValuePair(inputPassword2Name, this.appleIdPassword));
        requestParameters.add(new BasicNameValuePair(selectQuestion1Name, "" + random.nextInt(5)));
        requestParameters.add(new BasicNameValuePair(inputAnswer1Name, RandomStringUtils.randomAlphanumeric(10 + random.nextInt(20))));
        requestParameters.add(new BasicNameValuePair(selectQuestion2Name, "" + random.nextInt(5)));
        requestParameters.add(new BasicNameValuePair(inputAnswer2Name, RandomStringUtils.randomAlphanumeric(10 + random.nextInt(20))));
        requestParameters.add(new BasicNameValuePair(selectQuestion3Name, "" + random.nextInt(5)));
        requestParameters.add(new BasicNameValuePair(inputAnswer3Name, RandomStringUtils.randomAlphanumeric(10 + random.nextInt(20))));
        requestParameters.add(new BasicNameValuePair(inputRecoveryEmailName, this.recoveryEmail));
        requestParameters.add(new BasicNameValuePair(selectBirthMonthName, "" + (this.birthMonth - 1)));
        requestParameters.add(new BasicNameValuePair(selectBirthDayName, "" + (this.birthDay - 1)));
        requestParameters.add(new BasicNameValuePair(inputBirthYearName, "" + this.birthYear));
        requestParameters.add(new BasicNameValuePair(inputNewsLetterName, inputNewsLetterName));
        requestParameters.add(new BasicNameValuePair(inputMarketingName, inputMarketingName));
        requestParameters.add(new BasicNameValuePair(inputContinueName, "Continue"));

        Page page = this.httpPost("https://p" + pod + "-buy.itunes.apple.com" + refererUrl, "https://p" + pod + "-buy.itunes.apple.com" + url, requestParameters);

        savePage(page, "registerAppleIdStep4.html");

        if(page.getText().contains("The email address you entered is already associated with an Apple ID")) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 注册 Apple ID] Apple ID 已被注册 (邮箱: %s, 密码: %s, 信用卡号码: %s)", DateHelper.toString(new Date()), email, appleIdPassword, cardNumber != null ? cardNumber : "")));
            return false;
        }

        if (page.getFirstByXPath("//select[@id='country']/@name") == null) {
            return false;
        }

        String selectCountryName = page.getFirstByXPath("//select[@id='country']/@name").getNodeValue();
        String inputCreditCardTypeName = page.getFirstByXPath("//input[@name='credit-card-type']/@name").getNodeValue();
        String inputCreditCardNumberName = page.getFirstByXPath("//input[@id='cc_number']/@name").getNodeValue();
        String selectCreditCardMonthName = page.getFirstByXPath("//select[@id='cc_month']/@name").getNodeValue();
        String selectCreditCardYearName = page.getFirstByXPath("//select[@id='cc_year']/@name").getNodeValue();
        String inputCreditCardCcvName = page.getFirstByXPath("//input[@id='cc_ccv']/@name").getNodeValue();
        String inputRedemptionCodeName = page.getFirstByXPath("//input[@id='codeRedemptionField']/@name").getNodeValue();
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
        String inputCreateAppleIdName = page.getFirstByXPath("//input[@value='Create Apple ID']/@name").getNodeValue();

        refererUrl = url;

        url = page.getFirstByXPath("//form[@action]/@action").getNodeValue();

        return registerAppleIdStep5(
                selectCountryName, inputCreditCardTypeName,
                inputCreditCardNumberName, selectCreditCardMonthName, selectCreditCardYearName, inputCreditCardCcvName,
                inputRedemptionCodeName, inputSalutationName,
                inputFirstNameName, inputLastNameName,
                inputStreet1Name, inputStreet2Name,
                inputCityName, selectStateName,
                inputPostalCodeName,
                inputPhone1AreaCodeName, inputPhone1NumberName, inputCreateAppleIdName,
                refererUrl, url
        );
    }

    private boolean registerAppleIdStep5(String selectCountryName, String inputCreditCardTypeName, String inputCreditCardNumberName, String selectCreditCardMonthName, String selectCreditCardYearName, String inputCreditCardCcvName, String inputRedemptionCodeName, String inputSalutationName, String inputFirstNameName, String inputLastNameName, String inputStreet1Name, String inputStreet2Name, String inputCityName, String selectStateName, String inputPostalCodeName, String inputPhone1AreaCodeName, String inputPhone1NumberName, String inputCreateAppleIdName, String refererUrl, String url) {
        autoDelay();

        this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 注册 Apple ID] 第5步", DateHelper.toString(new Date()))));

        this.getHttpClient().getCookieStore().addCookie(newCookie("itsMetricsR", "Signup%2DAccount-US@@Signup@@@@", ".apple.com", "/"));

        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
        requestParameters.add(new BasicNameValuePair(selectCountryName, "US"));
        requestParameters.add(new BasicNameValuePair(inputCreditCardTypeName, ""));

        if (cardNumber == null || cardNumber.isEmpty()) {
            requestParameters.add(new BasicNameValuePair("None", "None"));
        } else if (cardNumber.startsWith("4")) {
            requestParameters.add(new BasicNameValuePair("Visa", "Visa"));
        } else if (cardNumber.startsWith("5")) {
            requestParameters.add(new BasicNameValuePair("MasterCard", "MasterCard"));
        } else {
            throw new IllegalArgumentException();
        }

        requestParameters.add(new BasicNameValuePair(inputCreditCardNumberName, ""));
        requestParameters.add(new BasicNameValuePair(selectCreditCardMonthName, "0"));
        requestParameters.add(new BasicNameValuePair(selectCreditCardYearName, "0"));
        requestParameters.add(new BasicNameValuePair(inputCreditCardCcvName, ""));
        requestParameters.add(new BasicNameValuePair(inputRedemptionCodeName, ""));
        requestParameters.add(new BasicNameValuePair(inputSalutationName, "WONoSelectionString"));
        requestParameters.add(new BasicNameValuePair(inputFirstNameName, ""));
        requestParameters.add(new BasicNameValuePair(inputLastNameName, ""));
        requestParameters.add(new BasicNameValuePair(inputStreet1Name, ""));
        requestParameters.add(new BasicNameValuePair(inputStreet2Name, ""));
        requestParameters.add(new BasicNameValuePair(inputCityName, ""));
        requestParameters.add(new BasicNameValuePair(selectStateName, "WONoSelectionString"));
        requestParameters.add(new BasicNameValuePair(inputPostalCodeName, ""));
        requestParameters.add(new BasicNameValuePair(inputPhone1NumberName, ""));
        requestParameters.add(new BasicNameValuePair(inputCreateAppleIdName, ""));

        Page page = this.httpPost("https://p" + pod + "-buy.itunes.apple.com" + refererUrl, "https://p" + pod + "-buy.itunes.apple.com" + url, requestParameters);

        savePage(page, "registerAppleIdStep5.html");

        selectCountryName = page.getFirstByXPath("//select[@id='country']/@name").getNodeValue();
        inputCreditCardTypeName = page.getFirstByXPath("//input[@name='credit-card-type']/@name").getNodeValue();
        inputCreditCardNumberName = cardNumber == null || cardNumber.isEmpty() ? "" : page.getFirstByXPath("//input[@id='cc_number']/@name").getNodeValue();
        selectCreditCardMonthName = cardNumber == null || cardNumber.isEmpty() ? "" : page.getFirstByXPath("//select[@id='cc_month']/@name").getNodeValue();
        selectCreditCardYearName = cardNumber == null || cardNumber.isEmpty() ? "" : page.getFirstByXPath("//select[@id='cc_year']/@name").getNodeValue();
        inputCreditCardCcvName = cardNumber == null || cardNumber.isEmpty() ? "" : page.getFirstByXPath("//input[@id='cc_ccv']/@name").getNodeValue();
        inputRedemptionCodeName = page.getFirstByXPath("//input[@id='codeRedemptionField']/@name").getNodeValue();
        inputSalutationName = page.getFirstByXPath("//select[@id='salutation']/@name").getNodeValue();
        inputFirstNameName = page.getFirstByXPath("//input[@id='firstName']/@name").getNodeValue();
        inputLastNameName = page.getFirstByXPath("//input[@id='lastName']/@name").getNodeValue();
        inputStreet1Name = page.getFirstByXPath("//input[@id='street1']/@name").getNodeValue();
        inputStreet2Name = page.getFirstByXPath("//input[@id='street2']/@name").getNodeValue();
        inputCityName = page.getFirstByXPath("//input[@id='city']/@name").getNodeValue();
        selectStateName = page.getFirstByXPath("//select[@id='state']/@name").getNodeValue();
        inputPostalCodeName = page.getFirstByXPath("//input[@id='postalcode']/@name").getNodeValue();
        inputPhone1AreaCodeName = page.getFirstByXPath("//input[@id='phone1AreaCode']/@name").getNodeValue();
        inputPhone1NumberName = page.getFirstByXPath("//input[@id='phone1Number']/@name").getNodeValue();
        inputCreateAppleIdName = page.getFirstByXPath("//input[@value='Create Apple ID']/@name").getNodeValue();

        url = page.getFirstByXPath("//form[@action]/@action").getNodeValue();

        return registerAppleIdStep6(selectCountryName,
                inputCreditCardTypeName, inputCreditCardNumberName,
                selectCreditCardMonthName, selectCreditCardYearName, inputCreditCardCcvName,
                inputRedemptionCodeName, inputSalutationName,
                inputFirstNameName, inputLastNameName,
                inputStreet1Name, inputStreet2Name,
                inputCityName, selectStateName,
                inputPostalCodeName, inputPhone1AreaCodeName, inputPhone1NumberName, inputCreateAppleIdName,
                refererUrl, url);
    }

    private boolean registerAppleIdStep6(String selectCountryName, String inputCreditCardTypeName, String inputCreditCardNumberName, String selectCreditCardMonthName, String selectCreditCardYearName, String inputCreditCardCcvName, String inputRedemptionCodeName, String inputSalutationName, String inputFirstNameName, String inputLastNameName, String inputStreet1Name, String inputStreet2Name, String inputCityName, String selectStateName, String inputPostalCodeName, String inputPhone1AreaCode, String inputPhone1Number, String inputCreateAppleIdName, String refererUrl, String url) {
        autoDelay();

        this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 注册 Apple ID] 第6步", DateHelper.toString(new Date()))));

        this.getHttpClient().getCookieStore().addCookie(newCookie("itsMetricsR", "Signup%2DAddress-US@@Signup@@@@", ".apple.com", "/"));

        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
        requestParameters.add(new BasicNameValuePair(selectCountryName, "US"));
        requestParameters.add(new BasicNameValuePair(inputCreditCardTypeName, ""));

        if (cardNumber == null || cardNumber.isEmpty()) {
            requestParameters.add(new BasicNameValuePair(inputRedemptionCodeName, ""));
            requestParameters.add(new BasicNameValuePair(inputSalutationName, "0"));
            requestParameters.add(new BasicNameValuePair(inputFirstNameName, RandomStringUtils.randomAlphabetic(6 + random.nextInt(10))));
            requestParameters.add(new BasicNameValuePair(inputLastNameName, RandomStringUtils.randomAlphabetic(10 + random.nextInt(5))));
            requestParameters.add(new BasicNameValuePair(inputStreet1Name, RandomStringUtils.randomAlphabetic(10 + random.nextInt(5))));
            requestParameters.add(new BasicNameValuePair(inputStreet2Name, ""));
            requestParameters.add(new BasicNameValuePair(inputCityName, RandomStringUtils.randomAlphabetic(10 + random.nextInt(5))));
            requestParameters.add(new BasicNameValuePair(selectStateName, "" + (1 + random.nextInt(49))));
            requestParameters.add(new BasicNameValuePair(inputPostalCodeName, RandomStringUtils.random(5, "123456789")));
            requestParameters.add(new BasicNameValuePair(inputPhone1AreaCode, RandomStringUtils.random(3, "123456789")));
            requestParameters.add(new BasicNameValuePair(inputPhone1Number, RandomStringUtils.random(7, "123456789")));
        } else {
            requestParameters.add(new BasicNameValuePair(inputCreditCardNumberName, cardNumber));
            requestParameters.add(new BasicNameValuePair(selectCreditCardMonthName, "" + (expirationMonth - 1)));
            requestParameters.add(new BasicNameValuePair(selectCreditCardYearName, "" + (expirationYear - 2012)));
            requestParameters.add(new BasicNameValuePair(inputCreditCardCcvName, ccv));
            requestParameters.add(new BasicNameValuePair(inputRedemptionCodeName, ""));
            requestParameters.add(new BasicNameValuePair(inputSalutationName, "0"));
            requestParameters.add(new BasicNameValuePair(inputFirstNameName, this.firstName));
            requestParameters.add(new BasicNameValuePair(inputLastNameName, this.lastName));
            requestParameters.add(new BasicNameValuePair(inputStreet1Name, this.street));
            requestParameters.add(new BasicNameValuePair(inputStreet2Name, ""));
            requestParameters.add(new BasicNameValuePair(inputCityName, this.city));
            requestParameters.add(new BasicNameValuePair(selectStateName, "" + (1 + USState.getByName(state).getId())));
            requestParameters.add(new BasicNameValuePair(inputPostalCodeName, this.postalCode));
            requestParameters.add(new BasicNameValuePair(inputPhone1AreaCode, this.areaCode));
            requestParameters.add(new BasicNameValuePair(inputPhone1Number, this.phone));
        }

        requestParameters.add(new BasicNameValuePair(inputCreateAppleIdName, "Create Apple ID"));

        Page page = this.httpPost("https://p" + pod + "-buy.itunes.apple.com" + refererUrl, "https://p" + pod + "-buy.itunes.apple.com" + url, requestParameters);

        savePage(page, "registerAppleIdStep6.html");

        if (page.getText().contains("Please contact iTunes support to complete this transaction")) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 注册 Apple ID] 联系iTunes支持 (邮箱: %s, 密码: %s, 信用卡号码: %s)", DateHelper.toString(new Date()), email, appleIdPassword, cardNumber != null ? cardNumber : "")));
            return false;
        } else if (page.getText().contains("Connections Get Started")) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 注册 Apple ID] 返回页面错误 (邮箱: %s, 密码: %s, 信用卡号码: %s)", DateHelper.toString(new Date()), email, appleIdPassword, cardNumber != null ? cardNumber : "")));
            return false;
        } else if (page.getText().contains("Your request is temporarily unable to be processed")) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 注册 Apple ID] 请求暂时无法处理 (邮箱: %s, 密码: %s, 信用卡号码: %s)", DateHelper.toString(new Date()), email, appleIdPassword, cardNumber != null ? cardNumber : "")));
            return false;
        } else if (page.getText().contains("The email address you entered is already associated with an Apple ID")) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 注册 Apple ID] Apple ID 已被注册 (邮箱: %s, 密码: %s, 信用卡号码: %s)", DateHelper.toString(new Date()), email, appleIdPassword, cardNumber != null ? cardNumber : "")));
            return false;
        }

        return page.getText().contains("Verify Your Apple ID");
    }

    @Override
    protected List<Header> prepareHeaders() {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Origin", "https://p" + pod + "-buy.itunes.apple.com"));

        headers.add(new BasicHeader("X-Apple-Store-Front", mainlandChina ? "143465-2,12" : "143441-1,12")); //Mainland China/US

        headers.add(new BasicHeader("X-Apple-Tz", "28800"));
        headers.add(new BasicHeader("Accept-Language", "zh-cn, zh;q=0.75, en-us;q=0.50, en;q=0.25"));
        headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
        return headers;
    }

    public String getEmail() {
        return email;
    }

    public String getAppleIdPassword() {
        return appleIdPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getPhone() {
        return phone;
    }

    public String getRecoveryEmail() {
        return recoveryEmail;
    }

    public static boolean registerNewAppleId(String machineName, String guid, String email, String appleIdPassword, String cardNumber, String ccv, int expirationMonth, int expirationYear, String firstName, String lastName, String street, String city, String state, String postalCode, String areaCode, String phone, Action1<CrawlerLoggingEvent> eventCallback, String proxyHost, int proxyPort) {
        String recoveryEmail = EmailPrefixHelper.generateEmailPrefix() + "@gmail.com";

        int birthMonth = 1 + random.nextInt(12);
        int birthDay = 1 + random.nextInt(28);
        int birthYear = 1960 + random.nextInt(30);

        RegisterAppleIdNoJSCrawler crawler = new RegisterAppleIdNoJSCrawler(machineName, guid, email, appleIdPassword, recoveryEmail, birthMonth, birthDay, birthYear, cardNumber, ccv, expirationMonth, expirationYear, firstName, lastName, street, city, state, postalCode, areaCode, phone, eventCallback, proxyHost, proxyPort);
        return crawler.run();
    }

    public static boolean registerNewFreeAppleId(String machineName, String guid, String email, String appleIdPassword, Action1<CrawlerLoggingEvent> eventCallback, String proxyHost, int proxyPort) {
        String recoveryEmail = EmailPrefixHelper.generateEmailPrefix() + "@gmail.com";

        int birthMonth = 1 + random.nextInt(12);
        int birthDay = 1 + random.nextInt(28);
        int birthYear = 1960 + random.nextInt(30);

        RegisterAppleIdNoJSCrawler crawler = new RegisterAppleIdNoJSCrawler(machineName, guid, email, appleIdPassword, recoveryEmail, birthMonth, birthDay, birthYear, null, null, -1, -1, null, null, null, null, null, null, null, null, eventCallback, proxyHost, proxyPort);
        return crawler.run();
    }
}
