package net.pickapack.spider.noJs.crawler.xml.example;

import net.pickapack.action.Action1;
import net.pickapack.net.url.URLHelper;
import net.pickapack.spider.noJs.crawler.xml.TaskCrawlerEvent;
import net.pickapack.spider.noJs.crawler.xml.TaskNoJSCrawler;
import net.pickapack.spider.noJs.spider.HttpMethod;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class TaskCrawlerStartup {
    public static void searchImages(String keywords) throws UnsupportedEncodingException {
        final TaskNoJSCrawler taskCrawler = new TaskNoJSCrawler(TaskCrawlerStartup.class.getResource("/com/picky/spiderNew/crawler/xml/example/dogpile.xml").toString());

        ArrayList<NameValuePair> requestParameters = new ArrayList<NameValuePair>();

        requestParameters.add(new BasicNameValuePair("adultFilter", "None"));
        requestParameters.add(new BasicNameValuePair("boldSearchTerms", "True"));
        requestParameters.add(new BasicNameValuePair("showSearchHistory", "False"));
        requestParameters.add(new BasicNameValuePair("webResultsCount", "10"));
        requestParameters.add(new BasicNameValuePair("imageResultsCount", "18"));
        requestParameters.add(new BasicNameValuePair("videoResultsCount", "10"));
        requestParameters.add(new BasicNameValuePair("newsResultsSort", "Relevance"));
        requestParameters.add(new BasicNameValuePair("newsResultsCount", "20"));
        requestParameters.add(new BasicNameValuePair("localResultsSort", "Relevance"));
        requestParameters.add(new BasicNameValuePair("localResultsCount", "10"));
        requestParameters.add(new BasicNameValuePair("userLocation", ""));
        requestParameters.add(new BasicNameValuePair("openResultInNewWindow", "true"));
        requestParameters.add(new BasicNameValuePair("openResultInNewWindow", "false"));
        requestParameters.add(new BasicNameValuePair("savePreferences", "Save Settings"));

        try {
            taskCrawler.getPage(new URL("http://www.dogpile.com/search/preferences"), HttpMethod.POST, requestParameters, null, null).getText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }

        taskCrawler.setBlackListEnabled(true);
        taskCrawler.getTask().getConfigs().put("storageFolder", taskCrawler.getTask().getConfigs().get("storageFolder") + "/" + keywords);
        taskCrawler.getEventDispatcher().addListener(TaskCrawlerEvent.class, new Action1<TaskCrawlerEvent>() {
            @Override
            public void apply(TaskCrawlerEvent event) {
                if (event.getRule().getId().equals("img")) {
                    Node node = (Node) event.getMatch();
                    String href = node.getNodeValue();

                    String refererUrl = URLHelper.getQueryParameterFromUrl(href, "du");
                    String documentUrl = URLHelper.getQueryParameterFromUrl(href, "ru");

                    try {
                        taskCrawler.downloadDocumentByUrls(new URL(refererUrl), taskCrawler.getTask().getStorageFolder(), new URL(documentUrl));
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        taskCrawler.run("http://www.dogpile.com/search/images?fcoid=417&fcop=topnav&fpid=2&q=" + URLEncoder.encode(keywords, "utf-8") + "&ql=");
        taskCrawler.close();
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        if (args.length == 1) {
            searchImages(args[0]);
        }
    }
}
