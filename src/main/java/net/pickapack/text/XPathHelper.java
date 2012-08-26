package net.pickapack.text;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.lang.Object;import java.lang.RuntimeException;import java.lang.String;import java.lang.SuppressWarnings;import java.util.ArrayList;
import java.util.List;

public class XPathHelper {
    private static XPath xpath = XPathFactory.newInstance().newXPath();

    public static Node getFirstByXPath(Object obj, String xpathExpression) {
        return evaluate(obj, xpathExpression, XPathConstants.NODE);
    }

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
}
