package net.pickapack.text;

import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.Object;import java.lang.RuntimeException;import java.lang.String;import java.lang.SuppressWarnings;import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Min Cai
 */
public class XPathHelper {
    private static XPath xpath = XPathFactory.newInstance().newXPath();

    /**
     *
     * @param obj
     * @param xpathExpression
     * @return
     */
    public static Node getFirstByXPath(Object obj, String xpathExpression) {
        return evaluate(obj, xpathExpression, XPathConstants.NODE);
    }

    /**
     *
     * @param obj
     * @param xpathExpression
     * @return
     */
    public static List<Node> getByXPath(Object obj, String xpathExpression) {
        NodeList list1 = evaluate(obj, xpathExpression, XPathConstants.NODESET);

        List<Node> nodes = new ArrayList<Node>();
        for(int i = 0; i < list1.getLength(); i++) {
            nodes.add(list1.item(i));
        }

        return nodes;
    }

    @SuppressWarnings("unchecked")
    private static <T> T evaluate(Object obj, String xpathExpression, QName qName) {
        try {
            return (T) xpath.compile(xpathExpression).evaluate(obj, qName);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param text
     * @return
     * @throws IOException
     * @throws TransformerException
     * @throws XPathExpressionException
     */
    public static Document parse(String text) throws IOException, TransformerException, XPathExpressionException {
        return parse(text.getBytes("UTF-8"));
    }

    /**
     *
     * @param data
     * @return
     * @throws IOException
     * @throws TransformerException
     * @throws XPathExpressionException
     */
    public static Document parse(byte[] data) throws IOException, TransformerException, XPathExpressionException {
        return parse(data, HtmlCleaner.DEFAULT_CHARSET);
    }

    /**
     *
     * @param data
     * @param charset
     * @return
     * @throws IOException
     * @throws TransformerException
     * @throws XPathExpressionException
     */
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
