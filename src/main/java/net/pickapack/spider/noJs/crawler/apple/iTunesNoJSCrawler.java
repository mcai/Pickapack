package net.pickapack.spider.noJs.crawler.apple;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.net.url.URLHelper;
import net.pickapack.spider.noJs.crawler.CrawlerLoggingEvent;
import net.pickapack.spider.noJs.crawler.NoJSCrawler;
import net.pickapack.spider.noJs.spider.HttpMethod;
import net.pickapack.spider.noJs.spider.Page;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.net.URL;
import java.util.*;

public abstract class iTunesNoJSCrawler extends NoJSCrawler {
    protected static Random random = new Random();
    public static final String USER_AGENT_ITUNES = "iTunes/10.6 (Windows; Microsoft Windows 7 x64 Ultimate Edition Service Pack 1 (Build 7601)) AppleWebKit/534.54.16";
    public static final String USER_AGENT_IPHONE = "iTunes-iPhone/5.0 (4; 32GB)";

    //        protected boolean mainlandChina = true; //TODO
    protected boolean mainlandChina = false; //TODO

    protected String pod;
    protected String machineName;
    protected String guid;

    public iTunesNoJSCrawler(String userAgent, String machineName, String guid, String proxyHost, int proxyPort) {
        super(userAgent, 60000, proxyHost, proxyPort);

        this.machineName = machineName;
        this.guid = guid;
        this.pod = "12";

        this.getHttpClient().getCookieStore().addCookie(newCookie("groupingPillToken", "1_iphone", ".apple.com", "/"));
        this.getHttpClient().getCookieStore().addCookie(newCookie("itsMetricsR", "Genre-US-Main Main-38@@Main-main@@@@", ".apple.com", "/"));
        this.getHttpClient().getCookieStore().addCookie(newCookie("mz_user_info_version", "0", ".apple.com", ""));
        this.getHttpClient().getCookieStore().addCookie(newCookie("Pod", pod, ".apple.com", "/"));
        this.getHttpClient().getCookieStore().addCookie(newCookie("s_membership", "1%3Ait10", ".apple.com", "/"));
        this.getHttpClient().getCookieStore().addCookie(newCookie("s_vi", "[CS]v1|2780157E8516150A-600001A64029221D[CE]", ".apple.com", "/"));
        this.getHttpClient().getCookieStore().addCookie(newCookie("s_vnum_n2_us", "0%7C1", ".apple.com", ""));
    }

    public static String generateAppleIdPassword() {
        String result = RandomStringUtils.randomAlphabetic(1 + random.nextInt(3)).toUpperCase() + RandomStringUtils.randomAlphabetic(1 + random.nextInt(3)).toLowerCase() + RandomStringUtils.randomNumeric(6 + random.nextInt(3));

        if (containsRepetitiveDigits(result)) {
            return generateAppleIdPassword();
        }

        return result;
    }

    private static boolean containsRepetitiveDigits(String tpin) {
        char prevChar = tpin.charAt(0);
        for (int i = 1; i < tpin.length(); i++) {
            char nextChar = tpin.charAt(i);
            if ((Character.valueOf(nextChar)).compareTo(prevChar) == 0) {
                return true;
            }
            prevChar = nextChar;
        }
        return false;
    }

    protected static String generateMachineName() {
        return RandomStringUtils.randomAlphabetic(10 + random.nextInt(10));
    }

    protected static String generateGuid() {
        List<String> parts = new ArrayList<String>();

//        String guid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
//
//        for (int i = 0; i < guid.length(); i++) {
//            sb.append(guid.charAt(i));
//
//            if (i > 0 && i < guid.length() - 1 && (i + 1) % 8 == 0) {
//                sb.append(".");
//            }
//        }

        for (int i = 0; i < 7; i++) {
            parts.add(String.format("%04X%04X", random.nextInt(0xffff), random.nextInt(0xffff)));
        }

        return StringUtils.join(parts, ".");
    }

    private void addSavedCookies() {
        for (Cookie cookie : loadCookies()) {
            this.getHttpClient().getCookieStore().addCookie(cookie);
        }
    }

    protected void savePage(Page page, String fileName) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(FileUtils.getUserDirectoryPath() + File.separator + fileName));
            pw.println(page.getText());
            pw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Page httpGet(String refererUrl, String url, List<NameValuePair> requestParameters) {
        try {
            List<Header> extraHeaders = prepareHeaders();
            if (refererUrl != null) {
                extraHeaders.add(new BasicHeader("Referer", refererUrl));
            }

            return this.getPage(new URL(url), HttpMethod.GET, requestParameters, extraHeaders, DEFAULT_CHARSET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    protected Page httpGet(String url, List<NameValuePair> requestParameters) {
        return httpGet(null, url, requestParameters);
    }

    protected Page httpPost(String refererUrl, String url, List<NameValuePair> requestParameters) {
        try {
            List<Header> extraHeaders = prepareHeaders();
            if (refererUrl != null) {
                extraHeaders.add(new BasicHeader("Referer", refererUrl));
            }

            return this.getPage(new URL(url), HttpMethod.POST, requestParameters, extraHeaders, DEFAULT_CHARSET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    protected Page httpPost(String refererUrl, String url, String body) {
        try {
            List<Header> extraHeaders = prepareHeaders();
            if (refererUrl != null) {
                extraHeaders.add(new BasicHeader("Referer", refererUrl));
            }

            return this.getPage(new URL(url), HttpMethod.POST, body, extraHeaders, DEFAULT_CHARSET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    protected Page httpPost(String url, List<NameValuePair> requestParameters) {
        return httpPost(null, url, requestParameters);
    }

    protected Page httpPost(String url, String body) {
        return httpPost(null, url, body);
    }

    protected static Cookie newCookie(String name, String value, String domain, String path) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setVersion(0);
        cookie.setDomain(domain);
        cookie.setPath(path);
        return cookie;
    }

    protected abstract List<Header> prepareHeaders();

    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());
    }

    protected void autoDelay() {
        if (autoDelayEnabled) {
            try {
                int delayInSeconds = 5 + random.nextInt(5);

                this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 自动延迟] 自动延迟 %s 秒", DateHelper.toString(new Date()), delayInSeconds)));

                Thread.sleep(delayInSeconds * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public AppInfo getAppInfo(String appId) {
        autoDelay();

        this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 获取App信息] 开始获取App信息 (App ID: %s)", DateHelper.toString(new Date()), appId)));

        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();

        requestParameters.add(new BasicNameValuePair("mt", "8"));

        Page page = this.httpGet("http://itunes.apple.com/us/app/id" + appId, requestParameters);

        if (page.getText().contains("requested is not currently available")) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 获取App信息] App目前已经下架 (App ID: %s)", DateHelper.toString(new Date()), appId)));
            return null;
        }

        String buyParams = page.getFirstByXPath("//div[@class='multi-button buy large application']/div/button[@metrics-loc='Buy']/@buy-params").getNodeValue();

        String appExtVrsId = URLHelper.getQueryParameterFromUrl("?" + buyParams, "appExtVrsId");
        String price = URLHelper.getQueryParameterFromUrl("?" + buyParams, "price");
        String pricingParameters = URLHelper.getQueryParameterFromUrl("?" + buyParams, "pricingParameters");
        String productType = URLHelper.getQueryParameterFromUrl("?" + buyParams, "productType");
        String salableAdamId = URLHelper.getQueryParameterFromUrl("?" + buyParams, "salableAdamId");

        this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 获取App信息] 获取App信息成功 (App ID: %s)", DateHelper.toString(new Date()), appId)));

        return new AppInfo(appExtVrsId, price, pricingParameters, productType, salableAdamId);
    }

    //    private static boolean autoDelayEnabled = true;
    private static boolean autoDelayEnabled = false;

    public static List<Cookie> loadCookies(Reader reader) {
        List<Cookie> cookies = new ArrayList<Cookie>();

        BufferedReader br = new BufferedReader(reader);
        try {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.matches("\\s*(?:#.*)?")) {
                    String[] tokens = line.split("\\t");
                    if (tokens.length == 7) {
                        String domain = tokens[0];
                        String name = tokens[5];
                        String value = tokens[6];
                        String path = tokens[2];

                        System.out.printf("domain: %s, name: %s, value: %s, path: %s\n", domain, name, value, path);

                        cookies.add(newCookie(name, value, domain, path));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return cookies;
    }

    private static List<Cookie> loadCookies() {
        try {
            return loadCookies(new FileReader("/home/itecgo/Desktop/cookies.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
