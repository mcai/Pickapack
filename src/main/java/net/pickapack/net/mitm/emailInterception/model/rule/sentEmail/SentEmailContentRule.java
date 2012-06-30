package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;

public class SentEmailContentRule implements SentEmailRule {
    private String content;

    public SentEmailContentRule() {
    }

    public SentEmailContentRule(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean apply(SentEmailEvent sentEmailEvent) {
        return sentEmailEvent.getContent() == null || !sentEmailEvent.getContent().contains(this.content);
    }
}
