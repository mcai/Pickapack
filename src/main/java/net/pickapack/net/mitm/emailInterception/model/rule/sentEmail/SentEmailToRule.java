package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;

public class SentEmailToRule implements SentEmailRule {
    private String to;

    public SentEmailToRule() {
    }

    public SentEmailToRule(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }

    @Override
    public boolean apply(SentEmailEvent sentEmailEvent) {
        for (String to : sentEmailEvent.getTos()) {
            if (to != null && to.contains(this.to)) {
                return false;
            }
        }

        return true;
    }
}
