package net.pickapack.spider.withJs;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.lang.RandomStringUtils;

import java.io.IOException;
import java.util.Random;

public class TestSpider extends WebSpider {
    public TestSpider() {
        super(BrowserVersion.FIREFOX_3_6);
        this.getWebClient().setJavaScriptEnabled(true);
    }

    public void run(String userId, String password) {
        try {
            HtmlPage page = this.getWebClient().getPage("http://reg.email.163.com/mailregAll/reg0.jsp");

            HtmlInput inputUserId = page.getFirstByXPath("//input[@id='unameInp']");
            inputUserId.setValueAttribute(userId);

            HtmlInput inputPassword = page.getFirstByXPath("//input[@id='passwInp']");
            inputPassword.setValueAttribute(password);

            HtmlInput inputPasswordConfirm = page.getFirstByXPath("//input[@id='passConfim']");
            inputPasswordConfirm.setValueAttribute(password);



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String userId = RandomStringUtils.randomAlphanumeric(10 + new Random().nextInt(10));
        String password = RandomStringUtils.randomAlphanumeric(10 + new Random().nextInt(10));

        TestSpider testSpider = new TestSpider();
        testSpider.run(userId, password);
    }
}
