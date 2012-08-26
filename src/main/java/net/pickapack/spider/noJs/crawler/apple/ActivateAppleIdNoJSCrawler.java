package net.pickapack.spider.noJs.crawler.apple;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.action.Action1;
import net.pickapack.mail.HotmailHelper;
import net.pickapack.spider.noJs.crawler.CrawlerLoggingEvent;
import net.pickapack.spider.noJs.crawler.NoJSCrawler;
import net.pickapack.spider.noJs.spider.HttpMethod;
import net.pickapack.spider.noJs.spider.Page;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.SearchTerm;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ActivateAppleIdNoJSCrawler extends NoJSCrawler {
    public ActivateAppleIdNoJSCrawler(Action1<CrawlerLoggingEvent> eventCallback) {
        this(eventCallback, null, -1);
    }

    public ActivateAppleIdNoJSCrawler(Action1<CrawlerLoggingEvent> eventCallback, String proxyHost, int proxyPort) {
        super(FIREFOX_3_6, 60000, proxyHost, proxyPort);
        this.getEventDispatcher().addListener(CrawlerLoggingEvent.class, eventCallback);
    }

    public boolean activateAppleId(String email, String appleIdPassword, String emailPassword) {
        String activationLink = getActivationLink(email, emailPassword);
        if (activationLink == null) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 激活Apple ID] 没有找到Apple ID激活邮件 (邮箱: %s)", DateHelper.toString(new Date()), email)));
            return false;
        }

        if (!activationLink.startsWith("https://")) {
            this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 激活Apple ID] 激活链接无效 (邮箱: %s, 激活链接: %s)", DateHelper.toString(new Date()), email, activationLink)));
            return false;
        }

        this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 激活Apple ID] 开始激活Apple ID (邮箱: %s, 激活链接: %s)", DateHelper.toString(new Date()), email, activationLink)));

        try {
            Page page = this.getPage(new URL(activationLink));

            preprocess(page);

            if (page.getFirstByXPath("//input[@name='theAccountName']") == null) {
                this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 激活Apple ID] Apple ID已被激活 (邮箱: %s, 激活链接: %s)", DateHelper.toString(new Date()), email, activationLink)));
                return true;
            }

            String wosid = page.getFirstByXPath("//input[@name='wosid']/@value").getNodeValue();

            List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
            requestParameters.add(new BasicNameValuePair("theAccountName", email));
            requestParameters.add(new BasicNameValuePair("theAccountPW", appleIdPassword));
            requestParameters.add(new BasicNameValuePair("signInHyperLink", "Verify Address"));
            requestParameters.add(new BasicNameValuePair("theTypeValue", ""));
            requestParameters.add(new BasicNameValuePair("wosid", wosid));

            String url = page.getFirstByXPath("//form[@name='appleConnectForm'][@action]/@action").getNodeValue();

            page = this.httpPost("https://id.apple.com" + url, requestParameters);

            preprocess(page);

            return page.getText().contains("Email address verified");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private void preprocess(Page page) {
        page.setText(page.getText().replace("<!DOCTYPE html>", ""));
        page.setText(page.getText().replace("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en-us\" lang=\"en-us\">", "<html>"));
    }

    protected Page httpPost(String url, List<NameValuePair> requestParameters) {
        try {
            return this.getPage(new URL(url), HttpMethod.POST, requestParameters, null, DEFAULT_CHARSET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());
    }

    public String getActivationLink(String email, String emailPassword) {
        this.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 激活Apple ID] 登录邮箱 (邮箱: %s)", DateHelper.toString(new Date()), email)));
        try {
            Store store = HotmailHelper.connect(email, emailPassword);

            Folder inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_ONLY);

            SearchTerm term = new SearchTerm() {
                @Override
                public boolean match(Message mess) {
                    try {
                        return mess.getFrom()[0].toString().equals("Apple <appleid@id.apple.com>");
                    } catch (MessagingException ex) {
                        System.out.println(ex);
                        return false;
                    }
                }
            };

            Message[] searchResults = inbox.search(term);

            if (searchResults.length == 0) {
                return null;
            }

            Message m = searchResults[0];

            MimeMultipart content = (MimeMultipart) m.getContent();

            String body = (String) content.getBodyPart(0).getContent();

            String[] lines = body.split("\n");

            store.close();

            if (lines.length > 5) {
                return lines[5];
            }

            return null;
        } catch (MessagingException e) {
            System.out.println(e);
            return null;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    public static boolean activate(String email, String appleIdPassword, String emailPassword, Action1<CrawlerLoggingEvent> eventCallback) {
        ActivateAppleIdNoJSCrawler activateAppleIdCrawler = new ActivateAppleIdNoJSCrawler(eventCallback);
        boolean activated = activateAppleIdCrawler.activateAppleId(email, appleIdPassword, emailPassword);

        if (activated) {
            activateAppleIdCrawler.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 激活Apple ID] Apple ID激活成功 (邮箱: %s, 密码: %s)", DateHelper.toString(new Date()), email, appleIdPassword)));
        } else {
            activateAppleIdCrawler.getEventDispatcher().dispatch(new CrawlerLoggingEvent(String.format("[%s 激活Apple ID] Apple ID激活失败 (邮箱: %s, 密码: %s)", DateHelper.toString(new Date()), email, appleIdPassword)));
        }

        return activated;
    }
}
