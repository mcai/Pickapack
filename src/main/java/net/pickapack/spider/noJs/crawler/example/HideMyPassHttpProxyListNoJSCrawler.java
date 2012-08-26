package net.pickapack.spider.noJs.crawler.example;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.action.Function1;
import net.pickapack.spider.noJs.crawler.NoJSCrawler;
import net.pickapack.spider.noJs.spider.Page;
import net.pickapack.text.XPathHelper;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HideMyPassHttpProxyListNoJSCrawler extends NoJSCrawler {
    private List<HideMyPassHttpProxy> proxies;
    private int maxNumPages = 10;
    private int numPages = 0;

    public HideMyPassHttpProxyListNoJSCrawler(String userAgent) {
        this(userAgent, null, -1);
    }

    public HideMyPassHttpProxyListNoJSCrawler(String userAgent, String proxyHost, int proxyPort) {
        super(userAgent, 60000, proxyHost, proxyPort);
        this.proxies = new ArrayList<HideMyPassHttpProxy>();
    }

    @SuppressWarnings("unchecked")
    public void run(String url) {
        try {
            this.visit(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());

        String pageUrl = page.getUrl().toString();

        if (pageUrl.startsWith("http://hidemyass.com/proxy-list")) {
            List<Node> rows = page.getByXPath("//table[@id='listtable']//tr[not(@id='theader')]");

            for (Node row : rows) {
                String timeSinceLastCheck = XPathHelper.getFirstByXPath(row, ".//td[1]//span//text()").getNodeValue().trim();

                Node nodeProxyHost = XPathHelper.getFirstByXPath(row, ".//td[2]/span");
                List<Node> nodesToExclude = XPathHelper.getByXPath(nodeProxyHost, "./*[@style='display:none']");

                for (Node node : nodesToExclude) {
                    nodeProxyHost.removeChild(node);
                }

                String proxyHost = nodeProxyHost.getTextContent();
                int proxyPort = Integer.parseInt(XPathHelper.getFirstByXPath(row, ".//td[3]//text()").getNodeValue().trim());
                String country = XPathHelper.getFirstByXPath(row, ".//td[4]//span//text()").getNodeValue().trim();
                String protocol = XPathHelper.getFirstByXPath(row, ".//td[7]//text()").getNodeValue();
                HideMyPassHttpProxy newProxy = new HideMyPassHttpProxy(timeSinceLastCheck, proxyHost, proxyPort, country, protocol);
                this.proxies.add(newProxy);
                System.out.printf("[%s] New http proxy retrieved: %s\n", DateHelper.toString(new Date()), newProxy);
            }

            if (++numPages < maxNumPages) {
                this.visitLinks(page, "//div[@class='pagination']//li[@class='nextpageactive']//a[@class='next']", null, new Function1<Node, String>() {
                    @Override
                    public String apply(Node param) {
                        return "http://hidemyass.com" + param.getAttributes().getNamedItem("href").getNodeValue();
                    }
                });
            }
        }
    }

    public List<HideMyPassHttpProxy> retrieveProxyList() {
        try {
            this.proxies.clear();

//            ArrayList<org.apache.http.NameValuePair> requestParameters = new ArrayList<org.apache.http.NameValuePair>();
//
//            requestParameters.add(new BasicNameValuePair("c[]", "China"));
//            requestParameters.add(new BasicNameValuePair("p", ""));
//            requestParameters.add(new BasicNameValuePair("pr[]", "0"));
//            requestParameters.add(new BasicNameValuePair("a[]", "0"));
//            requestParameters.add(new BasicNameValuePair("a[]", "1"));
//            requestParameters.add(new BasicNameValuePair("a[]", "2"));
//            requestParameters.add(new BasicNameValuePair("a[]", "3"));
//            requestParameters.add(new BasicNameValuePair("a[]", "4"));
//            requestParameters.add(new BasicNameValuePair("pl", "on"));
//            requestParameters.add(new BasicNameValuePair("sp[]", "3"));
//            requestParameters.add(new BasicNameValuePair("ct[]", "3"));
//            requestParameters.add(new BasicNameValuePair("s", "0"));
//            requestParameters.add(new BasicNameValuePair("o", "0"));
//            requestParameters.add(new BasicNameValuePair("pp", "2"));
//            requestParameters.add(new BasicNameValuePair("sortedBy", "date"));
//
//            Page page = this.getPage(new URL("http://hidemyass.com/proxy-list/"), HttpMethod.POST, requestParameters, null);
//            System.out.println(page.getResponse().getStatusCode());
//            if(page.getResponse().getStatusCode() == 302) {
//                for(NameValuePair  header : page.getResponse().getHeaders()) {
//                    if(header.getName().equals("Location")) {
//                        this.visit(new URL("http://hidemyass.com" + header.getValue()));
//                        return this.proxies;
//                    }
//                }
//            }
//
//            throw new IllegalArgumentException();

            this.visit(new URL("http://hidemyass.com/proxy-list/"));
            return this.proxies;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
//        } catch (TransformerException e) {
//            throw new RuntimeException(e);
//        } catch (XPathExpressionException e) {
//            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
