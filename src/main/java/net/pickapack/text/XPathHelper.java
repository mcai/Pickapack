/*******************************************************************************
 * Copyright (c) 2010-2012 by Min Cai (min.cai.china@gmail.com).
 *
 * This file is part of the PickaPack library.
 *
 * PickaPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PickaPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PickaPack. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
 * XPath helper.
 *
 * @author Min Cai
 */
public class XPathHelper {
    private static XPath xpath = XPathFactory.newInstance().newXPath();

    /**
     * Get the first node from the specified object using the specified XPath expression.
     *
     * @param obj the object
     * @param xpathExpression the XPath expression
     * @return the first node from the specified object matched using the specified XPath expression
     */
    public static Node getFirstByXPath(Object obj, String xpathExpression) {
        return evaluate(obj, xpathExpression, XPathConstants.NODE);
    }

    /**
     * Get the list of nodes from the specified object using the specified XPath expression.
     *
     * @param obj the object
     * @param xpathExpression the XPath expression
     * @return the list of nodes from the specified object matched using the specified XPath expression.
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
     * Get the document parsed from the specified text.
     *
     * @param text the text
     * @return the document parsed from the specified text
     * @throws IOException
     * @throws TransformerException
     * @throws XPathExpressionException
     */
    public static Document parse(String text) throws IOException, TransformerException, XPathExpressionException {
        return parse(text.getBytes("UTF-8"));
    }

    /**
     * Get the document parsed from the specified byte array.
     *
     * @param data the byte array
     * @return the document parsed from the specified byte array
     * @throws IOException
     * @throws TransformerException
     * @throws XPathExpressionException
     */
    public static Document parse(byte[] data) throws IOException, TransformerException, XPathExpressionException {
        return parse(data, System.getProperty("file.encoding"));
    }

    /**
     * Get the document parsed from the specified byte array using the specified charset.
     *
     * @param data the byte array
     * @param charset the charset
     * @return the document parsed from the specified byte array using the specified charset
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
