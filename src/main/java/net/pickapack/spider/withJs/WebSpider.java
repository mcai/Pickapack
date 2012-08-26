package net.pickapack.spider.withJs;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.action.Action1;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.io.*;
import java.net.*;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class WebSpider {
    private WebClient webClient;
    private Map<Page, Action1<Page>> pageUrlChangedHandlers;

    public WebSpider(BrowserVersion browserVersion) {
        this(browserVersion, null, -1);
    }
    
    public WebSpider(BrowserVersion browserVersion, String proxyHost, int proxyPort) {
        this.webClient = new WebClient(browserVersion);
        this.pageUrlChangedHandlers = new HashMap<Page, Action1<Page>>();

        this.setProxy(proxyHost, proxyPort);

        this.webClient.setCssEnabled(false); //Important to enable hotmail mail content access

        this.webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {
            public void scriptException(HtmlPage htmlPage, ScriptException e) {
//                System.out.println("scriptException occurred: " + htmlPage + ", " + e);
            }

            public void timeoutError(HtmlPage htmlPage, long l, long l1) {
//                System.out.println("timeoutError occurred");
            }

            public void malformedScriptURL(HtmlPage htmlPage, String s, MalformedURLException e) {
//                System.out.println("malformedScriptURL occurred");
            }

            public void loadScriptError(HtmlPage htmlPage, URL url, Exception e) {
//                System.out.println("loadScriptError occurred");
            }
        });

        this.webClient.setAjaxController(new NicelyResynchronizingAjaxController());

        this.webClient.setThrowExceptionOnScriptError(false);
        this.webClient.setThrowExceptionOnFailingStatusCode(false);

        try {
            this.webClient.setUseInsecureSSL(true);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        this.webClient.setPopupBlockerEnabled(false);

        CookieManager cm = new CookieManager();
        this.webClient.setCookieManager(cm);
        this.webClient.setJavaScriptTimeout(3600);
        this.webClient.setTimeout(9000);

        this.webClient.setIncorrectnessListener(new IncorrectnessListener() {
            public void notify(String s, Object o) {
//                System.out.printf("%s: %s%n", s, o);
            }
        });

        this.webClient.addWebWindowListener(new WebWindowListener() {
            public void webWindowOpened(WebWindowEvent webWindowEvent) {
//                System.out.println("window opened: " + webWindowEvent.toString());
            }

            public void webWindowContentChanged(WebWindowEvent webWindowEvent) {
//                System.out.println("window content changed: " + webWindowEvent.toString());

                if (webWindowEvent.getOldPage() instanceof HtmlPage && webWindowEvent.getNewPage() instanceof HtmlPage) {
                    if (getPageUrlChangedHandlers().containsKey(webWindowEvent.getOldPage())) {
                        getPageUrlChangedHandlers().get(webWindowEvent.getOldPage()).apply(webWindowEvent.getNewPage());
                        getPageUrlChangedHandlers().remove(webWindowEvent.getOldPage());
                    }
                }
            }

            public void webWindowClosed(WebWindowEvent webWindowEvent) {
//                System.out.println("window closed: " + webWindowEvent.toString());
            }
        });

        this.webClient.setRedirectEnabled(true);

        this.webClient.setRefreshHandler(new WaitingRefreshHandler());
    }

    public void downloadDocument(String url, File localFile) {
        this.downloadDocument(url, localFile.getAbsolutePath());
    }

    public void downloadDocument(String url, String localFileName) {
        new File(localFileName).getParentFile().mkdirs();

        try {
            System.out.printf("[%s]         Downloading document: %s to %s\n", DateHelper.toString(new Date()), url, localFileName);

            URLConnection urlConnection = this.getWebClient().getProxyConfig().getProxyHost() != null ? new URL(url).openConnection(new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(this.getWebClient().getProxyConfig().getProxyHost(), this.getWebClient().getProxyConfig().getProxyPort()))) : new URL(url).openConnection();

            urlConnection.setConnectTimeout(this.getWebClient().getTimeout());

            InputStream is = urlConnection.getInputStream();
            OutputStream os = new BufferedOutputStream(new FileOutputStream(localFileName));
            IOUtils.copy(is, os);
            is.close();
            os.close();

            System.out.printf("[%s]         Document downloaded: %s to %s\n", DateHelper.toString(new Date()), url, localFileName);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void downloadImage(HtmlImage image, File localFile) {
        this.downloadImage(image, localFile.getAbsolutePath());
    }

    public synchronized void downloadImage(HtmlImage image, String localFileName) {
        ImageIO.setUseCache(false);

        try {
            System.out.printf("[%s]         Downloading image: %s to %s\n", DateHelper.toString(new Date()), image.getSrcAttribute(), localFileName);

            ImageReader reader = image.getImageReader();
            FileOutputStream out = new FileOutputStream(localFileName);
            ImageIO.write(reader.read(0), reader.getFormatName(), out);
            out.close();

            System.out.printf("[%s]         Image downloaded: %s to %s\n", DateHelper.toString(new Date()), image.getSrcAttribute(), localFileName);
        } catch (IOException e) {
            recordException(e);
        }
    }

    protected static void recordException(Exception e) {
        System.out.print(String.format("[%s Exception] %s\r\n", DateHelper.toString(new Date()), e));
        e.printStackTrace();
    }

    public void setProxy(String proxyHost, int proxyPort) {
        if(proxyHost != null) {
            this.webClient.setProxyConfig(new ProxyConfig(proxyHost, proxyPort));
        }
    }

    protected WebClient getWebClient() {
        return webClient;
    }

    protected Map<Page, Action1<Page>> getPageUrlChangedHandlers() {
        return pageUrlChangedHandlers;
    }

    static {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }

    protected static String readLine() {
        try {
            return new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected byte[] urlToBytes(String url) {
        try {
            UnexpectedPage page = this.getWebClient().getPage(new URL(url));
            InputStream in = page.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(in, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
