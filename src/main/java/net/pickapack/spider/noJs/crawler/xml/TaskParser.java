package net.pickapack.spider.noJs.crawler.xml;

import net.pickapack.text.XPathHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TaskParser {
    public static Task parse(String fileName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fileName);

            Node nodeTitle = XPathHelper.getFirstByXPath(doc, "/task/title/text()");
            String title = nodeTitle != null ? nodeTitle.getNodeValue() : "";

            Task task = new Task(title);

            List<Node> nodePaths = XPathHelper.getByXPath(doc, "/task/configs/config");
            for (Node node : nodePaths) {
                String id = XPathHelper.getFirstByXPath(node, "./@id").getNodeValue();
                String value = XPathHelper.getFirstByXPath(node, "./@value").getNodeValue();
                task.getConfigs().put(id, value);
            }

            List<Node> nodeUrlPatterns = XPathHelper.getByXPath(doc, "task/urlPatterns/*");
            for (Node node : nodeUrlPatterns) {
                if (node.getNodeName().equals("startsWith")) {
                    String id = XPathHelper.getFirstByXPath(node, "./@id").getNodeValue();
                    String prefix = XPathHelper.getFirstByXPath(node, "./@prefix").getNodeValue();
                    task.getUrlPatterns().put(id, new StartsWithURLPattern(id, prefix));
                } else if (node.getNodeName().equals("equals")) {
                    String id = XPathHelper.getFirstByXPath(node, "./@id").getNodeValue();
                    String url = XPathHelper.getFirstByXPath(node, "./@url").getNodeValue();
                    task.getUrlPatterns().put(id, new EqualsURLPattern(id, url));
                }
            }

            List<Node> nodeRules = XPathHelper.getByXPath(doc, "/task/rules/rule");
            parseRules(task.getRules(), nodeRules);

            return task;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void parseRules(Map<String, Rule> rules, List<Node> nodes) {
        for (Node node : nodes) {
            String type = XPathHelper.getFirstByXPath(node, "./@type").getNodeValue();
            String id = XPathHelper.getFirstByXPath(node, "./@id").getNodeValue();
            String path = XPathHelper.getFirstByXPath(node, "./@path").getNodeValue();
            String applyUrlPatternIds = XPathHelper.getFirstByXPath(node, "./@applyUrlPatternIds").getNodeValue();
            boolean follow = Boolean.parseBoolean(XPathHelper.getFirstByXPath(node, "./@follow").getNodeValue());
            boolean download = Boolean.parseBoolean(XPathHelper.getFirstByXPath(node, "./@download").getNodeValue());
            boolean print = Boolean.parseBoolean(XPathHelper.getFirstByXPath(node, "./@print").getNodeValue());
            if (type.equals("nodes")) {
                NodesRule rule = new NodesRule(id, path, applyUrlPatternIds, follow, download, print);
                rules.put(id, rule);
                parseRules(rule.getChildren(), XPathHelper.getByXPath(node, "./rule"));
            } else if (type.equals("node")) {
                NodeRule rule = new NodeRule(id, path, applyUrlPatternIds, follow, download, print);
                rules.put(id, rule);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }
}
