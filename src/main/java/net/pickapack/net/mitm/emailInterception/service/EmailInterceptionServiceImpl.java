package net.pickapack.net.mitm.emailInterception.service;

import com.j256.ormlite.dao.Dao;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.model.ModelElement;
import net.pickapack.net.IOHelper;
import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.task.EmailInterceptionTask;
import net.pickapack.net.url.URLHelper;
import net.pickapack.service.AbstractService;
import net.pickapack.spider.noJs.spider.DownloadedContent;
import net.pickapack.spider.noJs.spider.Page;
import net.pickapack.spider.noJs.spider.WebResponse;
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
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.*;

public class EmailInterceptionServiceImpl extends AbstractService implements EmailInterceptionService {
    private Dao<EmailInterceptionTask, Long> emailInterceptionTasks;
    private Dao<ReceivedEmailEvent, Long> receivedEmailEvents;
    private Dao<SentEmailEvent, Long> sentEmailEvents;

    @SuppressWarnings("unchecked")
    public EmailInterceptionServiceImpl() {
        super(ServiceManager.getDatabaseUrl(), Arrays.<Class<? extends ModelElement>>asList(EmailInterceptionTask.class, ReceivedEmailEvent.class, SentEmailEvent.class));

        this.emailInterceptionTasks = createDao(EmailInterceptionTask.class);
        this.receivedEmailEvents = createDao(ReceivedEmailEvent.class);
        this.sentEmailEvents = createDao(SentEmailEvent.class);
    }

    @Override
    public List<EmailInterceptionTask> getEmailInterceptionTasks() {
        return this.getAllItems(this.emailInterceptionTasks);
    }

    @Override
    public EmailInterceptionTask getEmailInterceptionTaskById(long id) {
        return this.getItemById(this.emailInterceptionTasks, id);
    }

    @Override
    public void addEmailInterceptionTask(EmailInterceptionTask emailInterceptionTask) {
        this.addItem(this.emailInterceptionTasks, EmailInterceptionTask.class, emailInterceptionTask);
    }

    @Override
    public void removeEmailInterceptionTaskById(long id) {
        this.removeItemById(this.emailInterceptionTasks, EmailInterceptionTask.class, id);
    }

    @Override
    public void updateEmailInterceptionTask(EmailInterceptionTask emailInterceptionTask) {
        this.updateItem(this.emailInterceptionTasks, EmailInterceptionTask.class, emailInterceptionTask);
    }

    @Override
    public List<ReceivedEmailEvent> getReceivedEmailEvents() {
        return this.getAllItems(this.receivedEmailEvents);
    }

    @Override
    public ReceivedEmailEvent getReceivedEmailEventById(long id) {
        return this.getItemById(this.receivedEmailEvents, id);
    }

    @Override
    public ReceivedEmailEvent getReceivedEmailEventByNo(String no) {
        return this.getFirstItemByTitle(this.receivedEmailEvents, no);
    }

    @Override
    public void addReceivedEmailEvent(ReceivedEmailEvent receivedEmailEvent) {
        this.addItem(this.receivedEmailEvents, ReceivedEmailEvent.class, receivedEmailEvent);
    }

    @Override
    public void removeReceivedEmailEventById(long id) {
        this.removeItemById(this.receivedEmailEvents, ReceivedEmailEvent.class, id);
    }

    @Override
    public void updateReceivedEmailEvent(ReceivedEmailEvent receivedEmailEvent) {
        this.updateItem(this.receivedEmailEvents, ReceivedEmailEvent.class, receivedEmailEvent);
    }

    @Override
    public List<SentEmailEvent> getSentEmailEvents() {
        return this.getAllItems(this.sentEmailEvents);
    }

    @Override
    public SentEmailEvent getSentEmailEventById(long id) {
        return this.getItemById(this.sentEmailEvents, id);
    }

    @Override
    public SentEmailEvent getSentEmailEventByNo(String no) {
        return this.getFirstItemByTitle(this.sentEmailEvents, no);
    }

    @Override
    public void addSentEmailEvent(SentEmailEvent sentEmailEvent) {
        this.addItem(this.sentEmailEvents, SentEmailEvent.class, sentEmailEvent);
    }

    @Override
    public void removeSentEmailEventById(long id) {
        this.removeItemById(this.sentEmailEvents, SentEmailEvent.class, id);
    }

    @Override
    public void updateSentEmailEvent(SentEmailEvent sentEmailEvent) {
        this.updateItem(this.sentEmailEvents, SentEmailEvent.class, sentEmailEvent);
    }

    @Override
    public void runEmailInterceptionTask(final EmailInterceptionTask emailInterceptionTask) {
        IOHelper.extractResource("mitm/FakeCA.cer");
        IOHelper.extractResource("mitm/FakeCAStore");

        HttpRequestHandler requestHandler = new DefaultHttpRequestHandler();

        BufferedMessageInterceptor interceptor = new BufferedMessageInterceptor() {
            @Override
            public Action directResponse(RequestHeader request, MutableResponseHeader response) {
                try {
                    String url = getUrlFromRequestHeader(request);

                    if (url.contains("https://mail.google.com/mail/")) {
                        String view = URLHelper.getQueryParameterFromUrl(url, "view");
                        String search = URLHelper.getQueryParameterFromUrl(url, "search");
                        String action = URLHelper.getQueryParameterFromUrl(url, "act");

                        if (search != null && search.equals("inbox") && view != null && (view.equals("tl") || view.equals("cv"))) {
                            System.out.println("buffered: " + url);
                            return Action.BUFFER;
                        }

                        if (action != null && (action.equals("fup") || action.equals("sm"))) {
                            System.out.println("buffered: " + url);
                            return Action.BUFFER;
                        }
                    }

                    System.out.println("ignored: " + url);
                    return Action.IGNORE;
                } catch (MessageFormatException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void processResponse(BufferedRequest request, MutableBufferedResponse response) {
                try {
                    if (!handleRequestAndResponse(emailInterceptionTask, request, response)) {
                        response.setStatus("404");
                    }
                } catch (MessageFormatException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (XPathExpressionException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (TransformerException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        };

        requestHandler = new BufferingHttpRequestHandler(requestHandler, interceptor, 10240);

        HttpProxyConnectionHandler httpProxy = new HttpProxyConnectionHandler(requestHandler);

        try {
            SSLContextSelector contextSelector = new AutoGeneratingContextSelector(new File("mitm/FakeCAStore"), "JKS", "passphrase".toCharArray(), "passphrase".toCharArray(), "mykey");
            SSLConnectionHandler ssl = new SSLConnectionHandler(contextSelector, true, httpProxy);
            httpProxy.setConnectHandler(ssl);
            InetSocketAddress listen = new InetSocketAddress(emailInterceptionTask.getPort());
            SocksConnectionHandler socks = new SocksConnectionHandler(ssl, true);
            Server server = new Server(listen, socks);
            server.start();
            System.out.printf("[%s] Gmail interception proxy server started listening at: %s\n", DateHelper.toString(new Date()), listen.toString());
            System.in.read();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final BasicLineParser LINE_PARSER = new BasicLineParser(new ProtocolVersion("HTTP", 1, 1));

    private static boolean handleRequestAndResponse(EmailInterceptionTask emailInterceptionTask, BufferedRequest request, MutableBufferedResponse response) throws MessageFormatException, IOException, TransformerException, XPathExpressionException {
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

        URL url = new URL(getUrlFromRequestHeader(request));

        Page pageRequest = new Page(url, new WebResponse(new DownloadedContent.InMemory(request.getDecodedContent()), -1, "", requestHeaders, null));

        Page pageResponse = new Page(url, new WebResponse(new DownloadedContent.InMemory(response.getDecodedContent()), statusLine.getStatusCode(), statusLine.getReasonPhrase(), responseHeaders, contentEncoding));

        return handleGmailRequestAndResponse(emailInterceptionTask, pageRequest, pageResponse);
    }

    private static boolean handleGmailRequestAndResponse(EmailInterceptionTask emailInterceptionTask, Page pageRequest, Page pageResponse) throws IOException, TransformerException, XPathExpressionException {
        String url = pageResponse.getUrl().toString();

        if (url.contains("https://mail.google.com/mail/")) {
            String view = URLHelper.getQueryParameterFromUrl(url, "view");
            String search = URLHelper.getQueryParameterFromUrl(url, "search");
            String action = URLHelper.getQueryParameterFromUrl(url, "act");

            if (search != null && search.equals("inbox") && view != null && view.equals("cv")) {
                return handleReceivedEmail(emailInterceptionTask, pageRequest, pageResponse);
            } else if (action != null && action.equals("fup")) {
                return handleAttachmentUploaded(emailInterceptionTask, pageRequest, pageResponse);
            } else if (action != null && action.equals("sm")) {
                return handleSentMail(emailInterceptionTask, pageRequest, pageResponse);
            }
        }

        return true;
    }

    private static boolean handleReceivedEmail(EmailInterceptionTask emailInterceptionTask, Page pageRequest, Page pageResponse) throws IOException, TransformerException, XPathExpressionException {
        String rawCookie = pageResponse.getResponse().getHeader("set-cookie");

        String email = "";

        if (rawCookie != null) {
            for (String pair : rawCookie.split(";")) {
                String[] parts = pair.split("=");
                if (parts.length == 2 && parts[0].equals("gmailchat")) {
                    email = parts[1].substring(0, parts[1].lastIndexOf("/"));
                    break;
                }
            }
        }

        String json = GmailJsonParser.extractJson(pageResponse.getText());

        List<ReceivedEmailEvent> receivedEmailEvents = GmailJsonParser.parseReceivedEmails(emailInterceptionTask, email, json);

        for (ReceivedEmailEvent receivedEmailEvent : receivedEmailEvents) {
            ServiceManager.getEmailInterceptionService().addReceivedEmailEvent(receivedEmailEvent);
        }

        for (ReceivedEmailEvent receivedEmailEvent : receivedEmailEvents) {
            if (!emailInterceptionTask.getReceivedEmailRule().apply(receivedEmailEvent)) {
                return false;
            }
        }

        return true;
    }

    private static Map<String, List<String>> attachments = new LinkedHashMap<String, List<String>>();

    private static boolean handleAttachmentUploaded(EmailInterceptionTask emailInterceptionTask, Page pageRequest, Page pageResponse) throws IOException {
        String headerContentDisposition = pageRequest.getResponse().getHeader("Content-Disposition");
        String[] parts = headerContentDisposition.split(";");

        String attachmentName = null;

        if (parts.length == 2 && parts[0].equals("attachment")) {
            attachmentName = parts[1].split("=")[1];
            attachmentName = attachmentName.substring(attachmentName.indexOf("\""), attachmentName.lastIndexOf("\""));
        }

        if (attachmentName == null) {
            return true;
        }

        String json = GmailJsonParser.extractJson(pageResponse.getText());

        String no = GmailJsonParser.parseAttachmentUploaded(emailInterceptionTask, json);

        if (no == null) {
            return true;
        }

        if (!attachments.containsKey(no)) {
            attachments.put(no, new ArrayList<String>());
        }

        attachments.get(no).add(attachmentName);

        return true;
    }

    private static boolean handleSentMail(EmailInterceptionTask emailInterceptionTask, Page pageRequest, Page pageResponse) throws IOException, TransformerException, XPathExpressionException {
        String rawCookie = pageResponse.getResponse().getHeader("set-cookie");

        String email = "";

        if (rawCookie != null) {
            for (String pair : rawCookie.split(";")) {
                String[] parts = pair.split("=");
                if (parts.length == 2 && parts[0].equals("gmailchat")) {
                    email = parts[1].substring(0, parts[1].lastIndexOf("/"));
                    break;
                }
            }
        }

        final String urlFromQueryString = getUrlFromQueryString(pageRequest.getText().trim());

        final String json = GmailJsonParser.extractJson(pageResponse.getText());

        List<String> tos = new ArrayList<String>() {{
            addAll(Arrays.asList(URLHelper.getQueryParameterFromUrl(urlFromQueryString, "to").split(",")));
        }};
        String subject = URLHelper.getQueryParameterFromUrl(urlFromQueryString, "subject");
        String content = URLHelper.getQueryParameterFromUrl(urlFromQueryString, "body");

        SentEmailEvent sentEmailEvent = GmailJsonParser.parseSentEmails(emailInterceptionTask, email, json, tos, subject, content);
        if(sentEmailEvent != null) {
            if (attachments.containsKey(sentEmailEvent.getNo())) {
                sentEmailEvent.setAttachmentNames(attachments.get(sentEmailEvent.getNo()));
                attachments.remove(sentEmailEvent.getNo());
            }
            else {
                sentEmailEvent.setAttachmentNames(new ArrayList<String>());
            }

            ServiceManager.getEmailInterceptionService().addSentEmailEvent(sentEmailEvent);
            if (!emailInterceptionTask.getSentEmailRule().apply(sentEmailEvent)) {
                return false;
            }
        }

        return true;
    }

    private static String getUrlFromRequestHeader(RequestHeader requestHeader) throws MalformedURLException, MessageFormatException {
        return (requestHeader.isSsl() ? "https://" : "http://") + requestHeader.getHeader("Host") + requestHeader.getResource();
    }

    private static String getUrlFromQueryString(String queryString) {
        return "http://gmail.com?" + queryString;
    }
}
