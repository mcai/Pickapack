package net.pickapack.spider.noJs.crawler.xml;

import net.pickapack.dateTime.DateHelper;
import net.pickapack.spider.noJs.crawler.NoJSCrawler;
import net.pickapack.spider.noJs.spider.Page;
import net.pickapack.text.XPathHelper;
import org.w3c.dom.Node;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TaskNoJSCrawler extends NoJSCrawler {
    private Task task;

    public TaskNoJSCrawler(String fileName) {
        this(TaskParser.parse(fileName));
    }

    public TaskNoJSCrawler(Task task) {
        super(task.getUserAgent(), 60000, task.getProxyHost(), task.getProxyPort());
        this.task = task;
    }

    @Override
    protected void onPageVisited(Page page, Map<String, Object> context) {
        System.out.printf("[%s] Visiting page: %s\n", DateHelper.toString(new Date()), page.getUrl());
        visitRules(page, page.getDocument(), task.getRules());
    }

    private void visitRules(Page page, Object parent, Map<String, Rule> rules) {
        for (String ruleId : rules.keySet()) {
            Rule rule = rules.get(ruleId);
            if (match(rule, page.getUrl().toString())) {
                if (rule instanceof NodesRule) {
                    List<Node> nodes = XPathHelper.getByXPath(parent, rule.getPath());
                    Map<String, Rule> childRules = ((NodesRule) rule).getChildren();
                    for (Node node : nodes) {
                        this.visitRules(page, node, childRules);
                        visitRule(page, parent, rule, node);
                    }
                } else if (rule instanceof NodeRule) {
                    Node node = XPathHelper.getFirstByXPath(parent, rule.getPath());
                    if (node != null) {
                        visitRule(page, parent, rule, node);
                    }
                }
            }
        }
    }

    private void visitRule(Page page, Object parent, Rule rule, Node node) {
        String nodeValue = node.getNodeValue();
        if (nodeValue != null) {
            if (rule.isFollow()) {
                try {
                    this.visit(new URL(page.getUrl(), nodeValue));
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (rule.isDownload()) {
                try {
                    this.downloadDocumentByUrls(page.getUrl(), this.getTask().getStorageFolder(), new URL(page.getUrl(), nodeValue));
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (rule.isPrint()) {
                System.out.println(nodeValue);
            }
        }
        this.getEventDispatcher().dispatch(new TaskCrawlerEvent(page, parent, rule, node));
    }

    public boolean match(Rule rule, String url) {
        for (String applyUrl : rule.getApplyUrlPatternIds()) {
            URLPattern urlPattern = this.getTask().getUrlPatterns().get(applyUrl);
            if (!urlPattern.match(url)) {
                return false;
            }
        }

        return true;
    }

    public void run(String url) {
        try {
            this.visit(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Task getTask() {
        return task;
    }
}
