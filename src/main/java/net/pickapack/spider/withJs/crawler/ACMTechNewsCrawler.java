package net.pickapack.spider.withJs.crawler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import net.pickapack.dateTime.DateHelper;

import java.util.Date;
import java.util.List;
import java.util.Map;

//import de.l3s.boilerpipe.BoilerpipeProcessingException;
//import de.l3s.boilerpipe.extractors.ArticleExtractor;

public abstract class ACMTechNewsCrawler extends WebCrawler {
    private boolean latestOnly;
    private boolean latestVisited;

    public ACMTechNewsCrawler(boolean latestOnly) {
        super(BrowserVersion.INTERNET_EXPLORER_8);
        this.latestOnly = latestOnly;
        this.getWebClient().setJavaScriptEnabled(false);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());

        if (page instanceof HtmlPage) {
            String url = page.getUrl().toString();

            if (url.equals("http://technews.acm.org/archives.cfm")) {
                if (!latestOnly) {
                    this.visitLinks((HtmlPage) page, "//td[@id='TRcontent']//b//a[starts-with(@href,'archives.cfm?d=')]", null, false);
                }
                else {
                    this.visitFirstLink((HtmlPage) page, "//td[@id='TRcontent']//b//a[starts-with(@href,'archives.cfm?d=')]", null, false);
                }
            } else if (url.startsWith("http://technews.acm.org/archives.cfm?d=")) {
                if (!latestOnly) {
                    this.visitLinks((HtmlPage) page, "//td[@id='TRcontent']//b//a[starts-with(@href,'archives.cfm?fo=')]", null, false);
                }
                else {
                    this.visitFirstLink((HtmlPage) page, "//td[@id='TRcontent']//b//a[starts-with(@href,'archives.cfm?fo=')]", null, false);
                }
            } else if (url.startsWith("http://technews.acm.org/archives.cfm?fo=")) {
                if (!latestOnly || !latestVisited) {
                    latestVisited = true;

                    //TODO: should for each passage, first begin at title, then grab author, abstract and full text url
                    List<DomText> titles = (List<DomText>) ((HtmlPage) page).getByXPath("//td[@id='TRcontent']//a[@name]//following-sibling::b[1]//text()");
                    List<DomText> authors = (List<DomText>) ((HtmlPage) page).getByXPath("//td[@id='TRcontent']//a[@name]//following-sibling::i[1]//text()");
                    List<DomText> summaries = (List<DomText>) ((HtmlPage) page).getByXPath("//td[@id='TRcontent']//a[@name]//following-sibling::text()[1]");
                    List<DomAttr> fullTextUrls = (List<DomAttr>) ((HtmlPage) page).getByXPath("//td[@id='TRcontent']//a[@name]//following-sibling::div[@style][1]//a[@target]//@href");

                    if (titles.size() == authors.size() && titles.size() == summaries.size() && titles.size() == fullTextUrls.size()) {
                        for (int i = 0; i < titles.size(); i++) {
                            String title = titles.get(i).toString();
                            String author = authors.get(i).toString();
                            String fullTextUrl = fullTextUrls.get(i).getValue();
                            String summary = summaries.get(i).toString();

                            System.out.printf("[%s] %s%n", DateHelper.toString(new Date()), title);
                            System.out.printf("[%s]   author: %s%n", DateHelper.toString(new Date()), author);
                            System.out.printf("[%s]   url: %s%n", DateHelper.toString(new Date()), fullTextUrl);
                            System.out.printf("[%s]   summary: %s%n", DateHelper.toString(new Date()), summary);

                            String fullText = "";

                            //TODO: commented for the moment
//                            try {
//                                HtmlPage pageFullText = this.getWebClient().getPage(fullTextUrl);
//                                fullText = ArticleExtractor.INSTANCE.getText(pageFullText.asXml());
//                                System.out.printf("[%s]   body: %s%n", DateHelper.toString(new Date()), fullText);
//                                this.visitPage(page, pageFullText, null);
//                            } catch (BoilerpipeProcessingException e) {
//                                recordException(e);
//                            } catch (MalformedURLException e) {
//                                recordException(e);
//                            } catch (IOException e) {
//                                recordException(e);
//                            }
                            this.onNewsItemFound(fullTextUrl, title, summary, author, fullText);
                        }
                    }
                }
            }
        }
    }

    public void visit() {
        this.visit("http://technews.acm.org/archives.cfm");
    }

    protected abstract void onNewsItemFound(String fullTextUrl, String title, String summary, String author, String fullText);

    public static void main(String[] args) {
        ACMTechNewsCrawler spider = new ACMTechNewsCrawler(true) {
            @Override
            protected void onNewsItemFound(String fullTextUrl, String title, String summary, String author, String fullText) {
            }
        };
        spider.setProxy("10.26.27.29", 3128);
        spider.visit();
    }
}
