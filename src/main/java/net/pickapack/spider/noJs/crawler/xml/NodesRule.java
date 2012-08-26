package net.pickapack.spider.noJs.crawler.xml;

import java.util.LinkedHashMap;
import java.util.Map;

public class NodesRule extends Rule {
    private Map<String, Rule> children;

    public NodesRule(String id, String path, String applyUrlPatternIds, boolean follow, boolean download, boolean print) {
        super(id, path, applyUrlPatternIds, follow, download, print);
        this.children = new LinkedHashMap<String, Rule>();
    }

    public Map<String, Rule> getChildren() {
        return children;
    }
}
