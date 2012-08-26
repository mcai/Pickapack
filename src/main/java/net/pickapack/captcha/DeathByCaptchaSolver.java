package net.pickapack.captcha;

import net.pickapack.net.url.URLHelper;
import net.pickapack.spider.noJs.crawler.NoJSCrawler;
import net.pickapack.spider.noJs.spider.HttpMethod;
import net.pickapack.spider.noJs.spider.NoJSSpider;
import net.pickapack.spider.noJs.spider.Page;
import net.schmizz.sshj.common.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeathByCaptchaSolver implements CaptchaSolver {
    private NoJSCrawler crawler;
    private String userId;
    private String password;

    public DeathByCaptchaSolver(String userId, String password) {
        this(new NoJSCrawler(NoJSCrawler.FIREFOX_3_6, 60000) {
            @Override
            protected void onPageVisited(Page page, Map<String, Object> context) {
            }
        }, userId, password);
    }

    public DeathByCaptchaSolver(NoJSCrawler crawler, String userId, String password) {
        this.crawler = crawler;
        this.userId = userId;
        this.password = password;
    }

    public CaptchaSolveResult solveCaptcha(byte[] data) {
        System.out.println("Solving captcha");

        try {
            List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
            requestParameters.add(new BasicNameValuePair("username", this.userId));
            requestParameters.add(new BasicNameValuePair("password", this.password));
            requestParameters.add(new BasicNameValuePair("captchafile", "base64:" + Base64.encodeBytes(data)));

            Page page = this.getCrawler().getPage(new URL("http://api.dbcapi.me/api/captcha"), HttpMethod.POST, requestParameters, null, null);

            if(page.getResponse().getStatusCode() != 200) {
                System.out.println("Failed to upload captcha");
                return new CaptchaSolveResult(CaptchaSolveResultType.FAILED_UPLOADING, new Captcha(-1, data, null));
            }

            String response = page.getText();
            int captchaId = Integer.parseInt(URLHelper.getQueryParameterFromUrl("http://api.dbcapi.me/api/captcha?" + response, "captcha"));

            for(int i = 0; i < 12; i++) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                String captchaText = this.pollUploadedCaptcha(captchaId);
                if(captchaText != null) {
                    System.out.println("Captcha solved: " + captchaText);
                    return new CaptchaSolveResult(CaptchaSolveResultType.OK, new Captcha(captchaId, data, captchaText));
                }
            }

            System.out.println("Failed to solve captcha due to timeout");
            return new CaptchaSolveResult(CaptchaSolveResultType.FAILED_SOLVING, new Captcha(captchaId, data, null));
        } catch (IOException e) {
            NoJSSpider.recordException(e);
            System.out.println("Failed to solve captcha due to exception: " + e);
            return new CaptchaSolveResult(CaptchaSolveResultType.FAILED_MISC, new Captcha(-1, data, null));
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private String pollUploadedCaptcha(int captchaId) {
        try {
            Page page = this.getCrawler().getPage(new URL("http://api.dbcapi.me/api/captcha" + "/" + captchaId));
            String response = page.getText();
            return URLHelper.getQueryParameterFromUrl("http://api.dbcapi.me/api/captcha?" + response, "text");
        } catch (IOException e) {
            NoJSSpider.recordException(e);
            return null;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public double getBalance() {
        try {
            List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
            requestParameters.add(new BasicNameValuePair("username", this.userId));
            requestParameters.add(new BasicNameValuePair("password", this.password));

            Page page = this.getCrawler().getPage(new URL("http://api.dbcapi.me/api/user"), HttpMethod.POST, requestParameters, null, null);
            String response = page.getText();

            return Double.parseDouble(URLHelper.getQueryParameterFromUrl("http://api.dbcapi.me/?" + response, "balance"));
        } catch (IOException e) {
            NoJSSpider.recordException(e);
            return -1;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean reportWrongCaptcha(int captchaId) {
        return true; //TODO
    }

    public NoJSCrawler getCrawler() {
        return crawler;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public static void main(String[] args) throws IOException {
        NoJSCrawler crawler = new NoJSCrawler(NoJSSpider.FIREFOX_3_6, 60000, "10.26.27.29", 3128) {
            @Override
            protected void onPageVisited(Page page, Map<String, Object> context) {
            }
        };

        DeathByCaptchaSolver deathByCaptchaSolver = new DeathByCaptchaSolver(crawler, "fifoxbnc", "quskyoi2580");
        System.out.println(deathByCaptchaSolver.getBalance());

        byte[] captcha = crawler.urlToBytes("http://www.google.com/recaptcha/api/image?c=03AHJ_Vushy-Bw1AJhzCG9802OPgGiZIxX6x-sR6ZMzlq-IJWyhuJiI3dyzRoqn_WwqEb_sY-jovHJcEb8s4pYrilexu8jedj6rVvr3hbyqZM7fGa8SY98yQ3m-s5lldXvrgAXDc1k1NswLLVx_83dYNbqFzFuP1M_Aw");
        String captchaText = deathByCaptchaSolver.solveCaptcha(captcha).getCaptcha().getText();
        if(captchaText != null) {
            System.out.println("Captcha solved: " + captchaText);
        }
        else {
            System.out.println("Failed to solve captcha");
        }

        crawler.close();
    }
}
