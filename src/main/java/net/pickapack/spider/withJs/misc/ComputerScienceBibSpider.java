package net.pickapack.spider.withJs.misc;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.html.*;
import net.pickapack.spider.withJs.WebSpider;
import net.pickapack.dateTime.DateHelper;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class ComputerScienceBibSpider extends WebSpider {
    public ComputerScienceBibSpider() {
        super(BrowserVersion.INTERNET_EXPLORER_8);
        this.getWebClient().setJavaScriptEnabled(false);
    }

    @SuppressWarnings("unchecked")
    public void run(String query) {
        try {
            HtmlPage page1 = this.getWebClient().getPage("http://liinwww.ira.uka.de/bibliography/");
            HtmlTextInput inputQuery = page1.getFirstByXPath("//input[@type='text'][@id='query1']");
            HtmlSubmitInput inputSubmit = page1.getFirstByXPath("//input[@type='submit'][@value='Search']");

            inputQuery.setValueAttribute(query);

            HtmlPage page2 = inputSubmit.click();

            for (; ; ) {
                List<HtmlTable> tableCitations = (List<HtmlTable>) page2.getByXPath("//table[@class='citation']");

                for (HtmlTable tableCitation : tableCitations) {
                    HtmlTableDataCell tdScore = tableCitation.getFirstByXPath(".//td[@class='score']");

                    List<HtmlAnchor> anchorAuthors = (List<HtmlAnchor>) tableCitation.getByXPath(".//td[@class='citaton']//a[contains(@title,'Search for publications authored by')]");

                    HtmlSpan spanTitle = tableCitation.getFirstByXPath(".//span[@class='b_title']");

                    String title = spanTitle != null ? spanTitle.asText() : "";

                    HtmlAnchor anchorBibtex = tableCitation.getFirstByXPath(".//td[@class='biblinks']//a[@title='Full BibTeX record']");

                    System.out.println("[" + tdScore.asText().replaceAll(":", "") + "] " + title);

                    if (anchorBibtex != null) {
                        HtmlPage pageBibtex = anchorBibtex.click();
                        HtmlPreformattedText preBibtex = pageBibtex.getFirstByXPath("//pre[@class='bibtex']");

                        System.out.println("  bibtex: " + preBibtex.asText());

//                    BibTeXDatabase bibTeXDatabase = parseBibTeX(new StringReader(preBibtex.asText()));
//
//                    if(bibTeXDatabase != null) {
//                        formatBibTeX(bibTeXDatabase, new PrintWriter(System.out));
//                    }
                    } else {
                        System.out.println("  No bibtex found");
                    }

                    for (HtmlAnchor anchorAuthor : anchorAuthors) {
                        System.out.println("  " + anchorAuthor.getTextContent());
                    }
                }

                HtmlImageInput inputNextMatches = page2.getFirstByXPath("//input[@type='image'][@alt='Next matches']");

                if(inputNextMatches != null) {
                    page2 = (HtmlPage) inputNextMatches.click();
                }
                else {
                    break;
                }
            }
        } catch (IOException e) {
            recordException(e);
        }
    }

    private static void recordError(Error e) {
        System.out.print(String.format("[%s Error] %s\r\n", DateHelper.toString(new Date()), e));
        e.printStackTrace();
    }

    public static void main(String[] args) {
        ComputerScienceBibSpider spider = new ComputerScienceBibSpider();
        spider.setProxy("localhost", 8888);
        spider.run("multicore cache prefetch");
    }
}
