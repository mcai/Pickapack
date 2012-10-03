package net.pickapack.spider.withJs.crawler.media.video;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import net.pickapack.spider.withJs.crawler.WebCrawler;
import net.pickapack.dateTime.DateHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class JkForumCrawler extends WebCrawler {
    private String folderImages;

    public JkForumCrawler(String folderImages) {
        super(BrowserVersion.FIREFOX_3_6);
        this.folderImages = folderImages;
        this.getWebClient().setJavaScriptEnabled(false);
    }

    @SuppressWarnings("unchecked")
    public void run(String url, String userName, String password) {
        try {
            System.out.printf("[%s] Logging in as: %s\n", DateHelper.toString(new Date()), userName);

            WebRequest request = new WebRequest(new URL("http://www.jkforum.net/member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&inajax=1"));
            request.setHttpMethod(HttpMethod.POST);
            request.setRequestParameters(new ArrayList<NameValuePair>());
            request.getRequestParameters().add(new NameValuePair("fastloginfield", "username"));
            request.getRequestParameters().add(new NameValuePair("username", userName));
            request.getRequestParameters().add(new NameValuePair("password", password));
            request.getRequestParameters().add(new NameValuePair("quickforward", "yes"));
            request.getRequestParameters().add(new NameValuePair("handlekey", "ls"));
            request.getRequestParameters().add(new NameValuePair("questionid", "0"));
            request.getRequestParameters().add(new NameValuePair("answer", ""));
            XmlPage page = this.getWebClient().getPage(request);

            if(!page.asText().contains("歡迎你回來")) {
                throw new IllegalArgumentException();
            }

            System.out.printf("[%s] Logged in as: %s\n", DateHelper.toString(new Date()), userName);

            this.visit(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Random random = new Random();

    @SuppressWarnings("unchecked")
    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());

        String pageUrl = page.getUrl().toString();
        String threadPrefix = "http://www.jkforum.net/thread-";

        if(page instanceof HtmlPage && pageUrl.startsWith("http://www.jkforum.net/forum")) {
            this.visitLinks((HtmlPage) page, "//tbody[starts-with(@id,'normalthread_')]//tr//th//a[@class='xst'][@onclick='atarget(this)'][starts-with(@href,'http://www.jkforum.net/thread-')]", null, true);
            this.visitLinks((HtmlPage) page, "//div[@class='pg']//a[@class='nxt']", null, true);
        } else if(page instanceof HtmlPage && pageUrl.startsWith(threadPrefix)) {
            DomText domTextSubject = ((HtmlPage) page).<DomText>getFirstByXPath("//a[@id='thread_subject']//text()");

            if(domTextSubject != null) {
                System.out.printf("%s%n", domTextSubject.toString());
            }

            List<DomAttr> subjectContentUrlAttributes = (List<DomAttr>) ((HtmlPage) page).getByXPath("//td[starts-with(@id,'postmessage_')]//img[@src]/@src");

            List<String> urls = new ArrayList<String>();
            for(DomAttr urlAttribute : subjectContentUrlAttributes) {
                urls.add(urlAttribute.getValue());
            }

            this.downloadDocumentByUrls(urls, FileUtils.getUserDirectoryPath() + this.folderImages + "/" + pageUrl.substring(pageUrl.indexOf(threadPrefix) + threadPrefix.length()));

            HtmlAnchor anchorReplyPost = ((HtmlPage) page).getFirstByXPath("//div[@class='like_locked']//div//a[text()='回覆']");
            if(anchorReplyPost != null) {
                String replyUrl = anchorReplyPost.getHrefAttribute();

                try {
                    HtmlPage pageReply = this.getWebClient().getPage("http://www.jkforum.net/" + replyUrl);

                    String formhash = pageReply.<HtmlHiddenInput>getFirstByXPath("//form[@id='postform']//input[@name='formhash']").getValueAttribute();
                    String postTime = pageReply.<HtmlHiddenInput>getFirstByXPath("//form[@id='postform']//input[@name='posttime']").getValueAttribute();

                    HtmlForm form = pageReply.getFirstByXPath("//form[@id='postform']");

                    WebRequest request = new WebRequest(new URL("http://www.jkforum.net/" + form.getActionAttribute()));
                    request.setHttpMethod(HttpMethod.POST);
                    request.setRequestParameters(new ArrayList<NameValuePair>());
                    request.getRequestParameters().add(new NameValuePair("formhash", formhash));
                    request.getRequestParameters().add(new NameValuePair("posttime", postTime));
                    request.getRequestParameters().add(new NameValuePair("wysiwyg", "0"));
                    request.getRequestParameters().add(new NameValuePair("noticeauthor", ""));
                    request.getRequestParameters().add(new NameValuePair("noticetrimstr", ""));
                    request.getRequestParameters().add(new NameValuePair("noticeauthormsg", ""));
                    request.getRequestParameters().add(new NameValuePair("subject", ""));
                    request.getRequestParameters().add(new NameValuePair("checkbox", "0"));
                    request.getRequestParameters().add(new NameValuePair("message", RandomStringUtils.randomAlphabetic(random.nextInt(100) + 100)));
                    request.getRequestParameters().add(new NameValuePair("save", ""));

                    HtmlPage pageReplyResult = this.getWebClient().getPage(request);

                    if (pageReplyResult.asText().contains("非常感謝")) {
                        this.onPageVisited(pageReply, null);
                        this.onPageVisited(pageReplyResult, null);

                        System.out.println("replied...");

                        this.removePageByUrl(page.getUrl().toString());
                        this.visitPage(page, this.getWebClient().getPage(page.getUrl()), null);
                    } else if (pageReplyResult.getUrl().toString().equals(page.getUrl().toString()) && (pageReplyResult.getFirstByXPath("//div[@class='like_locked']//div//a[text()='回覆']") != null)) {
                        this.onPageVisited(pageReply, null);
                        this.onPageVisited(pageReplyResult, null);

                        System.out.println("replied...");

                        this.removePageByUrl(page.getUrl().toString());
                        this.visitPage(page, this.getWebClient().getPage(page.getUrl()), null);
                    } else if(pageReplyResult.asText().contains("對不起，您兩次發表間隔少於 15 秒，請稍後再發表。")) {
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        this.onPageVisited(pageReply, null);
                        this.onPageVisited(pageReplyResult, null);

                        System.out.println("retry replying after 15 seconds...");

                        this.removePageByUrl(page.getUrl().toString());
                        this.visitPage(page, this.getWebClient().getPage(page.getUrl()), null);
                    } else {
                        this.onPageVisited(pageReply, null);
                        this.onPageVisited(pageReplyResult, null);

                        System.out.printf("failed to reply: %s (reply url: %s)%n", page.getUrl(), pageReplyResult.getUrl());
                        System.out.println(pageReplyResult.asXml());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                System.out.println("retrieved...");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String url = "http://www.jkforum.net/forum-54-1.html";

        if(args.length == 1) {
            url = args[0];
        }

        JkForumCrawler crawler = new JkForumCrawler("/OOXXPicsSpider/JkForumNoJSCrawler");
        crawler.setProxy("10.26.27.29", 3128);
        crawler.run(url, "$$$", "###");//TODO: supply password here
    }
}
