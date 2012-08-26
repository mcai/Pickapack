package net.pickapack.spider.noJs.spider;

import org.apache.http.NameValuePair;

import java.util.List;

public class WebResponse {
    private DownloadedContent responseBody;
    private int statusCode;
    private String statusMessage;
    private List<NameValuePair> headers;
    private String encoding;

    public WebResponse(DownloadedContent responseBody, int statusCode, String statusMessage, List<NameValuePair> headers, String encoding) {
        this.responseBody = responseBody;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.encoding = encoding;

        if (this.encoding == null) {
            this.encoding = "UTF-8";
        }
    }

    public String getHeader(String name) {
        for(NameValuePair pair : this.headers) {
            if(pair.getName().equals(name)) {
                return pair.getValue();
            }
        }

        return null;
    }

    public String getEncoding() {
        return encoding;
    }

    public DownloadedContent getResponseBody() {
        return responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public List<NameValuePair> getHeaders() {
        return headers;
    }
}
