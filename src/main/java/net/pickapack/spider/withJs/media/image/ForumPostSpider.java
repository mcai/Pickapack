package net.pickapack.spider.withJs.media.image;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.html.*;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.net.url.URLHelper;
import net.pickapack.spider.withJs.WebSpider;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.*;

public class ForumPostSpider extends WebSpider {
    public static final String SPIDER_HOME = FileUtils.getUserDirectoryPath() + "/iBaoWebSearchSpider/";

    public ForumPostSpider() {
        super(BrowserVersion.INTERNET_EXPLORER_8);
        this.getWebClient().setJavaScriptEnabled(false);
    }

    public boolean run(String userName, String password, String... forumIds) throws IOException, URISyntaxException {
        System.out.printf("[%s] Logging in: %s\n", DateHelper.toString(new Date()), "http://www.ibaoyang.net/forum.php");

        if (!login(userName, password)) {
            this.getWebClient().closeAllWindows();
            return false;
        }

        for (String forumId : forumIds) {
            visitForum(forumId);
        }

        this.getWebClient().closeAllWindows();

        return true;
    }

    protected boolean login(String userName, String password) throws IOException {
        HtmlPage page1 = this.getWebClient().getPage("http://www.ibaoyang.net/forum.php");

        HtmlTextInput inputUserName = page1.getFirstByXPath("//input[@name='username']");
        HtmlPasswordInput inputPassword = page1.getFirstByXPath("//input[@name='password']");
        HtmlButton buttonLogin = page1.getFirstByXPath("//button[@type='submit'][@class='pn vm']");

        inputUserName.setValueAttribute(userName);
        inputPassword.setValueAttribute(password);

        HtmlPage page2 = buttonLogin.click();

        return page2.asText().contains("退出");
    }

    @SuppressWarnings("unchecked")
    protected void visitForum(String forumId) throws IOException {
        String forumTitle = "";

        for (int pagePostsNo = 1; ; pagePostsNo++) {
            HtmlPage pagePosts = this.getWebClient().getPage("http://www.ibaoyang.net/forum.php?mod=forumdisplay&fid=" + forumId + "&page=" + pagePostsNo);

            forumTitle = pagePosts.<HtmlMeta>getFirstByXPath("//meta[@name='keywords']").getContentAttribute();

            if (pagePostsNo == 1) {
                System.out.printf("[%s]   Visiting forum #%s %s\n", DateHelper.toString(new Date()), forumId, forumTitle);

                String folder = SPIDER_HOME + forumTitle;

                if (!new File(folder).exists()) {
                    new File(folder).mkdirs();
                }
            }

            System.out.printf("[%s]     Visiting page %s/#%d of posts\n", DateHelper.toString(new Date()), forumTitle, pagePostsNo);

            List<HtmlTableBody> postsByPage = (List<HtmlTableBody>) pagePosts.getByXPath("//tbody[contains(@id, 'normalthread_')]");

            if (postsByPage.isEmpty()) {
                break;
            }

            int postNo = 1;

            for (HtmlTableBody post : postsByPage) {
                HtmlAnchor anchorPost = post.getFirstByXPath(".//a[contains(@href, 'forum.php?mod=viewthread')][@onclick='atarget(this)']");
                HtmlAnchor anchorPostTitle = post.getFirstByXPath(".//a[@class='xst']");

                String postTitle = anchorPostTitle.getTextContent();

                System.out.printf("[%s]       Visiting post %s '%s' (%s)\n", DateHelper.toString(new Date()), forumTitle + "/" + pagePostsNo + "/" + postNo, postTitle, "http://www.ibaoyang.net/" + anchorPost.getHrefAttribute());

                HtmlPage pagePost = anchorPost.click();

                String tid = URLHelper.getQueryParameterFromUrl(pagePost.getUrl() + "", "tid");

                if(tid == null) {
                    throw new IllegalArgumentException();
                }

                Map<String, String> attributes = analyzePostAttributes(pagePost);

                attributes.put("URL", "http://www.ibaoyang.net/forum.php?mod=viewthread&tid=" + tid);

                for (String key : attributes.keySet()) {
                    System.out.printf("[%s]         %s: %s\n", DateHelper.toString(new Date()), key, attributes.get(key));
                }

                List<String> imageUrls = new ArrayList<String>();

                List<HtmlImage> imagesTypeA = (List<HtmlImage>) pagePost.getByXPath("//img[contains(@file, 'data/attachment/forum/')]");
                for (HtmlImage image : imagesTypeA) {
                    imageUrls.add(image.getAttribute("file"));
                }

                List<HtmlImage> imagesTypeB = (List<HtmlImage>) pagePost.getByXPath("//img[contains(@src, 'data/attachment/forum/')]");
                for (HtmlImage image : imagesTypeB) {
                    imageUrls.add(image.getAttribute("src"));
                }

                for (String imageUrl : imageUrls) {
                    String url = "http://www.ibaoyang.net/" + imageUrl;
                    downloadDocument(url, SPIDER_HOME + forumTitle + "/" + tid + "_" + url.substring(url.lastIndexOf('/') + 1, url.length()));
                }

                System.out.println();

                PrintWriter pw = new PrintWriter(SPIDER_HOME + forumTitle + "/" + tid + "_" + "README");
                pw.println("标题: " + postTitle);

                for (String key : attributes.keySet()) {
                    pw.printf("%s: %s\n", key, attributes.get(key));
                }

                pw.close();

                postNo++;
            }
        }
    }

    private Map<String, String> analyzePostAttributes(HtmlPage pagePost) {
        Map<String, String> attributes = new LinkedHashMap<String, String>();

        HtmlAnchor poster = pagePost.getFirstByXPath("//a[contains(@href, 'home.php?mod=')][@class='xw1']");

        HtmlEmphasis postedTime = pagePost.getFirstByXPath("//em[contains(@id, 'authorposton')]");

        HtmlDefinitionTerm ageKey = pagePost.getFirstByXPath("//dt[contains(., '年龄:')]");
        HtmlDefinitionTerm heightKey = pagePost.getFirstByXPath("//dt[contains(., '身高:')]");
        HtmlDefinitionTerm weightKey = pagePost.getFirstByXPath("//dt[contains(., '体重:')]");
        HtmlDefinitionTerm sanWeiKey = pagePost.getFirstByXPath("//dt[contains(., '三围:')]");
        HtmlDefinitionTerm educationKey = pagePost.getFirstByXPath("//dt[contains(., '学历:')]");
        HtmlDefinitionTerm careerKey = pagePost.getFirstByXPath("//dt[contains(., '职业:')]");
        HtmlDefinitionTerm cityKey = pagePost.getFirstByXPath("//dt[contains(., '城市:')]");
        HtmlDefinitionTerm motivationKey = pagePost.getFirstByXPath("//dt[contains(., '交友性质:')]");
        HtmlDefinitionTerm hobbiesKey = pagePost.getFirstByXPath("//dt[contains(., '兴趣爱好:')]");

        HtmlDivision div1 = pagePost.getFirstByXPath("//div[@class='t_fsz']");

        if (poster != null) {
            attributes.put("楼主", poster.getTextContent());
        }

        if (postedTime != null) {
            attributes.put("发表时间", postedTime.getTextContent());
        }

        if (ageKey != null) {
            HtmlDefinitionDescription ageValue = (HtmlDefinitionDescription) ageKey.getNextSibling();
            attributes.put("年龄", ageValue.asText().trim());
        }

        if (heightKey != null) {
            HtmlDefinitionDescription heightValue = (HtmlDefinitionDescription) heightKey.getNextSibling();
            attributes.put("身高", heightValue.asText().trim());
        }

        if (weightKey != null) {
            HtmlDefinitionDescription weightValue = (HtmlDefinitionDescription) weightKey.getNextSibling();
            attributes.put("体重", weightValue.asText().trim());
        }

        if (sanWeiKey != null) {
            HtmlDefinitionDescription sanWeiValue = (HtmlDefinitionDescription) sanWeiKey.getNextSibling();
            attributes.put("三围", sanWeiValue.asText().trim());
        }

        if (educationKey != null) {
            HtmlDefinitionDescription educationValue = (HtmlDefinitionDescription) educationKey.getNextSibling();
            attributes.put("学历", educationValue.asText().trim());
        }

        if (careerKey != null) {
            HtmlDefinitionDescription careerValue = (HtmlDefinitionDescription) careerKey.getNextSibling();
            attributes.put("职业", careerValue.asText().trim());
        }

        if (cityKey != null) {
            HtmlDefinitionDescription cityValue = (HtmlDefinitionDescription) cityKey.getNextSibling();
            attributes.put("城市", cityValue.asText().trim());
        }

        if (motivationKey != null) {
            HtmlDefinitionDescription motivationValue = (HtmlDefinitionDescription) motivationKey.getNextSibling();
            attributes.put("交友性质", motivationValue.asText().trim());
        }

        if (hobbiesKey != null) {
            HtmlDefinitionDescription hobbiesValue = (HtmlDefinitionDescription) hobbiesKey.getNextSibling();
            attributes.put("兴趣爱好", hobbiesValue.asText().trim());
        }

        if (div1 != null) {
            attributes.put("描述", div1.asText().trim());
        }

        return attributes;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        // "52", "54", "53", "55"
        if (args.length > 0) {
            for (String arg : args) {
                ForumPostSpider spider = new ForumPostSpider();
//                spider.setProxy("localhost", 8888);

                spider.run("itecgo", "bywwnss", arg);
            }
        }
    }
}
