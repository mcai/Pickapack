package net.pickapack.spider.noJs.spider;

import net.pickapack.text.XPathHelper;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class Page {
    private URL url;
    private WebResponse response;
    private String text;
    private Document document;

    public Page(URL url, WebResponse response) {
        this.url = url;
        this.response = response;
    }

    public Node getFirstByXPath(String xpathExpression) {
        return XPathHelper.getFirstByXPath(this.getDocument(), xpathExpression);
    }

    public List<Node> getByXPath(String xpathExpression) {
        return XPathHelper.getByXPath(this.getDocument(), xpathExpression);
    }

    private void buildText() {
        try {
            StringWriter sw = new StringWriter();
            IOUtils.copy(this.response.getResponseBody().getInputStream(), sw, Charset.forName(this.response.getEncoding()));
            this.text = sw.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildDocument() {
        try {
            this.document = XPathHelper.parse(this.getText().getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public URL getUrl() {
        return url;
    }

    public WebResponse getResponse() {
        return response;
    }

    public String getText() {
        if(text == null) {
            this.buildText();
        }

        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Document getDocument() {
        if(document == null) {
            this.buildDocument();
        }

        return document;
    }
}
