package net.pickapack.spider.noJs.crawler.xml.example;

import net.pickapack.Reference;
import net.pickapack.action.Action1;
import net.pickapack.spider.noJs.crawler.xml.TaskCrawlerEvent;
import net.pickapack.spider.noJs.crawler.xml.TaskNoJSCrawler;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DaqiPicsCrawlerStartup {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        final TaskNoJSCrawler taskCrawler = new TaskNoJSCrawler(DaqiPicsCrawlerStartup.class.getResource("/com/picky/spiderNew/crawler/xml/example/daqiPics.xml").toString());

        final Reference<String> currentTitle = new Reference<String>("");

        taskCrawler.getEventDispatcher().addListener(TaskCrawlerEvent.class, new Action1<TaskCrawlerEvent>() {
            @Override
            public void apply(TaskCrawlerEvent event) {
                String ruleId = event.getRule().getId();

                if (ruleId.equals("title")) {
                    String title = ((Node) (event.getMatch())).getNodeValue();
                    title = title.replaceAll(":", "_");
                    currentTitle.set(title);
                } else if (ruleId.equals("slidePic")) {
                    String src = ((Node) (event.getMatch())).getNodeValue();
                    try {
                        taskCrawler.downloadDocumentByUrls(event.getPage().getUrl(), "/home/itecgo/Desktop/111/" + currentTitle.get(), new URL(src));
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                } else if (ruleId.equals("nextPage")) {
                    String href = ((Node) (event.getMatch())).getNodeValue();

                    if (href != null) {
                        href = "http://pic.daqi.com/slide/" + href;
                        try {
                            taskCrawler.visit(new URL(href));
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

        taskCrawler.run("http://pic.daqi.com/slide/3299142.html");
        taskCrawler.close();
    }
}
