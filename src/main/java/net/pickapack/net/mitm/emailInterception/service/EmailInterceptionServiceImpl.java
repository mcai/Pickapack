package net.pickapack.net.mitm.emailInterception.service;

import com.j256.ormlite.dao.Dao;
import com.jayway.jsonpath.JsonPath;
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
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EmailInterceptionServiceImpl extends AbstractService implements EmailInterceptionService {
    private Dao<EmailInterceptionTask, Long> emailInterceptionTasks;
    private Dao<ReceivedEmailEvent, Long> receivedEmailEvents;
    private Dao<SentEmailEvent, Long> sentEmailEvents;

    @SuppressWarnings("unchecked")
    public EmailInterceptionServiceImpl(){
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
                    if(!handleRequestAndResponse(emailInterceptionTask, request, response)) {
                        response.setStatus("500");
                    }
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

        try {
            SSLContextSelector contextSelector = new AutoGeneratingContextSelector(new File("mitm/FakeCAStore"), "JKS", "passphrase".toCharArray(), "passphrase".toCharArray(), "mykey");
            SSLConnectionHandler ssl = new SSLConnectionHandler(contextSelector, true, httpProxy);
            httpProxy.setConnectHandler(ssl);
            InetSocketAddress listen = new InetSocketAddress("localhost", emailInterceptionTask.getPort());
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

        URL url = getUrl(request);

        Page pageRequest = new Page(url, new WebResponse(new DownloadedContent.InMemory(request.getDecodedContent()), -1, "", requestHeaders, null));

        Page pageResponse = new Page(url, new WebResponse(new DownloadedContent.InMemory(response.getDecodedContent()), statusLine.getStatusCode(), statusLine.getReasonPhrase(), responseHeaders, contentEncoding));

        return handleGmailRequestAndResponse(emailInterceptionTask, pageRequest, pageResponse);
    }

    private static boolean handleGmailRequestAndResponse(EmailInterceptionTask emailInterceptionTask, Page pageRequest, Page pageResponse) throws IOException, TransformerException, XPathExpressionException {
        String url = pageResponse.getUrl().toString();

        if(url.contains("https://mail.google.com/mail/")) {
            String view = URLHelper.getQueryParameterFromUrl(url, "view");
            String search = URLHelper.getQueryParameterFromUrl(url, "search");
            String action = URLHelper.getQueryParameterFromUrl(url, "act");

            if (search != null && search.equals("inbox") && view != null && view.equals("cv")) {
                return handleReceivedEmail(emailInterceptionTask, pageRequest, pageResponse);
            }
            else if(action != null && action.equals("sm")) {
                return handleSentMail(emailInterceptionTask, pageRequest, pageResponse);
            }
        }

        return true;
    }

    private static boolean handleReceivedEmail(EmailInterceptionTask emailInterceptionTask, Page pageRequest, Page pageResponse) throws IOException, TransformerException, XPathExpressionException {
        String email = ""; //TODO
        List<String> attachmentNames = null; //TODO

        for (Object row : JsonPath.<List<Object>>read(extractJson(pageResponse), "$[0][*]")) {
            if (row.toString().contains("[\"ms\"")) {
                long id = Long.parseLong(JsonPath.read(row.toString(), "$[1]").toString());

                String from = JsonPath.read(row.toString(), "$[6]").toString();

                List<String> tos = JsonPath.read(row.toString(), "$[13][1]");

                String subject = JsonPath.read(row.toString(), "$[13][5]").toString();

                Long receiveTime = JsonPath.read(row.toString(), "$[7]");

                String content = JsonPath.read(row.toString(), "$[13][6]").toString();

                ReceivedEmailEvent receivedEmailEvent = new ReceivedEmailEvent(emailInterceptionTask, id, email, from, tos, subject, content, attachmentNames);
                receivedEmailEvent.setReceiveTime(receiveTime);
                ServiceManager.getEmailInterceptionService().addReceivedEmailEvent(receivedEmailEvent);

                return emailInterceptionTask.getReceivedEmailRule().apply(receivedEmailEvent);
            }
        }

        throw new IllegalArgumentException();
    }

    private static boolean handleSentMail(EmailInterceptionTask emailInterceptionTask, Page pageRequest, Page pageResponse) throws IOException, TransformerException, XPathExpressionException {
        String requestBody = pageRequest.getText().trim();
        String sendMailParameters = pageRequest.getUrl().getProtocol() + "://" + pageRequest.getUrl().getHost() + "/?" + requestBody;

        String email = ""; //TODO
        List<String> attachmentNames = null; //TODO

        for (Object row : JsonPath.<List<Object>>read(extractJson(pageResponse), "$[*]")) {
            if (row.toString().contains("[\"a\"")) {
                long id = Long.parseLong(JsonPath.read(row.toString(), "$[3][0]").toString());

                String to = URLHelper.getQueryParameterFromUrl(sendMailParameters, "to");
                String subject = URLHelper.getQueryParameterFromUrl(sendMailParameters, "subject");
                String content = URLHelper.getQueryParameterFromUrl(sendMailParameters, "body");

                String result = JsonPath.read(row.toString(), "$[2]").toString();

                SentEmailEvent sentEmailEvent = new SentEmailEvent(emailInterceptionTask, id, email, Arrays.asList(to), subject, content, attachmentNames, result);
                ServiceManager.getEmailInterceptionService().addSentEmailEvent(sentEmailEvent);

                return emailInterceptionTask.getSentEmailRule().apply(sentEmailEvent);
            }
        }

        throw new IllegalArgumentException();
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
