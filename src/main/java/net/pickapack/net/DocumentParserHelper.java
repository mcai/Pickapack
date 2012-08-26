package net.pickapack.net;

import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DocumentParserHelper {
    public static Document parse(byte[] data) throws IOException, TransformerException, XPathExpressionException {
        return parse(data, HtmlCleaner.DEFAULT_CHARSET);
    }

    public static Document parse(byte[] data, String charset) throws IOException, TransformerException, XPathExpressionException {
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode rootNode = cleaner.clean(new ByteArrayInputStream(data), charset);
        try {
            return new DomSerializer(cleaner.getProperties(), true).createDOM(rootNode);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
