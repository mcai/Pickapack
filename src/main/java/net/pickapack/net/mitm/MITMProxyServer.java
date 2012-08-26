package net.pickapack.net.mitm;

import net.pickapack.net.IOHelper;
import net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail.ReceivedEmailRule;
import net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail.ReceivedEmailSubjectRule;
import net.pickapack.net.mitm.emailInterception.model.rule.sentEmail.SentEmailRule;
import net.pickapack.net.mitm.emailInterception.model.task.EmailInterceptionTask;
import net.pickapack.net.mitm.emailInterception.service.ServiceManager;
import net.pickapack.spider.noJs.spider.DownloadedContent;
import net.pickapack.spider.noJs.spider.Page;
import net.pickapack.spider.noJs.spider.WebResponse;
import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicNameValuePair;
import org.owasp.proxy.daemon.Server;
import org.owasp.proxy.http.*;
import org.owasp.proxy.http.server.*;
import org.owasp.proxy.socks.SocksConnectionHandler;
import org.owasp.proxy.ssl.AutoGeneratingContextSelector;
import org.owasp.proxy.ssl.SSLConnectionHandler;
import org.owasp.proxy.ssl.SSLContextSelector;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MITMProxyServer {
    public static final BasicLineParser LINE_PARSER = new BasicLineParser(new ProtocolVersion("HTTP", 1, 1));

    public static void main(String[] args) throws IOException, GeneralSecurityException, SQLException {
        List<ReceivedEmailRule> receivedEmailRules = new ArrayList<ReceivedEmailRule>();
        List<SentEmailRule> sentEmailRules = new ArrayList<SentEmailRule>();

        receivedEmailRules.add(new ReceivedEmailSubjectRule("rerer")); //TODO: load from file

        EmailInterceptionTask emailInterceptionTask = new EmailInterceptionTask(receivedEmailRules, sentEmailRules);

        ServiceManager.getEmailInterceptionService().addEmailInterceptionTask(emailInterceptionTask);

        final PrintStream ps = new PrintStream(new File(FileUtils.getUserDirectoryPath() + File.separator + "mitm_" +
                new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".log"));

        IOHelper.extractResource("mitm/FakeCA.cer");
        IOHelper.extractResource("mitm/FakeCAStore");

        HttpRequestHandler requestHandler = new DefaultHttpRequestHandler();

        BufferedMessageInterceptor interceptor = new BufferedMessageInterceptor() {
            @Override
            public Action directResponse(RequestHeader request, MutableResponseHeader response) {
                try {
                    String responseHeaderContentType = response.getHeader("Content-Type");

                    ContentType responseContentType = responseHeaderContentType == null ? null : ContentType.parse(responseHeaderContentType);

                    StatusLine statusLine = BasicLineParser.parseStatusLine(response.getStartLine(), LINE_PARSER);

                    if (statusLine.getStatusCode() != 200) {
                        return Action.IGNORE;
                    }

                    if (responseContentType != null && !responseContentType.getMimeType().contains("text/html") && !responseContentType.getMimeType().contains("text/javascript")) {
                        return Action.IGNORE;
                    }

                    if (getUrl(request).toString().contains("https://mail.google.com/mail/u/0/channel/bind")) {
                        return Action.IGNORE;
                    }

                    if (getUrl(request).toString().contains("https://mail.google.com/mail/u/0")) {
                        return Action.BUFFER;
                    }

                    return Action.IGNORE;
                } catch (MessageFormatException e) {
                    throw new RuntimeException(e);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void processResponse(BufferedRequest request, MutableBufferedResponse response) {
                try {
                    handleRequestAndResponse(request, response, ps);
                } catch (MessageFormatException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (XPathExpressionException e) {
                    throw new RuntimeException(e);
                } catch (TransformerException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        requestHandler = new BufferingHttpRequestHandler(requestHandler, interceptor, 10240);

        HttpProxyConnectionHandler httpProxy = new HttpProxyConnectionHandler(requestHandler);

        SSLContextSelector contextSelector = new AutoGeneratingContextSelector(new File("mitm/FakeCAStore"), "JKS", "passphrase".toCharArray(), "passphrase".toCharArray(), "mykey");
        SSLConnectionHandler ssl = new SSLConnectionHandler(contextSelector, true, httpProxy);
        httpProxy.setConnectHandler(ssl);
        InetSocketAddress listen = new InetSocketAddress("localhost", 3737);
        SocksConnectionHandler socks = new SocksConnectionHandler(ssl, true);
        Server server = new Server(listen, socks);
        server.start();
        System.out.println("MITM proxy server started listening at: " + listen.toString());
        System.in.read();
    }

    private static void handleRequestAndResponse(BufferedRequest request, MutableBufferedResponse response, PrintStream ps) throws MessageFormatException, IOException, TransformerException, XPathExpressionException {
        String responseHeaderContentType = response.getHeader("Content-Type");

        ContentType responseContentType = responseHeaderContentType == null ? null : ContentType.parse(responseHeaderContentType);

        StatusLine statusLine = BasicLineParser.parseStatusLine(response.getStartLine(), LINE_PARSER);

        List<NameValuePair> headers = new ArrayList<NameValuePair>();

        for (NamedValue header : response.getHeaders()) {
            headers.add(new BasicNameValuePair(header.getName(), header.getValue()));
        }

        String contentEncoding = null;

        if (responseContentType != null && responseContentType.getCharset() != null) {
            contentEncoding = responseContentType.getCharset().name();
        }

        if (contentEncoding == null) {
            contentEncoding = "UTF-8";
        }

        ps.printf("------ BEGIN at %s ------\n", new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));

        URL url = getUrl(request);

        ps.println(request.getMethod() + " " + url);

        ps.printf("------ request ------\n");

        ps.println(request.getStartLine());

        for (NamedValue header : request.getHeaders()) {
            ps.println(header.getName() + header.getSeparator() + header.getValue());
        }

        if (request.getDecodedContent() != null) {
            ps.println(new String(request.getDecodedContent()));
        }

        ps.println();

        ps.printf("------ response ------\n");

        ps.println(response.getStartLine());

        for (NamedValue header : response.getHeaders()) {
            ps.println(header.getName() + header.getSeparator() + header.getValue());
        }

        Page page = new Page(url, new WebResponse(new DownloadedContent.InMemory(response.getDecodedContent()), statusLine.getStatusCode(), statusLine.getReasonPhrase(), headers, contentEncoding));

        ps.println();

        ps.println(page.getUrl());

        ps.println();

        ps.println(page.getText());

        ps.println();

        ps.printf("------ END ------\n\n");
    }

    private static URL getUrl(RequestHeader requestHeader) throws MalformedURLException, MessageFormatException {
        return new URL((requestHeader.isSsl() ? "https://" : "http://") + requestHeader.getHeader("Host") + requestHeader.getResource());
    }
}
