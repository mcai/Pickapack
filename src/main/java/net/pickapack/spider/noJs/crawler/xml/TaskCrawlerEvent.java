package net.pickapack.spider.noJs.crawler.xml;

import net.pickapack.event.BlockingEvent;
import net.pickapack.spider.noJs.spider.Page;

public class TaskCrawlerEvent implements BlockingEvent {
    private Page page;
    private Object parent;
    private Rule rule;
    private Object match;

    public TaskCrawlerEvent(Page page, Object parent, Rule rule, Object match) {
        this.page = page;
        this.parent = parent;
        this.rule = rule;
        this.match = match;
    }

    public Page getPage() {
        return page;
    }

    public Object getParent() {
        return parent;
    }

    public Rule getRule() {
        return rule;
    }

    public Object getMatch() {
        return match;
    }

    @Override
    public String toString() {
        return String.format("TaskCrawlerEvent{page.url=%s, parent=%s, rule=%s, match=%s}", page.getUrl(), parent, rule, match);
    }
}
