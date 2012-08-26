package net.pickapack.spider.noJs.spider;

import net.pickapack.dateTime.DateHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoJSSpider {
    private DefaultHttpClient httpClient;
    private HttpHost proxy;
    private List<String> blackListedHosts;
    private String userAgent;
    private boolean blackListEnabled = true;

    public NoJSSpider(String userAgent, int timeout) {
        this(userAgent, null, timeout);
    }

    public NoJSSpider(String userAgent, HttpHost proxy, int timeout) {
        this.userAgent = userAgent;

        SSLSocketFactory sf = null;
        try {
            sf = new SSLSocketFactory(new TrustStrategy(){
                @Override
                public boolean isTrusted(X509Certificate[] chain,
                                         String authType) throws CertificateException {
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        registry.register(new Scheme("https", 443, sf));
        ClientConnectionManager ccm = new PoolingClientConnectionManager(registry);

        this.httpClient = new DefaultHttpClient(ccm);
        this.proxy = proxy;

        if (this.proxy != null) {
            this.httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, this.proxy);
        }

        this.httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
        this.httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);

        this.blackListedHosts = new ArrayList<String>();

        this.httpClient.setCookieStore(new BasicCookieStore());

        this.httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

        this.httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
    }

    public Page getPage(URL url) throws IOException, XPathExpressionException, TransformerException {
        return this.getPage(url, HttpMethod.GET, null, null, null, null);
    }

    public Page getPage(URL url, HttpMethod submitMethod, List<NameValuePair> requestParameters, List<Header> extraHeaders, String encoding) throws IOException, XPathExpressionException, TransformerException {
        return getPage(url, submitMethod, null, requestParameters, extraHeaders, encoding);
    }

    public Page getPage(URL url, HttpMethod submitMethod, String body, List<Header> extraHeaders, String encoding) throws IOException, XPathExpressionException, TransformerException {
        return getPage(url, submitMethod, body, null, extraHeaders, encoding);
    }

    public Page getPage(URL url, HttpMethod submitMethod, String body, List<NameValuePair> requestParameters, List<Header> extraHeaders, String encoding) throws IOException, XPathExpressionException, TransformerException {
        try {
            HttpRequestBase httpMethod = buildHttpMethod(submitMethod, url.toURI());

            if (httpMethod instanceof HttpEntityEnclosingRequest) {
                if (body != null && requestParameters != null) {
                    throw new IllegalArgumentException();
                }

                if (body != null) {
                    ((HttpEntityEnclosingRequest) httpMethod).setEntity(new StringEntity(body, Charset.forName("utf-8")));
                } else if (requestParameters != null) {
                    ((HttpEntityEnclosingRequest) httpMethod).setEntity(new UrlEncodedFormEntity(requestParameters, Charset.forName("utf-8")));
                }
            } else {
                if (requestParameters != null) {
                    String query = URLEncodedUtils.format(requestParameters, DEFAULT_CHARSET);
                    httpMethod.setURI(new URI(new URIBuilder(url.toURI()).setQuery(query).toString()));
                }
            }

            httpMethod.setHeader(new BasicHeader("User-Agent", this.userAgent));

            if (extraHeaders != null) {
                for (Header extraHeader : extraHeaders) {
                    httpMethod.setHeader(extraHeader);
                }
            }

            HttpResponse response = httpClient.execute(httpMethod);
            HttpEntity entity = response.getEntity();
            DownloadedContent downloadedBody = downloadResponseBody(response);
            if (encoding == null) {
                Charset charset = ContentType.getOrDefault(entity).getCharset();
                encoding = charset != null ? charset.name() : null;
            }
            WebResponse webResponse = makeWebResponse(response, downloadedBody, encoding);
            EntityUtils.consume(entity);

            return new Page(url, webResponse);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpRequestBase buildHttpMethod(HttpMethod submitMethod, final URI uri) {
        final HttpRequestBase method;
        switch (submitMethod) {
            case GET:
                method = new HttpGet(uri);
                break;

            case POST:
                method = new HttpPost(uri);
                break;

            case PUT:
                method = new HttpPut(uri);
                break;

            case DELETE:
                method = new HttpDelete(uri);
                break;

            case OPTIONS:
                method = new HttpOptions(uri);
                break;

            case HEAD:
                method = new HttpHead(uri);
                break;

            case TRACE:
                method = new HttpTrace(uri);
                break;

            default:
                throw new IllegalStateException("Submit method not yet supported: " + submitMethod);
        }
        return method;
    }

    private WebResponse makeWebResponse(final HttpResponse httpResponse, final DownloadedContent responseBody, String encoding) throws IOException {
        String statusMessage = httpResponse.getStatusLine().getReasonPhrase();
        if (statusMessage == null) {
            statusMessage = "Unknown status message";
        }
        final int statusCode = httpResponse.getStatusLine().getStatusCode();
        final List<NameValuePair> headers = new ArrayList<NameValuePair>();
        for (final Header header : httpResponse.getAllHeaders()) {
            headers.add(new BasicNameValuePair(header.getName(), header.getValue()));
        }

        return new WebResponse(responseBody, statusCode, statusMessage, headers, encoding);
    }

    protected DownloadedContent downloadResponseBody(final HttpResponse httpResponse) throws IOException {
        final HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity == null) {
            return new DownloadedContent.InMemory(new byte[]{});
        }

        return downloadContent(httpEntity.getContent());
    }

    public static DownloadedContent downloadContent(final InputStream is) throws IOException {
        if (is == null) {
            return new DownloadedContent.InMemory(new byte[]{});
        }

        final File file = File.createTempFile("spider", ".tmp");
        file.deleteOnExit();
        DownloadCountingOutputStream out = new DownloadCountingOutputStream(new FileOutputStream(file));
        OutputStream os = new BufferedOutputStream(out);
        out.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Downloaded bytes : " + ((DownloadCountingOutputStream) e.getSource()).getByteCount());
            }
        });
        IOUtils.copy(is, os);
        is.close();
        os.close();

        return new DownloadedContent.OnFile(file);
    }

    public void downloadDocument(URL refererUrl, URL url, File localFile) {
        this.downloadDocument(refererUrl, url, localFile.getAbsolutePath());
    }

    public void downloadDocument(URL refererUrl, URL url, String localFileName) {
        if (this.blackListedHosts.contains(url.getHost())) {
            System.out.printf("[%s]         Skipped downloading document from blacklisted host: %s to %s\n", DateHelper.toString(new Date()), url, localFileName);
            return;
        }

        new File(localFileName).getParentFile().mkdirs();

        try {
            System.out.printf("[%s]         Downloading document: %s to %s\n", DateHelper.toString(new Date()), url, localFileName);

            ArrayList<Header> extraHeaders = new ArrayList<Header>();

            if (refererUrl != null) {
                extraHeaders.add(new BasicHeader("Referer", refererUrl.toString()));
            }

            Page page = this.getPage(url, HttpMethod.GET, null, null, extraHeaders, null);

            InputStream is = page.getResponse().getResponseBody().getInputStream();
            FileOutputStream out = new FileOutputStream(localFileName);
            OutputStream os = new BufferedOutputStream(out);
            IOUtils.copy(is, os);
            is.close();
            os.close();

            System.out.printf("[%s]         Document downloaded: %s to %s\n", DateHelper.toString(new Date()), url, localFileName);
        } catch (IOException e) {
            recordException(e);

            if (blackListEnabled && e instanceof SocketTimeoutException || e instanceof HttpHostConnectException || e instanceof NoHttpResponseException || e instanceof ConnectTimeoutException) {
                this.blackListedHosts.add(url.getHost());
            }
        } catch (XPathExpressionException e) {
            recordException(e);
        } catch (TransformerException e) {
            recordException(e);
        } catch (Exception e) {
            recordException(e);
        }
    }

    public static void recordException(Exception e) {
        System.out.print(String.format("[%s Exception] %s\r\n", DateHelper.toString(new Date()), e));
        e.printStackTrace();
    }

    public DefaultHttpClient getHttpClient() {
        return httpClient;
    }

    public HttpHost getProxy() {
        return proxy;
    }

    public boolean isBlackListEnabled() {
        return blackListEnabled;
    }

    public void setBlackListEnabled(boolean blackListEnabled) {
        this.blackListEnabled = blackListEnabled;
    }

    private static final long MAX_IN_MEMORY = 2000 * 1024;

    public static final String DEFAULT_CHARSET = "ISO-8859-1";

    public static final String FIREFOX_3_6 = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8";

    public static final String INTERNET_EXPLORER_6 = "Mozilla/4.0 (compatible; MSIE 6.0; Windows 98)";

    public static final String INTERNET_EXPLORER_7 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)";

    public static final String INTERNET_EXPLORER_8 = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0)";

    public static class DownloadCountingOutputStream extends CountingOutputStream {
        private ActionListener listener = null;

        public DownloadCountingOutputStream(OutputStream out) {
            super(out);
        }

        public void setListener(ActionListener listener) {
            this.listener = listener;
        }

        @Override
        protected void afterWrite(int n) throws IOException {
            super.afterWrite(n);
            if (listener != null) {
                listener.actionPerformed(new ActionEvent(this, 0, null));
            }
        }
    }
}
