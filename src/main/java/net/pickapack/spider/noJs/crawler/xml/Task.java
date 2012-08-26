package net.pickapack.spider.noJs.crawler.xml;

import java.util.LinkedHashMap;
import java.util.Map;

public class Task {
    private String title;
    private Map<String, String> configs;
    private Map<String, URLPattern> urlPatterns;
    private Map<String, Rule> rules;

    public Task(String title) {
        this.title = title;
        this.configs = new LinkedHashMap<String, String>();
        this.urlPatterns = new LinkedHashMap<String, URLPattern>();
        this.rules = new LinkedHashMap<String, Rule>();
    }

    public String getTitle() {
        return title;
    }

    public Map<String, String> getConfigs() {
        return configs;
    }

    public Map<String, URLPattern> getUrlPatterns() {
        return urlPatterns;
    }

    public Map<String, Rule> getRules() {
        return rules;
    }

    public String getUserAgent() {
        String id = "userAgent";
        return configs.containsKey(id) ? configs.get(id) : null;
    }

    public String getProxyHost() {
        String id = "proxyHost";
        return configs.containsKey(id) ? configs.get(id) : null;
    }

    public int getProxyPort() {
        String id = "proxyPort";
        return configs.containsKey(id) ? Integer.parseInt(configs.get(id)) : -1;
    }

    public String getStorageFolder() {
        String id = "storageFolder";
        return configs.containsKey(id) ? configs.get(id) : null;
    }
}
