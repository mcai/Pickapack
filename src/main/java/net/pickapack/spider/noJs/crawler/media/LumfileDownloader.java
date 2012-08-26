package net.pickapack.spider.noJs.crawler.media;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.spider.noJs.crawler.NoJSCrawler;
import net.pickapack.spider.noJs.spider.HttpMethod;
import net.pickapack.spider.noJs.spider.NoJSSpider;
import net.pickapack.spider.noJs.spider.Page;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class LumfileDownloader extends MediaFileDownloader {
    public LumfileDownloader(NoJSCrawler crawler) {
        super(HOST, crawler);
    }

    @Override
    public void doDownloadMediaFile(String storageFolder, URL url) {
        try {
            Page page = this.getCrawler().getPage(url);
            String op = page.getFirstByXPath("//div[@id='contact']//form//input[@name='op']//@value").getNodeValue();
            String usrLogin = page.getFirstByXPath("//div[@id='contact']//form//input[@name='usr_login']//@value").getNodeValue();
            String id = page.getFirstByXPath("//div[@id='contact']//form//input[@name='id']//@value").getNodeValue();
            String fname = page.getFirstByXPath("//div[@id='contact']//form//input[@name='fname']//@value").getNodeValue();
            String referer = page.getFirstByXPath("//div[@id='contact']//form//input[@name='referer']//@value").getNodeValue();

            ArrayList<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
            requestParameters.add(new BasicNameValuePair("op", op));
            requestParameters.add(new BasicNameValuePair("usr_login", usrLogin));
            requestParameters.add(new BasicNameValuePair("id", id));
            requestParameters.add(new BasicNameValuePair("fname", fname));
            requestParameters.add(new BasicNameValuePair("referer", referer));
            requestParameters.add(new BasicNameValuePair("method_free", "Download slow speed"));

            page = this.getCrawler().getPage(url, HttpMethod.POST, requestParameters, null, null);

            if (page.getText().contains("You have to wait ")) {
                System.out.println("File download cancelled due to download limit");
                return;
            }

            String op2 = page.getFirstByXPath("//div[@id='freedownload']//form//input[@name='op']//@value").getNodeValue();
            String id2 = page.getFirstByXPath("//div[@id='freedownload']//form//input[@name='id']//@value").getNodeValue();
            String rand2 = page.getFirstByXPath("//div[@id='freedownload']//form//input[@name='rand']//@value").getNodeValue();
            String referer2 = page.getFirstByXPath("//div[@id='freedownload']//form//input[@name='referer']//@value").getNodeValue();

            String recaptchaScriptSrc = page.getFirstByXPath("//script[starts-with(@src,'http://www.google.com/recaptcha/api/challenge?k=')]/@src").getNodeValue();

            String challenge = getRecaptchaChallenge(recaptchaScriptSrc);
            String captcha = solveRecaptchaChallenge(challenge);
            if (captcha != null) {
                ArrayList<NameValuePair> requestParameters2 = new ArrayList<NameValuePair>();
                requestParameters2.add(new BasicNameValuePair("op", op2));
                requestParameters2.add(new BasicNameValuePair("id", id2));
                requestParameters2.add(new BasicNameValuePair("rand", rand2));
                requestParameters2.add(new BasicNameValuePair("referer", referer2));
                requestParameters2.add(new BasicNameValuePair("method_free", "Download slow speed"));
                requestParameters2.add(new BasicNameValuePair("method_premium", ""));
                requestParameters2.add(new BasicNameValuePair("recaptcha_challenge_field", challenge));
                requestParameters2.add(new BasicNameValuePair("recaptcha_response_field", captcha));
                requestParameters2.add(new BasicNameValuePair("down_script", "1"));

                Page pageMedia = this.getCrawler().getPage(url, HttpMethod.POST, requestParameters2, null, null);
                if (pageMedia.getResponse().getStatusCode() == 302) {
                    String location = pageMedia.getResponse().getHeader("Location");
                    String hashedUrl = this.getCrawler().getHashedUrl(location);
                    File localFile = new File(storageFolder, hashedUrl);
                    System.out.printf("[%s]         Downloading document: %s to %s\n", DateHelper.toString(new Date()), location, localFile.getAbsolutePath());
                    this.getCrawler().getEventDispatcher().dispatch(new MediaFileBeginDownloadingEvent(location, localFile.getAbsolutePath()));
//                    this.getCrawler().downloadDocument(null, new URL(location), localFile);
                } else {
                    System.out.println("Failed to download file due to incorrect captcha");
                }
            } else {
                System.out.println("File download cancelled by user when typing captcha");
            }
        } catch (IOException e) {
            NoJSSpider.recordException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String HOST = "lumfile.com";
}
