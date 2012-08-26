package net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;

public class ReceivedEmailContentRule implements ReceivedEmailRule {
    private String content;

    public ReceivedEmailContentRule(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean apply(ReceivedEmailEvent receivedEmailEvent) {
        return receivedEmailEvent.getContent() == null || !receivedEmailEvent.getContent().contains(this.content);
    }
}
