package net.pickapack.net.mitm.emailInterception.client;

import net.pickapack.net.IOHelper;
import net.pickapack.net.mitm.emailInterception.helper.GmailJsonHelper;
import net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail.ReceivedEmailRule;
import net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail.ReceivedEmailSubjectRule;
import net.pickapack.net.mitm.emailInterception.model.rule.sentEmail.SentEmailRule;
import net.pickapack.net.mitm.emailInterception.model.task.EmailInterceptionTask;
import net.pickapack.net.mitm.emailInterception.service.ServiceManager;
import net.pickapack.net.url.URLHelper;
import net.pickapack.spider.noJs.spider.DownloadedContent;
import net.pickapack.spider.noJs.spider.Page;
import net.pickapack.spider.noJs.spider.WebResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
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
import java.io.*;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Startup {//TODO: logic to be moved into EmailInterceptionService(Impl)!!!
    public static final BasicLineParser LINE_PARSER = new BasicLineParser(new ProtocolVersion("HTTP", 1, 1));

    public static void main(String[] args) throws IOException, GeneralSecurityException, SQLException {
        List<ReceivedEmailRule> receivedEmailRules = new ArrayList<ReceivedEmailRule>();
        List<SentEmailRule> sentEmailRules = new ArrayList<SentEmailRule>();

        receivedEmailRules.add(new ReceivedEmailSubjectRule("rerer")); //TODO: load from file

        EmailInterceptionTask emailInterceptionTask = new EmailInterceptionTask(receivedEmailRules, sentEmailRules);

        ServiceManager.getEmailInterceptionService().addEmailInterceptionTask(emailInterceptionTask);

        final PrintStream ps = new PrintStream(new File("mitm_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".log"));

        IOHelper.extractResource("mitm/FakeCA.cer");
        IOHelper.extractResource("mitm/FakeCAStore");

        HttpRequestHandler requestHandler = new DefaultHttpRequestHandler();

        BufferedMessageInterceptor interceptor = new BufferedMessageInterceptor() {
            @Override
            public Action directResponse(RequestHeader request, MutableResponseHeader response) {
                try {
                    String url = getUrl(request).toString();
                    System.out.println(url);

                    if(url.contains("https://mail.google.com/mail/")) {
                        String view = URLHelper.getQueryParameterFromUrl(url, "view");
                        String search = URLHelper.getQueryParameterFromUrl(url, "search");
                        String action = URLHelper.getQueryParameterFromUrl(url, "act");

                        if(search != null && search.equals("inbox") && view != null && (view.equals("tl") || view.equals("cv"))) {
                            return Action.BUFFER;
                        }

                        if(action != null && action.equals("sm")) {
                            return Action.BUFFER;
                        }
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
        System.out.println("Gmail interception proxy server started listening at: " + listen.toString());
        System.in.read();
    }

    private static void handleRequestAndResponse(BufferedRequest request, MutableBufferedResponse response, PrintStream ps) throws MessageFormatException, IOException, TransformerException, XPathExpressionException {
        String responseHeaderContentType = response.getHeader("Content-Type");

        ContentType responseContentType = responseHeaderContentType == null ? null : ContentType.parse(responseHeaderContentType);

        StatusLine statusLine = BasicLineParser.parseStatusLine(response.getStartLine(), LINE_PARSER);

        List<NameValuePair> requestHeaders = new ArrayList<NameValuePair>();

        for (NamedValue header : request.getHeaders()) {
            requestHeaders.add(new BasicNameValuePair(header.getName(), header.getValue()));
        }

        List<NameValuePair> responseHeaders = new ArrayList<NameValuePair>();

        for (NamedValue header : response.getHeaders()) {
            responseHeaders.add(new BasicNameValuePair(header.getName(), header.getValue()));
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

        Page pageRequest = new Page(url, new WebResponse(new DownloadedContent.InMemory(request.getDecodedContent()), -1, "", requestHeaders, null));

        ps.println();

        ps.println(pageRequest.getUrl());

        ps.println();

        ps.println(pageRequest.getText());

        ps.println();

        ps.printf("------ response ------\n");

        ps.println(response.getStartLine());

        for (NamedValue header : response.getHeaders()) {
            ps.println(header.getName() + header.getSeparator() + header.getValue());
        }

        Page pageResponse = new Page(url, new WebResponse(new DownloadedContent.InMemory(response.getDecodedContent()), statusLine.getStatusCode(), statusLine.getReasonPhrase(), responseHeaders, contentEncoding));

        ps.println();

        ps.println(pageResponse.getUrl());

        ps.println();

        ps.println(pageResponse.getText());

        ps.println();

        ps.printf("------ END ------\n\n");

        handleGmailRequestAndResponse(pageRequest, pageResponse);
    }

    private static void handleGmailRequestAndResponse(Page pageRequest, Page pageResponse) throws IOException, TransformerException, XPathExpressionException {
        String url = pageResponse.getUrl().toString();

        if(url.contains("https://mail.google.com/mail/")) {
            String view = URLHelper.getQueryParameterFromUrl(url, "view");
            String search = URLHelper.getQueryParameterFromUrl(url, "search");
            String action = URLHelper.getQueryParameterFromUrl(url, "act");

            if(search != null && search.equals("inbox") && view != null) {
                if(view.equals("tl")) {
                    handleThreadList(pageRequest, pageResponse);
                } else if(view.equals("cv")) {
                    handleConversationView(pageRequest, pageResponse);
                }
            }

            if(action != null && action.equals("sm")) {
                handleSendMail(pageRequest, pageResponse);
            }
        }
    }

    private static void handleThreadList(Page pageRequest, Page pageResponse) throws IOException, TransformerException, XPathExpressionException {
        GmailJsonHelper.parseThreadListResponseJson(extractJson(pageResponse));
    }

    private static void handleConversationView(Page pageRequest, Page pageResponse) throws IOException, TransformerException, XPathExpressionException {
        GmailJsonHelper.parseConversationViewResponseJson(extractJson(pageResponse));
    }

    private static void handleSendMail(Page pageRequest, Page pageResponse) throws IOException, TransformerException, XPathExpressionException {
        String requestBody = pageRequest.getText().trim();
        String sendMailParameters = pageRequest.getUrl().getProtocol() + "://" + pageRequest.getUrl().getHost() + "/?" + requestBody;

        String to = URLHelper.getQueryParameterFromUrl(sendMailParameters, "to");
        String subject = URLHelper.getQueryParameterFromUrl(sendMailParameters, "subject");
        String content = URLHelper.getQueryParameterFromUrl(sendMailParameters, "body");

        GmailJsonHelper.parseSendMailResponseJson(to, subject, content, extractJson(pageResponse));
    }

    private static URL getUrl(RequestHeader requestHeader) throws MalformedURLException, MessageFormatException {
        return new URL((requestHeader.isSsl() ? "https://" : "http://") + requestHeader.getHeader("Host") + requestHeader.getResource());
    }

    private static String extractJson(Page page) throws IOException {
        String text = StringUtils.substringAfter(page.getText(), "while(1);").trim();

        List<String> lines = IOUtils.readLines(new StringReader(text));

        List<String> resultLines = new ArrayList<String>();

        for(String line : lines) {
            if(!StringUtils.isNumeric(line.trim())) {
                resultLines.add(line);
            }
        }

        return StringUtils.join(resultLines, "\n");
    }
}
