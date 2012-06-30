package net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;

public class ReceivedEmailFromRule implements ReceivedEmailRule {
    private String from;

    public ReceivedEmailFromRule() {
    }

    public ReceivedEmailFromRule(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    @Override
    public boolean apply(ReceivedEmailEvent receivedEmailEvent) {
        return receivedEmailEvent.getFrom() == null || !receivedEmailEvent.getFrom().contains(this.from);
    }
}
